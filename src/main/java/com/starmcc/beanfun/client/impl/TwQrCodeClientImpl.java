package com.starmcc.beanfun.client.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.client.QrCodeClient;
import com.starmcc.beanfun.entity.client.*;
import com.starmcc.beanfun.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.Cookie;

import java.util.List;

/**
 * 台湾二维码客户端实现
 *
 * @author starmcc
 * @date 2022/09/23
 */
@Slf4j
public class TwQrCodeClientImpl extends QrCodeClient {


    @Override
    public BeanfunQrCodeResult getQrCodeImage() throws Exception {
        BeanfunQrCodeResult result = new BeanfunQrCodeResult();

        BeanfunStringResult sessionKeyResult = BeanfunClient.run().getSessionKey();

        if (!sessionKeyResult.isSuccess()) {
            result.setCode(sessionKeyResult.getCode());
            result.setMsg(sessionKeyResult.getMsg());
            return result;
        }
        result.setSessionKey(sessionKeyResult.getData());
        // 加载二维码表单
        String url = "https://tw.newlogin.beanfun.com/login/qr_form.aspx";
        ReqParams params = ReqParams.getInstance().addParam("skey", sessionKeyResult.getData());
        QsHttpResponse response = HttpClient.getInstance().get(url, params);
        if (!response.getSuccess()) {
            log.error("二维码加载异常-{}", 1);
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }

        // 获取二维码图片
        url = "https://tw.newlogin.beanfun.com/generic_handlers/get_qrcodeData.ashx";
        params = ReqParams.getInstance().addParam("skey", sessionKeyResult.getData());
        response = HttpClient.getInstance().get(url, params);
        if (!response.getSuccess()) {
            log.error("二维码加载异常-{}", 2);
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        JSONObject jsonObject = JSON.parseObject(response.getContent());
        if (jsonObject.getIntValue("intResult") != 1) {
            log.error("二维码加载异常-{}", 3);
            return result.error(BeanfunResult.CodeEnum.BEANFUN_ERROR);
        }

        String strEncryptData = jsonObject.getString("strEncryptData");
        String strEncryptBCDOData = jsonObject.getString("strEncryptBCDOData");
        result.setStrEncryptData(strEncryptData);
        result.setStrEncryptBCDOData(strEncryptBCDOData);
        if (StringUtils.isBlank(strEncryptData)) {
            log.error("二维码加载异常-{}", 4);
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }

        String imgUrl = "https://tw.newlogin.beanfun.com/qrhandler.ashx?u=https://beanfunstor.blob.core.windows.net/redirect/appCheck.html?url=beanfunapp://Q/gameLogin/gtw/"
                + strEncryptData;
        result.setQrImageUrl(imgUrl);
        return result;
    }

    @Override
    public int verifyQrCodeSuccess(String strEncryptData) throws Exception {
        String url = "https://tw.newlogin.beanfun.com/generic_handlers/CheckLoginStatus.ashx";
        ReqParams params = ReqParams.getInstance().addParam("status", strEncryptData);
        QsHttpResponse response = HttpClient.getInstance().post(url, params);
        if (!response.getSuccess()) {
            log.error("验证二维码异常-{}", 1);
            return -1;
        }
        JSONObject jsonObject = JSON.parseObject(response.getContent());
        return jsonObject.getIntValue("Result");
    }

    @Override
    public BeanfunStringResult login(String sessionKey) throws Exception {
        BeanfunStringResult result = new BeanfunStringResult();
        String url = "https://tw.newlogin.beanfun.com/login/qr_step2.aspx";
        ReqParams params = ReqParams.getInstance()
                .addHeader("Referer", "https://tw.newlogin.beanfun.com/login/qr_form.aspx?skey=" + sessionKey)
                .addParam("skey", sessionKey);

        QsHttpResponse response = HttpClient.getInstance().get(url, params);
        if (!response.getSuccess()) {
            log.error("二维码登录异常-{}", 1);
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }

        String content = response.getContent();
        List<List<String>> dataList = RegexUtils.regex(RegexUtils.Constant.TW_QRCODE_AKEY, content);
        String aKey = RegexUtils.getIndex(0, 1, dataList);
        String authKey = RegexUtils.getIndex(0, 2, dataList);

        if (StringUtils.isBlank(aKey) || StringUtils.isBlank(authKey)) {
            log.error("二维码登录异常-{}", 2);
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }

        url = "https://tw.newlogin.beanfun.com/login/final_step.aspx";

        params = ReqParams.getInstance()
                .addParam("akey", aKey)
                .addParam("authkey", authKey)
                .addParam("bfapp", "1");


        response = HttpClient.getInstance().get(url, params);

        if (!response.getSuccess()) {
            log.error("二维码登录异常-{}", 3);
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }

        List<Cookie> cookies = HttpClient.getInstance().getCookieStore().getCookies();
        String bfWebToken = null;
        for (Cookie cookie : cookies) {
            if (StringUtils.equals(cookie.getDomain(), "beanfun.com") && StringUtils.equals(cookie.getName(), "bfWebToken")) {
                bfWebToken = cookie.getValue();
                break;
            }
        }

        url = "https://tw.beanfun.com/beanfun_block/bflogin/return.aspx";
        params = ReqParams.getInstance()
                .addParam("SessionKey", sessionKey)
                .addParam("AuthKey", aKey)
                .addParam("ServiceCode", "")
                .addParam("ServiceRegion", "")
                .addParam("ServiceAccountSN", "0");

        HttpClient.getInstance().post(url, params);
        result.setData(bfWebToken);
        return result.success();
    }
}
