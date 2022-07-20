package root.iv.protection.zip;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import root.iv.protection.R;

public class HuffmanFragment extends Fragment {
    private TextView viewTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_huffman, container, false);
        viewTitle = view.findViewById(R.id.viewTitle);
        return view;
    }

    public static HuffmanFragment getInstance() {
        HuffmanFragment fragment = new HuffmanFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setTitle(String title) {
        viewTitle.setText(title);
    }
}
