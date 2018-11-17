package root.iv.protection;

import org.junit.Test;

import cipher.enigma.Enigma;

import static org.junit.Assert.assertArrayEquals;

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
        byte[] content = new byte[256];
        byte i = Byte.MIN_VALUE;
        do {
            System.out.println(i);
            i++;
        } while (i != Byte.MIN_VALUE);
    }

}