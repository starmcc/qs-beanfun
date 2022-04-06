package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.constant.QsConstant;
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


    public static void runGame(String gamePath, String accountId, String password) {
        if (BooleanUtils.isTrue(QsConstant.config.getPassInput())
                && StringUtils.isNotBlank(accountId)
                && StringUtils.isNotBlank(password)) {
            gamePath = gamePath + " tw.login.maplestory.gamania.com 8484 BeanFun " + accountId + " " + password;
        }
        String lrExe = QsConstant.APP_PATH + "LRProc.exe";
        String lrDll = QsConstant.APP_PATH + "LRHookx64.dll";
        String[] cmd = {"cmd", "/c", lrExe + " tms \"" + lrDll + "\" " + gamePath};
        log.info("执行命令 runGame = {}", cmd[2]);

        try {
            Runtime.getRuntime().exec(cmd, null, new File(gamePath).getParentFile());
        } catch (Exception e) {
            log.error("运行异常 e={}", e.getMessage(), e);
        }
    }


}
