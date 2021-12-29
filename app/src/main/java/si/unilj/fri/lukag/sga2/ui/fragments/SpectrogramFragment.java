package si.unilj.fri.lukag.sga2.ui.fragments;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import processing.android.PFragment;
import processing.core.PApplet;
import si.unilj.fri.lukag.sga2.databinding.FragmentHomeBinding;
import si.unilj.fri.lukag.sga2.processing.FrequencyGraph;
import si.unilj.fri.lukag.sga2.processing.Spectrogram;

public class SpectrogramFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PApplet sketch;
    boolean set = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

//        this.sketch = new Spectrogram(displayMetrics.heightPixels - 380, displayMetrics.widthPixels);
//        PFragment fragment = new PFragment(this.sketch);
//        FrameLayout frame = new FrameLayout(getContext());
//
//        frame.setId(binding.sketch.getId());
//        fragment.setView(frame, getActivity());

        root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (!set) {
                sketch = new Spectrogram(root.getHeight(), root.getWidth());
                PFragment fragment = new PFragment(sketch);
                FrameLayout frame = new FrameLayout(getContext());

                frame.setId(binding.sketch.getId());
                fragment.setView(frame, getActivity());
            }
            set = true;
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}