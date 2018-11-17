package cipher.enigma;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import cipher.CipherServiceAPI;
import root.iv.protection.App;
import root.iv.protection.CipherActivity.CipherReceiver;
import cipher.CipherStatus;

/**
 * Сбои не происходят только на текстовых файлах. В чем причина?
 */
public class EnigmaService extends IntentService implements CipherServiceAPI {
    public static final String INTENT_POS1 = "INTENT_POS1";
    public static final String INTENT_POS2 = "INTENT_POS2";
    public static final String INTENT_POS3 = "INTENT_POS3";

    private final static String TAG = "Enigma Service: ";

    public EnigmaService() {
        super("EnigmaService");
    }
    private Enigma enigma;
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String basePath = bundle.getString(INTENT_PATH);
            String decipherPath = bundle.getString(INTENT_DECIPHER_NAME);
            String cipherPath = bundle.getString(INTENT_OUTFILE_NAME);

            enigma = new Enigma(
                    bundle.getInt(INTENT_POS1),
                    bundle.getInt(INTENT_POS2),
                    bundle.getInt(INTENT_POS3)
            );

            try {
                Intent cipherIntent = new Intent().setAction(CipherReceiver.ACTION);
                int[] baseContent = fromByteToInt(FileUtils.readFileToByteArray(new File(basePath)));
                sendStatus(cipherIntent, CipherStatus.READ_BASE_FILE);

                int[] cipherContent = enigma.cipher(baseContent);
                FileUtils.writeByteArrayToFile(new File(cipherPath), fromIntToByte(cipherContent));
                sendStatus(cipherIntent, CipherStatus.CIPHER_FILE);

                enigma.reset();

                int[] decipherContent = enigma.cipher(cipherContent);
                FileUtils.writeByteArrayToFile(new File(decipherPath), fromIntToByte(decipherContent));
                sendStatus(cipherIntent, CipherStatus.DECIPHER_FILE);

            } catch (IOException e) {
                App.logE(TAG + e.getMessage());
            }
        }
    }

    /**
     * @deprecated - Потому что
     * @param path - путь к файлу
     */
    @Deprecated
    private void copy(String path) {
        try {
            File file = new File(path);
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_16LE);
            FileUtils.writeByteArrayToFile(new File(path + "_str_byte"), content.getBytes());
        } catch (IOException e) {
            App.logE(e.getMessage());
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
        intent.putExtra(CipherReceiver.INTENT_STATUS, status);
        sendBroadcast(intent);
    }

}