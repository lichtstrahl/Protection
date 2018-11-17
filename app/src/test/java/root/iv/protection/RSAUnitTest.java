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
        Assert.assertEquals(BigInteger.ZERO, RSA.powerMod(BigInteger.ONE,BigInteger.ONE,BigInteger.ONE));
        Assert.assertEquals(BigInteger.valueOf(32635), RSA.powerMod(
                BigInteger.valueOf(1234),
                BigInteger.valueOf(535),
                BigInteger.valueOf(99999)
        ));
        Assert.assertEquals(BigInteger.ZERO, RSA.powerMod(BigInteger.TEN,BigInteger.TEN,BigInteger.TEN));
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
