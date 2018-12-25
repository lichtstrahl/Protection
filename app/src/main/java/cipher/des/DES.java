package cipher.des;

import java.util.Arrays;
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




    public static boolean[] transfer(boolean[] src, int[] transferMatrix) {
        boolean[] oldBits = Arrays.copyOf(src, src.length);
        boolean[] newBits = new boolean[transferMatrix.length];

        for (int i = 0; i < transferMatrix.length; i++) {
            newBits[i] = oldBits[transferMatrix[i]-1];
        }

        return newBits;
    }

    public DES(boolean[] k) {
        if (k.length != 56) throw new IllegalArgumentException("Ключ должен быть 56 бит");
        key = k;
        calcuteKey();
    }

    @Override
    public int[] cipher(int[] input) {
        // Задали корректный размер.
        input = toStdSize(input);

        // Теперь количество блоков обязательно кратно 8, спокойно делим
        int countBlocks = input.length / SIZE_OF_BLOCK;
        Block[] blocks = new Block[countBlocks];
        for (int i = 0; i < countBlocks; i++) {
            blocks[i] = new Block(toBit64(Arrays.copyOfRange(input,i*SIZE_OF_BLOCK, (i+1)*SIZE_OF_BLOCK)));
        }

        for (Block b : blocks) {
            // Начальная перестановка
            b.transfer(Const.IP);

            // 16 Раундов шифрования
            for (int i = 0; i < 16; i++) {
                b.value = roundCipher(blocks[0].value, i);
            }

            // Конечная перестановка
            b.transfer(Const.IP_1);
        }

        // Каждый блок - 64 бита - 8 байт. Длина зашифрованного сообщения не должна была измениться
        int[] result = new int[input.length];
        for (int i = 0; i < blocks.length; i++) {
            int[] bytes = fromBit(blocks[i].value);  // 8 чисел
            for (int j = i*8; j < (i+1)*8; j++) {
                result[j] = bytes[j - i*8];
            }
        }

        return result;
    }

    private int[] toStdSize(int[] input) {
        if (input.length % SIZE_OF_BLOCK != 0) {
            int n = input.length + (SIZE_OF_BLOCK - input.length%SIZE_OF_BLOCK);    // Новый размер
            int[] newM = new int[n];                                                // Дополненное сообщение
            for (int i = 0; i < newM.length; i++) {
                newM[i] = i < input.length ? input[i] : 0;                          // Дополняем сообщение 0 в конце
            }
            return newM;
        }
        return input;
    }

    @Override
    public int cipher(int m) {
        return 0;
    }

    private boolean[] roundCipher(boolean[] input, int iter) {
        if (input.length != 64) throw new IllegalArgumentException("roundCipher: Длина input != 64");
        boolean[] oldL = Arrays.copyOfRange(input, 0, 32);
        boolean[] oldR = Arrays.copyOfRange(input, 32, 64);

        // Li = Ri-1
        boolean[] newL = Arrays.copyOfRange(input, 32, 64);
        boolean[] newR = XOR(oldL, f(oldR, keys[iter]));

        boolean[] result = new boolean[64];

        for (int i = 0; i < 64; i++) {
            result[i] = (i < 32) ? newL[i] : newR[i-32];
        }

        return result;
    }

    private boolean[] f(boolean[] R, boolean[] key) {
        // R - 32, key - 48
        boolean[] ER = transfer(R, Const.E);

        // Размер: 48. Т.е. 8 блоков B по 6 бит
        boolean[] postXOR = XOR(ER, key);
        boolean[][] B = new boolean[8][6];

        for (int i = 0; i < 48; i++) {
            B[i / 6][i % 6] = postXOR[i];
        }

        // Преобразования с S1 ... S8
        boolean[] B_ = new boolean[32];
        for (int j = 0; j < 8; j++) {
            boolean[] Bj = B[j];        // Конкретное Bj
            int[] S = Const.SBLOCK[j];  // Конкретная перестановка S для Bj
            String strA = boolToStr(Bj[0]) + boolToStr(Bj[5]);
            String strB = boolToStr(Bj[1]) + boolToStr(Bj[2]) + boolToStr(Bj[3]) + boolToStr(Bj[4]);
            int a = Integer.valueOf(strA, 2);  // 0 .. 3
            int b = Integer.valueOf(strB, 2);  // 0 .. 15
            int Bj_ = S[a*16 + b];
            String strBj_ = Integer.toBinaryString(Bj_);    // Всегда число 0 .. 15
            boolean[] boolBj = strToBoolArray(strBj_, 4);      // 4 бита
            for (int i = j*4; i < (j+1)*4; i++) {
                B_[i] = boolBj[i - j*4];
            }
        }

        // Последняя перестановка P
        return transfer(B_, Const.P);
    }

    boolean[] strToBoolArray(String str, int size) {
        int n = str.length();
        boolean[] result = new boolean[size];
        int offset = size - n;

        for (int i = 0; i < n; i++) {
            result[i] = (i >= offset) && charToBool(str.charAt(i));
        }

        return result;
    }

    private String boolToStr(boolean b) {
        return b ? "1" : "0";
    }

    private boolean charToBool(char c) {
        return c == '1';
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


    private int[] fromBit(boolean[] value) {
        int n = value.length/8;
        int[] result = new int[n];

        for (int i = 0; i < n; i++) {
            StringBuilder builder = new StringBuilder();
            for (int j = i*8; j < (i+1)*8; j++) {
                builder.append(boolToStr(value[j]));
            }
            result[i] = Integer.valueOf(builder.toString(), 2);
        }

        return result;
    }

    private String intToStr(int value) {
        String str = Integer.toBinaryString(value);
        int zeroCount = 8 - str.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < zeroCount; i++)
            builder.append('0');
        builder.append(str);
        return builder.toString();
    }

    private boolean[] toBit64(int[] value) {
        if (value.length != 8) throw new IllegalArgumentException("toBit64: Длина массива != 8");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length; i++)
            builder.append(intToStr(value[i]));

        return strToBoolArray(builder.toString(), 64);
    }

    private boolean getBit(int n, int k) {
        return 1 == (n & (int)Math.pow(2,k));
    }

    private int count1(int beginIndex, int endIndex) {
        int count = 0;
        for (int i = beginIndex; i <= endIndex; i++) if (key[i]) count++;
        return count;
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

    private boolean[] XOR(boolean[] x1, boolean[] x2) {
        if (x1.length != x2.length) throw new IllegalArgumentException("Не совпадают размеры блоков для XOR");
        boolean[] result = new boolean[x1.length];
        for (int i = 0; i < x1.length; i++) {
            result[i] = x1[i] ^ x2[i];
        }
        return result;
    }

    private int[] f(int[] x1, int[] x2) {
        return XOR(x1, x2);
    }
}
