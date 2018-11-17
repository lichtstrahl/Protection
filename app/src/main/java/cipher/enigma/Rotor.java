package cipher.enigma;

import java.util.Collections;

public class Rotor extends Wheel implements RotorAPI {
    private int countRotate;
    private RotorAPI nextRotor;

    @Override
    public void rotate() {
        countRotate++;

        int buf = wheel.get(SIZE-1);
        for (int i = SIZE-1; i > 0; i--)
            wheel.set(i, wheel.get(i-1));
        wheel.set(0, buf);

        // Условие поворота. Крючок.
        if (countRotate % SIZE == 0 && nextRotor != null)
            nextRotor.rotate();
    }

    @Override
    public void addNextRotor(RotorAPI r) {
        nextRotor = r;
    }

    @Override
    public void reverse() {
        int buf = wheel.get(0);
        for (int i = 0; i < SIZE-1; i++)
            wheel.set(i, wheel.get(i+1));
        wheel.set(SIZE-1, buf);
    }

    @Override
    public void reset() {
        for (int i = 0; i < countRotate; i++) {
            reverse();
        }
    }

    public Rotor(int pos) {
        countRotate = 0;
        for (int i = 0; i < SIZE; i++)
            wheel.add(i);
        // Перемешиваем контакты на колесе
        Collections.shuffle(wheel);

        for (int i = 0; i < pos; i++)
            rotate();
    }

    @Override
    public int cipher(int c) {
        return wheel.get(c);
    }
    @Override
    public int decipher(int c) {
        return wheel.indexOf(c);
    }
}
