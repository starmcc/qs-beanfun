package com.starmcc.beanfun.utils;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.windows.JFXStage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.awt.*;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


/**
 * 窗口工具类
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public class FrameUtils {

    public static ExecutorService THREAD_POOL = null;


    /**
     * 打开页面
     *
     * @param page  页面
     * @param build 构建
     * @throws Exception 异常
     */
    public static void openWindow(QsConstant.Page page, Consumer<JFXStage> build) throws Exception {
        openWindow(page, null, build);
    }


    /**
     * 打开页面
     *
     * @param page        页面
     * @param parentStage 父窗口
     * @param build       构建
     * @throws Exception 异常
     */
    public static void openWindow(QsConstant.Page page, Stage parentStage, Consumer<JFXStage> build) throws Exception {
        Stage stage = new Stage();
        if (Objects.nonNull(parentStage)) {
            stage.initOwner(parentStage);
        }
        Parent parent = FXMLLoader.load(FrameUtils.class.getResource(page.getUrl()));
        JFXStage jfxStage = JFXStage.of(stage, parent);
        jfxStage.setTitle(page.getTitle());
        if (Objects.nonNull(build)) {
            build.accept(jfxStage);
        }
        jfxStage.build(() -> {
            log.info("关闭");
            if (Objects.nonNull(THREAD_POOL)) {
                THREAD_POOL.shutdownNow();
                THREAD_POOL = null;
            }
        });
        stage.getIcons().add(new Image("static/images/ico.png"));
        stage.setResizable(false);
        stage.setTitle(page.getTitle());
        stage.show();
    }

    public static boolean closeWindow(JFXStage jfxStage) {
        if (Objects.isNull(jfxStage) || Objects.isNull(jfxStage.getStage())) {
            return false;
        }
        jfxStage.getStage().close();
        return true;
    }

    public static void executeThread(Runnable runnable) {
        if (Objects.isNull(THREAD_POOL) || THREAD_POOL.isShutdown()) {
            THREAD_POOL = new ThreadPoolExecutor(3, 6,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(2048),
                    new BasicThreadFactory.Builder().namingPattern("FrameUtils-schedule-pool-%d").daemon(true).build(),
                    new ThreadPoolExecutor.AbortPolicy());
        }
        THREAD_POOL.execute(runnable);
    }


    /**
     * 打开web url
     *
     * @param url url
     */
    public static void openWebUrl(String url) {

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
}
