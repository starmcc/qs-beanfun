package com.starmcc.beanfun.entity.model;

import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.entity.thread.ThrowRunnable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 加载中面板
 *
 * @author starmcc
 * @date 2022/09/24
 */
public class LoadingPage {
    private static final Map<String, Pane> PANE_MAP = new HashMap<>(16);


    /**
     * 打开
     *
     * @param pageEnum 页面枚举
     */
    public static void open(FXPageEnum pageEnum) {
        task(pageEnum, null, null);
    }

    /**
     * 打开
     *
     * @param pageEnum 页面枚举
     * @param message  消息
     * @return {@link Pane}
     */
    public static void open(FXPageEnum pageEnum, String message) {
        task(pageEnum, message, null);
    }

    /**
     * 异步任务
     *
     * @param pageEnum 页面枚举
     * @param message  消息
     * @param runnable 可运行
     */
    public static void taskAsync(FXPageEnum pageEnum, String message, ThrowRunnable runnable) {
        task(pageEnum, message, null);
        if (Objects.isNull(runnable)) {
            return;
        }
        ThreadPoolManager.execute(() -> closeLoadingPageByTaskRunning(pageEnum, runnable));
    }


    /**
     * 同步任务
     *
     * @param pageEnum 页面枚举
     * @param message  消息
     * @return {@link Pane}
     */
    public static void task(FXPageEnum pageEnum, String message, ThrowRunnable runnable) {
        Pane stagePane = getStageParentPane(pageEnum);
        if (Objects.isNull(stagePane)) {
            throw new RuntimeException("find pageEnum error");
        }
        message = StringUtils.isBlank(message) ? "Loading.." : message;
        Pane pane = buildBasicPage(message, stagePane);
        pane.setId(pageEnum.getFileName() + "-loading");
        PANE_MAP.put(pageEnum.getFileName(), pane);
        FrameManager.getInstance().runLater(() -> stagePane.getChildren().add(pane));
        if (Objects.isNull(runnable)) {
            return;
        }
        closeLoadingPageByTaskRunning(pageEnum, runnable);
    }

    /**
     * 关闭
     *
     * @param pageEnum 页面枚举
     */
    public static void close(FXPageEnum pageEnum) {
        Pane pane = PANE_MAP.get(pageEnum.getFileName());
        if (Objects.isNull(pane)) {
            return;
        }
        PANE_MAP.remove(pageEnum.getFileName());
        Pane stagePane = getStageParentPane(pageEnum);
        if (Objects.isNull(stagePane)) {
            return;
        }
        FrameManager.getInstance().runLater(() -> stagePane.getChildren().remove(pane));
    }


    // ==============================================================================


    /**
     * 通过运行任务关闭加载页面
     *
     * @param pageEnum 页面枚举
     * @param runnable 可运行
     */
    private static void closeLoadingPageByTaskRunning(FXPageEnum pageEnum, ThrowRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(pageEnum);
        }
    }


    /**
     * 构建加载中面板
     *
     * @return {@link Pane}
     */
    private static Pane buildBasicPage(String message, Pane stagePane) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefWidth(stagePane.getWidth());
        stackPane.setPrefHeight(stagePane.getHeight());
        stackPane.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        Image image = new Image("static/images/loading.gif");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);
        vBox.getChildren().add(imageView);
        if (StringUtils.isNotBlank(message)) {
            Label label = new Label();
            label.setAlignment(Pos.CENTER);
            label.setText(message);
            label.setFont(new Font(14));
            label.setTextAlignment(TextAlignment.CENTER);
            label.setTextFill(Color.WHITE);
            vBox.getChildren().add(label);
        }
        stackPane.getChildren().add(vBox);
        return stackPane;
    }


    /**
     * 获取窗口Pane
     *
     * @param pageEnum 页面枚举
     * @return {@link Pane}
     */
    private static Pane getStageParentPane(FXPageEnum pageEnum) {
        JFXStage jfxStage = QsConstant.JFX_STAGE_DATA.get(pageEnum.getFileName());
        if (Objects.isNull(jfxStage) || Objects.isNull(jfxStage.getStage())) {
            return null;
        }
        Pane root = (Pane) jfxStage.getStage().getScene().getRoot();
        if (root instanceof BorderPane) {
            // 如果是自己构建的BorderPane布局，则使用center中的节点
            BorderPane borderPane = (BorderPane) root;
            root = (Pane) borderPane.getCenter();
        }
        return root;
    }


}
