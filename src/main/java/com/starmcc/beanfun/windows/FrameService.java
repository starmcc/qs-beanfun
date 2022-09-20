package com.starmcc.beanfun.windows;

import com.starmcc.beanfun.constant.FXPages;
import com.starmcc.beanfun.thread.Runnable2;
import com.starmcc.beanfun.windows.impl.FrameServiceImpl;

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

    void openWindow(FXPages page, FXPages parentPage) throws Exception;

    void openWindow(FXPages page) throws Exception;

    boolean closeWindow(FXPages page);

    void openWebUrl(String url);

    void openWebBrowser(String url);

    void exit();

    void runLater(Runnable2 runnable2);

    void killAllTask();


}
