package com.starmcc.beanfun.model;

import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.windows.FrameService;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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

/**
 * jfxstage
 *
 * @author starmcc
 * @date 2022/09/21
 */
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

    JFXStage(Stage stage, Parent root) {
        this.stage = stage;
        this.root = root;
    }

    public static JFXStage of(Stage stage, Parent root) {
        return new JFXStage(stage, root);
    }


    public void buildSimple(FXPageEnum page) {
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle(page.getTitle());
        root.setStyle("-fx-border-width: 1; -fx-border-color: black");
        new DragListener(stage).enableDrag(root);
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
     * 构建
     *
     * @param page 页面
     */
    public void build(FXPageEnum page) {
        final Insets insets = new Insets(5, 10, 5, 10);
        HBox hbox = new HBox();
        hbox.setPrefHeight(26);
        hbox.setPadding(insets);
        hbox.getStylesheets().add(this.getClass().getResource("/static/css/title.css").toExternalForm());
        hbox.getStyleClass().add(HBOX_CSS_CLASS);
        // LOGO
        Label logo = new Label();
        logo.setPadding(insets);
        logo.setPrefWidth(16);
        logo.setPrefHeight(16);
        logo.getStyleClass().add(LOGO_CSS_CLASS);
        hbox.getChildren().add(logo);
        // TITLE
        Label titleLbl = new Label(page.getTitle());
        titleLbl.setId("customTitle");
        titleLbl.setPadding(insets);
        titleLbl.setPrefHeight(16);
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
        close.setPadding(insets);
        close.setPrefWidth(16);
        close.setPrefHeight(16);
        close.getStyleClass().add(CLOSE_CSS_CLASS);
        close.setOnMouseClicked(e -> {
            if (page == FXPageEnum.登录页面 || page == FXPageEnum.主界面) {
                FrameService.getInstance().exit();
                return;
            }
            FrameService.getInstance().closeWindow(page);
        });

        if (page.getShowMinButton()) {
            // MIN
            Label min = new Label();
            min.setPadding(insets);
            min.setPrefWidth(16);
            min.setPrefHeight(16);
            min.getStyleClass().add(MIN_CSS_CLASS);
            min.setOnMouseClicked(e -> {
                stage.setIconified(true);
                if (stage.isIconified()) {
                    stage.setIconified(false);
                }
                stage.hide();
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
        root.setStyle("-fx-border-width: 1; -fx-border-color: black");
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
