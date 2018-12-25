package root.iv.protection.cipher.enigma;

import java.io.Serializable;

public class RotorState implements Serializable {
    public int pos1, pos2, pos3;
    public RotorState(int p1, int p2, int p3) {
        pos1 = p1;
        pos2 = p2;
        pos3 = p3;
    }
}
