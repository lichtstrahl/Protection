package root.iv.protection;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import cipher.CipherService;
import cipher.Encoder;
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

    @Ignore
    public void anigmaALLByte() {
        Enigma enigma1 = new Enigma(15, 1, 0);
        Enigma enigma2 = (Enigma)enigma1.clone();
        assertEquals(enigma1, enigma2);

        byte[] array = new byte[256];
        for (byte i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; i++)
            array[i+128] = i;
        array[255] = 127;
    }

}