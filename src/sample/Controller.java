package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;

import javax.sound.sampled.*;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.*;
import javafx.scene.input.DragEvent;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Controller {
    private Player player;
    public Visualiser vis;

    public Boolean seekerDown = false;

    public Label nowplaying;
    public ToggleButton pbutton;
    public Button skip;
    public Button prev;
    public Button close;
    public Button minimise;
    public Button song1;
    public Canvas canvas;
    public ImageView plypsimage;
    public Slider slider;
    public VBox plist;
    State st = State.paused;

    @FXML
    public void initialize() throws IOException, LineUnavailableException,
            UnsupportedAudioFileException {
        player = new Player();
        //player.openFile("/Users/heman/Downloads/lovedramatic.mp3");
        //vis = player.getVisualiser();
        //setupSeeker();
    }

    public void startVisualiser(){
        if (vis == null){ return; }
        GraphicsContext gc = canvas.getGraphicsContext2D();
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

        ses.scheduleAtFixedRate(()->
        {
            Platform.runLater(() -> {
                int rectWidth = (int) (canvas.getWidth() / (double) vis.data.length) * 5;
                gc.clearRect(0, 50, canvas.getWidth(), canvas.getHeight());
                int xpos = 0;
                for (int i = 0; i < vis.data.length; i++) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(xpos, canvas.getHeight() - (60 + vis.data[i])*1.5, rectWidth, (60 + vis.data[i])*1.5);
                    xpos += rectWidth;
                }
            });
        }, 0, 30, TimeUnit.MILLISECONDS);
    }

    private void setupSeeker() {
        DoubleProperty svalue = slider.valueProperty();
        slider.setMin(0.0);
        slider.setMax(player.getStopTime().toSeconds());
        slider.setMajorTickUnit(0.5);
        vis = player.getVisualiser();
        startVisualiser();

        player.getStopTimeProperty().addListener(
                new ChangeListener<Duration>() {
                    @Override
                    public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                        slider.setMax(newValue.toSeconds());
                    }
                }
        );

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

    private void setSongTitle(){
        nowplaying.setText("Now Playing: " + player.getTitle());
    }

    public void playPressed(){
        if (st != State.playing){
            player.play();
            st = State.playing;
            setupSeeker();
        }
        else{
            player.pause();
            st = State.paused;
        }
        ToggleImage();
        setSongTitle();
    }

    public void stopPressed(){
        if (st == State.playing) { playPressed(); }
        st = State.stopped;
        player.stop();
        slider.setValue(0);
    }

    public void prevPressed(){
        player.previous();
        playPressed();
        playPressed();
        slider.setValue(0);
    }

    public void sliderPressed(){
        seekerDown=true;
    }

    public void sliderReleased(){
        seekerDown=false;
        player.seek(slider.getValue());
    }

    public void ToggleImage(){
        try{
            if (st == State.playing){
                Image pauseimg = new Image(getClass().getResource("icons/pause.png").toURI().toString());
                plypsimage.setImage(pauseimg);
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
        updateSongList();
    }

    private void updateSongList(){
        List<String> slist = player.getPlaylist();
        plist.getChildren().clear();
        for (int i=0; i < slist.size(); i++){
            Button btn = new Button(i + " - " + slist.get(i));
            btn.setOnAction(this::playlistHandler);
            plist.getChildren().add(btn);
        }
    }

    public void playlistHandler(ActionEvent e){
        int i;
        i = ((Button) e.getSource()).getText().charAt(0) - '0';
        player.pickTrack(i);
        slider.setValue(0.00);
    }

    public void skipPressed(){
        player.skipTrack();
        playPressed();
        slider.setValue(0.00);
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
