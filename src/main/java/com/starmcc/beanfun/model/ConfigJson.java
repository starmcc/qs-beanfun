package com.starmcc.beanfun.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConfigJson {

    private Boolean recordActPwd;
    private Boolean passInput;
    private String gamePath;

    private List<ActPwd> actPwds;

    public ConfigJson() {
        this.recordActPwd = false;
        this.passInput = false;
        this.gamePath = "";
        this.actPwds = new ArrayList<>();
    }

    @Data
    public static class ActPwd {
        private String act;
        private String pwd;

    }

}
