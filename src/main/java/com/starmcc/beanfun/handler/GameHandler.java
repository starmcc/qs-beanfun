package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.WindowManager;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * 游戏处理程序
 *
 * @author starmcc
 * @date 2022/03/21
 */
@Slf4j
public class GameHandler {


    /**
     * 运行游戏
     *
     * @param gamePath            游戏路径
     * @param accountId           帐户id
     * @param password            密码
     * @param killStartPalyWindow 关闭开始窗口
     */
    public static void runGame(String gamePath, String accountId, String password) {
        if (BooleanUtils.isTrue(QsConstant.config.getPassInput())
                && StringUtils.isNotBlank(accountId)
                && StringUtils.isNotBlank(password)) {
            gamePath = gamePath + " tw.login.maplestory.gamania.com 8484 BeanFun " + accountId + " " + password;
        }

        String lrExe = QsConstant.PluginEnum.LOCALE_REMULATOR.getMainPath();
        String lrDll = QsConstant.PluginEnum.LOCALE_REMULATOR.getTargetPath() + "\\LRHookx64.dll";
        String[] cmd = {"cmd", "/c", lrExe + " tms \"" + lrDll + "\" " + gamePath};
        log.info("执行命令 runGame = {}", cmd[2]);

        try {
            Runtime.getRuntime().exec(cmd, null, new File(gamePath).getParentFile());
            if (BooleanUtils.isTrue(QsConstant.config.getKillStartPalyWindow())) {
                WindowManager.getInstance().closeMapleStoryStart();
            }
            if (BooleanUtils.isTrue(QsConstant.config.getKillGamePatcher())) {
                WindowManager.getInstance().stopAutoPatcher(process -> {
                    FrameManager.getInstance().message("当前游戏版本不是最新版本\n已为您阻止自动更新!", Alert.AlertType.INFORMATION);
                });
            }
        } catch (Exception e) {
            log.error("运行异常 e={}", e.getMessage(), e);
        }
    }


}
