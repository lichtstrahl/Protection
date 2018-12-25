package root.iv.protection.cipher.enigma;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import root.iv.protection.App;

public class Rotor extends Wheel {
    private int countRotate;
    private Rotor nextRotor;

    public Rotor(int pos) {
        countRotate = 0;
        for (int i = 0; i < SIZE; i++)
            wheel.add(i);
        // Перемешиваем контакты на колесе
        Collections.shuffle(wheel);

        for (int i = 0; i < pos; i++)
            rotate();
    }

    public void rotate() {
        countRotate++;

        originalRotate();

        // Условие поворота. Крючок.
        if (countRotate % SIZE == 0 && nextRotor != null)
            nextRotor.rotate();
    }

    public void originalRotate() {
        int buf = wheel.get(SIZE-1);
        for (int i = SIZE-1; i > 0; i--)
            wheel.set(i, wheel.get(i-1));
        wheel.set(0, buf);
    }

    void addNextRotor(Rotor r) {
        nextRotor = r;
    }

    private void reverse() {
        int buf = wheel.get(0);
        for (int i = 0; i < SIZE-1; i++)
            wheel.set(i, wheel.get(i+1));
        wheel.set(SIZE-1, buf);
    }

    void reset() {
        for (int i = 0; i < countRotate; i++) {
            reverse();
        }
        countRotate = 0;
    }

    public int cipher(int c) {
        return wheel.get(c);
    }

    int decipher(int c) {
        return wheel.indexOf(c);
    }


    void printState(PrintStream stream) {
        for (int i = 0; i < wheel.size(); i++)
            stream.print(String.format(Locale.ENGLISH, "%4d", wheel.get(i)));
        stream.println();
    }

    @Override
    protected Object clone() {
        Rotor r = new Rotor(0);
        r.wheel = new ArrayList<>(wheel);
        r.countRotate = countRotate;
        return r;
    }
}
