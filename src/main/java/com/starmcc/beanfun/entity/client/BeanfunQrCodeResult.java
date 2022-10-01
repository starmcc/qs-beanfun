package com.starmcc.beanfun.entity.client;

import lombok.Data;

import java.io.Serializable;

/**
 * beanfun二维码结果
 *
 * @author starmcc
 * @date 2022/10/01
 */
@Data
public class BeanfunQrCodeResult extends AbstractBeanfunResult implements Serializable {

    private static final long serialVersionUID = 2021437261083355340L;

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
