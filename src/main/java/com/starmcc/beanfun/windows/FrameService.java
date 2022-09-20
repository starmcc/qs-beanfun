package com.starmcc.beanfun.windows;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.JFXStage;
import com.starmcc.beanfun.thread.Runnable2;
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

    void openWindow(QsConstant.Page page, Stage parentStage) throws Exception;

    void openWindow(QsConstant.Page page) throws Exception;

    boolean closeWindow(JFXStage jfxStage);

    boolean closeWindow(JFXStage jfxStage, boolean killAllTask);

    void openWebUrl(String url);

    void openWebBrowser(String url);

    void exit();

    void runLater(Runnable2 runnable2);


}
