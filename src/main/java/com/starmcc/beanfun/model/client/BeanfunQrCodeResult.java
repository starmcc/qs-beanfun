package com.starmcc.beanfun.model.client;

import lombok.Data;

import java.io.Serializable;

@Data
public class BeanfunQrCodeResult extends AbstractBeanfunResult implements Serializable {

    private String sessionKey;
    private String strEncryptData;
    private String strEncryptBCDOData;
    private String qrImageUrl;

    public BeanfunQrCodeResult() {
        this(CodeEnum.SUCCESS);
    }

    protected BeanfunQrCodeResult(CodeEnum codeEnum) {
        super(codeEnum);
    }

    public static BeanfunQrCodeResult error(CodeEnum codeEnum) {
        return new BeanfunQrCodeResult(codeEnum);
    }

}
