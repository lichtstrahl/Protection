package root.iv.protection.util.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OpenFileDialog extends AlertDialog.Builder {
    protected String currentPath = Environment.getExternalStorageDirectory().getPath();
    protected List<File> files = new ArrayList<File>();
    protected TextView title;
    protected ListView listView;
    protected FilenameFilter filenameFilter;
    protected int selectedIndex = -1;

    public interface OpenDialogListener {
        void OnSelectedFile(String fileName);
    }
    protected OpenDialogListener listener;

    public OpenFileDialog setOpenDialogListener(OpenDialogListener listener) {
        this.listener = listener;
        return this;
    }

    public OpenFileDialog(Context context) {
        super(context);
        title = createTitle(context);
        changeTitle();
        LinearLayout linearLayout = createMainLayout(context);
        linearLayout.addView(createBackItem(context));
        listView = createListView(context);
        linearLayout.addView(listView);

        setCustomTitle(title).setView(linearLayout);
        setPositiveButtonListener();
        setNegativeButtonListener();
    }

    protected void setPositiveButtonListener() {
        setCustomTitle(title)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (selectedIndex > -1 && listener != null) {
                    listener.OnSelectedFile(listView.getItemAtPosition(selectedIndex).toString());
                }
            }
        });
    }
    protected void setNegativeButtonListener() {
        setCustomTitle(title).setNegativeButton(android.R.string.cancel, null);
    }

    @Override
    public AlertDialog show() {
        files.addAll(getFiles(currentPath));
        listView.setAdapter(new FileAdapter(getContext(), files));
        return super.show();
    }

    public OpenFileDialog setFileter(final String filter) {
        filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                File tempFile = new File(String.format("%s/%s", file.getPath(), s));
                if (tempFile.isFile())
                    return tempFile.getName().matches(filter);
                return true;
            }
        };
        return this;
    }

    private int getItemHeight(Context context) {
        TypedValue value = new TypedValue();
        DisplayMetrics metrics = new DisplayMetrics();
        context.getTheme().resolveAttribute(android.R.attr.rowHeight, value, true);
        getDefaultDisplay(context).getMetrics(metrics);
        return (int)TypedValue.complexToDimension(value.data, metrics);
    }

    public int getTextWidth(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text,0, text.length(), bounds);
        return bounds.left + bounds.width() + 80;
    }
    private void changeTitle() {
        String titleText = currentPath;
        int screenWidth = getScreenSize(getContext()).x;
        int maxWidth = (int)(screenWidth*0.99);
        if (getTextWidth(titleText, title.getPaint()) > maxWidth) {
            while (getTextWidth("..." + titleText, title.getPaint()) > maxWidth) {
                int start = titleText.indexOf("/", 2);
                if (start > 0)
                    titleText = titleText.substring(start);
                else titleText = titleText.substring(2);
            }
            title.setText("..." + titleText);
        } else {
            title.setText(titleText);
        }
    }

    private TextView createTextView(Context context, int style) {
        TextView textView = new TextView(context);
        textView.setTextAppearance(context, style);
        int itemHeight = getItemHeight(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        textView.setMinHeight(itemHeight);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(15,0,0,0);
        return textView;
    }

    private TextView createTitle(Context context) {
        return createTextView(context, android.R.style.TextAppearance_DeviceDefault_DialogWindowTitle);
    }


    private ImageView createBackItem(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(getContext().getResources().getDrawable(android.R.drawable.ic_menu_directions));
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(currentPath);
                File parentDirectory = file.getParentFile();
                if (parentDirectory != null) {
                    currentPath = parentDirectory.getPath();
                    rebuildFiles((FileAdapter)listView.getAdapter());
                }
            }
        });
        return imageView;
    }

    private LinearLayout createMainLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setMinimumHeight(getLinearLayoutMinHeight(context));
        return linearLayout;
    }
    // Изменение файлов
    private void rebuildFiles(ArrayAdapter<File> adapter) {
        try {
            if (currentPath.equals("/"))
                throw new NullPointerException();
            List<File> fileList = getFiles(currentPath);
            files.clear();
            selectedIndex = -1;
            files.addAll(fileList);
            adapter.notifyDataSetChanged();
            changeTitle();
        } catch (NullPointerException e) {
            Toast.makeText(getContext(), android.R.string.unknownName, Toast.LENGTH_SHORT).show();
        }
    }
    private static Display getDefaultDisplay(Context context) {
        return ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    private static android.graphics.Point getScreenSize(Context context) {
        android.graphics.Point screenSize = new android.graphics.Point();
        getDefaultDisplay(context).getSize(screenSize);
        return screenSize;
    }

    private static int getLinearLayoutMinHeight(Context context) {
        return getScreenSize(context).y;
    }

    private ListView createListView(Context context) {
        ListView listView = new ListView(context);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final ArrayAdapter<File> adapter = (FileAdapter) adapterView.getAdapter();
                File file = adapter.getItem(i);
                if (file.isDirectory()) {
                    currentPath = file.getPath();
                    rebuildFiles(adapter);
                } else {
                    if (i != selectedIndex)
                        selectedIndex = i;
                    else
                        selectedIndex = -1;
                    adapter.notifyDataSetChanged();
                }
            }
        });
        return listView;
    }

    private List<File> getFiles(String directoryPath) {
        File directory = new File(directoryPath);
        File[] list = directory.listFiles(filenameFilter);
        List<File> fileList = Arrays.asList(list);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File file, File file2)
            {
                if (file.isDirectory() && file2.isDirectory())
                    return -1;
                else if (file.isFile() && file2.isDirectory())
                    return 1;
                else
                    return file.getPath().compareTo(file2.getPath());
            }
        });
        return fileList;
    }

    private class FileAdapter extends ArrayAdapter<File> {
        public FileAdapter(Context context, List<File> files) {
            super(context, android.R.layout.simple_list_item_1, files);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            File file = getItem(position);
            view.setText(file.getName());
            if (selectedIndex == position)
                view.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_blue_light));
            else
                view.setBackgroundColor(getContext().getResources().getColor(android.R.color.background_light));
            return view;
        }
    }
}
