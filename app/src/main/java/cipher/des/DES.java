package cipher.des;

import java.util.Arrays;

public class DES {
    private static final int SIZE_OF_BLOCK = 8; // Размер блока в байтах, также размер одного символа
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
        init(k);
    }

    public DES(long k) {
        if (k <= 0 || k >= 72057594037927936L) throw new IllegalArgumentException("Целочисленный ключ не корректен");
        String strKey = Long.toBinaryString(k);
        int zeroCount = 56 - strKey.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < zeroCount; i++) {
            builder.append('0');
        }
        builder.append(strKey);

        init(strToBoolArray(builder.toString(), 56));
    }

    private void init(boolean[] k) {
        key = k;
        calcuteKey();
    }

    public int[] cipher(int[] input) {
        // Задали корректный размер.
        input = toStdSize(input);

        // Теперь количество блоков обязательно кратно 8, спокойно делим
        Block[] blocks = splitBlocks(input);

        for (Block b : blocks) {
            // Начальная перестановка
            b.transfer(Const.IP);

            // 16 Раундов шифрования
            for (int i = 0; i < 16; i++) {
                b.value = roundCipher(b.value, i);
            }

            // Конечная перестановка
            b.transfer(Const.IP_1);
        }

        // Преобразуем зашифрованные блоки в последовательность байт
        return buildFromBlocks(blocks);
    }

    public int[] decipher(int[] input) {
        // Размер точно кратен 8
        Block[] blocks = splitBlocks(input);

        for (Block b : blocks) {
            b.transfer(Const.IP);

            // 16 раундов расшифрования
            for (int i = 15; i >=0; i--) {
                b.value = roundDecipher(b.value, i);
            }

            b.transfer(Const.IP_1);
        }

        int[] result =  buildFromBlocks(blocks);
        int n = result.length;
        return Arrays.copyOfRange(result, 0, n - 1 - result[n-1]);
    }

    public boolean[] roundDecipher(boolean[] input, int iter) {
        if (input.length != 64) throw new IllegalArgumentException("roundDecipher: Длина input != 64");
        boolean[] oldL = Arrays.copyOfRange(input, 0, 32);
        boolean[] oldR = Arrays.copyOfRange(input, 32, 64);

        // Li = Ri-1
        boolean[] newL = XOR(oldR, f(oldL, keys[iter]));
        boolean[] newR = Arrays.copyOfRange(input, 0, 32);


        boolean[] result = new boolean[64];

        for (int i = 0; i < 64; i++) {
            result[i] = (i < 32) ? newL[i] : newR[i-32];
        }

        return result;

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

    private Block[] splitBlocks(int[] input) {
        int countBlocks = input.length / SIZE_OF_BLOCK;
        Block[] blocks = new Block[countBlocks];
        for (int i = 0; i < countBlocks; i++) {
            blocks[i] = new Block(toBit64(Arrays.copyOfRange(input, i*SIZE_OF_BLOCK, (i+1)*SIZE_OF_BLOCK)));
        }
        return blocks;
    }

    private int[] buildFromBlocks(Block[] blocks) {
        // Каждый блок - 64 бита - 8 байт. Длина зашифрованного сообщения не должна была измениться
        int[] result = new int[blocks.length*8];
        for (int i = 0; i < blocks.length; i++) {
            int[] bytes = fromBit(blocks[i].value);  // 8 чисел
            for (int j = i*8; j < (i+1)*8; j++) {
                result[j] = bytes[j - i*8];
            }
        }

        return result;
    }

    private int[] toStdSize(int[] input) {
//        if (input.length % SIZE_OF_BLOCK != 0) {
//            int n = input.length + (SIZE_OF_BLOCK - input.length%SIZE_OF_BLOCK);    // Новый размер
//            int[] newM = new int[n];                                                // Дополненное сообщение
//            for (int i = 0; i < newM.length; i++) {
//                newM[i] = i < input.length ? input[i] : 0;                          // Дополняем сообщение 0 в конце
//            }
//            return newM;
//        }
//        return input;
//
        int delta = (input.length+1) % SIZE_OF_BLOCK != 0
                ? (SIZE_OF_BLOCK - (input.length+1)%SIZE_OF_BLOCK)
                : 0;
        int n = input.length + 1 + delta;
        int[] newM = new int[n];

        for (int i = 0; i < newM.length-1; i++) {
            newM[i] = i < input.length ? input[i] : 0;
        }
        newM[n-1] = delta;
        return newM;
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

    private boolean[] strToBoolArray(String str, int size) {
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
        for (int v : value)
            builder.append(intToStr(v));

        return strToBoolArray(builder.toString(), 64);
    }

    private int count1(int beginIndex, int endIndex) {
        int count = 0;
        for (int i = beginIndex; i <= endIndex; i++) if (key[i]) count++;
        return count;
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

    private boolean[] XOR(boolean[] x1, boolean[] x2) {
        if (x1.length != x2.length) throw new IllegalArgumentException("Не совпадают размеры блоков для XOR");
        boolean[] result = new boolean[x1.length];
        for (int i = 0; i < x1.length; i++) {
            result[i] = x1[i] ^ x2[i];
        }
        return result;
    }

}
