package si.unilj.fri.lukag.sga2.processing;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import ddf.minim.analysis.FFT;
import processing.core.PApplet;

public class Spectrogram extends PApplet {
    private static final String TAG = Spectrogram.class.getSimpleName();
    private final int heightPoints = 256;
    private float[][] values;

    AudioRecord audioRecord = null;
    FFT fft;

    float[] buffer = null;
    int bufferSize = 512;
    boolean logScaleFreq = false;
    int specSize;

    float rectWidth;
    float rectHeight;

    float graphBottom;

    public Spectrogram(int height, int width) {
        this.height = height;
        this.width = width;
        this.graphBottom = this.height - 50;
    }

    public void settings() {
        size(this.width, this.height);
    }

    public void setup() {
        background(0);
        frameRate(120);
        Log.d(TAG, "setup: " + this.height + " " + this.width);

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
            this.specSize = this.fft.specSize();
            this.rectWidth = (float)(width / this.specSize);
            this.rectHeight = (float)(height / heightPoints);
        }
        this.values = new float[heightPoints][this.fft.specSize()];
    }

    public void mousePressed() {
//        background(0);
//        fill(255);
//        textSize(72.0f);
//        text(String.format("%s %s", this.mouseX, this.mouseY), ((float) this.width) / 2.0f, ((float) this.height) / 2.0f);
    }

    public void draw() {
        drawSpectrogram();
        drawBottomLabel();
    }

    private void drawSpectrogram() {
        noStroke();
        noSmooth();

        this.audioRecord.read(this.buffer, 0, this.bufferSize, AudioRecord.READ_NON_BLOCKING);
        this.fft.forward(this.buffer);

        // Copy with offset
        for (int i = heightPoints - 1; i > 0; i--) {
            System.arraycopy(this.values[i - 1], 0, this.values[i], 0, this.specSize);
        }

        float max = 0;
        for (int wi = 0; wi < this.fft.specSize(); wi++) {
            float tmp = this.fft.getBand(wi);
            if (tmp > max) {
                max = tmp;
            }
        }

        for (int wi = 0; wi < this.specSize; wi++) {
            float bandValue = this.fft.getBand(wi) / max * 255;
            this.values[0][wi] = bandValue;
        }

        // Draw values
        for (int hi = 0; hi < heightPoints; hi++) {
            for (int wi2 = 0; wi2 < this.specSize; wi2++) {
                fill(lerpColor(color(0, 19, 48), color(243, 255, 207) , this.values[hi][wi2] / 256));
                rect(
                        map((float) wi2, 0.0f, this.specSize, 0.0f, this.width),
                        map((float) hi, 0.0f, heightPoints, 0.0f, this.graphBottom),
                        rectWidth + 0.25f,
                        rectHeight + 1f
//                        rectWidth,
//                        rectHeight
                );
            }
        }
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

    public float getIndex(float g) {
        float index;
        if (this.logScaleFreq) {
            double tmp = Math.pow(this.specSize, 1 - g/this.specSize);
            index = width - map((float) tmp, 0, 257, 0, 1080);
        } else {
            index = g;
        }
        return index;
    }

    public void stop() {
        super.stop();
        this.audioRecord.stop();
        this.audioRecord.release();
        this.audioRecord = null;
    }
}
