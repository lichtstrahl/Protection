package root.iv.protection;

import org.junit.Test;

import java.util.BitSet;

import root.iv.protection.cipher.CipherService;
import root.iv.protection.cipher.des.DES;

public class DESUnitTest {
    @Test
    public void testShiftKey() {
        int[] baseContent = new int[] {0,0,1,0,5,0,0,1};
        boolean[] key = new boolean[56];
        for (int i = 0; i < key.length; i++) {
            key[i] = false;
        }

        DES des = new DES(key);
        int[] cipherContent = des.cipher(baseContent);
        int[] decipherContent = des.decipher(cipherContent);
    }
}
