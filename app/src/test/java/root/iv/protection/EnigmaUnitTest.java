package root.iv.protection;

import org.junit.Assert;
import org.junit.Test;

import cipher.enigma.Enigma;
import cipher.enigma.EnigmaService;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class EnigmaUnitTest {
    private Enigma enigma = new Enigma(0,0,0);
    @Test
    public void enigma1() {
        int[] content = new int[] { 1, 2, 3, 0, 1};
        int[] cipher = enigma.cipher(content);
        enigma.reset();
        int[] decipher = enigma.cipher(cipher);
        assertArrayEquals(content, decipher);
    }

    @Test
    public void anigmaALLByte() {
        Enigma enigma1 = new Enigma(0,0,0);
        Enigma enigma2 = (Enigma)enigma1.clone();
        assertEquals(enigma1, enigma2);

        byte[] array = new byte[256];
        for (byte i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; i++)
            array[i+128] = i;
        array[255] = 127;

//        System.out.println("Begin:");
//        enigma.printState(System.out);
//        System.out.println();

        int[] baseArray = EnigmaService.fromByteToInt(array);
        assertEquals(enigma1, enigma2);
        int[] cipherArray1 = new int[baseArray.length];
        int[] cipherArray2 = new int[baseArray.length];
        for (int i = 0; i < baseArray.length; i++) {
            cipherArray1[i] = enigma1.cipher(baseArray[i]);
            cipherArray2[i] = enigma2.cipher(baseArray[i]);
            assertArrayEquals(cipherArray1, cipherArray2);
            assertEquals(enigma1, enigma2);
        }


        int[] decipherArray1 = enigma1.cipher(cipherArray1);
        int[] decipherArray2 = enigma2.cipher(cipherArray2);
        assertArrayEquals(decipherArray1, decipherArray2);
        assertEquals(enigma1, enigma2);
    }

}