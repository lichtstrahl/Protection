package cipher.des;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

import cipher.Encoder;

public class DES extends Encoder {
    private static final int SIZE_OF_BLOCK = 8; // Размер блока в байтах, также размер одного символа
//    private static final int SIZE_OF_CHAR = 8;  // Размер одного символа (utf8)
    private static final int shiftKey = 2;      // Сдвиг ключа
    private static final int countOfRounds = 16;// Количество раундов в сети Фейстеля
    private static final Random random = new Random();
    private boolean[] key;
    private boolean[][] keys;


    // 64-х битный блок
    public class Block {
        public int[] value;
        Block(int[] v) {
            if (v.length != 8) throw new IllegalArgumentException("Длина блока не равна 8 байтам");
            value = v;
        }
        public final int SIZE_OF_BYTE = 8;
        public final int SIZE_OF_BITS = 64;

        public void beginTransfer() {
            long[] longValue = new long[value.length];
            for (int i = 0; i < value.length; i++)
                longValue[i] = value[i];

            BitSet bitsValue = BitSet.valueOf(longValue);
            BitSet bitsResult = BitSet.valueOf(longValue);
            int len = bitsResult.length();
            // i-ый бит из longValue переходит в beginIP[i]-ый бит result
            for (int i = 0; i < Const.IP.length; i++) {
                if (bitsValue.get(toIndex(i)))   // Если true, то также ставим 1, иначе ставим 0
                    bitsResult.set(toIndex(Const.IP[i]-1));
                else
                    bitsResult.clear(toIndex(Const.IP[i]-1));
            }

           longValue = bitsResult.toLongArray();
            for (int i = 0; i < value.length; i++)
                value[i] = (int)longValue[i];
        }

    }

    private int toIndex(int i) {
        int index = 64*(i/8) + i%8;    // Индекс нужен для ориентирования в BitSet, где каждый элемент занимает 64 бита.
        return index;
    }


    public DES(boolean[] k) {
        if (k.length != 56) throw new IllegalArgumentException("Ключ должен быть 56 бит");
        key = k;
        calcuteKey();
    }


    @Override
    public int[] cipher(int[] input) {
        // Задали корректный размер
        if (input.length % SIZE_OF_BLOCK != 0) {
            int n = input.length + (SIZE_OF_BLOCK - input.length%SIZE_OF_BLOCK);    // Новый размер
            int[] newM = new int[n];                                        // Дополненное сообщение
            for (int i = 0; i < newM.length; i++) {
                newM[i] = i < input.length ? input[i] : 0;                          // Дополняем сообщение 0 в конце
            }

            input = Arrays.copyOf(newM, newM.length);
        }

        // Теперь количество блоков обязательно кратно 8, спокойно делим
        int countBlocks = input.length / SIZE_OF_BLOCK;
        Block[] blocks = new Block[countBlocks];
        for (int i = 0; i < countBlocks; i++) {
            blocks[i] = new Block(
                    Arrays.copyOfRange(input,i*SIZE_OF_BLOCK, (i+1)*SIZE_OF_BLOCK)
            );
        }

        blocks[0].beginTransfer();

        roundCipher(blocks[0].value, key);

        return new int[0];
    }

    @Override
    public int cipher(int m) {
        return 0;
    }

    private int[] roundCipher(int[] input, boolean[] key) {
        if (input.length != 8) throw new IllegalArgumentException("Размер блока не 8");
        int[] L = Arrays.copyOfRange(input, 0, input.length/2);
        int[] R = Arrays.copyOfRange(input, input.length/2, input.length-1);
        // Это будет началом массива. Т.е. первые 4 числа
        int[] oldR = Arrays.copyOfRange(input, input.length/2, input.length-1);

        // Расширение E над R
        BitSet setR = new BitSet(32);
        BitSet ER = new BitSet(48);

        for (int i = 0; i < R.length; i++) {
            int b = R[i];
            for (int k = 7; k >= 0; k--) {
                if (getBit(b,k))setR.set(i*8 + 7-k);
            }
        }

        // Расширяющая подстановка E. Она делает из 32 -> 48 бит (путём дублирования некоторых значений)
        for (int i = 0; i < ER.size(); i++) {
            if (setR.get(Const.E[i]))
                ER.set(i);
            else ER.clear(i);
        }



        return new int[0];
    }

