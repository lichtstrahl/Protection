package zip;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cipher.CipherService;
import cipher.OperationStatus;
import root.iv.protection.App;
import root.iv.protection.CipherActivity;
import zip.node.Node;

public class HuffmanService extends CipherService {
    private static final String NAME = "Huffman service";
    private static final String TAG = NAME + ": ";
    private static final String FILE_TREE = "tree";

    public HuffmanService() {
        super("HuffmanService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            String basePath = bundle.getString(INTENT_PATH);
            String decipherPath = bundle.getString(INTENT_DECIPHER_NAME);
            String cipherPath = bundle.getString(INTENT_OUTFILE_NAME);
            File currentDir = new File(basePath).getParentFile();

            Huffman huffman = new Huffman();

            try {
                byte[] byteContent = FileUtils.readFileToByteArray(new File(basePath));
                int[] baseContent = fromByteToInt(byteContent);
                CipherActivity.receiveStatus(this, OperationStatus.READ_BASE_FILE);

                Container container = huffman.zip(baseContent);
                FileUtils.writeByteArrayToFile(new File(cipherPath), fromIntToByte(container.getZip()));
                saveRoot(container.root, currentDir);
                CipherActivity.receiveStatus(this, OperationStatus.ZIP_FILE);

                Node root = readRoot(currentDir);
                int[] zipContent = fromByteToInt(FileUtils.readFileToByteArray(new File(cipherPath)));
                int[] unzipContent = huffman.unzip(zipContent, root);
                FileUtils.writeByteArrayToFile(new File(decipherPath), fromIntToByte(unzipContent));
                double k = (zipContent.length * 1.0) / (unzipContent.length != 0 ? unzipContent.length : zipContent.length * 1.0);

                CipherActivity.receiveStatus(this, OperationStatus.UNZIP_FILE, k);
            } catch (Exception e) {
                App.logE(TAG + e.getMessage());
            }
        }
    }

    private void saveRoot(Node root, File path) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            App.logI(TAG + "Нет доступа к памяти");
            return;
        }

        try {
            File file = new File(path, FILE_TREE);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(root);
            oos.close();
        } catch (Exception e) {
            App.logE(TAG + e.getMessage());
        }

    }

    @Nullable
    private Node readRoot(File path) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            App.logI(TAG + "Нет доступа к памяти");
            return null;
        }

        Node root = null;
        try {
            File file = new File(path, FILE_TREE);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            root = (Node) ois.readObject();
            ois.close();
        } catch (Exception e) {
            App.logE(e.getMessage());
        }
        return root;
    }
}
