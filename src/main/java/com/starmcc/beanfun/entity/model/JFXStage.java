package com.starmcc.beanfun.entity.model;

import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.manager.FrameManager;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * jfxstage
 *
 * @author starmcc
 * @date 2022/09/21
 */
@Slf4j
@Data
public class JFXStage implements EventHandler<MouseEvent> {

    private final Stage stage;
    private final Parent root;

    private double xOffset = 0;
    private double yOffset = 0;

    private boolean closeKillThread = false;

    private boolean closeAndExitApp = false;

    // CSS
    private static final String HBOX_CSS_CLASS = "hbox";
    private static final String LOGO_CSS_CLASS = "logo";
    private static final String TITLE_CSS_CLASS = "title";
    private static final String MIN_CSS_CLASS = "min";
    private static final String CLOSE_CSS_CLASS = "close";
    private static final String ABOUT_CSS_CLASS = "about";

    JFXStage(Stage stage, Parent root) {
        this.stage = stage;
        this.root = root;
    }

    public static JFXStage of(Stage stage, Parent root) {
        return new JFXStage(stage, root);
    }


    /**
     * 拖动监听器
     *
     * @author starmcc
     * @date 2022/09/21
     */
    private static class DragListener implements EventHandler<MouseEvent> {

        private double xOffset = 0;
        private double yOffset = 0;
        private final Stage stage;

        public DragListener(Stage stage) {
            this.stage = stage;
        }

        @Override
        public void handle(MouseEvent event) {
            event.consume();
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                stage.setX(event.getScreenX() - xOffset);
                if (event.getScreenY() - yOffset < 0) {
                    stage.setY(0);
                } else {
                    stage.setY(event.getScreenY() - yOffset);
                }
            }
        }

        public void enableDrag(Node node) {
            node.setOnMousePressed(this);
            node.setOnMouseDragged(this);
        }
    }


    /**
     * 简单构建
     *
     * @param page 页面
     */
    public void buildSimple(FXPageEnum page) {
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle(page.getTitle());
        root.setStyle("-fx-border-width: 1; -fx-border-color: black");
        new DragListener(stage).enableDrag(root);
    }

    /**
     * 构建
     *
     * @param page 页面
     */
    public void build(FXPageEnum page) {
        HBox hbox = new HBox();
        hbox.setPrefHeight(26);
        String cssFile = "/static/css/title.css";
        if (page == FXPageEnum.登录页) {
            cssFile = "/static/css/login.css";
        }
        hbox.getStylesheets().add(this.getClass().getResource(cssFile).toExternalForm());
        hbox.getStyleClass().add(HBOX_CSS_CLASS);
        if (page.getShowIco()) {
            // LOGO
            Label logo = new Label();
            logo.setPrefWidth(22);
            logo.setPrefHeight(22);
            logo.getStyleClass().add(LOGO_CSS_CLASS);
            hbox.getChildren().add(logo);
        }
        // TITLE
        Label titleLbl = new Label(page.getTitle());
        if (page == FXPageEnum.主页) {
            titleLbl.setText(page.getTitle() + "-" + QsConstant.APP_VERSION);
        }
        titleLbl.setId("customTitle");
        titleLbl.setPrefHeight(22);
        titleLbl.setFont(Font.font(12));
        titleLbl.setAlignment(Pos.CENTER);
        titleLbl.getStyleClass().add(TITLE_CSS_CLASS);
        hbox.getChildren().add(titleLbl);
        // PANE
        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        hbox.getChildren().add(pane);
        // CLOSE
        Label close = new Label();
        close.setPrefWidth(22);
        close.setPrefHeight(22);
        close.getStyleClass().add(CLOSE_CSS_CLASS);
        close.setOnMouseClicked(e -> {
            if (page == FXPageEnum.登录页 || page == FXPageEnum.主页) {
                FrameManager.getInstance().exit();
                return;
            }
            FrameManager.getInstance().closeWindow(page);
        });

        if (page.getAboutButton()) {
            // about
            Label about = new Label();
            about.setPrefWidth(22);
            about.setPrefHeight(22);
            about.getStyleClass().add(ABOUT_CSS_CLASS);
            about.setOnMouseClicked(event -> {
                try {
                    FrameManager.getInstance().openWindow(FXPageEnum.关于我, page);
                } catch (Exception e) {
                    log.error("发生异常 e={}", e.getMessage(), e);
                }
            });
            hbox.getChildren().add(about);
        }

        if (page.getShowMinButton()) {
            // MIN
            Label min = new Label();
            min.setPrefWidth(22);
            min.setPrefHeight(22);
            min.getStyleClass().add(MIN_CSS_CLASS);
            min.setOnMouseClicked(e -> {
                stage.setIconified(true);
                if (QsConstant.config.getMinimizeMode())
                {
                    if (stage.isIconified()) {
                        stage.setIconified(false);
                    }
                    stage.hide();
                }
            });
            hbox.getChildren().add(min);
        }

        hbox.getChildren().add(close);
        // STAGE
        stage.initStyle(StageStyle.UNDECORATED);
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(hbox);
        borderPane.setCenter(root);
        Scene scene = new Scene(borderPane);
        scene.getRoot().setOnMousePressed(this);
        scene.getRoot().setOnMouseDragged(this);
        hbox.setOnMousePressed(this);
        hbox.setOnMouseDragged(this);
        borderPane.setStyle("-fx-border-width: 1; -fx-border-color: black");
        stage.setScene(scene);
    }

    /**
     * 重写鼠标handle
     *
     * @param event 事件
     */
    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            stage.setX(event.getScreenX() - xOffset);
            if (event.getScreenY() - yOffset < 0) {
                stage.setY(0);
            } else {
                stage.setY(event.getScreenY() - yOffset);
            }
        }
    }
}
