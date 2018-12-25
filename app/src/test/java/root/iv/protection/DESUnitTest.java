package root.iv.protection;

import org.junit.Test;

import java.util.BitSet;

import cipher.CipherService;
import cipher.des.DES;

public class DESUnitTest {
    @Test
    public void testShiftKey() {
        int[] baseContent = new int[] {0,0,0,0,0,0,0,1};
        boolean[] key = new boolean[56];
        for (int i = 0; i < key.length; i++) {
            key[i] = false;
        }

        DES des = new DES(key);
        int[] cipherContent = des.cipher(baseContent);

    }
}
