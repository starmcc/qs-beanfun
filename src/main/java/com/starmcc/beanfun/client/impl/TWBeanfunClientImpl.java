package com.starmcc.beanfun.client.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.model.ReqParams;
import com.starmcc.beanfun.model.client.*;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
public class TWBeanfunClientImpl extends BeanfunClient {

    @Override
    public String getSessionKey() throws Exception {
        String url = "https://tw.beanfun.com/beanfun_block/bflogin/default.aspx";
        ReqParams params = ReqParams.getInstance().addParam("service", "999999_T0");
        QsHttpResponse qsHttpResponse = HttpClient.getInstance().get(url, params);
        if (!qsHttpResponse.getSuccess()) {
            return null;
        }

        String content = qsHttpResponse.getContent();

        if (StringUtils.indexOf(content, "IP已自動被系統鎖定") >= 0) {
            return "IP锁定";
        }

        List<List<String>> dataList = RegexUtils.regex(RegexUtils.Constant.TW_SESSION_KEY, content);

        return RegexUtils.getIndex(0, 1, dataList);
    }

    @Override
    public BeanfunStringResult login(String account, String password, Consumer<Double> process) throws Exception {
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.ACT_PWD_IS_NULL);
        }
        // 1. 请求获取SessionKey
        String sessionKey = this.getSessionKey();
        process.accept(0.2);
        if (StringUtils.isBlank(sessionKey)) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.OTP_GET_EMPTY);
        } else if (StringUtils.equals(sessionKey, "IP锁定")) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.IP_BANK);
        }


        // 2. 获取签名信息
        String url = "https://tw.newlogin.beanfun.com/login/id-pass_form.aspx";
        ReqParams params = ReqParams.getInstance().addParam("skey", sessionKey);
        QsHttpResponse response = HttpClient.getInstance().get(url, params);
        process.accept(0.4);
        if (!response.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }

        String content = response.getContent();

        List<List<String>> dataList = new ArrayList<>();
        dataList = RegexUtils.regex(RegexUtils.Constant.TW_VIEWSTATE, content);
        String viewstate = RegexUtils.getIndex(0, 1, dataList);
        dataList = RegexUtils.regex(RegexUtils.Constant.TW_EVENTVALIDATION, content);
        String eventvalidation = RegexUtils.getIndex(0, 1, dataList);
        dataList = RegexUtils.regex(RegexUtils.Constant.TW_VIEWSTATEGENERATOR, content);
        String viewstateGenerator = RegexUtils.getIndex(0, 1, dataList);
        if (StringUtils.isEmpty(viewstate) || StringUtils.isEmpty(eventvalidation) || StringUtils.isEmpty(viewstateGenerator)) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.OTP_SIGN_GET_ERROR);
        }


        // 3. 登录接口 获取authKey
        params = ReqParams.getInstance()
                .addParam("__EVENTTARGET", "")
                .addParam("__EVENTARGUMENT", "")
                .addParam("__VIEWSTATE", viewstate)
                .addParam("__VIEWSTATEGENERATOR", viewstateGenerator)
                .addParam("__EVENTVALIDATION", eventvalidation)
                .addParam("t_AccountID", account)
                .addParam("t_Password", password)
                .addParam("btn_login", "登入");
        url = "https://tw.newlogin.beanfun.com/login/id-pass_form.aspx?skey=" + sessionKey;

        response = HttpClient.getInstance().post(url, params);
        process.accept(0.6);
        if (!response.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }

        content = response.getContent();

        dataList = RegexUtils.regex(RegexUtils.Constant.TW_LOGIN_ERROR_MSG, content);
        String errMsg = RegexUtils.getIndex(0, 1, dataList);
        if (StringUtils.isNotBlank(errMsg)) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.LOGIN_ERROR_MSG, errMsg);
        }

        dataList = RegexUtils.regex(RegexUtils.Constant.TW_LOGIN_AKEY, content);
        String aKey = RegexUtils.getIndex(0, 1, dataList);

        if (StringUtils.isBlank(aKey)) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.LOGIN_ERROR_MSG);
        }

        // 4. 通过akey请求获取bfWebToken
        params = ReqParams.getInstance().addParam("SessionKey", sessionKey)
                .addParam("AuthKey", aKey)
                .addParam("ServiceCode", "")
                .addParam("ServiceRegion", "")
                .addParam("ServiceAccountSN", "0");

        url = "https://tw.beanfun.com/beanfun_block/bflogin/return.aspx";
        response = HttpClient.getInstance().post(url, params);
        process.accept(0.8);
        if (!response.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String bfWebToken = this.getBfWebToken();
        if (StringUtils.isBlank(bfWebToken)) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }

        return BeanfunStringResult.success(bfWebToken);
    }

    @Override
    public BeanfunAccountResult getAccountList(String token) throws Exception {
        // 授权接口访问，携带cookies和token信息，查询账号列表
        BeanfunAccountResult actResult = new BeanfunAccountResult();
        String url = "https://tw.beanfun.com/beanfun_block/auth.aspx";
        ReqParams params = ReqParams.getInstance()
                .addParam("channel", "game_zone")
                .addParam("page_and_query", "game_start.aspx?service_code_and_region=610074_T9")
                .addParam("web_token", token);
        QsHttpResponse httpResponse = HttpClient.getInstance().get(url, params);
        if (!httpResponse.getSuccess()) {
            return BeanfunAccountResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String content = httpResponse.getContent();

        List<List<String>> dataList = RegexUtils.regex(RegexUtils.Constant.TW_ACCOUNT_MAX, content);
        String maxActNumberStr = RegexUtils.getIndex(0, 1, dataList);
        Integer maxActNumber = 0;
        if (StringUtils.isNotBlank(maxActNumberStr)) {
            maxActNumber = Integer.parseInt(maxActNumberStr);
        }
        actResult.setMaxActNumber(maxActNumber);

        dataList = RegexUtils.regex(RegexUtils.Constant.TW_ACCOUNT_LIST, content);
        if (DataTools.collectionIsEmpty(dataList)) {
            // 进阶认证校验
            dataList = RegexUtils.regex(RegexUtils.Constant.TW_CERT_VERIFY, content);
            String certStr = RegexUtils.getIndex(0, 1, dataList);
            if (StringUtils.indexOf(certStr, "進階認證") >= 0) {
                // 没有做进阶认证
                actResult.setCertStatus(false);
            }
            if (RegexUtils.test(RegexUtils.Constant.TW_CREATE_ACCOUNT, content)) {
                // 新账号，没有账号
                actResult.setNewAccount(true);
            }
            return actResult;
        }
        List<Account> accountList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            Account account = new Account();
            account.setStatus(RegexUtils.getIndex(i, 1, dataList) != "");
            account.setId(RegexUtils.getIndex(i, 2, dataList));
            account.setSn(RegexUtils.getIndex(i, 3, dataList));
            account.setName(RegexUtils.getIndex(i, 4, dataList));
            // 新港号需要手动查询创建时间
            Date accountCreateTime = this.getAccountCreateTime(account.getSn());
            account.setCreateTime(accountCreateTime);
            accountList.add(account);
        }
        actResult.setAccountList(accountList);
        return actResult;
    }

    @Override
    public BeanfunStringResult getDynamicPassword(Account account, String token) throws Exception {
        if (Objects.isNull(account) || StringUtils.isBlank(account.getId())) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.GET_DYNAMIC_PWD_ACT_INFO_EMPTY);
        }
        String url = "https://tw.beanfun.com/beanfun_block/game_zone/game_start_step2.aspx";
        ReqParams params = ReqParams.getInstance();
        params.addParam("service_code", "610074");
        params.addParam("service_region", "T9");
        params.addParam("sotp", account.getSn());
        Date d = new Date();
        String strDateTime = "" + d.getYear() + d.getMonth() + d.getDate() + d.getHours() + d.getMinutes() + d.getSeconds() + d.getMinutes();
        params.addParam("dt", strDateTime);

        QsHttpResponse httpResponse = HttpClient.getInstance().get(url, params);
        if (!httpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String content = httpResponse.getContent();
        List<List<String>> dataList = RegexUtils.regex(RegexUtils.Constant.TW_GET_PWD_OTP_KEY, content);
        String pollingKey = RegexUtils.getIndex(0, 1, dataList);

        if (Objects.isNull(account.getCreateTime())) {
            dataList = RegexUtils.regex(RegexUtils.Constant.TW_GET_SERVICE_CREATE_TIME, content);
            String time = RegexUtils.getIndex(0, 1, dataList);
            Date createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
            account.setCreateTime(createTime);
        }
        url = "https://tw.newlogin.beanfun.com/generic_handlers/get_cookies.ashx";
        httpResponse = HttpClient.getInstance().get(url);
        if (!httpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        content = httpResponse.getContent();
        dataList = RegexUtils.regex(RegexUtils.Constant.TW_GET_PWD_OTP_SECRET, content);
        String secret = RegexUtils.getIndex(0, 1, dataList);

        url = "https://tw.beanfun.com/beanfun_block/generic_handlers/record_service_start.ashx";
        params = ReqParams.getInstance()
                .addParam("service_code", "610074")
                .addParam("service_region", "T9")
                .addParam("service_account_id", account.getId())
                .addParam("sotp", account.getSn())
                .addParam("service_account_display_name", account.getName())
                .addParam("service_account_create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(account.getCreateTime()));

        httpResponse = HttpClient.getInstance().post(url, params);

        if (!httpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }

        content = httpResponse.getContent();

        JSONObject jsonObject = JSON.parseObject(content);
        if (Objects.isNull(jsonObject) || jsonObject.getIntValue("intResult") != 1) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }

        url = "https://tw.beanfun.com/beanfun_block/generic_handlers/get_webstart_otp.ashx";
        params = ReqParams.getInstance()
                .addParam("sn", pollingKey)
                .addParam("WebToken", token)
                .addParam("SecretCode", secret)
                .addParam("ppppp", "F9B45415B9321DB9635028EFDBDDB44B4012B05F95865CB8909B2C851CFE1EE11CB784F32E4347AB7001A763100D90768D8A4E30BCC3E80C")
                .addParam("ServiceCode", "610074")
                .addParam("ServiceRegion", "T9")
                .addParam("ServiceAccount", account.getId())
                .addParam("CreateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(account.getCreateTime()))
                .addParam("d", String.valueOf(System.currentTimeMillis()));
        httpResponse = HttpClient.getInstance().get(url, params);

        if (!httpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        content = httpResponse.getContent();
        String pwd = super.decrDesPkcs5Hex(content);
        if (StringUtils.isBlank(pwd)) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.GET_DYNAMIC_PWD_ERROR);
        }
        return BeanfunStringResult.success(pwd);
    }

    @Override
    public void loginOut(String token) throws Exception {
        String url = "https://tw.beanfun.com/generic_handlers/remove_bflogin_session.ashx";
        String time = new SimpleDateFormat("yyyyMMddHHmmss.SSS").format(new Date());
        ReqParams params = ReqParams.getInstance().addParam("d", time);
        HttpClient.getInstance().get(url, params);
    }

    @Override
    public int getGamePoints(String token) throws Exception {
        String url = "https://tw.beanfun.com/beanfun_block/generic_handlers/get_remain_point.ashx";
        String time = new SimpleDateFormat("yyyyMMddHHmmss.SSS").format(new Date());
        ReqParams params = ReqParams.getInstance().addParam("webtoken", "1").addParam("noCacheIE", time);
        QsHttpResponse response = HttpClient.getInstance().get(url, params);
        if (!response.getSuccess()) {
            return 0;
        }
        String content = response.getContent();
        List<List<String>> dataList = RegexUtils.regex(RegexUtils.Constant.TW_GAME_POINTS, content);
        String points = RegexUtils.getIndex(0, 1, dataList);
        if (StringUtils.isBlank(points)) {
            return 0;
        }
        try {
            return Integer.parseInt(points);
        } catch (NumberFormatException e) {
            log.error("点数转换异常! e={}", e.getMessage());
        }
        return 0;
    }

    @Override
    public BeanfunStringResult addAccount(String newName) throws Exception {
        String url = "https://tw.beanfun.com/generic_handlers/gamezone.ashx";
        ReqParams payload = ReqParams.getInstance()
                .addParam("strFunction", "AddServiceAccount")
                .addParam("npsc", "")
                .addParam("npsr", "")
                .addParam("sc", "610074")
                .addParam("sr", "T9")
                .addParam("sadn", newName.trim())
                .addParam("sag", "");
        QsHttpResponse response = HttpClient.getInstance().post(url, payload);
        if (!response.getSuccess()) {
            log.error("添加账号失败");
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String json = response.getContent();
        JSONObject jsonObject = JSON.parseObject(json);
        int intResult = jsonObject.getIntValue("intResult");
        if (intResult != 1) {
            String strOutstring = jsonObject.getString("strOutstring");
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.ACCOUNT_OPT_EXCEPTION, strOutstring);
        }
        return BeanfunStringResult.success();
    }

    @Override
    public BeanfunStringResult changeAccountName(String accountId, String newName) throws Exception {
        String url = "https://tw.beanfun.com/generic_handlers/gamezone.ashx";
        ReqParams payload = ReqParams.getInstance()
                .addParam("strFunction", "ChangeServiceAccountDisplayName")
                .addParam("sl", "610074_T9")
                .addParam("said", accountId)
                .addParam("nsadn", newName.trim());
        QsHttpResponse httpResponse = HttpClient.getInstance().post(url, payload);
        if (!httpResponse.getSuccess()) {
            log.error("修改账号名称失败");
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String json = httpResponse.getContent();
        JSONObject jsonObject = JSON.parseObject(json);
        int intResult = jsonObject.getIntValue("intResult");
        if (intResult != 1) {
            String strOutstring = jsonObject.getString("strOutstring");
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.ACCOUNT_OPT_EXCEPTION, strOutstring);
        }
        return BeanfunStringResult.success();
    }

    @Override
    public String getWebUrlMemberTopUp(String token) {
        return "https://tw.beanfun.com/TW/auth.aspx?channel=gash&page_and_query=default.aspx%3Fservice_code%3D999999%26service_region%3DT0&web_token=" + token;
    }

    @Override
    public String getWebUrlMemberCenter(String token) {
        return "https://tw.beanfun.com/TW/auth.aspx?channel=member&page_and_query=default.aspx%3Fservice_code%3D999999%26service_region%3DT0&web_token=" + token;
    }

    @Override
    public String getWebUrlServiceCenter() {
        return "https://tw.beanfun.com/customerservice/www/main.aspx";
    }

    @Override
    public String getWebUrlRegister() {
        String time = new SimpleDateFormat("yyyyMMddHHmmss.SSS").format(new Date());
        return "https://bfweb.beanfun.com/Register/register?isbfApp=0&service=999999_T0&dt=" + time;
    }

    @Override
    public String getWebUrlForgotPwd() {
        return "https://tw.beanfun.com/member/forgot_pwd.aspx";
    }

    @Override
    public boolean heartbeat() throws Exception {
        String url = "https://tw.beanfun.com/beanfun_block/generic_handlers/echo_token.ashx?webtoken=1";
        QsHttpResponse response = HttpClient.getInstance().get(url, ReqParams.getInstance().addParam("webtoken", "1"));
        log.info("心跳 = {}", response.getContent());
        return true;
    }


    private Date getAccountCreateTime(String sn) throws Exception {
        String url = "https://tw.beanfun.com/beanfun_block/game_zone/game_start_step2.aspx";
        ReqParams params = ReqParams.getInstance();
        params.addParam("service_code", "610074");
        params.addParam("service_region", "T9");
        params.addParam("sotp", sn);
        Date d = new Date();
        String strDateTime = "" + d.getYear() + d.getMonth() + d.getDate() + d.getHours() + d.getMinutes() + d.getSeconds() + d.getMinutes();
        params.addParam("dt", strDateTime);

        QsHttpResponse httpResponse = HttpClient.getInstance().get(url, params);
        if (!httpResponse.getSuccess()) {
            return null;
        }
        String content = httpResponse.getContent();
        List<List<String>> dataList = RegexUtils.regex(RegexUtils.Constant.TW_GET_SERVICE_CREATE_TIME, content);
        String time = RegexUtils.getIndex(0, 1, dataList);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
    }
}
