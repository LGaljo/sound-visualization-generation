package si.unilj.fri.lukag.sga2.processing;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.core.app.ActivityCompat;

import ddf.minim.analysis.FFT;
import processing.core.PApplet;

public class FrequencyGraph extends PApplet {
    private static final String TAG = FrequencyGraph.class.getSimpleName();

    Integrator[] interpolators;
    AudioRecord audioRecord = null;
    FFT fft;

    float[] buffer = null;
    int bufferSize = 512;
    boolean logScaleFreq = true;
    int specSize;

    float graphBottom;

    public FrequencyGraph(int height, int width) {
        this.height = height;
        this.width = width;

        this.graphBottom = this.height - 50;
    }

    public void settings() {
        size(this.width, this.height);
    }

    public void setup() {
        orientation(1);
        frameRate(120);

        int buflen = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT);
        if (ActivityCompat.checkSelfPermission(getActivity(), "android.permission.RECORD_AUDIO") == 0) {
            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.UNPROCESSED,
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_FLOAT,
                    buflen
            );
            audioRecord.startRecording();
            this.buffer = new float[this.bufferSize];
            this.fft = new FFT(this.bufferSize, (float) this.audioRecord.getSampleRate());
            this.specSize = fft.specSize();
        }

        interpolators = new Integrator[this.specSize];
        for (int i = 0; i < specSize; i++) {
            interpolators[i] = new Integrator(this.height, 0.2f, 0.4f);
        }
    }

    @Override
    public void mousePressed() {
        super.mousePressed();
        this.logScaleFreq = !this.logScaleFreq;
    }

    public void setWindowSize(int height, int width) {
        size(width, height);
    }

    public void draw() {
        background(0);
        fill(128);
        stroke(255);
        strokeWeight(2);

        for (int i = 0; i < this.specSize; i++) {
            interpolators[i].update();
        }

        this.audioRecord.read(this.buffer, 0, this.bufferSize, AudioRecord.READ_NON_BLOCKING);

        this.fft.forward(this.buffer);

        for (int i = 0; i < this.specSize; i++) {
            float bandValue = this.fft.getBand(i);
            interpolators[i].target(graphBottom - bandValue * graphBottom);
        }

        beginShape();
        vertex(0, this.graphBottom);
        for (int i2 = 0; i2 < this.specSize; i2++) {
            vertex(getIndex(i2), interpolators[i2].value);
        }
        vertex(this.width, this.graphBottom);
        endShape();

        drawBottomLabel();
    }

    public float getIndex(float g) {
        float index;
        if (this.logScaleFreq) {
            double tmp = Math.pow(this.specSize, 1 - g/this.specSize);
            index = width - map((float) tmp, 0, 257, 0, 1080);
        } else {
            index = map(g, 0, 257, 0, 1080);
        }
        return index;
    }

    @SuppressLint("DefaultLocale")
    private void drawBottomLabel() {
        textSize(14f);
        for (int i = 0; i < 22000; i += 1000) {
            float x = map(i, 0.0f, 22000, 0.0f, this.width);
            stroke(255);
            strokeWeight(2f);
            line(x, graphBottom + 10, x, graphBottom - 10);
            fill(255);
            text(String.format("%d kHz", i/1000), x, graphBottom + 30);
        }
    }

    public void stop() {
        super.stop();
        this.audioRecord.stop();
        this.audioRecord.release();
        this.audioRecord = null;
    }
}
