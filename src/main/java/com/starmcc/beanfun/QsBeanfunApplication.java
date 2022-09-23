package com.starmcc.beanfun;

import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.manager.FrameManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;


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
                FrameManager.getInstance().exit();
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
        for (String arg : args) {
            if (StringUtils.equals(args[0], "dev")){
                QsConstant.DEV = true;
                break;
            }
        }
        Application.launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        // ====================== 加载初始化 ======================
        InitApplication.initApp();
        // ====================== 加载界面 ======================
        Platform.setImplicitExit(false);
        FrameManager.getInstance().openWindow(FXPageEnum.登录页, primaryStage);
        log.info("QsBeanfun" + QsConstant.APP_VERSION + "启动成功..");
    }


}
