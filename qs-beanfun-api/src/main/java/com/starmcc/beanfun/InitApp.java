package com.starmcc.beanfun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.ConfigJson;
import com.starmcc.beanfun.model.QsTary;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.FileUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * 初始化应用程序
 *
 * @author starmcc
 * @date 2022/06/10
 */
@Slf4j
public class InitApp extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        initApp();
        QsTary.init();
        Platform.setImplicitExit(false);
        try {
            Runtime.getRuntime().exec("cmd /c start " + QsConstant.serverAddress);
        } catch (Exception e) {
            log.error("异常 e={}", e.getMessage(), e);
        }
    }


    private static void initApp() {
        try {
            QsConstant.config = loadConfig();
        } catch (Exception e) {
            log.error("读取配置异常 e={}", e.getMessage(), e);
            System.exit(0);
            return;
        }
    }

    /**
     * 加载配置
     *
     * @return {@link ConfigJson}
     */
    private static ConfigJson loadConfig() {
        String dbJson = FileUtils.readConfig();
        ConfigJson configJson = new ConfigJson();
        if (StringUtils.isNotEmpty(dbJson)) {
            configJson = JSON.parseObject(dbJson, new TypeReference<ConfigJson>() {
            });
        }
        if (DataTools.collectionIsEmpty(configJson.getActPwds())) {
            configJson.setActPwds(new ArrayList<>());
        }
        FileUtils.writeConfig(configJson);
        return configJson;
    }

}
