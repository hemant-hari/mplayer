package sample;

import  javafx.scene.media.AudioSpectrumListener;

public class Visualiser implements AudioSpectrumListener {

    float[] data;

    public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases){
        setVisualiserValues(magnitudes);
    }

    public void setVisualiserValues(float[] magnitudes){
        data = new float[magnitudes.length];
        for (int i=0; i<data.length; i++){
            data[i] = magnitudes[i];
        }
    }

    public float[] getVisualiserValues() {
        return data;
    }
}
