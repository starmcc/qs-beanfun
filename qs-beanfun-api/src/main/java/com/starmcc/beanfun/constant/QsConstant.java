package com.starmcc.beanfun.constant;

import com.starmcc.beanfun.model.ConfigJson;
import com.starmcc.beanfun.model.client.Account;
import com.starmcc.beanfun.model.client.BeanfunModel;
import com.starmcc.beanfun.utils.FrameUtils;
import com.starmcc.beanfun.windows.JFXStage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * 常量
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public class QsConstant {
    public static final String APP_VERSION = "4.0.0";
    public static final String APP_PATH = System.getProperties().getProperty("user.dir") + "\\QsBeanfun\\";
    public static final String APP_CONFIG = APP_PATH + "config.json";
    public static final String APP_NAME = "QsBeanfun";
    public static JFXStage equippingJFXStage;
    public static JFXStage webJFXStage;
    public static ConfigJson config;
    public static HttpHost proxy = null;
    public static BigDecimal currentRateChinaToTw = new BigDecimal("4.5");
    public static BeanfunModel beanfunModel;
    public static Account nowAccount;
    public static Integer port;
    public static String serverAddress;

    public static void buildProxy(String host, int port) {
        QsConstant.proxy = new HttpHost(host, port);
    }


    /**
     * 资源文件枚举
     *
     * @author starmcc
     * @date 2022/03/22
     */
    public static enum Resources {
        LR_PROC_EXE(APP_PATH + "LRProc.exe", "lib/LRProc.exe"),
        LR_HOOKX64_DLL(APP_PATH + "LRHookx64.dll", "lib/LRHookx64.dll"),
        LR_HOOKX32_DLL(APP_PATH + "LRHookx32.dll", "lib/LRHookx32.dll"),
        LR_CONFIG_XML(APP_PATH + "LRConfig.xml", "lib/LRConfig.xml"),
        LR_SUB_MENUS_DLL(APP_PATH + "LRSubMenus.dll", "lib/LRSubMenus.dll"),

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
     * 对应页面的路由
     *
     * @author starmcc
     * @date 2022/03/17
     */
    @Getter
    public static enum Page {
        装备计算器("equipment", "Equipment", jfxStage -> {
            jfxStage.setMiniSupport(false);
            jfxStage.setCloseEvent(() -> FrameUtils.closeWindow(jfxStage));
            QsConstant.equippingJFXStage = jfxStage;
        }),
        网页客户端("web", "WebKit", jfxStage -> {
            jfxStage.setMiniSupport(true);
            jfxStage.setCloseEvent(() -> FrameUtils.closeWindow(jfxStage));
            QsConstant.webJFXStage = jfxStage;
        }),

        ;

        private final String url;
        private final String title;
        private final Consumer<JFXStage> buildMethod;

        Page(String url, String title, Consumer<JFXStage> buildMethod) {
            this.url = url;
            this.title = title;
            this.buildMethod = buildMethod;
        }

        public String getUrl() {
            return "/pages/" + url + ".fxml";
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

    /**
     * 获得数字版本
     *
     * @param name 名字
     * @return {@link Long}
     */
    private static Long getNumberVersion(String name) {
        if (StringUtils.isBlank(name)) {
            return 0L;
        }
        String[] split = name.split("\\.");
        List<String> versionList = Arrays.stream(split).collect(Collectors.toList());
        while (versionList.size() < 3) {
            versionList.add("0");
        }
        StringBuilder version = new StringBuilder();
        for (String s : versionList) {
            version.append(s);
        }
        return Long.parseLong(version.toString());
    }


    /**
     * 检查新版本
     *
     * @param targetVersion 目标版本
     * @return boolean 有新版本，返回true，否则false
     */
    public static boolean checkNewVersion(String targetVersion) {
        Long numberVersion = getNumberVersion(APP_VERSION);
        Long numberVersionTarget = getNumberVersion(targetVersion);
        if (numberVersionTarget == 0) {
            log.error("版本解析为0，解析失败 targetVersion={}", targetVersion);
            return false;
        }
        return numberVersion.compareTo(numberVersionTarget) == -1;
    }
}
