package si.unilj.fri.lukag.sga2.jsyn.android;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jsyn.ports.UnitInputPort;

/**
 * Horizontal fader that controls a JSyn Port.
 *
 * @author Phil Burk (C) 2012 Mobileer Inc
 */
public class PortFader extends LinearLayout {
    private static final int RESOLUTION = 5000;
    UnitInputPort port;
    SeekBar seekBar;
    TextView label;
    private boolean tracking;

    ChangeHerz changeHerz;

    public PortFader(Context context) {
        super(context);
    }

    public PortFader(Activity activity, UnitInputPort _port, ChangeHerz changeHerz) {
        super(activity);
        this.port = _port;
        setOrientation(HORIZONTAL);
        this.changeHerz = changeHerz;

        label = new TextView(activity);
        updateLabel(_port.getValue());
        label.setWidth(250);
        addView(label);

        this.changeHerz.changeHerzValue(_port.getValue());

        seekBar = new SeekBar(activity);
        addView(seekBar);
        seekBar.setMax(RESOLUTION);
        seekBar.setVisibility(View.VISIBLE);
        LayoutParams buttonLayoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(10, 40, 10, 10);
        seekBar.setLayoutParams(buttonLayoutParams);

        int progress = convertPortToFaderValue(_port.get());
        seekBar.setProgress(progress);

        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar arg0) {
                tracking = false;
                updateLabel(0.0);
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                tracking = true;
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                double value = convertFaderToPortValue(progress);
                port.set(value);
                updateLabel(value);
                changeHerz.changeHerzValue(value);
            }
        });
    }

    public void changePort(UnitInputPort input) {
        this.port = input;
        updateLabel(0.0);
    }

    public void setProgress(double change) {
        seekBar.setProgress((int) (seekBar.getProgress() + change));
    }

    private double convertFaderToPortValue(int progress) {
        double max = port.getMaximum();
        double min = port.getMinimum();
        double range = max - min;
        double value = min + (progress * range / RESOLUTION);
        return value;
    }

    private int convertPortToFaderValue(double value) {
        double max = port.getMaximum();
        double min = port.getMinimum();
        double range = max - min;
        int progress = (int) ((value - min) * RESOLUTION / range);
        return progress;
    }

    private void updateLabel(double value) {
        String text = tracking ? String.format("%8.1f", value)
                : String.format("%8s", port.getName());
        label.setText(text);
    }
}
