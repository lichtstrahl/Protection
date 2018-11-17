package dialog;

import android.content.Context;
import android.content.DialogInterface;

import dialog.OpenFileDialog;


public class SetDirDialog extends OpenFileDialog {
    public SetDirDialog(Context context) {
        super(context);
    }

    @Override
    protected void setPositiveButtonListener() {
        super.setPositiveButtonListener();
        setCustomTitle(title)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.OnSelectedFile(currentPath);
            }
        });
    }
}
