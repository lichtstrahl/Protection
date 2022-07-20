package root.iv.protection;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.transitionseverywhere.TransitionManager;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import root.iv.protection.cipher.OperationStatus;
import root.iv.protection.cipher.des.DESFragment;
import root.iv.protection.cipher.des.DESService;
import root.iv.protection.cipher.enigma.EnigmaFragment;
import root.iv.protection.cipher.enigma.EnigmaService;
import root.iv.protection.cipher.es.ESFragment;
import root.iv.protection.cipher.rsa.RSAFragment;
import root.iv.protection.cipher.rsa.RSAService;
import root.iv.protection.util.dialog.OpenFileDialog;
import root.iv.protection.zip.HuffmanFragment;
import root.iv.protection.zip.HuffmanService;

public class CipherActivity extends AppCompatActivity implements ESFragment.ESListener {
    private static final String SAVE_PROGRESS = "save:progress";
    private static final String SAVE_PATH = "save:path";
    private CipherReceiver cipherReceiver;
    Fragment fragment;
    @BindView(R.id.layoutContent)
    FrameLayout layoutContent;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.viewPath)
    TextView viewPath;
    @BindView(R.id.layoutMain)
    ViewGroup layoutMain;
    @BindView(R.id.buttonCipher)
    Button buttonCipher;

    @OnClick(R.id.buttonSelectFile)
    public void clickSelectFile() {

        root.iv.protection.util.dialog.OpenFileDialog dialog = new OpenFileDialog(this);
        dialog.setOpenDialogListener(path -> viewPath.setText(path)).setFileter(".*\\.*");
        dialog.show();
    }

    @OnClick(R.id.buttonCipher)
    public void clickCipher() {
        if (viewPath.getText().toString().isEmpty())
            return;

        switchVisibleProgress(View.VISIBLE);
        if (fragment instanceof EnigmaFragment) {
            EnigmaFragment ef = (EnigmaFragment)fragment;
            EnigmaService.start(this, viewPath.getText().toString(), ef.getPosL(), ef.getPosM(), ef.getPosR());
        }

        if (fragment instanceof RSAFragment) {
            RSAService.start(this, viewPath.getText().toString());
        }

        if (fragment instanceof DESFragment) {
            DESFragment df = (DESFragment)fragment;
            DESService.start(this, viewPath.getText().toString(), df.getKey());
        }

        if (fragment instanceof HuffmanFragment) {
            HuffmanService.start(this, viewPath.getText().toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher);
        ButterKnife.bind(this);
        cipherReceiver = new CipherReceiver();

        if (savedInstanceState == null) {
            fragment = setupDESFragment();
        } else {
            switchVisibleProgress(savedInstanceState.getBoolean(SAVE_PROGRESS)
                ? View.VISIBLE
                : View.GONE
            );
            viewPath.setText(savedInstanceState.getString(SAVE_PATH, ""));
        }
        TransitionManager.beginDelayedTransition(layoutMain);
        TransitionManager.getDefaultTransition()
                .setDuration(500);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVE_PROGRESS, progressBar.getVisibility() != View.GONE);
        outState.putString(SAVE_PATH, viewPath.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(cipherReceiver, new IntentFilter(CipherReceiver.ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(cipherReceiver);
    }

    private Fragment setupEnigmaFragment() {
        EnigmaFragment f = EnigmaFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutContent, f)
                .commit();
        return f;
    }

    private Fragment setupRSAFragment() {
        RSAFragment f = RSAFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutContent, f)
                .commit();
        return f;
    }

    private Fragment setupDESFragment() {
        DESFragment f = DESFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutContent, f)
                .commit();
        return f;
    }

    private Fragment setupHuffmanFragment() {
        HuffmanFragment f = HuffmanFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutContent, f)
                .commit();
        return f;
    }

    private Fragment setupESFragment() {
        ESFragment f = ESFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutContent, f)
                .commit();
        buttonCipher.setEnabled(false);
        return f;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cipher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        buttonCipher.setEnabled(true);
        switch (item.getItemId()) {
            case R.id.menuItemEnigma:
                fragment = setupEnigmaFragment();
                return true;

            case R.id.menuItemRSA:
                fragment = setupRSAFragment();
                return true;

            case R.id.menuItemDES:
                fragment = setupDESFragment();
                return true;

            case R.id.menuItemHuffman:
                fragment = setupHuffmanFragment();
                return true;

            case R.id.menuItemES:
                fragment = setupESFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void receiveStatus(Service service, OperationStatus status) {
        Intent intent = new Intent().setAction(CipherReceiver.ACTION);
        intent.putExtra(CipherReceiver.INTENT_STATUS, status);
        service.sendBroadcast(intent);
    }

    public static void receiveStatus(Service service, OperationStatus status, double k) {
        Intent intent = new Intent().setAction(CipherReceiver.ACTION);
        intent.putExtra(CipherReceiver.INTENT_STATUS, status);
        intent.putExtra(CipherReceiver.INTENT_K, k);
        service.sendBroadcast(intent);
    }

    public void switchVisibleProgress(int v) {
        progressBar.setVisibility(v);
    }


    public class CipherReceiver extends BroadcastReceiver {
        private static final String TAG = "CipherReceiver: ";
        public static final String ACTION = "root.iv.protection.END_CIPHER";
        public static final String INTENT_STATUS = "INTENT_STATUS";
        public static final String INTENT_K = "INTENT_K";

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                OperationStatus status = (OperationStatus)bundle.getSerializable(INTENT_STATUS);
                switch (status) {
                    case READ_BASE_FILE:
                        Toast.makeText(CipherActivity.this, R.string.baseFileReaded, Toast.LENGTH_SHORT).show();
                        break;

                    case CIPHER_FILE:
                        Toast.makeText(CipherActivity.this, R.string.fileCiphered, Toast.LENGTH_SHORT).show();
                        break;

                    case DECIPHER_FILE:
                        Toast.makeText(CipherActivity.this, R.string.fileDeciphered, Toast.LENGTH_SHORT).show();
                        switchVisibleProgress(View.GONE);
                        break;

                    case ZIP_FILE:
                        Toast.makeText(CipherActivity.this, R.string.fileZip, Toast.LENGTH_SHORT).show();
                        break;

                    case UNZIP_FILE:
                        double k = bundle.getDouble(INTENT_K);
                        HuffmanFragment f = (HuffmanFragment) fragment;
                        f.setTitle(String.format(Locale.ENGLISH, "Сжатый файл: %8.2f", k));
                        Toast.makeText(CipherActivity.this, R.string.fileUnzip, Toast.LENGTH_SHORT).show();
                        switchVisibleProgress(View.GONE);
                        break;

                    default:
                        throw new IllegalStateException("Не предусмотрено такого состояния");
                }
            }
        }
    }

    @Override
    public void successfulCreateES() {
        Toast.makeText(this, R.string.successfulCreateES, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void error(String msg) {
        App.logE(msg);
    }

    @Override
    public void validES() {
        Toast.makeText(this, R.string.validES, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void invalidES() {
        Toast.makeText(this, R.string.invalidES, Toast.LENGTH_SHORT).show();
    }
}
