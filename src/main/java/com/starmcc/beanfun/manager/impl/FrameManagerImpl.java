package com.starmcc.beanfun.manager.impl;

import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.model.JFXStage;
import com.starmcc.beanfun.entity.model.QsTray;
import com.starmcc.beanfun.entity.thread.ThrowRunnable;
import com.starmcc.beanfun.manager.AdvancedTimerMamager;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.RecordVideoManager;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.utils.JxBrowser;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
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
        openWindow(page, QsConstant.JFX_STAGE_DATA.get(parentPage).getStage(), page.getBuildMethod());
    }

    @Override
    public void openWindow(FXPageEnum page, Stage parentStage) throws Exception {
        openWindow(page, parentStage, page.getBuildMethod());
    }

    @Override
    public void openWindow(FXPageEnum page) throws Exception {
        openWindow(page, null, page.getBuildMethod());
    }

    @Override
    public void killAllTask() {
        ThreadPoolManager.shutdown();
        AdvancedTimerMamager.getInstance().removeAllTask();
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
        jfxStage.getStage().close();
        return true;
    }


    @Override
    public void exit() {
        this.killAllTask();
        AdvancedTimerMamager.shutdown();
        Platform.exit();
    }

    @Override
    public void runLater(ThrowRunnable throwRunnable) {
        Platform.runLater(() -> throwRunnable(throwRunnable));
    }

    private static void throwRunnable(ThrowRunnable throwRunnable) {
        try {
            throwRunnable.run();
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


    private void openWindow(FXPageEnum page, Stage parentStage, Consumer<JFXStage> build) throws Exception {
        Stage stage = new Stage();
        if (Objects.nonNull(parentStage)) {
            stage.initOwner(parentStage);
        }
        Parent parent = FXMLLoader.load(FrameManagerImpl.class.getResource(page.buildPath()));
        JFXStage jfxStage = JFXStage.of(stage, parent);
        // 构建外部方法
        if (Objects.nonNull(build)) {
            build.accept(jfxStage);
        }
        if (page.getShowTop()) {
            jfxStage.build(page);
        } else {
            jfxStage.buildSimple(page);
        }
        stage.getIcons().add(new Image("static/images/ico.png"));
        stage.setResizable(false);
        stage.setTitle(page.getTitle());
        stage.show();
        // 保存该Stage
        QsConstant.JFX_STAGE_DATA.put(page, jfxStage);
    }

    @Override
    public void openWebBrowser(String url) {
        try {
            JxBrowser.getInstance().open(url);
        } catch (Exception e) {
            log.error("打开浏览器发生异常 e={}", e.getMessage(), e);
        }
    }


    @Override
    public void messageSync(String msg, Alert.AlertType alertType, Runnable runnable) {
        final Alert alert = new Alert(alertType);
        alert.setTitle("");
        alert.setHeaderText("");
        alert.setContentText(msg);
        alert.showAndWait();
        if (Objects.nonNull(runnable)) {
            runnable.run();
        }
    }

    @Override
    public void messageSync(String msg, Alert.AlertType alertType) {
        this.messageSync(msg, alertType, null);
    }

    @Override
    public void message(String msg, Alert.AlertType alertType) {
        this.runLater(() -> this.messageSync(msg, alertType, null));
    }

    @Override
    public void message(String msg, Alert.AlertType alertType, Runnable runnable) {
        this.runLater(() -> this.messageSync(msg, alertType, runnable));
    }

    @Override
    public String dialogText(String tips, String defaultText) {
        TextInputDialog dialog = new TextInputDialog(defaultText);
        dialog.setTitle("");
        dialog.setHeaderText(tips);
        Optional<String> s = dialog.showAndWait();
        return s.isPresent() ? s.get() : "";
    }


    @Override
    public boolean dialogConfirm(String title, String tips) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(tips);
        return alert.showAndWait().get() == ButtonType.OK;
    }
}