    public void calcuteKey() {
        boolean[] bigKey = upKey();

        keys = new boolean[16][48];
        // Генерируем 16 клчюей
        for (int i = 0; i < 16; i++) {
            int[] C = new int[28];
            int[] D = new int[28];
            for (int q = 0; q < 28; q++)
                C[q] = Const.B[q];
            for (int q = 28; q < 56; q++)
                D[q-28] = Const.B[q];

            for (int q = 0; q < Const.Si[i]; q++) {
                C = shiftBack(C);
                D = shiftBack(D);
            }

            int[] res = new int[56];
            for (int q = 0; q < 28; q++)
                res[q] = C[q];
            for (int q = 28; q < 56; q++)
                res[q] = D[q-28];

            // Реузльтат подстановки CiDi
            boolean[] buf = new boolean[56];
            // Применяется перестановка Ci, Di
            for (int q = 0; q < 56; q++) {
                buf[q] = bigKey[res[q]-1];
            }

            // По таблице собираем ki(48)
            for (int q = 0; q < 48; q++) {
                keys[i][q] = buf[Const.Z[q]-1];
            }
        }
    }

    // Расширение ключа, путем добавления битов в 8, 16, ... 64 позиции
    public boolean[] upKey() {
        boolean[] bigKey = new boolean[64];

        for (int k = 0; k < 8; k++) System.arraycopy(key, 7 * k, bigKey, 8 * k, 7);
        for (int k = 0; k < 8; k++) bigKey[8*k+7] = (count1(8*k-k, 8*k + 6-k) % 2 ==0);

        return bigKey;
    }

    private boolean getBit(int n, int k) {
        return 1 == (n & (int)Math.pow(2,k));
    }

    private int count1(int beginIndex, int endIndex) {
        int count = 0;
        for (int i = beginIndex; i <= endIndex; i++) if (key[i]) count++;
        return count;
    }

    private void f(int R, int key) {

    }

    private int[] roundDecipher(int[] input, int[] key) {
        int[] L = Arrays.copyOfRange(input, 0, input.length/2);
        int[] R = Arrays.copyOfRange(input, input.length/2, input.length-1);

        return ADD(L,  XOR(R, f(L, key)));  // L + XOR(R, f(L,key))
    }

    private int[] shiftForward(int[] key) {
        int n = key.length;
        int buf = key[n-1];
        for (int i = n-1; i > 0; i--) {
            key[i] = key[i-1];
        }
        key[0] = buf;
        return key;
    }

    private int[] shiftBack(int[] key) {
        int n = key.length;
        int buf = key[0];
        for (int i = 0; i < n-1; i++) {
            key[i] = key[i+1];
        }
        key[n-1] = buf;
        return key;
    }

    private boolean[] shiftBack(boolean[] key) {
        int n = key.length;
        boolean buf = key[0];
        System.arraycopy(key, 1, key, 0, n - 1);
        key[n-1] = buf;
        return key;
    }

    private int[] ADD(int[] x1, int[] x2) {
        int[] result = new int[x1.length];
        for (int i = 0; i < x1.length; i++)
            result[i] = x1[i] + x2[i];
        return result;
    }

    private int[] XOR(int[] x1, int[] x2) {
        int[] result = new int[x1.length];
        for (int i = 0; i < x1.length; i++)
            result[i] = x1[i] ^ x2[i];
        return result;
    }

    private int[] f(int[] x1, int[] x2) {
        return XOR(x1, x2);
    }
}
