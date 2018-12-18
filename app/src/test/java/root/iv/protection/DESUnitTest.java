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
        boolean[] bigKey = des.upKey();
        for (int i = 0; i < bigKey.length; i++) {
            if (i % 8 == 0)
                System.out.println();
            System.out.print(bigKey[i] ? '1' : '0');
        }
        System.out.println();
//        int[] cipherContent = des.cipher(baseContent);
//        System.out.println(cipherContent);

    }
}
