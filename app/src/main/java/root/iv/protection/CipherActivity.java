package root.iv.protection;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cipher.CipherService;
import cipher.OperationStatus;
import cipher.des.DESFragment;
import cipher.des.DESService;
import cipher.rsa.RSAService;
import dialog.OpenFileDialog;
import cipher.enigma.EnigmaFragment;
import cipher.enigma.EnigmaService;
import cipher.rsa.RSAFragment;
import zip.Huffman;
import zip.HuffmanFragment;
import zip.HuffmanService;

public class CipherActivity extends AppCompatActivity {
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

    @OnClick(R.id.buttonSelectFile)
    public void clickSelectFile() {
        dialog.OpenFileDialog dialog = new OpenFileDialog(this);
        dialog.setOpenDialogListener(path -> viewPath.setText(path)).setFileter(".*\\.*");
        dialog.show();
    }

    @OnClick(R.id.buttonCipher)
    public void clickCipher() {
        if (viewPath.getText().toString().isEmpty())
            return;

        if (fragment instanceof EnigmaFragment) {
            EnigmaFragment ef = (EnigmaFragment)fragment;
            startEnigmaCipher(ef.getPosL(), ef.getPosM(), ef.getPosR());
        }

        if (fragment instanceof RSAFragment) {
            startRSACipher();
        }

        if (fragment instanceof DESFragment) {
            startDESCipher();
        }

        if (fragment instanceof HuffmanFragment) {
            startHuffmanZip();
        }
    }

    private void startEnigmaCipher(int pos1, int pos2, int pos3) {
        progressBar.setVisibility(View.VISIBLE);
        Intent enigmaIntent = new Intent(this, EnigmaService.class);
        String path = viewPath.getText().toString();
        String[] word = path.split("/");
        String filename = word[word.length-1];

        enigmaIntent.putExtra(CipherService.INTENT_PATH, path);
        enigmaIntent.putExtra(CipherService.INTENT_OUTFILE_NAME, path.replace(filename, "cipher_"+filename));
        enigmaIntent.putExtra(EnigmaService.INTENT_DECIPHER_NAME, path.replace(filename, "decipher_"+filename));
        enigmaIntent.putExtra(EnigmaService.INTENT_POS1, pos1);
        enigmaIntent.putExtra(EnigmaService.INTENT_POS2, pos2);
        enigmaIntent.putExtra(EnigmaService.INTENT_POS3, pos3);
        startService(enigmaIntent);
    }

    private void startRSACipher() {
        switchVisibleProgress(View.VISIBLE);

        Intent rsaIntent = new Intent(this, RSAService.class);
        String path = viewPath.getText().toString();
        String[] word = path.split("/");
        String filename = word[word.length-1];

        rsaIntent.putExtra(EnigmaService.INTENT_PATH, path);
        rsaIntent.putExtra(EnigmaService.INTENT_OUTFILE_NAME, path.replace(filename, "cipher_"+filename));
        rsaIntent.putExtra(EnigmaService.INTENT_DECIPHER_NAME, path.replace(filename, "decipher_"+filename));
        startService(rsaIntent);
    }

    private void startDESCipher() {
        switchVisibleProgress(View.VISIBLE);
        Intent intent = new Intent(this, DESService.class);
        startService(intent);
    }

    private void startHuffmanZip() {
        switchVisibleProgress(View.VISIBLE);

        Intent huffmanIntent = new Intent(this, HuffmanService.class);
        String path = viewPath.getText().toString();
        String[] word = path.split("/");
        String filename = word[word.length-1];

        huffmanIntent.putExtra(EnigmaService.INTENT_PATH, path);
        huffmanIntent.putExtra(EnigmaService.INTENT_OUTFILE_NAME, path.replace(filename, "cipher_"+filename));
        huffmanIntent.putExtra(EnigmaService.INTENT_DECIPHER_NAME, path.replace(filename, "decipher_"+filename));
        startService(huffmanIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher);
        ButterKnife.bind(this);
        cipherReceiver = new CipherReceiver();
        viewPath.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Toast.makeText(CipherActivity.this, v.getText().toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        if (savedInstanceState == null) fragment = setupRSAFragment();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cipher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        TransitionManager.beginDelayedTransition(layoutMain);
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
                        TransitionManager.beginDelayedTransition(layoutMain);
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
}
