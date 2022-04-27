package com.starmcc.beanfun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.client.UpdateClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.ConfigJson;
import com.starmcc.beanfun.model.UpdateModel;
import com.starmcc.beanfun.utils.ConfigFileUtils;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.FrameUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;


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
                closeWin();
            }
        });
    }

    static void closeWin() {
        try {
            BeanfunClient.uninitialize();
        } catch (Exception e) {
            log.error("退出时发生异常 e={}", e.getMessage(), e);
        }
    }


    // ============================== app 关闭事件监听 end==============================

    /**
     * 启动应用程序
     *
     * @param args args
     */
    public static void main(String[] args) {
        Application.launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        initApp();
        // 加载界面
        Platform.setImplicitExit(false);
        FrameUtils.openWindow(QsConstant.Page.登录页面, (jfxStage) -> {
            jfxStage.setCloseEvent(() -> Platform.exit());
            jfxStage.setMiniSupport(false);
            QsConstant.loginJFXStage = jfxStage;
        });
        log.info("QsBeanfun 启动成功..");
        FrameUtils.executeThread(() -> {
            UpdateModel versionModel = UpdateClient.getInstance().getVersionModel();
            Platform.runLater(() -> {
                if (versionModel.getState() == UpdateModel.State.有新版本) {
                    if (QsConstant.confirmDialog("是否前往更新？", versionModel.getTips())) {
                        FrameUtils.openWebUrl("https://github.com/starmcc/qs-beanfun/releases");
                    }
                }
            });
        });
    }


    // ====================== 加载初始化 ======================


    private static void initApp() {
        QsConstant.config = loadConfig();
        // 释放LR所需文件
        copyResourceFile(QsConstant.Resources.LR_PROC_EXE);
        copyResourceFile(QsConstant.Resources.LR_HOOKX64_DLL);
        copyResourceFile(QsConstant.Resources.LR_HOOKX32_DLL);
        copyResourceFile(QsConstant.Resources.LR_CONFIG_XML);
        copyResourceFile(QsConstant.Resources.LR_SUB_MENUS_DLL);
        copyResourceFile(QsConstant.Resources.REGISTER_REG);

    }

    /**
     * 加载配置
     *
     * @return {@link ConfigJson}
     */
    private static ConfigJson loadConfig() {
        String dbJson = ConfigFileUtils.readConfig();
        ConfigJson configJson = new ConfigJson();
        if (StringUtils.isNotEmpty(dbJson)) {
            configJson = JSON.parseObject(dbJson, new TypeReference<ConfigJson>() {
            });
        }
        if (DataTools.collectionIsEmpty(configJson.getActPwds())) {
            configJson.setActPwds(new ArrayList<>());
        }
        ConfigFileUtils.writeConfig(configJson);
        return configJson;
    }


    /**
     * 资源文件生成(无限重刷)
     *
     * @param rconstantResource rconstant资源
     */
    private static void copyResourceFile(QsConstant.Resources rconstantResource) {
        try {
            File targetFile = new File(rconstantResource.getTargetPath());
            // 创建上级目录
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            // 如果文件存在，则删除文件
            if (targetFile.exists()) {
                if (targetFile.delete()) {
                    copyResourceFile(rconstantResource);
                    return;
                }
            }
            InputStream resourceAsStream = QsBeanfunApplication.class.getClassLoader().getResourceAsStream(rconstantResource.getSourcePath());
            Files.copy(resourceAsStream, targetFile.toPath());
        } catch (Exception e) {
            log.error("输出文件发生异常 e={}", e.getMessage(), e);
        }
    }


}
