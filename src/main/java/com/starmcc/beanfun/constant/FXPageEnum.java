package com.starmcc.beanfun.constant;

import com.starmcc.beanfun.entity.model.JFXStage;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * 对应页面的路由枚举类
 *
 * @author starmcc
 * @date 2022/09/21
 */
@Getter
public enum FXPageEnum {
    登录页("login", "小梦出品", false, false, true,
            jfxStage -> jfxStage.getRoot().getStylesheets().add(QsConstant.class.getResource("/static/css/login.css").toExternalForm())),
    主页("main", "QsBeanfun", true, true, true),
    关于我("about", "关于.."),
    装备计算器("equipment", "装备计算器"),
    二维码登录("qrCode", "二维码登录", true, false, true),
    更新页("update", "自动更新", true, false, true),
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
     * 构建方法
     */
    private final Consumer<JFXStage> buildMethod;

    FXPageEnum(String fileName, String title) {
        this(fileName, title, true, false, false, null);
    }

    FXPageEnum(String fileName, String title, Boolean showTop) {
        this(fileName, title, showTop, false, false, null);
    }

    FXPageEnum(String fileName, String title, Boolean showTop, Boolean showMinButton) {
        this(fileName, title, showTop, showMinButton, false, null);
    }

    FXPageEnum(String fileName, String title, Boolean showTop, Boolean showMinButton,
               boolean closeAndKillThread) {
        this(fileName, title, showTop, showMinButton, closeAndKillThread, null);
    }

    FXPageEnum(String fileName, String title, Boolean showTop, Boolean showMinButton,
               boolean closeAndKillThread, Consumer<JFXStage> buildMethod) {
        this.fileName = fileName;
        this.title = title;
        this.showTop = showTop;
        this.showMinButton = showMinButton;
        this.closeAndKillThread = closeAndKillThread;
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