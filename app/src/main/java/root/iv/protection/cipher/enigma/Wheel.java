package root.iv.protection.cipher.enigma;

import java.util.ArrayList;

abstract public class Wheel {
    protected static final int SIZE = 256;
    protected ArrayList<Integer> wheel = new ArrayList<>();

    public ArrayList<Integer> getWheel() {
        return wheel;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Wheel) {
            Wheel w = (Wheel)obj;
            for (int i = 0; i < SIZE; i++)
                if (!wheel.get(i).equals(w.wheel.get(i)))
                    return false;
            return true;
        }
        return false;
    }

}
