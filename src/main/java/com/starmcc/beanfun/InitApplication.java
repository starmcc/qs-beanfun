package com.starmcc.beanfun;

import com.starmcc.beanfun.client.UpdateClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.UpdateModel;
import com.starmcc.beanfun.thread.ThreadPoolManager;
import com.starmcc.beanfun.utils.FileTools;
import com.starmcc.beanfun.windows.FrameService;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;

/**
 * 初始化应用程序
 *
 * @author starmcc
 * @date 2022/09/21
 */
@Slf4j
public class InitApplication {


    /**
     * 初始化应用程序
     */
    public static void initApp() {
        try {
            QsConstant.config = FileTools.readConfig();
        } catch (Exception e) {
            log.error("读取配置异常 e={}", e.getMessage(), e);
            StringBuilder msg = new StringBuilder();
            msg.append("读取配置异常,请检查以下路径文件,尝试删除该文件重新打开!\n");
            msg.append(QsConstant.PATH_APP_CONFIG);
            FrameService.getInstance().runLater(() -> QsConstant.alert(msg.toString(), Alert.AlertType.ERROR));
            Platform.exit();
            System.exit(0);
            return;
        }
        // 释放LR所需文件
        FileTools.copyResourceFile(QsConstant.Resources.LR_PROC_EXE);
        FileTools.copyResourceFile(QsConstant.Resources.LR_HOOKX64_DLL);
        FileTools.copyResourceFile(QsConstant.Resources.LR_HOOKX32_DLL);
        FileTools.copyResourceFile(QsConstant.Resources.LR_CONFIG_XML);
        FileTools.copyResourceFile(QsConstant.Resources.LR_SUB_MENUS_DLL);
    }


    public static void checkVersion() {
        ThreadPoolManager.execute(() -> {
            UpdateModel versionModel = UpdateClient.getInstance().getVersionModel();
            if (versionModel.getState() != UpdateModel.State.有新版本) {
                return;
            }
            FrameService.getInstance().runLater(() -> {
                if (!QsConstant.confirmDialog("是否前往更新？", versionModel.getUpdateText())) {
                    return;
                }
                FrameService.getInstance().openWebUrl("https://github.com/starmcc/qs-beanfun/releases");
            });
        });
    }


}
