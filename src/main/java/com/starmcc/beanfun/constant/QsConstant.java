package com.starmcc.beanfun.constant;

import com.starmcc.beanfun.model.ConfigModel;
import com.starmcc.beanfun.model.client.Account;
import com.starmcc.beanfun.model.client.BeanfunModel;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.math.BigDecimal;


/**
 * 全局常量
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public class QsConstant {
    public static final String APP_VERSION = "4.0.0";
    public static final Integer APP_VERSION_INT = 400;
    public static final String PATH_APP = System.getProperties().getProperty("user.dir");
    public static final String PATH_PLUGINS = PATH_APP + "\\qs-data\\";
    public static final String PATH_APP_CONFIG = PATH_PLUGINS + "config.json";
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
     * 资源文件枚举
     *
     * @author starmcc
     * @date 2022/03/22
     */
    public static enum Resources {
        LR_PROC_EXE(PATH_PLUGINS + "lr/LRProc.exe", "lib/lr/LRProc.exe"), LR_HOOKX64_DLL(PATH_PLUGINS + "lr/LRHookx64.dll", "lib/lr/LRHookx64.dll"), LR_HOOKX32_DLL(PATH_PLUGINS + "lr/LRHookx32.dll", "lib/lr/LRHookx32.dll"), LR_CONFIG_XML(PATH_PLUGINS + "lr/LRConfig.xml", "lib/lr/LRConfig.xml"), LR_SUB_MENUS_DLL(PATH_PLUGINS + "lr/LRSubMenus.dll", "lib/lr/LRSubMenus.dll"),

        ;

        private final String targetPath;
        private final String sourcePath;

        Resources(String targetPath, String sourcePath) {
            this.targetPath = targetPath;
            this.sourcePath = sourcePath;
        }

        public String getTargetPath() {
            return targetPath;
        }

        public String getSourcePath() {
            return sourcePath;
        }
    }


}
