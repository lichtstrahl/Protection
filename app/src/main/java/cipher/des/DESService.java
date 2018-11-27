package cipher.des;

import android.content.Intent;

import cipher.CipherService;
import cipher.CipherStatus;
import root.iv.protection.CipherActivity;

public class DESService extends CipherService {

    public DESService() {
        super("DESService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent cipherIntent = new Intent().setAction(CipherActivity.CipherReceiver.ACTION);

        

        sendStatus(cipherIntent, CipherStatus.DECIPHER_FILE);
    }

}
