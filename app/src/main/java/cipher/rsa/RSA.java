package cipher.rsa;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Locale;
import java.util.Random;

import root.iv.protection.App;

// TODO Выбор P и Q должен быть случайным
public class RSA {
    private static final int BLOCK_SIZE = 256;
    private static Random random = new Random();
    private Key publicKey;
    private Key privateKey;

    public RSA() {
        generateKeys();
    }

    private void generateKeys() {
        BitSet set = getPrimesUpTo(BLOCK_SIZE/2);

        // Получим большие простые P,Q
        int P = -1;
        int Q = -1;
        for (int i = set.length()-1; i >= 0; i--) {
            boolean bit = set.get(i);
            if (bit && P > 0) {
                Q = i;
                break;
            }

            if (bit && P < 0)
                P = i;
        }
        // Получим N
        BigInteger N = BigInteger.valueOf(P*Q);
        // Получим e
        BigInteger e = calcuteE(BigInteger.valueOf((P-1)*(Q-1)));
        // Ищем d
        BigInteger d = findD(e, BigInteger.valueOf((P-1)*(Q-1)));

        while (d.intValue() < 0) {
            e = calcuteE(BigInteger.valueOf((P-1)*(Q-1)));
            d = findD(e, BigInteger.valueOf((P-1)*(Q-1)));
        }

        publicKey = new Key(e, N);
        privateKey = new Key(d, N);
    }

    public int cipher(int msg) {
        int c = powerMod(BigInteger.valueOf(msg), publicKey.getKey(), publicKey.getMod()).intValue();
        return c;
    }

    public int[] cipher(int[] msg) {
        int[] c = new int[msg.length];

        for (int i = 0; i < msg.length; i++) {
            c[i] = cipher(msg[i]);
            double pr = i*100.0 / msg.length ;
            App.logI("Cipher %" + String.format(Locale.ENGLISH, "%2.3f", pr));
        }

        return c;
    }

    public int decipher(int c) {
        int m = powerMod(BigInteger.valueOf(c), privateKey.getKey(), privateKey.getMod()).intValue();
        return m;
    }

    public int[] decipher(int[] c) {
        int[] m = new int[c.length];
        for (int i =0; i < c.length; i++) {
            m[i] = decipher(c[i]);
            double pr = i*100.0 / c.length;
            App.logI("Decipher %" + String.format(Locale.ENGLISH, "%2.3f", pr));
        }

        return m;
    }

    public static BigInteger calcuteE(BigInteger f) {
        BigInteger e = BigInteger.valueOf(Math.abs(random.nextInt(Short.MAX_VALUE)));
        while (!nod(e,f).equals(BigInteger.ONE))
            e = BigInteger.valueOf(Math.abs(random.nextInt(Short.MAX_VALUE)));
        return e;
    }

    public static BigInteger findD(BigInteger a, BigInteger b) {
        BigInteger x;
        BigInteger[] E = new BigInteger[] {BigInteger.ONE,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ONE};
        while (true) {
            BigInteger q = a.divide(b);
            BigInteger r = a.mod(b);
            if ( r.equals(BigInteger.ZERO) )
            {
                x = E[1];
                break;
            }
            E = Mult2x2Matr( E, new BigInteger[] { BigInteger.ZERO, BigInteger.ONE, BigInteger.ONE, q.negate() } );
            a = b;
            b = r;
        }
        return x;
    }

    static BigInteger[] Mult2x2Matr(BigInteger[] a, BigInteger[] b)
    {
        BigInteger[] res = new BigInteger[4];
        res[0] = a[0].multiply(b[0]) .add(a[1].multiply(b[2]));  // a0*b0 + a1*b2
        res[1] = a[0].multiply(b[1]) .add(a[1].multiply(b[3]));  // a0*b1 + a1*b3
        res[2] = a[2].multiply(b[0]) .add(a[3]).multiply(b[2]);  // a2*b0 + a3*b2
        res[3] = a[2].multiply(b[1]) .add(a[3]).multiply(b[3]);  // a2*b1 + a3*b3
        return res;
    }



    private static BigInteger nod(BigInteger a, BigInteger b) {
        while (!b.equals(BigInteger.ZERO)) {
            BigInteger tmp = a.mod(b);
            a = b;
            b = tmp;
        }
        return a;
    }

    private static BitSet getPrimesUpTo(int limit) {
        BitSet sieve = new BitSet();
        // Предварительное просеивание
        for (long x2 = 1L, dx2 = 3L; x2 < limit; x2 += dx2, dx2 += 2L)
            for (long y2 = 1L, dy2 = 3L, n; y2 < limit; y2 += dy2, dy2 += 2L) {
                // n = 4x² + y²
                n = (x2 << 2L) + y2;
                if (n <= limit && (n % 12L == 1L || n % 12L == 5L))
                    sieve.flip((int)n);
                // n = 3x² + y²
                n -= x2;
                if (n <= limit && n % 12L == 7L)
                    sieve.flip((int)n);
                // n = 3x² - y² (при x > y)
                if (x2 > y2) {
                    n -= y2 << 1L;
                    if (n <= limit && n % 12L == 11L)
                        sieve.flip((int)n);
                }
            }
        // Все числа, кратные квадратам, помечаются как составные
        int r = 5;
        for (long r2 = r * r, dr2 = (r << 1L) + 1L; r2 < limit; ++r, r2 += dr2, dr2 += 2L)
            if (sieve.get(r))
                for (long mr2 = r2; mr2 < limit; mr2 += r2)
                    sieve.set((int)mr2, false);
        // Числа 2 и 3 — заведомо простые
        if (limit > 2)
            sieve.set(2, true);
        if (limit > 3)
            sieve.set(3, true);
        return sieve;
    }

    public static BigInteger powerMod(BigInteger a, BigInteger k, BigInteger n) {
        BigInteger r = BigInteger.ONE;
        BigInteger ax = a;
        while (!k.equals(BigInteger.ZERO)) {
            if (!k.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO))
                r = r.multiply(ax).mod(n);

            ax = ax.multiply(ax).mod(n);
            k = k.divide(BigInteger.valueOf(2));
        }

        return r;
    }

    public class Key {
        private BigInteger key;
        private BigInteger mod;
        Key(BigInteger k, BigInteger m) {
            key = k;
            mod = m;
        }

        BigInteger getKey() {
            return key;
        }

        BigInteger getMod() {
            return mod;
        }
    }
}
