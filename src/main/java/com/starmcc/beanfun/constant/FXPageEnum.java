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
    LOGIN("login", false, "QsBeanfun", false, true, true,
            jfxStage -> jfxStage.getRoot().getStylesheets().add(QsConstant.class.getResource("/static/css/login.css").toExternalForm())),
    MAIN("main", true, "QsBeanfun", true, false, true,
            jfxStage -> jfxStage.getRoot().getStylesheets().add(QsConstant.class.getResource("/static/css/main.css").toExternalForm())),
    QR_CODE("qrCode", true, "二维码登录", false, false, true, null),
    UPDATE("update", true, "自动更新", false, false, true, null),
    ABOUT("about", false, "关于..", false, false, false, null),
    CURRENCY("currency", true, "汇率查询", false, false, false, null),
    RECORD_VIDEO("recordVideo", true, "录像设置", false, false, false, null),
    ADV_LOGIN("advLogin", true, "进阶登录", false, false, false, null),

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