package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.utils.FileUtils;
import com.starmcc.beanfun.windows.MapleStoryAPIService;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 游戏处理程序
 *
 * @author starmcc
 * @date 2022/03/21
 */
@Slf4j
@Component
public class GameHandler {

    private static final int CLOSE_CODE = 0x10;

    public void runGame(String gamePath, String accountId, String password) {
        if (StringUtils.isNotBlank(accountId) && StringUtils.isNotBlank(password)) {
            gamePath = gamePath + " tw.login.maplestory.gamania.com 8484 BeanFun " + accountId + " " + password;
        }

        // 初始化环境 释放LR所需文件
        FileUtils.copyResourceFile(QsConstant.Resources.LR_PROC_EXE);
        FileUtils.copyResourceFile(QsConstant.Resources.LR_HOOKX64_DLL);
        FileUtils.copyResourceFile(QsConstant.Resources.LR_HOOKX32_DLL);
        FileUtils.copyResourceFile(QsConstant.Resources.LR_CONFIG_XML);
        FileUtils.copyResourceFile(QsConstant.Resources.LR_SUB_MENUS_DLL);

        String lrExe = QsConstant.APP_PATH + "LRProc.exe";
        String lrDll = QsConstant.APP_PATH + "LRHookx64.dll";
        String[] cmd = {"cmd", "/c", lrExe + " tms \"" + lrDll + "\" " + gamePath};
        log.info("执行命令 runGame = {}", cmd[2]);

        try {
            Runtime.getRuntime().exec(cmd, null, new File(gamePath).getParentFile());
            MapleStoryAPIService apiService = MapleStoryAPIService.getInstance();
            if (BooleanUtils.isTrue(QsConstant.config.getKillStartPalyWindow())) {
                apiService.closeMapleStoryStart();
            }
            if (BooleanUtils.isTrue(QsConstant.config.getKillGamePatcher())) {
                apiService.stopAutoPatcher((process ->
                        Platform.runLater(() -> QsConstant.alert("当前游戏版本不是最新版本,已为您阻止自动更新!", Alert.AlertType.INFORMATION))
                ));
            }
        } catch (Exception e) {
            log.error("运行异常 e={}", e.getMessage(), e);
        }
    }


}
