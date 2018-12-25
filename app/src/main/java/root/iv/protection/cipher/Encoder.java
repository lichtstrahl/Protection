package root.iv.protection.cipher;

abstract public class Encoder {

    abstract public int cipher(int m);

    public int[] cipher(int[] c) {
        int n = c.length;
        int[] m = new int[n];
        for (int i =0; i < n; i++) {
            m[i] = cipher(c[i]);
        }

        return m;
    }
}
