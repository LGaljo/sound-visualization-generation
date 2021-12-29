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

import processing.android.PFragment;
import processing.core.PApplet;
import si.unilj.fri.lukag.sga2.databinding.FragmentDashboardBinding;
import si.unilj.fri.lukag.sga2.processing.FrequencyGraph;

public class FrequencyGraphFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private FrequencyGraph sketch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        sketch = new FrequencyGraph(displayMetrics.heightPixels - 380, displayMetrics.widthPixels);
        PFragment fragment = new PFragment(sketch);
        FrameLayout frame = new FrameLayout(getContext());

        frame.setId(binding.sketch.getId());
        fragment.setView(frame, getActivity());

        root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Log.d("TAG", "height: " + root.getHeight());
            Log.d("TAG", "width: " + root.getWidth());

//            sketch.setWindowSize(root.getHeight(), root.getWidth());
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}