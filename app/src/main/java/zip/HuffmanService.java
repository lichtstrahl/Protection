package zip;

import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Scanner;

import cipher.CipherService;
import cipher.CipherStatus;
import root.iv.protection.App;
import root.iv.protection.CipherActivity;

public class HuffmanService extends CipherService {
    private static final String NAME = "Huffman service";
    private static final String TAG = NAME + ": ";

    public HuffmanService() {
        super("HuffmanService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            String basePath = bundle.getString(INTENT_PATH);
            String decipherPath = bundle.getString(INTENT_DECIPHER_NAME);
            String cipherPath = bundle.getString(INTENT_OUTFILE_NAME);

            Huffman huffman = new Huffman();
            Intent cipherIntent = new Intent().setAction(CipherActivity.CipherReceiver.ACTION);

            try {
                byte[] byteContent = FileUtils.readFileToByteArray(new File(basePath));
                int[] baseContent = fromByteToInt(byteContent);
                sendStatus(cipherIntent, CipherStatus.READ_BASE_FILE);


                int[] zipContent = huffman.zip(baseContent);
                sendStatus(cipherIntent, CipherStatus.DECIPHER_FILE);
            } catch (Exception e) {
                App.logE(e.getMessage());
            }
        }
    }
}
