package root.iv.protection;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

import cipher.rsa.RSA;

public class RSAUnitTest {
    private Random random = new Random();

    @Test
    public void testPowerModular() {
        Assert.assertEquals(BigInteger.ZERO, RSA.powerMod(1,1,1));
        Assert.assertEquals(32635, RSA.powerMod(
                1234,
                535,
                99999
        ));
        Assert.assertEquals(0, RSA.powerMod(10,10,10));
    }

    @Test
    public void testRSA() {
        RSA rsa = new RSA();

        for (int i = 0; i < 1000; i++) {

            int c = rsa.cipher(i);
            int m = rsa.decipher(c);

            Assert.assertEquals(m, i);
        }
    }

    @Test
    public void testFindD() {
//        long c = RSA.findD(115, 24);
    }
}
