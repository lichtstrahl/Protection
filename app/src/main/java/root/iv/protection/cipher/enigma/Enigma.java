package root.iv.protection.cipher.enigma;

import root.iv.protection.cipher.Encoder;

public class Enigma extends Encoder {
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

        lRotor.
                rotate();
        return b;
    }
    // ИУ7-72
    // 12.1 9:00 экзамен
    //

    public void reset() {
        lRotor.reset();
        mRotor.reset();
        rRotor.reset();
    }

    public void rotateL(int count) {
        for (int i = 0; i < count; i++)
            lRotor.originalRotate();
    }

    public void rotateM(int count) {
        for (int i = 0; i < count; i++)
            mRotor.originalRotate();
    }

    public void rotateR(int count) {
        for (int i = 0; i < count; i++)
            rRotor.originalRotate();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Enigma) {
            Enigma e = (Enigma)obj;
            return lRotor.equals(e.lRotor)
                    && rRotor.equals(e.rRotor)
                    && mRotor.equals(e.mRotor)
                    && reflector.equals(e.reflector);
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
