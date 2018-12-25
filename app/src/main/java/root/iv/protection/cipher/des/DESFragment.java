package root.iv.protection.cipher.des;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import me.itangqi.waveloadingview.WaveLoadingView;
import root.iv.protection.R;
import root.iv.protection.util.SeekListener;

public class DESFragment extends Fragment {
    private static final String SAVE_KEY = "save:key";

    private TextView viewKey;
    private SeekBar seekKey;
    private SeekListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_des, container, false);

        viewKey = view.findViewById(R.id.keyView);
        seekKey = view.findViewById(R.id.seekKey);
        listener = new SeekListener(seekKey);

        if (savedInstanceState != null) {
            int key = (int)savedInstanceState.getLong(SAVE_KEY);
            seekKey.setProgress(key);
            viewKey.setText(String.valueOf(key));
        }

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        listener.subscribe(x -> viewKey.setText(String.valueOf(x)));
    }

    @Override
    public void onStop() {
        super.onStop();
        listener.unsubscribe();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(SAVE_KEY, Long.valueOf(viewKey.getText().toString()));
    }

    public static DESFragment getInstance() {
        DESFragment fragment = new DESFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public long getKey() {
        return Long.valueOf(viewKey.getText().toString());
    }
}
