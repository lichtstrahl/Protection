package root.iv.protection;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Phone;
import retrofit.RequestPhonesProcessor;
import retrofit.bodyAnalyzer;


public class StartActivity extends AppCompatActivity implements bodyAnalyzer {
    private final  String TAG = "StartActivity";
    private static final String KEY_LICENSE = "KEY_LICENSE";
    private String id;
    private String device;

    @BindView(R.id.viewDevice)
    TextView viewDevice;

    @BindView(R.id.viewDone)
    ImageView viewDone;

    @BindView(R.id.buttonLoad)
    ImageButton buttonLoad;
    @OnClick(R.id.buttonLoad)
    public void clickLoad() {
        RequestPhonesProcessor processor = new RequestPhonesProcessor(this);
        App.getPhoneAPI().getAllPhones().enqueue(processor);
    }

    @BindView(R.id.buttonStart)
    Button buttonStart;
    @OnClick(R.id.buttonStart)
    public void clickStart() {
        Intent intent = new Intent(this, CipherActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        enableButtonStart();
        if (buttonStart.isEnabled())
            buttonStart.callOnClick();
    }

    private void enableButtonStart() {
        runOnUiThread(()-> {
                buttonStart.setEnabled(getPreferences(MODE_PRIVATE).getBoolean(KEY_LICENSE, false));
                viewDone.setVisibility(buttonStart.isEnabled() ? View.VISIBLE : View.GONE);
                if (buttonStart.isEnabled()) buttonLoad.setImageResource(R.drawable.ic_unlock);
            }
        );

    }

    private void phoneNotFound() {
        runOnUiThread(()->{
            Toast.makeText(StartActivity.this, R.string.notTargetDevice, Toast.LENGTH_LONG).show();
            StartActivity.this.viewDevice.setText(
                    String.format(Locale.ENGLISH, "%s %s;\n%s %s;\n", "Android id", id, "device", device)
            );
        });
    }

    @Override
    public void done(final List<Phone> phones) {
        new Thread(() -> {
                id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                device = Build.BRAND + " " + Build.DEVICE;    // Промышленное название

                for (Phone phone : phones)
                    if (phone.getAndroidId().equals(id)) {
                        getPreferences(MODE_PRIVATE).edit().putBoolean(KEY_LICENSE, true).apply();
                        enableButtonStart();
                        return;
                    }
                phoneNotFound();
        }).start();
    }

    @Override
    public void fail(int resMsg) {
        Toast.makeText(this, resMsg, Toast.LENGTH_LONG).show();
    }
}


