package org.fsk;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;

/**
 * @author 27260
 */
public class Main extends Application {

    public static volatile Stage primaryStage = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        StageManager.putMainStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getResource("fxml/main.fxml"));
        primaryStage.setTitle("Stock Monitor");
        //设置窗口的图标.
        URL url = getClass().getResource("static/title_ico.png");
        primaryStage.getIcons().add(new Image(url.openStream()));
        primaryStage.setScene(new Scene(root, 990, 744));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
