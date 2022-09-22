package com.starmcc.beanfun.constant;

import com.starmcc.beanfun.model.ConfigModel;
import com.starmcc.beanfun.model.client.Account;
import com.starmcc.beanfun.model.client.BeanfunModel;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.math.BigDecimal;
import java.util.Optional;


/**
 * 全局常量
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public class QsConstant {
    public static final String APP_VERSION = "4.0.0";
    public static final Integer APP_VERSION_INT = 301;
    public static final String PATH_APP = System.getProperties().getProperty("user.dir");
    public static final String PATH_PLUGINS = PATH_APP + "\\qs-data\\";
    public static final String PATH_APP_CONFIG = PATH_PLUGINS + "config.json";
    public static final String APP_NAME = "QsBeanfun";
    public static final String UPDATE_API_GITHUB = "https://api.github.com/repos/starmcc/qs-beanfun/releases/latest";
    public static final String UPDATE_API_SERVER = "https://update.qstms.com/qs-beanfun/version.json";
    public static final String GITHUB_URL = "https://github.com/starmcc/qs-beanfun";
    public static final JFXStageData JFX_STAGE_DATA = new JFXStageData();
    public static TrayIcon trayIcon;
    public static ConfigModel config;
    public static BigDecimal currentRateChinaToTw = new BigDecimal("4.5");
    public static BeanfunModel beanfunModel;
    public static Account nowAccount;

    /**
     * 资源文件枚举
     *
     * @author starmcc
     * @date 2022/03/22
     */
    public static enum Resources {
        LR_PROC_EXE(PATH_PLUGINS + "lr/LRProc.exe", "lib/lr/LRProc.exe"), LR_HOOKX64_DLL(PATH_PLUGINS + "lr/LRHookx64.dll", "lib/lr/LRHookx64.dll"), LR_HOOKX32_DLL(PATH_PLUGINS + "lr/LRHookx32.dll", "lib/lr/LRHookx32.dll"), LR_CONFIG_XML(PATH_PLUGINS + "lr/LRConfig.xml", "lib/lr/LRConfig.xml"), LR_SUB_MENUS_DLL(PATH_PLUGINS + "lr/LRSubMenus.dll", "lib/lr/LRSubMenus.dll"),

        ;

        private final String targetPath;
        private final String sourcePath;

        Resources(String targetPath, String sourcePath) {
            this.targetPath = targetPath;
            this.sourcePath = sourcePath;
        }

        public String getTargetPath() {
            return targetPath;
        }

        public String getSourcePath() {
            return sourcePath;
        }
    }


    /**
     * 信息框
     *
     * @param msg       消息
     * @param alertType 类型
     */
    public static void alert(String msg, Alert.AlertType alertType) {
        final Alert alert = new Alert(alertType);
        alert.setTitle("");
        alert.setHeaderText("");
        alert.setContentText(msg);
        alert.showAndWait();

    }


    /**
     * 文本输入框
     *
     * @param alertType 类型
     */
    public static String textDialog(String tips, String defaultText) {
        TextInputDialog dialog = new TextInputDialog(defaultText);
        dialog.setTitle("");
        dialog.setHeaderText(tips);
        Optional<String> s = dialog.showAndWait();
        return s.isPresent() ? s.get() : "";
    }


    /**
     * 确认对话框
     *
     * @param tips 提示
     * @return boolean
     */
    public static boolean confirmDialog(String title, String tips) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(tips);
        return alert.showAndWait().get() == ButtonType.OK;
    }

}
