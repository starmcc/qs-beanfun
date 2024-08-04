package com.starmcc.beanfun.entity.client;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * beanfun二维码结果
 *
 * @author starmcc
 * @date 2022/10/01
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BeanfunQrCodeResult extends BeanfunResult implements Serializable {

    private static final long serialVersionUID = 2021437261083355340L;

    private String sessionKey;
    private String strEncryptData;
    private String strEncryptBCDOData;
    private String qrImageUrl;

}
