package com.starmcc.beanfun.manager;

import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.entity.thread.ThrowRunnable;
import com.starmcc.beanfun.manager.impl.FrameManagerImpl;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.function.Consumer;


/**
 * 框架管理器
 *
 * @author starmcc
 * @date 2022/09/23
 */
public interface FrameManager {

    /**
     * 获得实例
     *
     * @return {@link FrameManager}
     */
    static FrameManager getInstance() {
        return new FrameManagerImpl();
    }

    /**
     * 打开窗口
     *
     * @param page       页面
     * @param parentPage 父页面
     * @throws Exception 异常
     */
    void openWindow(FXPageEnum page, FXPageEnum parentPage) throws Exception;

    /**
     * 打开窗口
     *
     * @param page        页面
     * @param parentStage 父页面
     * @throws Exception 异常
     */
    void openWindow(FXPageEnum page, Stage parentStage) throws Exception;

    /**
     * 打开窗口
     *
     * @param page 页面
     * @throws Exception 异常
     */
    void openWindow(FXPageEnum page) throws Exception;


    /**
     * 打开窗口
     *
     * @param page       页面
     * @param parentPage 父页面
     * @throws Exception 异常
     */
    void openWindowSync(FXPageEnum page, FXPageEnum parentPage) throws Exception;

    /**
     * 打开窗口
     *
     * @param page        页面
     * @param parentStage 父页面
     * @throws Exception 异常
     */
    void openWindowSync(FXPageEnum page, Stage parentStage) throws Exception;

    /**
     * 打开窗口
     *
     * @param page 页面
     * @throws Exception 异常
     */
    void openWindowSync(FXPageEnum page) throws Exception;

    /**
     * 关闭窗口
     *
     * @param page 页面
     * @return boolean
     */
    boolean closeWindow(FXPageEnum page);

    /**
     * 打开web url
     *
     * @param url url
     */
    void openWebUrl(String url);

    /**
     * 打开内置网页浏览器
     *
     * @param url url
     */
    void openWebBrowser(String url);

    /**
     * 打开内置网页浏览器
     *
     * @param url       url
     * @param newWindow 是否允许新窗口打开
     */
    void openWebBrowser(String url, boolean newWindow);

    /**
     * 退出App
     */
    void exit();

    /**
     * JavaFx主线程运行
     *
     * @param throwRunnable throwRunnable
     */
    void runLater(ThrowRunnable throwRunnable);


    /**
     * 杀死所有内置线程和任务,并退出托盘
     */
    void killAllTask();

    /**
     * 消息 (同步)
     */
    void message(String msg, Alert.AlertType alertType);

    /**
     * 消息 (同步)
     */
    void message(String msg, Alert.AlertType alertType, FXPageEnum parentPage);

    /**
     * 消息 (同步)
     */
    void message(String msg, Alert.AlertType alertType, FXPageEnum parentPage, Consumer<Alert> consumer);

    /**
     * 消息 (异步)
     */
    void messageAsync(String msg, Alert.AlertType alertType);

    /**
     * 消息 (异步)
     */
    void messageAsync(String msg, Alert.AlertType alertType, FXPageEnum parentPage);

    /**
     * 消息 (异步)
     */
    void messageAsync(String msg, Alert.AlertType alertType, FXPageEnum parentPage, Consumer<Alert> consumer);

    /**
     * 消息 (主线程-同步)
     */
    void messageMaster(String msg, Alert.AlertType alertType);

    /**
     * 消息 (主线程-同步)
     */
    void messageMaster(String msg, Alert.AlertType alertType, FXPageEnum parentPage);

    /**
     * 消息 (主线程-同步)
     */
    void messageMaster(String msg, Alert.AlertType alertType, FXPageEnum parentPage, Consumer<Alert> consumer);

    /**
     * 消息 (主线程-异步)
     */
    void messageMasterAsync(String msg, Alert.AlertType alertType);

    /**
     * 消息 (主线程-异步)
     */
    void messageMasterAsync(String msg, Alert.AlertType alertType, FXPageEnum parentPage);

    /**
     * 消息 (主线程-异步)
     */
    void messageMasterAsync(String msg, Alert.AlertType alertType, FXPageEnum parentPage, Consumer<Alert> consumer);

    /**
     * 文本输入框
     *
     * @param tips        提示
     * @param defaultText 默认文本
     * @return {@link String}
     */
    String dialogText(String tips, String defaultText);

    /**
     * 询问对话框
     *
     * @param title 标题
     * @param tips  提示
     * @return boolean
     */
    boolean dialogConfirm(String title, String tips);
}
