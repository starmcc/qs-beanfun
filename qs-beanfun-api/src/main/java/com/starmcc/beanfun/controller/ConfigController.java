package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.config.PassToken;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.handler.AutoLunShaoHandler;
import com.starmcc.beanfun.model.ConfigJson;
import com.starmcc.beanfun.model.ConfigModel;
import com.starmcc.beanfun.utils.AesUtil;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.FileUtils;
import com.starmcc.qmframework.body.QmBody;
import com.starmcc.qmframework.controller.QmResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class ConfigController {

    @Autowired
    private AutoLunShaoHandler autoLunShaoHandler;

    @PassToken
    @GetMapping("/config/reset")
    public String reset() {
        QsConstant.config = new ConfigJson();
        FileUtils.writeConfig(QsConstant.config);
        return QmResult.success();
    }

    @PassToken
    @GetMapping("/config/get_configs")
    public String getConfigs() {
        long runTime = autoLunShaoHandler.getRunTime();
        return QmResult.success(new ConfigModel(runTime));
    }

    @PassToken
    @GetMapping("/config/get_accounts")
    public String getAccounts() {
        List<ConfigJson.ActPwd> actPwds = QsConstant.config.getActPwds();
        final String key = DataTools.getComputerUniqueId();
        // 解密
        actPwds.forEach(item -> {
            try {
                if (item.getAct().indexOf("@") != -1) {
                    // 是明文，不处理
                    return;
                }
                String act = AesUtil.dncode(key, item.getAct());
                String pwd = AesUtil.dncode(key, item.getPwd());
                item.setAct(act);
                item.setPwd(pwd);
            } catch (Exception e) {
                log.error("解密异常 e={}", e.getMessage(), e);
            }
        });
        return QmResult.success(actPwds);
    }


    @PassToken
    @PostMapping("/config/set_configs")
    public String setConfigs(@QmBody ConfigModel config) {
        QsConstant.config.writeConfig(config);
        FileUtils.writeConfig(QsConstant.config);
        return QmResult.success();
    }


}
