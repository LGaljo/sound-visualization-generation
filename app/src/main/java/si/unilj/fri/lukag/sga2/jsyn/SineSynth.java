package si.unilj.fri.lukag.sga2.jsyn;

import android.util.Log;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.SquareOscillator;
import com.jsyn.unitgen.TriangleOscillator;
import com.jsyn.unitgen.UnitOscillator;

import si.unilj.fri.lukag.sga2.jsyn.devices.android.JSynAndroidAudioDevice;

/**
 * Play independent sine waves on the left and right channel.
 */
public class SineSynth {
    private static final String TAG = SineSynth.class.getSimpleName();

    private float frequency = 440;
    private float minFreq = 1;
    private float maxFreq = 22000;

    private final Synthesizer mSynth;

    private SineOscillator mSinOsc;
    private SawtoothOscillator mSawOsc;
    private SquareOscillator mSqrOsc;
    private TriangleOscillator mTriaOsc;

    private final LineOut mLineOut; // stereo output

    private UnitOscillator currentOscillator;

    public SineSynth() {
        // Create a JSyn synthesizer that uses the Android output.
        mSynth = JSyn.createSynthesizer(new JSynAndroidAudioDevice() );
        mSynth.add(mLineOut = new LineOut());

        setOscillators();

        setSineShape();

        getSineFrequencyPort().setName("Sinewave");
        getSineFrequencyPort().setup(minFreq, frequency, maxFreq);

        getSawFrequencyPort().setName("Sawtooth");
        getSawFrequencyPort().setup(minFreq, 0, maxFreq);

        getSquareFrequencyPort().setName("Square");
        getSquareFrequencyPort().setup(minFreq, 0, maxFreq);

        getTriangleFrequencyPort().setName("Triangle");
        getTriangleFrequencyPort().setup(minFreq, 0, maxFreq);
    }

    public void start() {
        mSynth.start();
        mLineOut.start();
    }

    public void stop() {
        mLineOut.stop();
        mSynth.stop();
    }

    private void setOscillators() {
        mSynth.add(mSinOsc = new SineOscillator());
        mSinOsc.output.connect(0, mLineOut.input, 0);
        mSinOsc.output.connect(0, mLineOut.input, 1);


        mSynth.add(mSawOsc = new SawtoothOscillator());
        mSawOsc.amplitude.set(0);
        mSawOsc.frequency.set(0);
        mSawOsc.output.connect(0, mLineOut.input, 0);
        mSawOsc.output.connect(0, mLineOut.input, 1);


        mSynth.add(mSqrOsc = new SquareOscillator());
        mSqrOsc.amplitude.set(0);
        mSqrOsc.frequency.set(0);
        mSqrOsc.output.connect(0, mLineOut.input, 0);
        mSqrOsc.output.connect(0, mLineOut.input, 1);


        mSynth.add(mTriaOsc = new TriangleOscillator());
        mTriaOsc.amplitude.set(0);
        mTriaOsc.frequency.set(0);
        mTriaOsc.output.connect(0, mLineOut.input, 0);
        mTriaOsc.output.connect(0, mLineOut.input, 1);
    }

    public UnitInputPort getSineFrequencyPort() {
        return mSinOsc.frequency;
    }

    public UnitInputPort getSawFrequencyPort() {
        return mSawOsc.frequency;
    }

    public UnitInputPort getSquareFrequencyPort() {
        return mSqrOsc.frequency;
    }

    public UnitInputPort getTriangleFrequencyPort() {
        return mTriaOsc.frequency;
    }

    public void setSineShape() {
        Log.d(TAG, "setSineShape: ");
        currentOscillator = mSinOsc;
        disableOthers();
        enableCurrent();
    }

    public void setSquareShape() {
        Log.d(TAG, "setSquareShape: ");
        currentOscillator = mSqrOsc;
        disableOthers();
        enableCurrent();
    }

    public void setTriangleShape() {
        Log.d(TAG, "setTriangleShape: ");
        currentOscillator = mTriaOsc;
        disableOthers();
        enableCurrent();
    }

    public void setSawtoothShape() {
        Log.d(TAG, "setSawtoothShape: ");
        currentOscillator = mSawOsc;
        disableOthers();
        enableCurrent();
    }

    private void enableCurrent() {
        currentOscillator.amplitude.set(1);
        currentOscillator.frequency.set(frequency);
    }

    private void disableOthers() {
        if (!(currentOscillator instanceof SineOscillator)) {
            mSinOsc.amplitude.set(0);
            mSinOsc.frequency.set(0);
        }
        if (!(currentOscillator instanceof SawtoothOscillator)) {
            mSawOsc.amplitude.set(0);
            mSawOsc.frequency.set(0);
        }
        if (!(currentOscillator instanceof SquareOscillator)) {
            mSqrOsc.amplitude.set(0);
            mSqrOsc.frequency.set(0);
        }
        if (!(currentOscillator instanceof TriangleOscillator)) {
            mTriaOsc.amplitude.set(0);
            mTriaOsc.frequency.set(0);
        }
    }
}
