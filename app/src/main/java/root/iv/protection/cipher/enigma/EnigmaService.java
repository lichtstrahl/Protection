package root.iv.protection.cipher.enigma;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import root.iv.protection.cipher.CipherService;
import root.iv.protection.cipher.OperationStatus;
import root.iv.protection.App;
import root.iv.protection.CipherActivity;

/**
 * Сбои не происходят только на текстовых файлах. В чем причина?
 */
public class EnigmaService extends CipherService {
    private static final String INTENT_POS1 = "INTENT_POS1";
    private static final String INTENT_POS2 = "INTENT_POS2";
    private static final String INTENT_POS3 = "INTENT_POS3";
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
                int[] baseContent = fromByteToInt(FileUtils.readFileToByteArray(new File(basePath)));
                CipherActivity.receiveStatus(this, OperationStatus.READ_BASE_FILE);

                int[] cipherContent = enigmaCipher.cipher(baseContent);
                FileUtils.writeByteArrayToFile(new File(cipherPath), fromIntToByte(cipherContent));
                CipherActivity.receiveStatus(this, OperationStatus.CIPHER_FILE);


                int[] decipherContent = enigmaDecipher.cipher(cipherContent);
                FileUtils.writeByteArrayToFile(new File(decipherPath), fromIntToByte(decipherContent));
                CipherActivity.receiveStatus(this, OperationStatus.DECIPHER_FILE);

            } catch (IOException e) {
                App.logE(TAG + e.getMessage());
            }
        }
    }

    public static void start(Activity activity, String path, int pos1, int pos2, int pos3) {
        Intent intent = new Intent(activity, EnigmaService.class);

        init(intent, path);
        intent.putExtra(INTENT_POS1, pos1);
        intent.putExtra(INTENT_POS2, pos2);
        intent.putExtra(INTENT_POS3, pos3);

        activity.startService(intent);
    }
}