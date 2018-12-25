package cipher;

import android.app.IntentService;
import android.content.Intent;

import cipher.enigma.EnigmaService;
import root.iv.protection.CipherActivity;

abstract public class CipherService extends IntentService {
    protected static final String INTENT_PATH = "INTENT_PATH";
    protected static final String INTENT_OUTFILE_NAME = "INTENT_OUTFILE_NAME";
    protected static final String INTENT_DECIPHER_NAME = "INTENT_DECIPHER_NAME";

    public CipherService(String name) {
        super(name);
    }


    public static int[] fromByteToInt(byte[] bytes) {
        int[] intArray = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] < 0) {
                intArray[i] =bytes[i] & (byte)0b01111111 + 128;
            } else {
                intArray[i] =bytes[i];
            }
        }
        return intArray;
    }

    public static byte[] fromIntToByte(int[] ints) {
        byte[] byteArray = new byte[ints.length];
        for (int i = 0; i < ints.length; i++)
            byteArray[i] = (ints[i]>127) ? (byte)( ints[i] - 256) : (byte)ints[i];
        return byteArray;
    }

    public static void init(Intent cipherIntent, String path) {
        String[] word = path.split("/");
        String filename = word[word.length-1];

        cipherIntent.putExtra(INTENT_PATH, path);
        cipherIntent.putExtra(INTENT_OUTFILE_NAME, path.replace(filename, "cipher_"+filename));
        cipherIntent.putExtra(INTENT_DECIPHER_NAME, path.replace(filename, "decipher_"+filename));
    }
}
