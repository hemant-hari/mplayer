package sample;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class Player extends Application {
    private Media track;
    private MediaPlayer player;
    private Boolean mediaLoaded = false;

    private int listIndex = 0;
    private State st;
    private String currentFile;
    private List<String> playlist = new LinkedList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        listIndex = 0;

        player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                System.out.println("end of track reached");
                skipTrack();
            }
        });
    }

    Boolean openFile(String dir) {
        try {
            track = new Media(Paths.get(dir).toUri().toString());
            player = new MediaPlayer(track);
            currentFile = dir;
            return true;
        } catch(Error e){
            e.printStackTrace();
            return false;
        }
    }

    public void play()
    {
        if (!mediaLoaded){openFile(playlist.get(0)); mediaLoaded = true;}
        player.play();
        st = State.playing;
    }

    public State pause(){
        player.pause();
        st = State.paused;
        return st;
    }

    public void stop()
    {
        player.dispose();
        openFile(currentFile);
    }

    public void previous()
    {
        if (player.getCurrentTime().greaterThan(new Duration(1000))){
            restart();
        }
        else{
            pickTrack(--listIndex);
        }
    }

    public void restart(){
        stop();
        play();
    }

    public void seek(double seconds){
        player.seek(Duration.seconds(seconds));
    }

    public Duration getStopTime(){
        if (player.getMedia().getDuration() == Duration.UNKNOWN) {return Duration.seconds(200);}
        return player.getMedia().getDuration();
    }

    public List<String> getPlaylist(){
        List<String> copy = new ArrayList<>();
        copy.addAll(playlist);
        return copy;
    }

    public void addTrack(String dir){
        playlist.add(dir);
    }

    public void skipTrack(){
        stop();
        if (playlist.size() == 0){
            return;
        }
        openFile(playlist.get(++listIndex));
        play();
    }

    public String getTitle(){
        String title = (String) player.getMedia().getMetadata().get("title");
        String artist = (String) player.getMedia().getMetadata().get("artist");
        return artist + " - " + title;
    }

    public void pickTrack(int index){
        stop();
        openFile(playlist.get(index));
        listIndex = index;
        play();
    }

    public ReadOnlyObjectProperty<Duration> getTimeProperty(){
        return player.currentTimeProperty();
    }

    public ObservableValue<Duration> getStopTimeProperty(){
        return player.stopTimeProperty();
    }

    public Visualiser getVisualiser(){
        Visualiser vis = new Visualiser();
        player.setAudioSpectrumListener(vis);
        return vis;
    }

    public static void main(String[] args) {
        Player prog = new Player();
        prog.run();
    }

    private void run(){
        openFile("soundfiles/lovedramatic.mp3");
        play();
    }
}