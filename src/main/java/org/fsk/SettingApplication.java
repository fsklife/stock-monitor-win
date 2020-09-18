package org.fsk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author fanshk
 * @date 2020/9/13 15:38
 */
public class SettingApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/setting.fxml"));
        primaryStage.setTitle("监控设置");
        primaryStage.setScene(new Scene(root, 516, 366));
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.setResizable(false);
        StageManager.putSettingStage(primaryStage);
        primaryStage.show();
    }
}
