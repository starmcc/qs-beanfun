package com.starmcc.beanfun.client;

import com.starmcc.beanfun.client.impl.TwQrCodeClientImpl;
import com.starmcc.beanfun.entity.client.BeanfunQrCodeResult;
import com.starmcc.beanfun.entity.client.BeanfunStringResult;

import java.util.Objects;

/**
 * 二维码客户端
 *
 * @author starmcc
 * @date 2022/09/21
 */
public abstract class QrCodeClient {

    private static QrCodeClient qrCodeClient = null;


    /**
     * 运行
     *
     * @return {@link QrCodeClient}
     */
    public static QrCodeClient run() {
        if (Objects.isNull(qrCodeClient)) {
            qrCodeClient = new TwQrCodeClientImpl();
        }
        return qrCodeClient;
    }

    /**
     * 获取二维码图片地址
     *
     * @return {@link String}
     * @throws Exception 异常
     */
    public abstract BeanfunQrCodeResult getQrCodeImage() throws Exception;


    /**
     * 验证二维码是否扫码成功
     *
     * @param strEncryptData 二维码凭据
     * @return int 1=成功 0=等待 2=失效 -1=异常
     * @throws Exception 异常
     */
    public abstract int verifyQrCodeSuccess(String strEncryptData) throws Exception;


    /**
     * 二维码登录
     *
     * @param sessionKey 会话密钥
     * @return {@link BeanfunStringResult}
     * @throws Exception 异常
     */
    public abstract BeanfunStringResult login(String sessionKey) throws Exception;
}
