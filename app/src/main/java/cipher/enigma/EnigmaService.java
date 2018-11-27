package cipher.enigma;

import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import cipher.CipherService;
import cipher.CipherStatus;
import root.iv.protection.App;
import root.iv.protection.CipherActivity.CipherReceiver;

/**
 * Сбои не происходят только на текстовых файлах. В чем причина?
 */
public class EnigmaService extends CipherService {
    public static final String INTENT_POS1 = "INTENT_POS1";
    public static final String INTENT_POS2 = "INTENT_POS2";
    public static final String INTENT_POS3 = "INTENT_POS3";
    private static final  String TAG = "Enigma Service: ";
    private static final Enigma originalEnigma = new Enigma(0,0,0);
    private Enigma enigmaCipher;
    private Enigma enigmaDecipher;

    public EnigmaService() {
        super("EnigmaService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String basePath = bundle.getString(INTENT_PATH);
            String decipherPath = bundle.getString(INTENT_DECIPHER_NAME);
            String cipherPath = bundle.getString(INTENT_OUTFILE_NAME);

            int countL = bundle.getInt(INTENT_POS1);
            int countM = bundle.getInt(INTENT_POS2);
            int countR = bundle.getInt(INTENT_POS3);

            // Получаем две копии оригинальной Энигмы с одинаковыми настройками
            enigmaCipher = (Enigma)originalEnigma.clone();
            enigmaCipher.rotateL(countL);
            enigmaCipher.rotateM(countM);
            enigmaCipher.rotateR(countR);


            enigmaDecipher = (Enigma)originalEnigma.clone();
            enigmaDecipher.rotateL(countL);
            enigmaDecipher.rotateM(countM);
            enigmaDecipher.rotateR(countR);


            try {
                Intent cipherIntent = new Intent().setAction(CipherReceiver.ACTION);
                int[] baseContent = fromByteToInt(FileUtils.readFileToByteArray(new File(basePath)));
                sendStatus(cipherIntent, CipherStatus.READ_BASE_FILE);

                int[] cipherContent = enigmaCipher.cipher(baseContent);
                FileUtils.writeByteArrayToFile(new File(cipherPath), fromIntToByte(cipherContent));
                sendStatus(cipherIntent, CipherStatus.CIPHER_FILE);


                int[] decipherContent = enigmaDecipher.cipher(cipherContent);
                FileUtils.writeByteArrayToFile(new File(decipherPath), fromIntToByte(decipherContent));
                sendStatus(cipherIntent, CipherStatus.DECIPHER_FILE);

            } catch (IOException e) {
                App.logE(TAG + e.getMessage());
            }
        }
    }
}