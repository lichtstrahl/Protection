package cipher.des;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;

import cipher.CipherService;
import cipher.OperationStatus;
import root.iv.protection.App;
import root.iv.protection.CipherActivity;

public class DESService extends CipherService {
    private static final String TAG = "DES Service: ";
    private static final String INTENT_KEY = "args:key";
    public DESService() {
        super("DESService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        App.logI(TAG + "started");
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            String basePath = bundle.getString(INTENT_PATH);
            String decipherPath = bundle.getString(INTENT_DECIPHER_NAME);
            String cipherPath = bundle.getString(INTENT_OUTFILE_NAME);
            long key = bundle.getLong(INTENT_KEY);

            DES des = new DES(key);

            try {
                int[] baseContent = fromByteToInt(FileUtils.readFileToByteArray(new File(basePath)));
                FileUtils.readFileToString(new File(basePath));
                CipherActivity.receiveStatus(this, OperationStatus.READ_BASE_FILE);

                int[] cipherContent = des.cipher(baseContent);
                FileUtils.writeByteArrayToFile(new File(cipherPath), fromIntToByte(cipherContent));
                CipherActivity.receiveStatus(this, OperationStatus.CIPHER_FILE);

                int[] decipherContent = des.decipher(cipherContent);
                FileUtils.writeByteArrayToFile(new File(decipherPath), fromIntToByte(decipherContent));
                CipherActivity.receiveStatus(this, OperationStatus.DECIPHER_FILE);
            } catch (IOException e) {
                App.logE(TAG + e.getMessage());
            }
        }

        CipherActivity.receiveStatus(this, OperationStatus.DECIPHER_FILE);
    }

    public static void start(Activity activity, String path, long key) {
        Intent intent = new Intent(activity, DESService.class);
        init(intent, path);
        intent.putExtra(INTENT_KEY, key);

        activity.startService(intent);
    }

}
