package cipher.des;

import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import cipher.CipherService;
import cipher.OperationStatus;
import root.iv.protection.App;
import root.iv.protection.CipherActivity;

public class DESService extends CipherService {
    private static final String TAG = "DES Service: ";
    public DESService() {
        super("DESService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        App.logI(TAG + "started");
        Bundle bundle = intent.getExtras();
        Intent cipherIntent = new Intent().setAction(CipherActivity.CipherReceiver.ACTION);
        if (bundle != null) {
            String basePath = bundle.getString(INTENT_PATH);
            String decipherPath = bundle.getString(INTENT_DECIPHER_NAME);
            String cipherPath = bundle.getString(INTENT_OUTFILE_NAME);

            DES des = new DES(fromByteToInt("key".getBytes()));

            try {
                int[] baseContent = fromByteToInt(FileUtils.readFileToByteArray(new File(basePath)));
                FileUtils.readFileToString(new File(basePath));
                CipherActivity.receiveStatus(this, OperationStatus.READ_BASE_FILE);

                CipherActivity.receiveStatus(this, OperationStatus.CIPHER_FILE);

                CipherActivity.receiveStatus(this, OperationStatus.DECIPHER_FILE);
            } catch (IOException e) {
                App.logE(TAG + e.getMessage());
            }
        }

        CipherActivity.receiveStatus(this, OperationStatus.DECIPHER_FILE);
    }

}
