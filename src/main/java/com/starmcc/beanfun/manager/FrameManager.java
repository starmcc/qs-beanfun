package com.starmcc.beanfun.manager;

import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.entity.thread.ThrowRunnable;
import com.starmcc.beanfun.manager.impl.FrameManagerImpl;
import javafx.scene.control.Alert;
import javafx.stage.Stage;


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
     * 打开内置网页浏览器(Miniblink)
     *
     * @param url url
     */
    void openWebBrowser(String url);

    /**
     * 退出App
     */
    void exit();

    /**
     * JavaFx主线程运行
     *
     * @param throwRunnable runnable2
     */
    void runLater(ThrowRunnable throwRunnable);


    /**
     * 杀死所有内置线程和任务,并退出托盘
     */
    void killAllTask();

    /**
     * 消息框(同步)
     *
     * @param msg       msg
     * @param alertType 警报类型
     */
    void messageSync(String msg, Alert.AlertType alertType);

    /**
     * 消息框(同步)
     *
     * @param msg       msg
     * @param alertType 警报类型
     * @param runnable  回调
     */
    void messageSync(String msg, Alert.AlertType alertType, Runnable runnable);

    /**
     * 消息框(主线程适配-异步)
     *
     * @param msg       消息
     * @param alertType 警报类型
     */
    void message(String msg, Alert.AlertType alertType);

    /**
     * 消息框(主线程适配-异步)
     *
     * @param msg       消息
     * @param alertType 警报类型
     * @param runnable  回调
     */
    void message(String msg, Alert.AlertType alertType, Runnable runnable);

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
