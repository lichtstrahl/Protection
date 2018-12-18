package cipher.rsa;

import java.io.Serializable;

public class Key implements Serializable {
    private long partKey;
    private long mod;

    Key(long k, long m) {
        partKey = k;
        mod = m;
    }

    long getKey() {
        return partKey;
    }

    long getMod() {
        return mod;
    }
}
