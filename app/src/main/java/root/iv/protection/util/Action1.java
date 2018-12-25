package root.iv.protection.util;

import java.io.Serializable;

@FunctionalInterface
public interface Action1<T> extends Serializable {
    void run(T x);
}