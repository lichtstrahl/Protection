package cipher;

import android.app.IntentService;
import android.content.Intent;

import root.iv.protection.CipherActivity;

abstract public class CipherService extends IntentService {
    public static final String INTENT_PATH = "INTENT_PATH";
    public static final String INTENT_OUTFILE_NAME = "INTENT_OUTFILE_NAME";
    public static final String INTENT_DECIPHER_NAME = "INTENT_DECIPHER_NAME";

    public CipherService(String name) {
        super(name);
    }


    protected int[] fromByteToInt(byte[] bytes) {
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

    protected byte[] fromIntToByte(int[] ints) {
        byte[] byteArray = new byte[ints.length];
        for (int i = 0; i < ints.length; i++)
            byteArray[i] = (ints[i]>127) ? (byte)( ints[i] - 256) : (byte)ints[i];
        return byteArray;
    }
}
