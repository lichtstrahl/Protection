package cipher.enigma;

public class Enigma {
    private RotorAPI lRotor;
    private RotorAPI mRotor;
    private RotorAPI rRotor;
    private Reflector reflector;

    private void configuration() {
        lRotor.addNextRotor(mRotor);
        mRotor.addNextRotor(rRotor);
    }

    public Enigma(int p1, int p2, int p3) {
        lRotor = new Rotor(p1);
        mRotor = new Rotor(p2);
        rRotor = new Rotor(p3);
        reflector = new Reflector();
        configuration();
    }

    public int cipher(int b) {
        b = lRotor.cipher(b);
        b = mRotor.cipher(b);
        b = rRotor.cipher(b);

        b = reflector.reflect(b);

        b = rRotor.decipher(b);
        b = mRotor.decipher(b);
        b = lRotor.decipher(b);

        lRotor.rotate();
        return b;
    }

    public int[] cipher(int[] bArray) {
        int[] result = new int[bArray.length];
        for (int i = 0; i < bArray.length; i++)
            result[i] = cipher(bArray[i]);
        return result;
    }

    public void reset() {
        lRotor.reset();
        mRotor.reset();
        rRotor.reset();
    }

    public RotorAPI getlRotor() {
        return lRotor;
    }

    public RotorAPI getmRotor() {
        return mRotor;
    }

    public RotorAPI getrRotor() {
        return rRotor;
    }

    public Reflector getReflector() {
        return reflector;
    }
}
