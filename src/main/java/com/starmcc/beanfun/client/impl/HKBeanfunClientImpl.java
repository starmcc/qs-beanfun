package com.starmcc.beanfun.client.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.entity.client.*;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * hkbeanfun客户端实现
 *
 * @author starmcc
 * @date 2022/09/23
 */
@Slf4j
public class HKBeanfunClientImpl extends BeanfunClient {

    @Override
    public BeanfunStringResult getSessionKey() throws Exception {
        BeanfunStringResult result = new BeanfunStringResult();
        HttpClient.getInstance().getCookieStore().clear();
        String url = "https://bfweb.hk.beanfun.com/beanfun_block/bflogin/default.aspx";
        ReqParams params = ReqParams.getInstance().addParam("service", "999999_T0");
        QsHttpResponse qsHttpResponse = HttpClient.getInstance().get(url, params);
        if (!qsHttpResponse.getSuccess()) {
            return result.error(BeanfunResult.CodeEnum.OTP_GET_EMPTY);
        }
        if (StringUtils.indexOf(qsHttpResponse.getContent(), "IP已自動被系統鎖定") != -1) {
            return result.error(BeanfunResult.CodeEnum.IP_BAN);
        }

        if (StringUtils.indexOf(qsHttpResponse.getContent(), "目前無法在您的國家或地區瀏覽此網站") != -1) {
            return result.error(BeanfunResult.CodeEnum.AREA_BAN);
        }

        List<URI> uris = qsHttpResponse.getRedirectLocations();
        if (DataTools.collectionIsEmpty(uris)) {
            return result.error(BeanfunResult.CodeEnum.OTP_GET_EMPTY);
        }

        Optional<String> sessionKey = uris.stream().map(uri -> {
            List<List<String>> regex = RegexUtils.regex(RegexUtils.Constant.HK_SESSION_KEY, uri.getRawQuery());
            return RegexUtils.getIndex(0, 1, regex);
        }).filter(StringUtils::isNotBlank).findFirst();
        if (!sessionKey.isPresent()) {
            return result.error(BeanfunResult.CodeEnum.OTP_GET_EMPTY);
        }
        result.setData(sessionKey.get());
        return result.success();
    }

    @Override
    public BeanfunStringResult login(String account, String password, Supplier<Object> extendFnc, Consumer<Double> process) throws Exception {
        BeanfunStringResult result = new BeanfunStringResult();
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            return result.error(BeanfunResult.CodeEnum.ACT_PWD_IS_NULL);
        }
        process.accept(0.1);
        // 1. 请求获取SessionKey
        BeanfunStringResult sessionKeyResult = this.getSessionKey();
        process.accept(0.2);
        if (!sessionKeyResult.isSuccess()) {
            return sessionKeyResult;
        }

