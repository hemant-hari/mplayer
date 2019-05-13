package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.MouseEvent;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
    private double xOffset = 0;
    private double yOffset = 0;
    private ScheduledExecutorService ses;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        mouseDragSetup(root, primaryStage);

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setAnimated(false); // axis animations are removed
        //yAxis.setAnimated(false); // axis animations are removed

        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        barChart.setAnimated(false);

        barChart.getData().add(series);

        Scene scene = new Scene(root, 450, 300, true, SceneAntialiasing.BALANCED);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setTitle("MusicPlayer");
        primaryStage.setScene(scene);
        primaryStage.show();
        //animateVisualiser(series);
    }

    public void animateVisualiser(XYChart.Series series){
        Random rand = new Random();
        ses = Executors.newSingleThreadScheduledExecutor();

        ses.scheduleAtFixedRate(()->{

            Platform.runLater(() -> {
                for (int i=0; i<32; i++){
                    series.getData().add(new XYChart.Data<>(i + "", rand.nextInt(128)));
                }
            }
            );
        }, 0, 30, TimeUnit.MILLISECONDS);
    }

    public void mouseDragSetup(Parent root, Stage primaryStage) {
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
