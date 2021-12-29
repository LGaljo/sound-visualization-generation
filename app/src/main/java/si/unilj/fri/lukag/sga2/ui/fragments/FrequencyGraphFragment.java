package si.unilj.fri.lukag.sga2.ui.fragments;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import processing.android.PFragment;
import processing.core.PApplet;
import si.unilj.fri.lukag.sga2.databinding.FragmentDashboardBinding;
import si.unilj.fri.lukag.sga2.processing.FrequencyGraph;

public class FrequencyGraphFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private FrequencyGraph sketch;
    boolean set = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (!set) {
                sketch = new FrequencyGraph(root.getHeight(), root.getWidth());
                PFragment fragment = new PFragment(sketch);
                FrameLayout frame = new FrameLayout(getContext());

                frame.setId(binding.sketch.getId());
                fragment.setView(frame, getActivity());
            }
            set = true;
        });

        Toast.makeText(getContext(), "Tap to switch lin/log scale", Toast.LENGTH_SHORT).show();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}