        // 2. 获取签名信息
        String url = "https://login.hk.beanfun.com/login/id-pass_form_newBF.aspx";
        process.accept(0.4);
        ReqParams params = ReqParams.getInstance().addParam("otp1", sessionKeyResult.getData());
        QsHttpResponse qsHttpResponse = HttpClient.getInstance().get(url, params);
        if (!qsHttpResponse.getSuccess()) {
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String content = qsHttpResponse.getContent();

        List<List<String>> dataList = new ArrayList<>();
        dataList = RegexUtils.regex(RegexUtils.Constant.HK_VIEWSTATE, content);
        String viewstate = RegexUtils.getIndex(0, 1, dataList);
        dataList = RegexUtils.regex(RegexUtils.Constant.HK_EVENTVALIDATION, content);
        String eventvalidation = RegexUtils.getIndex(0, 1, dataList);
        dataList = RegexUtils.regex(RegexUtils.Constant.HK_VIEWSTATEGENERATOR, content);
        String viewstateGenerator = RegexUtils.getIndex(0, 1, dataList);
        if (StringUtils.isEmpty(viewstate) || StringUtils.isEmpty(eventvalidation) || StringUtils.isEmpty(viewstateGenerator)) {
            return result.error(BeanfunResult.CodeEnum.OTP_SIGN_GET_ERROR);
        }

        // 3. 登录接口 获取authKey
        params = ReqParams.getInstance().addParam("__EVENTTARGET", "")
                .addParam("__EVENTARGUMENT", "")
                .addParam("__VIEWSTATEENCRYPTED", "")
                .addParam("__VIEWSTATE", viewstate)
                .addParam("__VIEWSTATEGENERATOR", viewstateGenerator)
                .addParam("__EVENTVALIDATION", eventvalidation)
                .addParam("t_AccountID", account)
                .addParam("t_Password", password)
                .addParam("token1", "")
                .addParam("g-recaptcha-response", "")
                .addParam("btn_login", "登入");
        url = "https://login.hk.beanfun.com/login/id-pass_form_newBF.aspx?otp1=" + sessionKeyResult.getData();
        qsHttpResponse = HttpClient.getInstance().post(url, params);
        process.accept(0.6);
        if (!qsHttpResponse.getSuccess()) {
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }

        content = qsHttpResponse.getContent();

        dataList = RegexUtils.regex(RegexUtils.Constant.HK_LOGIN_ERROR_MSG, content);
        String errMsg = RegexUtils.getIndex(0, 1, dataList);
        if (StringUtils.isNotBlank(errMsg)) {
            return result.error(BeanfunResult.CodeEnum.LOGIN_ERROR_MSG, errMsg);
        }


        String aKey = "";
        boolean dualStatus = RegexUtils.test(RegexUtils.Constant.HK_LOGIN_DUAL, content);
        if (dualStatus) {
            // 双重验证 content会返回存在 請輸入雙重驗證碼 关键字
            if (Objects.isNull(extendFnc)) {
                return result.error(BeanfunResult.CodeEnum.LOGIN_ERROR_MSG);
            }
            Object objs = extendFnc.get();
            if (Objects.isNull(objs)) {
                return result.error(BeanfunResult.CodeEnum.LOGIN_ERROR_MSG);
            }
            List<?> dualList = (List<?>) objs;
            if (DataTools.collectionIsEmpty(dualList)) {
                return result.error(BeanfunResult.CodeEnum.LOGIN_ERROR_MSG);
            }
            result = this.dualVerify(content, sessionKeyResult.getData(), dualList);
            if (!result.isSuccess()) {
                return result;
            }
            aKey = result.getData();
        } else {
            dataList = RegexUtils.regex(RegexUtils.Constant.HK_LOGIN_AKEY, content);
            aKey = RegexUtils.getIndex(0, 1, dataList);
        }
        process.accept(0.7);
        // 4. 通过akey请求获取webToken
        params = ReqParams.getInstance().addParam("SessionKey", sessionKeyResult.getData())
                .addParam("AuthKey", aKey)
                .addParam("ServiceCode", "")
                .addParam("ServiceRegion", "")
                .addParam("ServiceAccountSN", "0");

        url = "https://bfweb.hk.beanfun.com/beanfun_block/bflogin/return.aspx";
        qsHttpResponse = HttpClient.getInstance().post(url, params);
        process.accept(0.9);
        if (!qsHttpResponse.getSuccess()) {
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String bfWebToken = this.getBfWebToken();
        if (StringUtils.isBlank(bfWebToken)) {
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        process.accept(1.0);
        result.setData(bfWebToken);
        return result.success();
    }


    /**
     * 双重验证
     *
     * @param content  内容
     * @param otp1     otp1
     * @param dualList dv数据
     * @return {@link BeanfunStringResult}
     * @throws Exception 异常
     */
    private BeanfunStringResult dualVerify(String content, String otp1, List<?> dualList) throws Exception {
        BeanfunStringResult result = new BeanfunStringResult();
        List<List<String>> dataList = RegexUtils.regex(RegexUtils.Constant.HK_VIEWSTATE, content);
        String viewstate = RegexUtils.getIndex(0, 1, dataList);
        dataList = RegexUtils.regex(RegexUtils.Constant.HK_EVENTVALIDATION, content);
        String eventvalidation = RegexUtils.getIndex(0, 1, dataList);
        dataList = RegexUtils.regex(RegexUtils.Constant.HK_VIEWSTATEGENERATOR, content);
        String viewstateGenerator = RegexUtils.getIndex(0, 1, dataList);
        if (StringUtils.isEmpty(viewstate) || StringUtils.isEmpty(eventvalidation) || StringUtils.isEmpty(viewstateGenerator)) {
            return result.error(BeanfunResult.CodeEnum.OTP_SIGN_GET_ERROR);
        }
        String url = "https://login.hk.beanfun.com/login/id-pass_form_newBF.aspx?otp1=" + otp1;
        ReqParams params = ReqParams.getInstance().addParam("__EVENTTARGET", "")
                .addParam("__EVENTARGUMENT", "")
                .addParam("__VIEWSTATEENCRYPTED", "")
                .addParam("__VIEWSTATE", viewstate)
                .addParam("__VIEWSTATEGENERATOR", viewstateGenerator)
                .addParam("__EVENTVALIDATION", eventvalidation)
                .addParam("token1", "")
                .addParam("totpLoginBtn", "登入");
        for (int i = 0; i < dualList.size(); i++) {
            params = params.addParam("otpCode" + (i + 1), dualList.get(i).toString());
        }
        params.addHeader("Origin", "https://login.hk.beanfun.com");
        params.addHeader("Referer", url);
        params.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        QsHttpResponse qsHttpResponse = HttpClient.getInstance().post(url, params);
        if (!qsHttpResponse.getSuccess()) {
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        content = qsHttpResponse.getContent();
        if (content.contains("驗證碼錯誤")) {
            return result.error(BeanfunResult.CodeEnum.DUAL_VERIFICATIONS_ERROR);
        }
        dataList = RegexUtils.regex(RegexUtils.Constant.HK_LOGIN_ERROR_MSG, content);
        String errMsg = RegexUtils.getIndex(0, 1, dataList);
        if (StringUtils.isNotBlank(errMsg)) {
            return result.error(BeanfunResult.CodeEnum.LOGIN_ERROR_MSG, errMsg);
        }

        dataList = RegexUtils.regex(RegexUtils.Constant.HK_LOGIN_AKEY, content);
        String aKey = RegexUtils.getIndex(0, 1, dataList);
        result.setData(aKey);
        return result;
    }

    @Override
    public BeanfunAccountResult getAccountList(String token) throws Exception {
        // 授权接口访问，携带cookies和token信息，查询账号列表
        BeanfunAccountResult actResult = new BeanfunAccountResult();

        String url = "https://bfweb.hk.beanfun.com/beanfun_block/auth.aspx";
        ReqParams params = ReqParams.getInstance()
                .addParam("channel", "game_zone")
                .addParam("page_and_query", "game_start.aspx?service_code_and_region=610074_T9")
                .addParam("web_token", token);
        QsHttpResponse httpResponse = HttpClient.getInstance().get(url, params);
        if (!httpResponse.getSuccess()) {
            return actResult.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String content = httpResponse.getContent();

        List<List<String>> dataList = RegexUtils.regex(RegexUtils.Constant.TW_ACCOUNT_MAX, content);
        String maxActNumberStr = RegexUtils.getIndex(0, 1, dataList);
        int maxActNumber = 0;
        if (StringUtils.isNotBlank(maxActNumberStr)) {
            maxActNumber = Integer.parseInt(maxActNumberStr);
        }
        actResult.setMaxActNumber(maxActNumber);

        dataList = RegexUtils.regex(RegexUtils.Constant.HK_ACCOUNT_LIST, content);
        if (DataTools.collectionIsEmpty(dataList)) {
            // 进阶认证校验
            dataList = RegexUtils.regex(RegexUtils.Constant.HK_CERT_VERIFY, content);
            String certStr = RegexUtils.getIndex(0, 1, dataList);
            if (StringUtils.indexOf(certStr, "進階認證") >= 0) {
                // 没有做进阶认证
                actResult.setCertStatus(false);
            }
            if (RegexUtils.test(RegexUtils.Constant.HK_CREATE_ACCOUNT, content)) {
                // 新账号，没有账号
                actResult.setNewAccount(true);
            }
            return actResult;
        }
        List<Account> accountList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            Account account = new Account();
            account.setStatus(!Objects.equals(RegexUtils.getIndex(i, 1, dataList), ""));
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
        BeanfunStringResult result = new BeanfunStringResult();
        if (Objects.isNull(account) || StringUtils.isBlank(account.getId())) {
            return result.error(BeanfunResult.CodeEnum.GET_DYNAMIC_PWD_ACT_INFO_EMPTY);
        }
        String url = "https://bfweb.hk.beanfun.com/beanfun_block/game_zone/game_start_step2.aspx";
        ReqParams params = ReqParams.getInstance();
        params.addParam("service_code", "610074");
        params.addParam("service_region", "T9");
        params.addParam("sotp", account.getSn());
        Date d = new Date();
        String strDateTime = "" + d.getYear() + d.getMonth() + d.getDate() + d.getHours() + d.getMinutes() + d.getSeconds() + d.getMinutes();
        params.addParam("dt", strDateTime);

        QsHttpResponse httpResponse = HttpClient.getInstance().get(url, params);
        if (!httpResponse.getSuccess()) {
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String content = httpResponse.getContent();
        List<List<String>> dataList = RegexUtils.regex(RegexUtils.Constant.HK_GET_PWD_OTP_KEY, content);
        String pollingKey = RegexUtils.getIndex(0, 1, dataList);

        if (Objects.isNull(account.getCreateTime())) {
            dataList = RegexUtils.regex(RegexUtils.Constant.HK_GET_SERVICE_CREATE_TIME, content);
            String time = RegexUtils.getIndex(0, 1, dataList);
            Date createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
            account.setCreateTime(createTime);
        }
        String url2 = "https://login.hk.beanfun.com/generic_handlers/get_cookies.ashx";
        httpResponse = HttpClient.getInstance().get(url2);
        if (!httpResponse.getSuccess()) {
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        content = httpResponse.getContent();
        dataList = RegexUtils.regex(RegexUtils.Constant.HK_GET_PWD_OTP_SECRET, content);
        String secret = RegexUtils.getIndex(0, 1, dataList);

        url = "https://bfweb.hk.beanfun.com/beanfun_block/generic_handlers/record_service_start.ashx";
        params = ReqParams.getInstance()
                .addParam("service_code", "610074")
                .addParam("service_region", "T9")
                .addParam("service_account_id", account.getId())
                .addParam("sotp", account.getSn())
                .addParam("service_account_display_name", account.getName())
                .addParam("service_account_create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(account.getCreateTime()));

        HttpClient.getInstance().post(url, params);

        // TODO 暂未知此接口作用，估计是记录登录接口，不请求也能正常走通，这里请求返回域名错误，望有能之人解决之。
        // { "intResult": 0,  "strOutstring": "[generic_handlers - get_result.ashx] Error: The URL referrer is null or from a different domain!" }
        /*url = "https://bfweb.hk.beanfun.com/generic_handlers/get_result.ashx";
        params = ReqParams.getInstance();
        params.addParam("meth", "GetResultByLongPolling");
        params.addParam("key", pollingKey);
        params.addParam("_", String.valueOf(System.currentTimeMillis()));
        httpResponse = HttpClient.getInstance().get(url, params);*/

        url = "https://bfweb.hk.beanfun.com/beanfun_block/generic_handlers/get_webstart_otp.ashx";
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
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        content = httpResponse.getContent();
        String pwd = super.decrDesPkcs5Hex(content);
        if (StringUtils.isBlank(pwd)) {
            return result.error(BeanfunResult.CodeEnum.GET_DYNAMIC_PWD_ERROR);
        }
        result.setData(pwd);
        return result.success();
    }

    @Override
    public void loginOut(String token) throws Exception {
        String url = "https://bfweb.hk.beanfun.com/generic_handlers/remove_bflogin_session.ashx";
        ReqParams params = ReqParams.getInstance().addParam("d", String.valueOf(System.currentTimeMillis()));
        HttpClient.getInstance().get(url, params);
    }

    @Override
    public int getGamePoints(String token) throws Exception {
        String url = "https://bfweb.hk.beanfun.com/beanfun_block/generic_handlers/get_remain_point.ashx";
        String time = new SimpleDateFormat("yyyyMMddHHmmss.SSS").format(new Date());
        ReqParams params = ReqParams.getInstance().addParam("webtoken", "1").addParam("noCacheIE", time);
        QsHttpResponse response = HttpClient.getInstance().get(url, params);
        if (!response.getSuccess()) {
            return 0;
        }
        String content = response.getContent();
        List<List<String>> dataList = RegexUtils.regex(RegexUtils.Constant.HK_GAME_POINTS, content);
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
        BeanfunStringResult result = new BeanfunStringResult();
        String url = "https://bfweb.hk.beanfun.com/generic_handlers/gamezone.ashx";
        ReqParams payload = ReqParams.getInstance()
                .addParam("strFunction", "AddServiceAccount")
                .addParam("npsc", "")
                .addParam("npsr", "")
                .addParam("sc", "610074")
                .addParam("sr", "T9")
                .addParam("sadn", newName.trim())
                .addParam("sag", "");
        QsHttpResponse httpResponse = HttpClient.getInstance().post(url, payload);
        if (!httpResponse.getSuccess()) {
            log.error("添加账号失败");
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String json = httpResponse.getContent();
        JSONObject jsonObject = JSON.parseObject(json);
        int intResult = jsonObject.getIntValue("intResult");
        if (intResult != 1) {
            String strOutstring = jsonObject.getString("strOutstring");
            return result.error(BeanfunResult.CodeEnum.ACCOUNT_OPT_EXCEPTION, strOutstring);
        }
        return result.success();
    }

    @Override
    public BeanfunStringResult changeAccountName(String accountId, String newName) throws Exception {
        BeanfunStringResult result = new BeanfunStringResult();
        String url = "https://bfweb.hk.beanfun.com/generic_handlers/gamezone.ashx";
        ReqParams payload = ReqParams.getInstance()
                .addParam("strFunction", "ChangeServiceAccountDisplayName")
                .addParam("sl", "610074_T9")
                .addParam("said", accountId)
                .addParam("nsadn", newName.trim());
        QsHttpResponse httpResponse = HttpClient.getInstance().post(url, payload);
        if (!httpResponse.getSuccess()) {
            log.error("修改账号名称失败");
            return result.error(BeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String json = httpResponse.getContent();
        JSONObject jsonObject = JSON.parseObject(json);
        int intResult = jsonObject.getIntValue("intResult");
        if (intResult != 1) {
            String strOutstring = jsonObject.getString("strOutstring");
            return result.error(BeanfunResult.CodeEnum.ACCOUNT_OPT_EXCEPTION, strOutstring);
        }
        return result.success();
    }

    @Override
    public String getWebUrlMemberTopUp(String token) {
        return "https://bfweb.hk.beanfun.com/HK/auth.aspx?channel=gash&page_and_query=default.aspx%3Fservice_code%3D999999%26service_region%3DT0&web_token=" + token;
    }

    @Override
    public String getWebUrlMemberCenter(String token) {
        return "https://bfweb.hk.beanfun.com/HK/auth.aspx?channel=member&page_and_query=default.aspx%3Fservice_code%3D999999%26service_region%3DT0&web_token=" + token;
    }

    @Override
    public String getWebUrlServiceCenter() {
//        return "https://cs.hk.beanfun.com/faq/index.aspx";
        return "https://csp.hk.beanfun.com/";
    }

    @Override
    public String getWebUrlRegister() {
        String time = new SimpleDateFormat("yyyyMMddHHmmss.SSS").format(new Date());
        return "https://bfweb.hk.beanfun.com/beanfun_web_ap/signup/preregistration.aspx?service=999999_T0&dt=" + time;
    }

    @Override
    public String getWebUrlForgotPwd() {
        return "https://bfweb.hk.beanfun.com/member/forgot_pwd.aspx";
    }

    @Override
    public void heartbeat() throws Exception {
        HttpClient.getInstance().get("https://bfweb.hk.beanfun.com/");
        log.info("心跳 = {}", System.currentTimeMillis());
    }


    private Date getAccountCreateTime(String sn) throws Exception {
        String url = "https://bfweb.hk.beanfun.com/beanfun_block/game_zone/game_start_step2.aspx";
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
        List<List<String>> dataList = RegexUtils.regex(RegexUtils.Constant.HK_GET_SERVICE_CREATE_TIME, content);
        try {
            String time = RegexUtils.getIndex(0, 1, dataList);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        } catch (ParseException e) {
            log.error("error = {} content = {}", e.getMessage(), content, e);
            return null;
        }
    }
}
