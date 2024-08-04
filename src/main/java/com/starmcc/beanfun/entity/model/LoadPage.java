package com.starmcc.beanfun.entity.model;

import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.thread.ThrowConsumer;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 加载中面板
 *
 * @author starmcc
 * @date 2022/09/24
 */
@Slf4j
public class LoadPage {


    /**
     * 同步任务
     *
     * @param pageEnum 页面枚举
     * @param consumer 消费者
     */
    public static void task(FXPageEnum pageEnum, ThrowConsumer<Label> consumer) {
        Pane stagePane = getStageParentPane(pageEnum);
        if (Objects.isNull(stagePane)) {
            throw new RuntimeException("find pageEnum error");
        }
        if (Objects.isNull(consumer)) {
            throw new RuntimeException("find consumer error");
        }
        String message = "Loading..";
        Map<String, Object> map = buildBasicPage(message, stagePane);
        Pane pane = (Pane) map.get("pane");
        Label label = (Label) map.get("label");
        pane.setId(pageEnum.getFileName() + "-loading");
        FrameManager.getInstance().runLater(() -> {
            stagePane.getChildren().add(pane);
            stagePane.setDisable(true);
        });
        ThreadPoolManager.execute(() -> {
            try {
                consumer.run(label);
            } catch (Exception e) {
                log.error("error = {}", e.getMessage(), e);
            } finally {
                FrameManager.getInstance().runLater(() -> {
                    stagePane.getChildren().remove(pane);
                    stagePane.setDisable(false);
                });
            }
        });
    }


    // ==============================================================================


    /**
     * 构建加载中面板
     *
     * @return {@link Pane}
     */
    private static Map<String, Object> buildBasicPage(String message, Pane stagePane) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefWidth(stagePane.getWidth());
        stackPane.setPrefHeight(stagePane.getHeight());
        stackPane.setStyle("-fx-background-color: rgba(0,0,0,0.8);-fx-opacity: 1.0;");
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setText(message);
        label.setFont(new Font(14));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-opacity: 1.0;");
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(-1F);
        progressIndicator.setPrefWidth(56);
        progressIndicator.setPrefHeight(56);
        progressIndicator.setStyle("-fx-opacity: 1.0;");
        vBox.getChildren().addAll(progressIndicator, label);
        Map<String, Object> map = new HashMap<>(16);
        map.put("label", label);
        stackPane.getChildren().add(vBox);
        map.put("pane", stackPane);
        return map;
    }


    /**
     * 获取窗口Pane
     *
     * @param pageEnum 页面枚举
     * @return {@link Pane}
     */
    private static Pane getStageParentPane(FXPageEnum pageEnum) {
        JFXStage jfxStage = QsConstant.JFX_STAGE_DATA.get(pageEnum);
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
