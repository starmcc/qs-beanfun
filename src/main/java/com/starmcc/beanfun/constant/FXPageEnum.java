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
    登录页("login", false, "小梦出品", false, true, true,
            jfxStage -> jfxStage.getRoot().getStylesheets().add(QsConstant.class.getResource("/static/css/login.css").toExternalForm())),
    主页("main", true, "QsBeanfun", true, false, true,
            jfxStage -> jfxStage.getRoot().getStylesheets().add(QsConstant.class.getResource("/static/css/main.css").toExternalForm())),
    二维码登录("qrCode", true, "二维码登录", false, false, true, null),
    更新页("update", true, "自动更新", false, false, true, null),
    关于我("about", false, "关于..", false, false, false, null),
    装备计算器("equipment", true, "装备卷轴计算器", false, false, false, null),
    汇率查询("currency", true, "汇率查询", false, false, false, null),
    录像设置("recordVideo", true, "录像设置", false, false, false, null),
    ;

    /**
     * url
     */
    private final String fileName;
    /**
     * 标题
     */
    private final Boolean showIco;
    /**
     * 标题
     */
    private final String title;
    /**
     * 显示最小化按钮
     */
    private final Boolean showMinButton;
    /**
     * about按钮
     */
    private final Boolean aboutButton;
    /**
     * 关闭并杀死线程
     */
    private final Boolean closeAndKillThread;
    /**
     * 构建方法
     */
    private final Consumer<JFXStage> buildMethod;


    FXPageEnum(String fileName,
               Boolean showIco,
               String title,
               Boolean showMinButton,
               Boolean aboutButton,
               Boolean closeAndKillThread,
               Consumer<JFXStage> buildMethod) {
        this.fileName = fileName;
        this.showIco = showIco;
        this.title = title;
        this.showMinButton = showMinButton;
        this.aboutButton = aboutButton;
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