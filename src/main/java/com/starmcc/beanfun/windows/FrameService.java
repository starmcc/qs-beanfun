package com.starmcc.beanfun.windows;

import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.model.thread.Runnable2;
import com.starmcc.beanfun.windows.impl.FrameServiceImpl;
import javafx.stage.Stage;

/**
 * @author starmcc
 * @date 2022/9/14 17:55
 */
public interface FrameService {

    /**
     * 获得实例
     *
     * @return {@link FrameService}
     */
    static FrameService getInstance() {
        return new FrameServiceImpl();
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
     * @param runnable2 runnable2
     */
    void runLater(Runnable2 runnable2);

    /**
     * 杀死所有内置线程和任务,并退出托盘
     */
    void killAllTask();


}
