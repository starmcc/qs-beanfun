package com.starmcc.beanfun.constant;

import com.starmcc.beanfun.model.JFXStage;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * 对应页面的路由
 *
 * @author starmcc
 * @date 2022/03/17
 */
@Getter
public enum FXPages {
    登录页面("login", "QsBeanfun", false, false, true, true,
            jfxStage -> jfxStage.getRoot().getStylesheets().add(QsConstant.class.getResource("/static/css/login.css").toExternalForm())),
    主界面("main", "QsBeanfun", true, true, true),
    关于我("about", "About"),
    装备计算器("equipment", "Equipment"),
    二维码登录("qrCode", "QR-Code", true, false, true),
    ;

    /**
     * url
     */
    private final String fileName;
    /**
     * 标题
     */
    private final String title;
    /**
     * 显示头部
     */
    private final Boolean showTop;
    /**
     * 显示最小化按钮
     */
    private final Boolean showMinButton;
    /**
     * 关闭并杀死线程
     */
    private final Boolean closeAndKillThread;
    /**
     * 关闭并退出应用程序
     */
    private final Boolean closeAndExitApp;
    /**
     * 构建方法
     */
    private final Consumer<JFXStage> buildMethod;

    FXPages(String fileName, String title) {
        this(fileName, title, true, false, false, false, null);
    }

    FXPages(String fileName, String title, Boolean showTop) {
        this(fileName, title, showTop, false, false, false, null);
    }

    FXPages(String fileName, String title, Boolean showTop, Boolean showMinButton) {
        this(fileName, title, showTop, showMinButton, false, false, null);
    }

    FXPages(String fileName, String title, Boolean showTop, Boolean showMinButton,
            boolean closeAndKillThread) {
        this(fileName, title, showTop, showMinButton, closeAndKillThread, false, null);
    }

    FXPages(String fileName, String title, Boolean showTop, Boolean showMinButton,
            boolean closeAndKillThread, boolean closeAndExitApp) {
        this(fileName, title, showTop, showMinButton, closeAndKillThread, closeAndExitApp, null);
    }

    FXPages(String fileName, String title, Boolean showTop, Boolean showMinButton,
            boolean closeAndKillThread, boolean closeAndExitApp, Consumer<JFXStage> buildMethod) {
        this.fileName = fileName;
        this.title = title;
        this.showTop = showTop;
        this.showMinButton = showMinButton;
        this.closeAndKillThread = closeAndKillThread;
        this.closeAndExitApp = closeAndExitApp;
        this.buildMethod = buildMethod;
    }

    /**
     * 构建路径
     *
     * @return {@link String}
     */
    public String buildPath() {
        return "/pages/" + fileName + ".fxml";
    }

}