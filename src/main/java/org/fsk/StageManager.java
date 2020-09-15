package org.fsk;

import javafx.stage.Stage;
import org.fsk.controller.MonitorController;
import org.fsk.controller.SettingController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanshk
 * @date 2020/9/15 12:08
 */
public class StageManager {
    private static Map<String, Stage> STAGE = new HashMap<String, Stage>();
    private static Map<String, Object> CONTROLLER = new HashMap<String, Object>();
    public static final String MAIN_STAGE = "main_stage";
    public static final String MAIN_CTRL = "main_ctrl";
    public static final String SETTING_STAGE = "setting_stage";
    public static final String SETTING_CTRL = "setting_ctrl";

    public static void putMainStage(Stage stage) {
        STAGE.put(MAIN_STAGE, stage);
    }

    public static Stage getMainStage() {
        return STAGE.get(MAIN_STAGE);
    }

    public static void putMainCtrl(MonitorController controller) {
        CONTROLLER.put(MAIN_CTRL, controller);
    }

    public static MonitorController getMainCtrl() {
        return (MonitorController)CONTROLLER.get(MAIN_CTRL);
    }

    public static void putSettingStage(Stage stage) {
        STAGE.put(SETTING_STAGE, stage);
    }

    public static Stage getSettingStage() {
        return STAGE.get(SETTING_STAGE);
    }

    public static void putSettingCtrl(SettingController controller) {
        CONTROLLER.put(SETTING_CTRL, controller);
    }

    public static SettingController getSettingCtrl() {
        return (SettingController)CONTROLLER.get(SETTING_CTRL);
    }

}
