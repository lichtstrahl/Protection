package root.iv.protection.util;

public class CipherListener<T> implements Signed<Action1<T>> {
    private Action1<T> action;
    public CipherListener() { }


    public void notify(T p) {
        if (action != null) action.run(p);
    }

    @Override
    public void subscribe(Action1<T> a) {
        action = a;
    }

    @Override
    public void unsubscribe() {
        action = null;
    }
}
