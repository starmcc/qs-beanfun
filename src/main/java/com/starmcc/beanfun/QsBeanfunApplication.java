package com.starmcc.beanfun;

import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.manager.UpdateManager;
import com.starmcc.beanfun.windows.FrameService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;


/**
 * 应用程序
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public class QsBeanfunApplication extends Application {

    // ============================== app 关闭事件监听 ==============================

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                FrameService.getInstance().exit();
            }
        });
    }


    // ============================== app 关闭事件监听 end==============================

    /**
     * 启动应用程序 (入口)
     *
     * @param args args
     */
    public static void main(String[] args) {
        Application.launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        // ====================== 加载初始化 ======================
        InitApplication.initApp();
        // ====================== 加载界面 ======================
        Platform.setImplicitExit(false);
        FrameService.getInstance().openWindow(FXPageEnum.登录页, primaryStage);
        log.info("QsBeanfun 启动成功..");
        ThreadPoolManager.execute(() -> UpdateManager.getInstance().verifyAppVersion(true));
    }


}
