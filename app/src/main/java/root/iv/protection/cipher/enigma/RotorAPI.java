package root.iv.protection.cipher.enigma;

import java.io.PrintStream;

public interface RotorAPI {
    /**
     * Сдвиг ротора на одну позицию.
     */
    void rotate();

    /**
     * Вращение ротора в обратную сторону
     */
    void reverse();

    /**
     * Связывание со следующим ротором
     * @param r - новый ротор
     */
    void addNextRotor(RotorAPI r);

    /**
     * Возвращение ротора в исходное состояние
     */
    void reset();

    /**
     * Шифрование полученного байта
     * @param c - шифруемый символ
     * @return Результат шифрования для данного символа
     */
    int cipher(int c);
    /**
     *
     */
    int decipher(int c);

    void printState(PrintStream stream);
}