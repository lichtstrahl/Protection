package root.iv.protection.cipher.es;

import java.io.File;
import java.io.Serializable;

import root.iv.protection.cipher.rsa.Key;

public class MSG implements Serializable {
    private File file;
    private int[] cipherHash;
    private Key key;

    public MSG(File file, int[] cipherHash, Key key) {
        this.file = file;
        this.cipherHash = cipherHash;
        this.key = key;
    }

    public File getFile() {
        return file;
    }

    public int[] getCipherHash() {
        return cipherHash;
    }

    public Key getKey() {
        return key;
    }
}