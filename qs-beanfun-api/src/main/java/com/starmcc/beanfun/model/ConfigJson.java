package com.starmcc.beanfun.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConfigJson {

    private Boolean recordActPwd;
    private String gamePath;
    private Integer lunHuiKey;
    private Integer ranShaoKey;
    private Boolean killStartPalyWindow;
    private Boolean killGamePatcher;
    private Boolean passInput;
    private Integer loginType;

    private List<ActPwd> actPwds;

    public ConfigJson() {
        this.recordActPwd = false;
        this.gamePath = "";
        this.actPwds = new ArrayList<>();
        // 默认 B
        this.lunHuiKey = 66;
        // 默认 N
        this.ranShaoKey = 78;
        // 默认 关闭
        this.killStartPalyWindow = true;
        // 默认 新港号登录
        this.loginType = LoginType.TypeEnum.HK.getType();
        // 默认 关闭
        this.killGamePatcher = true;
        // 默认 不置顶
        this.passInput = false;
    }

    public void writeConfig(ConfigModel config) {
        this.recordActPwd = config.getRecordActPwd();
        this.gamePath = config.getGamePath();
        this.loginType = config.getLoginType();
        this.lunHuiKey = config.getLunHuiKey();
        this.ranShaoKey = config.getRanShaoKey();
        this.killStartPalyWindow = config.getKillStartPalyWindow();
        this.killGamePatcher = config.getKillGamePatcher();
        this.passInput = config.getPassInput();
    }

    @Data
    public static class ActPwd {
        private String act;
        private String pwd;

    }

}
