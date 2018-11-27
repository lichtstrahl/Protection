package cipher.des;

import android.content.Intent;

import cipher.CipherService;
import root.iv.protection.App;

public class DESService extends CipherService {

    public DESService() {
        super("DESService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        App.logI("DSA Service starded ...");
    }

}
