package si.unilj.fri.lukag.sga2.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import si.unilj.fri.lukag.sga2.databinding.FragmentNotificationsBinding;
import si.unilj.fri.lukag.sga2.jsyn.SineSynth;
import si.unilj.fri.lukag.sga2.jsyn.android.ChangeHerz;
import si.unilj.fri.lukag.sga2.jsyn.android.PortFader;

public class GenerationFragment extends Fragment {
    private static final String TAG = GenerationFragment.class.getSimpleName();

    private FragmentNotificationsBinding binding;

    private SineSynth mSineSynth;
    boolean running = false;
    private ChangeHerz herzChangeListener;
    PortFader pf;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("DefaultLocale")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mSineSynth = new SineSynth();

        fillRadioGroup();

        herzChangeListener = (value) -> binding.freq.setText(String.format("%8.1f Hz", value));
        createFader();

        binding.startGen.setOnClickListener((View v) -> {
            if (!running) {
                mSineSynth.start();
                running = true;
            } else {
                mSineSynth.stop();
                running = false;
            }
        });

        binding.freqDown.setOnClickListener((View v) -> pf.setProgress(-2.0));
        binding.freqDown.setOnLongClickListener((View v) -> {
            pf.setProgress(-100.0);
            return true;
        });
        binding.freqUp.setOnClickListener((View v) -> pf.setProgress(2.0));
        binding.freqUp.setOnLongClickListener((View v) -> {
            pf.setProgress(100.0);
            return true;
        });

        return root;
    }

    private void fillRadioGroup() {
        ArrayList<String> shapes = new ArrayList<>();
        shapes.add("sinus");
        shapes.add("square");
        shapes.add("triangle");
        shapes.add("sawtooth");

        RadioButton button;
        for (String shape : shapes) {
            button = new RadioButton(getActivity());
            button.setText(shape);
            binding.signalShape.addView(button);
        }

        binding.signalShape.check(1);

        binding.signalShape.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case 1:
                    mSineSynth.setSineShape();
                    pf.changePort(mSineSynth.getSineFrequencyPort());
                    break;
                case 2:
                    mSineSynth.setSquareShape();
                    pf.changePort(mSineSynth.getSquareFrequencyPort());
                    break;
                case 3:
                    mSineSynth.setTriangleShape();
                    pf.changePort(mSineSynth.getTriangleFrequencyPort());
                    break;
                case 4:
                    mSineSynth.setSawtoothShape();
                    pf.changePort(mSineSynth.getSawFrequencyPort());
                    break;
                default:
                    Log.d(TAG, "Switch default");
                    break;
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void createFader() {
        LinearLayout faderView = binding.layoutFaders;
        pf = new PortFader(getActivity(), mSineSynth.getSineFrequencyPort(), herzChangeListener);
        faderView.addView(pf);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSineSynth.stop();
        running = false;
        binding = null;
    }
}