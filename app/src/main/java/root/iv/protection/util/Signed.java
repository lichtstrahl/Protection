package root.iv.protection.util;

public interface Signed<T> {
    void subscribe(T a);
    void unsubscribe();
}
