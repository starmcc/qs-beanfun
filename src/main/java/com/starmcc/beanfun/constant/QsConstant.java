package com.starmcc.beanfun.constant;

import com.starmcc.beanfun.entity.client.Account;
import com.starmcc.beanfun.entity.client.BeanfunModel;
import com.starmcc.beanfun.entity.model.ConfigModel;
import com.starmcc.beanfun.utils.SystemTools;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;


/**
 * 全局常量
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public class QsConstant {

    private static final Properties DATA_PROS;
    public static final String ENV;
    public static final String APP_VERSION;
    public static final String APP_NAME;
    public static final String GITHUB_URL;
    public static final String GITHUB_API_LATSET;
    public static final String GITEE_URL;
    public static final String GITEE_API_LATSET;
    public static final String PATH_APP = System.getProperties().getProperty("user.dir");
    public static final String PATH_APP_PLUGINS = PATH_APP + "\\qs-data\\";
    public static final String PATH_APP_CONFIG = PATH_APP_PLUGINS + "config.json";
    public static final JFXStageData JFX_STAGE_DATA = new JFXStageData();
    public static TrayIcon trayIcon;
    public static ConfigModel config;
    public static BigDecimal currentRateChinaToTw = new BigDecimal("4.5");
    public static BeanfunModel beanfunModel;
    public static Account nowAccount;

    static {
        DATA_PROS = new Properties();
        InputStream in = null;
        try {
            in = QsConstant.class.getClassLoader().getResourceAsStream("data.properties");
            DATA_PROS.load(in);
        } catch (Exception e) {
            log.error("异常 error={}", e.getMessage(), e);
        } finally {
            SystemTools.close(in);
        }
        APP_VERSION = DATA_PROS.getProperty("app.version");
        APP_NAME = DATA_PROS.getProperty("app.name");
        ENV = DATA_PROS.getProperty("app.env", "prod");
        GITHUB_URL = DATA_PROS.getProperty("github.url");
        GITHUB_API_LATSET = DATA_PROS.getProperty("github.api.latest");
        GITEE_URL = DATA_PROS.getProperty("gitee.url");
        GITEE_API_LATSET = DATA_PROS.getProperty("gitee.api.latest");
    }

    /**
     * 插件枚举
     *
     * @author starmcc
     * @date 2022/09/24
     */
    @Getter
    public static enum PluginEnum {
        LOCALE_REMULATOR(PATH_APP_PLUGINS + "Locale_Remulator", "bin/Locale_Remulator.zip", "LRProc.exe"),
        MAPLESTORY_EMULATOR(PATH_APP_PLUGINS + "MapleStoryEmulator", "bin/MapleStoryEmulator.zip", "MapleStoryEmulator.exe"),
        WAR_ALLIANCE_HTML(PATH_APP_PLUGINS + "WarAllianceHtml", "bin/WarAllianceHtml.zip", "index.htm"),

        ;

        private final String targetPath;
        private final String sourcePath;
        private final String mainPath;

        PluginEnum(String targetPath, String sourcePath, String mainPath) {
            this.targetPath = targetPath;
            this.sourcePath = sourcePath;
            this.mainPath = this.targetPath + "\\" + mainPath;
        }

        public void run() {
            try {
                Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler file://" + this.getMainPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
