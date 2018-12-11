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
    private int[] KEY;


    // 64-х битный блок
    public class Block {
        public int[] value;
        Block(int[] v) {
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
            for (int i = 0; i < beginIP.length; i++) {
                if (bitsValue.get(toIndex(i)))   // Если true, то также ставим 1, иначе ставим 0
                    bitsResult.set(toIndex(beginIP[i]));
                else
                    bitsResult.clear(toIndex(beginIP[i]));
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


    public DES(int[] k) {
        KEY = k;
    }


    @Override
    public int[] cipher(int[] input) {
        // Задали корректный размер
        if (input.length % SIZE_OF_BLOCK != 0) {
            int n = input.length + (SIZE_OF_BLOCK - input.length%SIZE_OF_BLOCK);    // Новый размер
            int[] newM = new int[n];                                        // Дополненное сообдение
            for (int i = 0; i < newM.length; i++) {
                newM[i] = i < input.length ? input[i] : 0;                          // Дополняем сообщение 0 в конце
            }
            input = Arrays.copyOf(newM, newM.length);
        }

        int countBlocks = input.length / SIZE_OF_BLOCK;
        Block[] blocks = new Block[countBlocks];
        for (int i = 0; i < countBlocks; i++) {
            blocks[i] = new Block(
                    Arrays.copyOfRange(input,i*SIZE_OF_BLOCK, (i+1)*SIZE_OF_BLOCK)
            );
        }


        blocks[0].beginTransfer();

        return new int[0];
    }

    @Override
    public int cipher(int m) {
        return 0;
    }

    private int[] roundCipher(int[] input, int[] key) {
        int[] L = Arrays.copyOfRange(input, 0, input.length/2);
        int[] R = Arrays.copyOfRange(input, input.length/2, input.length-1);

        return ADD(R,  XOR(L, f(R, key)));  // R + XOR(L, f(R,key))
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

    private int[] beginIP = new int[] {
            58,	50,	42,	34,	26,	18,	10,	2,	60,	52,	44,	36,	28,	20,	12,	4,
            62,	54,	46,	38,	30,	22,	14,	6,	64,	56,	48,	40,	32,	24,	16,	8,
            57,	49,	41,	33,	25,	17,	9,  1,	59,	51,	43,	35,	27,	19,	11,	3,
            61,	53,	45,	37,	29,	21,	13,	5,	63,	55,	47,	39,	31,	23,	15,	7
    };
}
