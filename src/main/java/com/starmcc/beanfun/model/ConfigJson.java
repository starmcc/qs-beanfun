package com.starmcc.beanfun.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConfigJson {

    private Boolean recordActPwd;
    private Boolean passInput;
    private String gamePath;
    private Integer lunHuiKey;
    private Integer ranShaoKey;
    private Boolean killStartPalyWindow;
    private Integer loginType;

    private List<ActPwd> actPwds;

    public ConfigJson() {
        this.recordActPwd = false;
        this.passInput = false;
        this.gamePath = "";
        this.actPwds = new ArrayList<>();
        // 默认 B
        this.lunHuiKey = 66;
        // 默认 N
        this.ranShaoKey = 78;
        // 默认 不关闭
        this.killStartPalyWindow = false;
        // 默认 新港号登录
        this.loginType = LoginType.TypeEnum.HK_NEW.getType();
    }

    @Data
    public static class ActPwd {
        private String act;
        private String pwd;

    }

}
