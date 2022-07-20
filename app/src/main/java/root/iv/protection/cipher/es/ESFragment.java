package root.iv.protection.cipher.es;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import root.iv.protection.cipher.CipherService;
import root.iv.protection.cipher.rsa.Key;
import root.iv.protection.cipher.rsa.RSA;
import root.iv.protection.App;
import root.iv.protection.R;

public class ESFragment extends Fragment {
    private static final String TAG = "ESFragment: ";
    private static final String FILE_MSG = "MSG";
    private RSA rsa;
    private Key cipherKey;
    private Key decipherKey;
    private Button buttonCreateES;
    private Button buttonReadES;
    private ESListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_es, container, false);
        buttonCreateES = view.findViewById(R.id.buttonCreateES);
        buttonReadES = view.findViewById(R.id.buttonReadES);
        buttonCreateES.setOnClickListener(this::clickCreateES);
        buttonReadES.setOnClickListener(this::clickReadES);
        rsa = new RSA();
        cipherKey = rsa.getPublicKey();
        decipherKey = rsa.getPrivateKey();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ESListener)context;
    }

    public static ESFragment getInstance() {
        ESFragment f = new ESFragment();
        Bundle bundle= new Bundle();

        f.setArguments(bundle);
        return f;
    }

    public interface ESListener {
        void successfulCreateES();
        void error(String msg);
        void validES();
        void invalidES();
    }

    public void clickCreateES(View b) {
        try {
            TextView view = getActivity().findViewById(R.id.viewPath);
            String path = view.getText().toString();

            byte[] content = FileUtils.readFileToByteArray(new File(path));
            int H = Arrays.hashCode(content);
            String hash = String.valueOf(H);
            int[] cipherHash = rsa.cipher(CipherService.fromByteToInt(hash.getBytes()), cipherKey);

            saveES(new File(path).getParentFile(), new MSG(new File(path), cipherHash, decipherKey));
            listener.successfulCreateES();
        } catch (Exception e) {
            App.logE(e.getMessage());
            listener.error(e.getMessage());
        }
    }

    public void clickReadES(View b) {
        try {
            TextView view = getActivity().findViewById(R.id.viewPath);
            String path = view.getText().toString();

            MSG msg = readES(new File(path));
            File f = msg.getFile();
            int H = Arrays.hashCode(FileUtils.readFileToByteArray(f));
            int[] decipher = rsa.cipher(msg.getCipherHash(), msg.getKey());

            String hash = String.valueOf(H);

            for (int i = 0; i < Math.min(hash.length(), decipher.length); i++) {
                if (decipher[i] != (int) hash.charAt(i)) {
                    listener.invalidES();
                    return;
                }
            }
            listener.validES();

        } catch (Exception e) {
            App.logE(e.getMessage());
            listener.error(e.getMessage());
        }
    }

    private void saveES(File parent, MSG msg) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            App.logI(TAG + "Нет доступа к памяти");
            return;
        }

        try {
            File file = new File(parent, FILE_MSG);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(msg);
            oos.close();
        } catch (Exception e) {
            App.logE(TAG + e.getMessage());
        }
    }

    @Nullable
    private MSG readES(File path) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            App.logI(TAG + "Нет доступа к памяти");
            return null;
        }

        MSG msg = null;
        try {
            ObjectInputStream oos = new ObjectInputStream(new FileInputStream(path));
            msg = (MSG)oos.readObject();
            oos.close();
        } catch (Exception e) {
            App.logE(TAG + e.getMessage());
        }

        return msg;
    }
}
