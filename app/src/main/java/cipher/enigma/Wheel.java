package cipher.enigma;

import java.util.ArrayList;

abstract public class Wheel {
    protected static final int SIZE = 256;
    protected ArrayList<Integer> wheel = new ArrayList<>();

    public ArrayList<Integer> getWheel() {
        return wheel;
    }
}
