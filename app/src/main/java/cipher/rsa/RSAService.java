package cipher.rsa;

import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import cipher.CipherService;
import cipher.OperationStatus;
import root.iv.protection.App;
import root.iv.protection.CipherActivity;

public class RSAService extends CipherService {
    private static final String TAG = "RSA Service: ";

    public RSAService() {
        super("RSA Service");

    }
    @Override
    protected void onHandleIntent(Intent intent) {
        App.logI(TAG + "started");
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String basePath = bundle.getString(INTENT_PATH);
            String decipherPath = bundle.getString(INTENT_DECIPHER_NAME);
            String cipherPath = bundle.getString(INTENT_OUTFILE_NAME);


            RSA rsa = new RSA();

            try {
                byte[] byteContent = FileUtils.readFileToByteArray(new File(basePath));
                int[] baseContent = fromByteToInt(byteContent);
                CipherActivity.receiveStatus(this, OperationStatus.READ_BASE_FILE);

                int[] cipherContent = rsa.cipher(baseContent);
                FileUtils.writeByteArrayToFile(new File(cipherPath), fromIntToByte(cipherContent));
                CipherActivity.receiveStatus(this, OperationStatus.CIPHER_FILE);

                int[] decipherContent = rsa.decipher(cipherContent);
                FileUtils.writeByteArrayToFile(new File(decipherPath), fromIntToByte(decipherContent));
                CipherActivity.receiveStatus(this, OperationStatus.DECIPHER_FILE);
            } catch (IOException e) {
                App.logE(TAG + e.getMessage());
            }
        }
    }


}
