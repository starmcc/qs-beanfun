package com.starmcc.beanfun.manager.impl;

import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.model.JFXStage;
import com.starmcc.beanfun.entity.model.QsTray;
import com.starmcc.beanfun.entity.param.WindowXyParam;
import com.starmcc.beanfun.entity.thread.ThrowRunnable;
import com.starmcc.beanfun.entity.thread.ThrowSupplier;
import com.starmcc.beanfun.manager.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 框架管理器实现
 *
 * @author starmcc
 * @date 2022/09/23
 */
@Slf4j
public class FrameManagerImpl implements FrameManager {
    @Override
    public void openWindow(FXPageEnum page, FXPageEnum parentPage) throws Exception {
        openWindow(page, QsConstant.JFX_STAGE_DATA.get(parentPage).getStage(), page.getBuildMethod(), false);
    }

    @Override
    public void openWindow(FXPageEnum page, Stage parentStage) throws Exception {
        openWindow(page, parentStage, page.getBuildMethod(), false);
    }

    @Override
    public void openWindow(FXPageEnum page) throws Exception {
        openWindow(page, null, page.getBuildMethod(), false);
    }

    @Override
    public void openWindowSync(FXPageEnum page, FXPageEnum parentPage) throws Exception {
        openWindow(page, QsConstant.JFX_STAGE_DATA.get(parentPage).getStage(), page.getBuildMethod(), true);
    }

    @Override
    public void openWindowSync(FXPageEnum page, Stage parentStage) throws Exception {
        openWindow(page, parentStage, page.getBuildMethod(), true);
    }

    @Override
    public void openWindowSync(FXPageEnum page) throws Exception {
        openWindow(page, null, page.getBuildMethod(), true);

    }

    @Override
    public void killAllTask() {
        ThreadPoolManager.shutdown();
        AdvancedTimerManager.getInstance().removeAllTask();
        RecordVideoManager.getInstance().stop();
        if (Objects.nonNull(QsConstant.trayIcon)) {
            QsTray.remove(QsConstant.trayIcon);
        }
    }

    @Override
    public boolean closeWindow(FXPageEnum page) {
        if (Objects.isNull(page)) {
            return false;
        }

        if (page.getCloseAndKillThread()) {
            this.killAllTask();
        }

        JFXStage jfxStage = QsConstant.JFX_STAGE_DATA.get(page);
        if (Objects.isNull(jfxStage) || Objects.isNull(jfxStage.getStage())) {
            return false;
        }
        QsConstant.pageXyParams.put(page, new WindowXyParam(jfxStage.getStage().getX(), jfxStage.getStage().getY()));
        jfxStage.getStage().close();
        return true;
    }


    @Override
    public void exit() {
        this.killAllTask();
        AdvancedTimerManager.shutdown();
        Platform.exit();
    }

