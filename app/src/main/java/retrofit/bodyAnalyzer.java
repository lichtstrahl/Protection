package retrofit;

import java.util.List;

public interface bodyAnalyzer {
    void done(List<Phone> phones);
    void fail(int resMsg);
}
