package cipher.des;

// 64-х битный блок
public class Block {
    public boolean[] value;
    Block(boolean[] v) {
        if (v.length != 64) throw new IllegalArgumentException("Block: Длина блока не равна 64 битам");

        value = v;
    }

    public final int SIZE_OF_BYTE = 8;
    public final int SIZE_OF_BITS = 64;

    public void transfer(int[] transferMatrix) {
        value = DES.transfer(value, transferMatrix);
    }
}