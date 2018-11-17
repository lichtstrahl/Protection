package cipher.enigma;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import root.iv.protection.R;
import root.iv.protection.SeekListener;

public class EnigmaFragment extends Fragment {
    private static final String ARGS_RL = "args:rotorL";
    private static final String ARGS_RM = "args:rotorM";
    private static final String ARGS_RR = "args:rotorR";
    private TextView viewRotorL;
    private TextView viewRotorM;
    private TextView viewRotorR;
    private SeekListener listenerL;
    private SeekListener listenerM;
    private SeekListener listenerR;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enigma, container, false);

        viewRotorL = view.findViewById(R.id.viewRotorL);
        viewRotorL.setText(String.valueOf(
                savedInstanceState != null ? savedInstanceState.getInt(ARGS_RL) : 0
        ));
        viewRotorM = view.findViewById(R.id.viewRotorM);
        viewRotorM.setText(String.valueOf(
                savedInstanceState != null ? savedInstanceState.getInt(ARGS_RM) : 0
        ));
        viewRotorR = view.findViewById(R.id.viewRotorR);
        viewRotorR.setText(String.valueOf(
                savedInstanceState != null ? savedInstanceState.getInt(ARGS_RR) : 0
        ));
        listenerL = new SeekListener(view.findViewById(R.id.seekRotorL));
        listenerM = new SeekListener(view.findViewById(R.id.seekRotorM));
        listenerR = new SeekListener(view.findViewById(R.id.seekRotorR));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        if (bundle != null) {
            listenerL.subscribe(x -> {
                viewRotorL.setText(String.valueOf(x));
                bundle.putInt(ARGS_RL, x);
            });
            listenerM.subscribe(x -> {
                viewRotorM.setText(String.valueOf(x));
                bundle.putInt(ARGS_RM, x);
            });
            listenerR.subscribe(x -> {
                viewRotorR.setText(String.valueOf(x));
                bundle.putInt(ARGS_RR, x);
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        listenerL.unsubscribe();
        listenerM.unsubscribe();
        listenerR.unsubscribe();
    }

    public static EnigmaFragment getInstance() {
        EnigmaFragment fragment = new EnigmaFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_RL, 0);
        bundle.putInt(ARGS_RM, 0);
        bundle.putInt(ARGS_RR, 0);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARGS_RL, Integer.parseInt(viewRotorL.getText().toString()));
        outState.putInt(ARGS_RM, Integer.parseInt(viewRotorM.getText().toString()));
        outState.putInt(ARGS_RR, Integer.parseInt(viewRotorR.getText().toString()));
    }
}
