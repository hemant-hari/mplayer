package sample;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.*;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.*;
import javafx.scene.input.DragEvent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Controller {
    private Player player;
    private Visualiser vis;

    public Boolean seekerDown = false;

    public ToggleButton pbutton;
    public Button skip;
    public Button prev;
    public Button close;
    public Button minimise;
    public Button song1;
    public ImageView plypsimage;
    public Slider slider;
    State st = State.paused;

    @FXML
    public void initialize() throws IOException, LineUnavailableException,
            UnsupportedAudioFileException {
        player = new Player();
        //player.openFile("/Users/heman/Downloads/lovedramatic.mp3");
        //vis = player.getVisualiser();
        //setupSeeker();
    }

    private void setupSeeker() {
        DoubleProperty svalue = slider.valueProperty();
        slider.setMin(0.0);
        slider.setMax(player.getStopTime().toSeconds());
        slider.setMajorTickUnit(0.5);
        slider.setSnapToTicks(true);

        player.getStopTimeProperty().addListener(
                new ChangeListener<Duration>() {
                    @Override
                    public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                        slider.setMax(newValue.toSeconds());
                    }
                }
        );

        svalue.addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue <? extends Number >
                                                observable, Number oldValue, Number newValue)
                    {
                        if (Math.abs((double)oldValue - (double)newValue) > 1){
                            System.out.println("newval = " + (double) newValue); player.seek((double) newValue);}
                    }
                });

        player.getTimeProperty().addListener(
                new ChangeListener<Duration>() {
                    @Override
                    public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue)
                    {
                        if (!seekerDown) {slider.setValue(newValue.toSeconds());}
                    }
                }
        );
    }

    /*private void printVisualiser(){
        float[] visData = vis.getVisualiserValues();
        for (int i=0; i<visData.length; i++){
            System.out.print(visData[i]);
        }
        System.out.println("");
        System.out.println(player.getStopTime().toSeconds());
    }*/

    public void playPressed(){
        System.out.println("playbutton clicked");
        if (st != State.playing){
            player.play();
            System.out.println("playing");
            st = State.playing;
            setupSeeker();
        }
        else{
            player.pause();
            System.out.println("paused");
            st = State.paused;
        }
        ToggleImage();
    }

    public void stopPressed(){
        if (st == State.playing) { playPressed(); }
        st = State.stopped;
        player.stop();
        slider.setValue(0);
    }

    public void prevPressed(){
        player.previous();
    }

    public void sliderPressed(){
        seekerDown=true;
    }

    public void sliderReleased(){
        seekerDown=false;
    }

    public void ToggleImage(){
        try{
            if (st == State.playing){
                Image pauseimg = new Image(getClass().getResource("icons/pause.png").toURI().toString());
                plypsimage.setImage(pauseimg);
                System.out.println("changing img");
            }
            else{
                Image playimg = new Image(getClass().getResource("icons/play-button.png").toURI().toString());
                plypsimage.setImage(playimg);
            }}
        catch(URISyntaxException e){
                throw new Error(e);
            }
    }

    public void dragHandler(DragEvent event) {
        if (event.getDragboard().hasFiles()){
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void dropHandler(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        player.addTrack(files.get(0).getAbsolutePath());
        song1.setText(files.get(0).getAbsolutePath());
    }

    public void skipPressed(){
        player.skipTrack();
    }

    public void closeWindow(){
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();
    }

    public void minimiseWindow(){
        Stage stage = (Stage) minimise.getScene().getWindow();
        stage.setIconified(true);
    }
}
