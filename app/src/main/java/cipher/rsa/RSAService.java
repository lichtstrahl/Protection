package cipher.rsa;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import cipher.CipherServiceAPI;
import cipher.CipherStatus;
import root.iv.protection.App;
import root.iv.protection.CipherActivity;

public class RSAService extends IntentService implements CipherServiceAPI {
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

    private int[] fromByteToInt(byte[] bytes) {
        int[] intArray = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] < 0) {
                intArray[i] =bytes[i] & (byte)0b01111111;
            } else {
                intArray[i] =bytes[i] +  128;
            }
        }
        return intArray;
    }

    private byte[] fromIntToByte(int[] ints) {
        byte[] byteArray = new byte[ints.length];
        for (int i = 0; i < ints.length; i++)
            byteArray[i] = (byte)( ints[i] - 128);
        return byteArray;
    }

    private void sendStatus(Intent intent, CipherStatus status) {
        intent.putExtra(CipherActivity.CipherReceiver.INTENT_STATUS, status);
        sendBroadcast(intent);
    }
}
