package cipher.enigma;

import java.io.PrintStream;

public class Enigma {
    private Rotor lRotor;
    private Rotor mRotor;
    private Rotor rRotor;
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

    public Rotor getlRotor() {
        return lRotor;
    }

    public Rotor getmRotor() {
        return mRotor;
    }

    public Rotor getrRotor() {
        return rRotor;
    }

    public Reflector getReflector() {
        return reflector;
    }

    public void printState(PrintStream stream) {
        lRotor.printState(stream);
        mRotor.printState(stream);
        rRotor.printState(stream);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Enigma) {
            Enigma e = (Enigma)obj;
            return lRotor.equals(e.lRotor) && rRotor.equals(e.rRotor) && mRotor.equals(e.mRotor) && reflector.equals(e.reflector);
        }
        return false;
    }

    @Override
    public Object clone() {
        Enigma enigma = new Enigma(0,0,0);
        enigma.lRotor = (Rotor)lRotor.clone();
        enigma.mRotor = (Rotor)mRotor.clone();
        enigma.rRotor = (Rotor)rRotor.clone();
        enigma.reflector = (Reflector)reflector.clone();
        enigma.lRotor.addNextRotor(enigma.mRotor);
        enigma.mRotor.addNextRotor(enigma.rRotor);
        return enigma;
    }
}
