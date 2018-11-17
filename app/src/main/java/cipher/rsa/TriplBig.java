package cipher.rsa;

import java.math.BigInteger;

class TriplBig
{
    TriplBig(BigInteger one, BigInteger two, BigInteger three)
    {
        d=one;
        x=two;
        y=three;
    }

    TriplBig() {}

    BigInteger d;
    BigInteger x;
    BigInteger y;

    TriplBig gcdWide(BigInteger a, BigInteger b)
    {
        TriplBig temphere = new TriplBig(a,BigInteger.ONE,BigInteger.ZERO);
        TriplBig temphere2;

        if(b.equals(BigInteger.ZERO))
        {
            return temphere;
        }

        temphere2 = gcdWide(b, a.mod(b));
        temphere = new TriplBig();

        temphere.d=  temphere2.d;
        temphere.x = temphere2.y;
        temphere.y = temphere2.x.subtract(a.divide(b).multiply(temphere2.y));

        return temphere;
    }
}
