package com.starmcc.beanfun.constant;

import com.starmcc.beanfun.entity.client.Account;
import com.starmcc.beanfun.entity.client.BeanfunModel;
import com.starmcc.beanfun.entity.model.ConfigModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;


/**
 * 全局常量
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public class QsConstant {
    public static boolean DEV = false;
    public static final String APP_VERSION = "4.1.3";
    public static final String PATH_APP = System.getProperties().getProperty("user.dir");
    public static final String PATH_APP_PLUGINS = PATH_APP + "\\qs-data\\";
    public static final String PATH_APP_CONFIG = PATH_APP_PLUGINS + "config.json";
    public static final String APP_NAME = "QsBeanfun";
    public static final String UPDATE_API_GITHUB = "https://api.github.com/repos/starmcc/qs-beanfun/releases/latest";
    public static final String UPDATE_API_SERVER = "https://update.qstms.com/qs-beanfun/version.json";
    public static final String GITHUB_URL = "https://github.com/starmcc/qs-beanfun";
    public static final JFXStageData JFX_STAGE_DATA = new JFXStageData();
    public static TrayIcon trayIcon;
    public static ConfigModel config;
    public static BigDecimal currentRateChinaToTw = new BigDecimal("4.5");
    public static BeanfunModel beanfunModel;
    public static Account nowAccount;

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
