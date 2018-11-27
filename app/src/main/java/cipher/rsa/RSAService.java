package cipher.rsa;

import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import cipher.CipherService;
import cipher.CipherStatus;
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

            Intent cipherIntent = new Intent().setAction(CipherActivity.CipherReceiver.ACTION);
            try {
                int[] baseContent = fromByteToInt(FileUtils.readFileToByteArray(new File(basePath)));
                sendStatus(cipherIntent, CipherStatus.READ_BASE_FILE);

                int[] cipherContent = rsa.cipher(baseContent);
                FileUtils.writeByteArrayToFile(new File(cipherPath), fromIntToByte(cipherContent));
                sendStatus(cipherIntent, CipherStatus.CIPHER_FILE);

                int[] decipherContent = rsa.decipher(cipherContent);
                FileUtils.writeByteArrayToFile(new File(decipherPath), fromIntToByte(decipherContent));
                sendStatus(cipherIntent, CipherStatus.DECIPHER_FILE);
            } catch (IOException e) {
                App.logE(TAG + e.getMessage());
            }
        }
    }


}