    @Override
    public void runLater(ThrowRunnable throwRunnable) {
        Platform.runLater(() -> {
            try {
                throwRunnable.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public <T> T runLater(ThrowSupplier<T> throwSupplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                future.complete(throwSupplier.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        try {
            // 阻塞等待异步操作完成并获取结果
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void openWebUrl(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
                URI uri = new URI(url);
                desktop.browse(uri);
            }
        } catch (Exception e) {
            log.error("执行发生异常 e={}", e.getMessage(), e);
        }
    }


    private void openWindow(FXPageEnum page, Stage parentStage, Consumer<JFXStage> build, boolean sync) throws Exception {
        Stage stage = new Stage();
        if (Objects.nonNull(parentStage)) {
            stage.initOwner(parentStage);
            stage.initModality(Modality.APPLICATION_MODAL);
        }
        Parent parent = FXMLLoader.load(Objects.requireNonNull(FrameManagerImpl.class.getResource(page.buildPath())));
        JFXStage jfxStage = JFXStage.of(stage, parent);
        // 构建外部方法
        if (Objects.nonNull(build)) {
            build.accept(jfxStage);
        }
        jfxStage.build(page);
        stage.getIcons().add(new Image("static/images/ico.png"));
        stage.setResizable(false);
        stage.setTitle(page.getTitle());
        WindowXyParam windowXyParam = QsConstant.pageXyParams.get(page);
        if (Objects.nonNull(windowXyParam)) {
            jfxStage.getStage().setX(windowXyParam.getX());
            jfxStage.getStage().setY(windowXyParam.getY());
        }

        // 保存该Stage
        QsConstant.JFX_STAGE_DATA.put(page, jfxStage);
        if (sync) {
            stage.showAndWait();
        } else {
            stage.show();
        }

    }

    @Override
    public void openWebBrowser(String url) {
        this.openWebBrowser(url, true);
    }

    @Override
    public void openWebBrowser(String url, boolean newWindow) {
        try {
            FrameManager.getInstance().runLater(() -> JxBrowserManager.getInstance().open(url, newWindow));
        } catch (Exception e) {
            log.error("打开浏览器发生异常 e={}", e.getMessage(), e);
        }
    }

    @Override
    public void message(String msg, Alert.AlertType alertType) {
        this.message(msg, alertType, null, null, true);
    }

    @Override
    public void message(String msg, Alert.AlertType alertType, FXPageEnum parentPage) {
        this.message(msg, alertType, parentPage, null, true);
    }


    @Override
    public void message(String msg, Alert.AlertType alertType, FXPageEnum parentPage, Consumer<Alert> consumer) {
        this.message(msg, alertType, parentPage, consumer, true);
    }


    @Override
    public void messageAsync(String msg, Alert.AlertType alertType) {
        this.message(msg, alertType, null, null, false);
    }

    @Override
    public void messageAsync(String msg, Alert.AlertType alertType, FXPageEnum parentPage) {
        this.message(msg, alertType, parentPage, null, false);
    }

    @Override
    public void messageAsync(String msg, Alert.AlertType alertType, FXPageEnum parentPage, Consumer<Alert> consumer) {
        this.message(msg, alertType, parentPage, consumer, false);
    }

    @Override
    public void messageMaster(String msg, Alert.AlertType alertType) {
        this.runLater(() -> this.message(msg, alertType, null, null, true));
    }

    @Override
    public void messageMaster(String msg, Alert.AlertType alertType, FXPageEnum parentPage) {
        this.runLater(() -> this.message(msg, alertType, parentPage, null, true));
    }

    @Override
    public void messageMaster(String msg, Alert.AlertType alertType, FXPageEnum parentPage, Consumer<Alert> consumer) {
        this.runLater(() -> this.message(msg, alertType, parentPage, consumer, true));
    }

    @Override
    public void messageMasterAsync(String msg, Alert.AlertType alertType) {
        this.runLater(() -> this.message(msg, alertType, null, null, false));
    }

    @Override
    public void messageMasterAsync(String msg, Alert.AlertType alertType, FXPageEnum parentPage) {
        this.runLater(() -> this.message(msg, alertType, parentPage, null, false));
    }

    @Override
    public void messageMasterAsync(String msg, Alert.AlertType alertType, FXPageEnum parentPage, Consumer<Alert> consumer) {
        this.runLater(() -> this.message(msg, alertType, parentPage, consumer, false));
    }

    private void message(String msg, Alert.AlertType alertType, FXPageEnum parentPage, Consumer<Alert> consumer, boolean sync) {
        final Alert alert = new Alert(alertType);
        if (Objects.nonNull(parentPage)) {
            JFXStage jfxStage = QsConstant.JFX_STAGE_DATA.get(parentPage);
            if (Objects.nonNull(jfxStage)) {
                alert.initOwner(jfxStage.getStage());
            }
        }
        alert.setTitle("tips");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        if (sync) {
            alert.showAndWait();
        } else {
            alert.show();
        }
        if (Objects.nonNull(consumer)) {
            consumer.accept(alert);
        }
    }

    @Override
    public String dialogText(String tips, String defaultText) {
        TextInputDialog dialog = new TextInputDialog(defaultText);
        dialog.setTitle("");
        dialog.setHeaderText(tips);
        Optional<String> s = dialog.showAndWait();
        return s.orElse("");
    }


    @Override
    public boolean dialogConfirm(String title, String tips) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(tips);
        Optional<ButtonType> buttonType = alert.showAndWait();
        return buttonType.filter(type -> type == ButtonType.OK).isPresent();
    }
}
