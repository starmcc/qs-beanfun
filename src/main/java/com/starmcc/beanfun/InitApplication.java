package com.starmcc.beanfun;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.model.ConfigModel;
import com.starmcc.beanfun.handler.LocaleRemulatorHandler;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.manager.UpdateManager;
import com.starmcc.beanfun.utils.FileTools;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.io.File;

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
            StringBuffer msg = new StringBuffer();
            msg.append("读取配置异常,请检查以下路径文件,尝试删除该文件重新打开!\n");
            msg.append(QsConstant.PATH_APP_CONFIG);
            FrameManager.getInstance().messageMaster(msg.toString(), Alert.AlertType.ERROR);
            FrameManager.getInstance().exit();
            return;
        }
        // 写入依赖文件
        for (QsConstant.PluginEnum resource : QsConstant.PluginEnum.values()) {
            File file = new File(resource.getTargetPath());
            if (resource == QsConstant.PluginEnum.LOCALE_REMULATOR) {
                boolean b = FileTools.deleteFolder(new File(resource.getTargetPath()));
                log.info("重置LR文件 {}", b);
            }
            if (file.exists()) {
                continue;
            }
            FileTools.unzipResourceFile(resource);
        }
        // 自动检查更新
        if (BooleanUtils.isTrue(QsConstant.config.getCheckAppUpdate())) {
            ThreadPoolManager.execute(() -> UpdateManager.getInstance().verifyAppVersion(true));
        }

        // 修改LR配置文件
        ConfigModel.LRConfig lrConfig = QsConstant.config.getLrConfig();
        LocaleRemulatorHandler.settingHookInput(lrConfig.getHookInput());
    }


}
