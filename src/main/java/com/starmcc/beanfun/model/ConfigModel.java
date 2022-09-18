package com.starmcc.beanfun.model;

import com.starmcc.beanfun.constant.QsConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 配置
 *
 * @author starmcc
 * @date 2022/03/27
 */
@Data
public class ConfigModel implements Serializable {
    private static final long serialVersionUID = -7597847958912457122L;

    private Boolean recordActPwd;
    private String gamePath;
    private Boolean loginStatus;
    private Integer lunHuiKey;
    private Integer ranShaoKey;
    private Boolean killStartPalyWindow;
    private Boolean killGamePatcher;
    private Boolean passInput;
    private Integer loginType;
    private String version;
    private Long lunShaoRunTime;

    public ConfigModel(){}


}
