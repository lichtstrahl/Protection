package root.iv.protection;

import org.junit.Test;

import cipher.des.DES;

public class DESUnitTest {
    @Test
    public void testShiftKey() {
        int[] baseContent = new int[] {0,0,0,0,0,0,0,1};
        int[] key = new int[] {2, 6, 1};

        DES des = new DES(key);
        int[] cipherContent = des.cipher(baseContent);
        System.out.println(cipherContent);
    }
}
