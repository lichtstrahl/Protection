package cipher.enigma;

import java.util.ArrayList;
import java.util.Collections;

public class Reflector extends Wheel {
    public Reflector() {
        ArrayList<Integer> half = new ArrayList<>();
        for (int i = 0; i < SIZE/2; i++)
            half.add(SIZE/2 + i);
        Collections.shuffle(half);

        wheel.addAll(half);
        wheel.addAll(half);
        for (int i = 0; i < SIZE/2; i++) {
            wheel.set(i, half.get(i));
            wheel.set(half.get(i), i);
        }
    }

    int reflect(int c) {
        return wheel.get(c);
    }

    @Override
    protected Object clone() {
        Reflector reflector = new Reflector();
        reflector.wheel = new ArrayList<>(wheel);
        return reflector;
    }
}
