package com.starmcc.beanfun.client.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.model.ReqParams;
import com.starmcc.beanfun.model.client.*;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.RegexUtils;
import com.starmcc.beanfun.windows.BaseBFService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * beanfun客户端
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public class BeanfunOldHongKongClientImpl extends BeanfunClient {

    @Override
    public BeanfunStringResult login(String account, String password, Consumer<Double> process) throws Exception {
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.ACT_PWD_IS_NULL);
        }
        process.accept(0D);
        boolean load = BaseBFService.getInstance().initialize2();
        if (!load) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.PLUGIN_NOT_INSTALL);
        }
        process.accept(0.2D);
        // 1. 请求获取OTP
        String url = "";
        ReqParams params = ReqParams.getInstance().addParam("service", "999999_T0");
        url = "http://hk.beanfun.com/beanfun_block/login/index.aspx";
        QsHttpResponse httpResponse = HttpClient.get(url, params);
        if (!httpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String html = httpResponse.getContent();
        List<List<String>> dataList = RegexUtils.regex(RegexUtils.PatternOldHongKong.OTP.getPattern(), html);
        String otp1 = DataTools.collectionIsEmpty(dataList) ? "" : dataList.get(0).get(1);
        if (StringUtils.isEmpty(otp1)) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.OTP_GET_EMPTY);
        }
        log.info("BeanfunClient.login otp1={}", otp1);
        process.accept(0.4D);
        // 2. 获取签名 viewstate eventvalidation viewstateGenerator
        params = ReqParams.getInstance().addParam("otp1", otp1).addParam("seed", "0");
        url = "http://hk.beanfun.com/beanfun_block/login/id-pass_form.aspx";
        httpResponse = HttpClient.get(url, params);
        if (!httpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        html = httpResponse.getContent();
        dataList = RegexUtils.regex(RegexUtils.PatternOldHongKong.VIEWSTATE.getPattern(), html);
        String viewstate = RegexUtils.getIndex(0, 1, dataList);
        dataList = RegexUtils.regex(RegexUtils.PatternOldHongKong.EVENTVALIDATION.getPattern(), html);
        String eventvalidation = RegexUtils.getIndex(0, 1, dataList);
        dataList = RegexUtils.regex(RegexUtils.PatternOldHongKong.VIEWSTATEGENERATOR.getPattern(), html);
        String viewstateGenerator = RegexUtils.getIndex(0, 1, dataList);
        log.info("BeanfunClient.login viewstate={} eventvalidation={} viewstateGenerator={}",
                viewstate, eventvalidation, viewstateGenerator);

        if (StringUtils.isEmpty(viewstate) || StringUtils.isEmpty(eventvalidation) || StringUtils.isEmpty(viewstateGenerator)) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.OTP_SIGN_GET_ERROR);
        }
        process.accept(0.6D);
        // 3. 登录接口 login get token
        Map<String, String> param = new HashMap<>(16);
        param.put("__EVENTTARGET", "");
        param.put("__EVENTARGUMENT", "");
        param.put("__VIEWSTATE", viewstate);
        param.put("__VIEWSTATEGENERATOR", viewstateGenerator);
        param.put("__EVENTVALIDATION", eventvalidation);
        param.put("t_AccountID", account);
        param.put("t_Password", password);
        param.put("btn_login.x", "0");
        param.put("btn_login.y", "0");
        param.put("recaptcha_response_field", "manual_challenge");
        url = "http://hk.beanfun.com/beanfun_block/login/id-pass_form.aspx?otp1=" + otp1 + "&seed=0";
        httpResponse = HttpClient.post(url, param);
        if (!httpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        html = httpResponse.getContent();
        dataList = RegexUtils.regex(RegexUtils.PatternOldHongKong.TOKEN.getPattern(), html);
        String token = RegexUtils.getIndex(0, 1, dataList);
        log.info("BeanfunClient.login token = {}", token);
        if (StringUtils.isEmpty(token)) {
            dataList = RegexUtils.regex(RegexUtils.PatternOldHongKong.LOGIN_ERRMSG.getPattern(), html);
            // 信息框提示
            String errorMsg = RegexUtils.getIndex(0, 1, dataList);
            if (StringUtils.isNotBlank(errorMsg)) {
                return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.BEANFUN_ERROR, errorMsg);
            }
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.ACT_PWD_ERROR);
        }
        process.accept(0.8D);
        // 请求权限
        url = "http://hk.beanfun.com/beanfun_block/auth.aspx";
        params = ReqParams.getInstance()
                .addParam("channel", "game_zone")
                .addParam("page_and_query", "game_start.aspx?service_code_and_region=610074_T9")
                .addParam("token", getBfToken(token));
        httpResponse = HttpClient.get(url, params);
        if (!httpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        html = httpResponse.getContent();
        //dataList = RegexUtils.regex(RegexUtils.PTN_LOGIN_ACCOUNT_LOCATION, html);
        process.accept(0.9D);
        return BeanfunStringResult.success(token);
    }

    @Override
    public BeanfunAccountResult getAccountList(String token) throws Exception {
        String url = "https://hk.beanfun.com/beanfun_block/auth.aspx";
        ReqParams params = ReqParams.getInstance()
                .addParam("channel", "game_zone")
                .addParam("page_and_query", "game_start.aspx?service_code_and_region=610074_T9")
                .addParam("token", getBfToken(token));
        QsHttpResponse httpResponse = HttpClient.get(url, params);
        if (!httpResponse.getSuccess()) {
            return BeanfunAccountResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String html = httpResponse.getContent();
        url = "https://hk.beanfun.com/beanfun_block/game_zone/game_server_account_list.aspx";
        ReqParams reqParams = ReqParams.getInstance()
                .addParam("service_code", "610074")
                .addParam("service_region", "T9");
        httpResponse = HttpClient.get(url, reqParams);
        if (!httpResponse.getSuccess()) {
            return BeanfunAccountResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        html = httpResponse.getContent();
        List<List<String>> dataList = RegexUtils.regex(RegexUtils.PatternOldHongKong.LOGIN_ACCOUNT_LIST.getPattern(), html);
        if (DataTools.collectionIsEmpty(dataList)) {
            if (RegexUtils.test(RegexUtils.PatternOldHongKong.LOGIN_CREATE_ACCOUNT.getPattern(), html)) {
                // 新账号，没有账号
                return BeanfunAccountResult.success(new ArrayList<>(), true);
            }
            // 找不到账号信息，且不是新账号
            return BeanfunAccountResult.error(AbstractBeanfunResult.CodeEnum.GET_ACT_LIST_EMPTY);
        }
        List<Account> accountList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            Account account = new Account();
            account.setStatus(RegexUtils.getIndex(i, 1, dataList) != "stop");
            account.setId(RegexUtils.getIndex(i, 4, dataList));
            account.setSn(RegexUtils.getIndex(i, 5, dataList));
            account.setName(RegexUtils.getIndex(i, 6, dataList));
            account.setAuthType(RegexUtils.getIndex(i, 7, dataList));
            String createTimeStr = RegexUtils.getIndex(i, 8, dataList);
            if (StringUtils.isNotBlank(createTimeStr)) {
                Date createTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(createTimeStr);
                account.setCreateTime(createTime);
            }
            accountList.add(account);
        }
        return BeanfunAccountResult.success(accountList, false);
    }


    @Override
    public BeanfunStringResult getDynamicPassword(Account account, String token) throws Exception {
        if (Objects.isNull(account) || StringUtils.isBlank(account.getId())) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.GET_DYNAMIC_PWD_ACT_INFO_EMPTY);
        }


        String createTime = "";
        if (Objects.isNull(account.getCreateTime())) {
            createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        } else {
            createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(account.getCreateTime());
        }

        String url = "http://hk.beanfun.com/beanfun_block/generic_handlers/get_otp.ashx";
        ReqParams params = ReqParams.getInstance()
                .addParam("ppppp", "")
                .addParam("token", getBfToken(token))
                .addParam("account_service_code", "610074")
                .addParam("account_service_region", "T9")
                .addParam("service_account_id", account.getId())
                .addParam("create_time", createTime)
                .addParam("d", String.valueOf(System.currentTimeMillis()));
        QsHttpResponse httpResponse = HttpClient.get(url, params);
        if (!httpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String otp = httpResponse.getContent();
        String pwd = decrDesPkcs5Hex(otp);
        return BeanfunStringResult.success(pwd);
    }


    @Override
    public void loginOut(String token) throws Exception {
        HttpClient.get("http://hk.beanfun.com/beanfun_block/generic_handlers/remove_login_session.ashx", null);
        HttpClient.get("http://hk.beanfun.com/beanfun_web_ap/remove_login_session.ashx", null);
        HttpClient.get("http://hk.beanfun.com/beanfun_block/generic_handlers/erase_token.ashx?token=" + getBfToken(token), null);
        uninitialize();
    }

    @Override
    public void uninitialize() {
        BaseBFService.getInstance().uninitialize();
    }


    /**
     * 获取游戏点数
     *
     * @return int
     * @throws Exception 异常
     */
    @Override
    public int getGamePoints(String token) throws Exception {
        ReqParams params = ReqParams.getInstance().addParam("token", getBfToken(token));
        String url = "http://hk.beanfun.com/beanfun_block/generic_handlers/get_remain_point.ashx";
        QsHttpResponse httpResponse = httpResponse = HttpClient.get(url, params);
        if (!httpResponse.getSuccess()) {
            return 0;
        }
        String html = httpResponse.getContent();
        List<List<String>> regex = RegexUtils.regex(RegexUtils.PatternOldHongKong.GAME_POINTS.getPattern(), html);
        String result = RegexUtils.getIndex(0, 1, regex);
        if (StringUtils.isBlank(result)) {
            return 0;
        }
        return Integer.parseInt(result);
    }


    /**
     * 添加账户
     *
     * @param accountId 帐户id
     * @param newName   新名字
     * @return boolean
     * @throws Exception 异常
     */
    @Override
    public boolean addAccount(String newName) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("strFunction", "AddServiceAccount");
        params.put("npsc", "");
        params.put("npsr", "");
        params.put("sc", "610074");
        params.put("sr", "T9");
        params.put("sadn", newName);
        params.put("sag", "");
        String url = "https://hk.beanfun.com/beanfun_block/generic_handlers/gamezone.ashx";
        QsHttpResponse httpResponse = HttpClient.post(url, params);
        if (!httpResponse.getSuccess()) {
            return false;
        }
        String json = httpResponse.getContent();
        JSONObject jsonObject = JSON.parseObject(json);
        return StringUtils.equals(jsonObject.getString("intResult"), "1");
    }

    /**
     * 更改账户名称
     *
     * @param accountId 帐户id
     * @param newName   新名字
     * @return boolean
     * @throws Exception 异常
     */
    @Override
    public boolean changeAccountName(String accountId, String newName) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("strFunction", "ChangeServiceAccountDisplayName");
        params.put("sl", "610074_T9");
        params.put("said", accountId);
        params.put("nsadn", newName);
        String url = "https://hk.beanfun.com/beanfun_block/generic_handlers/gamezone.ashx";
        QsHttpResponse httpResponse = HttpClient.post(url, params);
        if (!httpResponse.getSuccess()) {
            return false;
        }
        String json = httpResponse.getContent();
        JSONObject jsonObject = JSON.parseObject(json);
        return StringUtils.equals(jsonObject.getString("intResult"), "1");
    }

    /**
     * 获取web url会员充值
     *
     * @return {@link String}
     */
    @Override
    public String getWebUrlMemberTopUp(String token) {
        return "https://hk.beanfun.com/beanfun_web_ap/auth.aspx?channel=gash&page_and_query=default.aspx%3fservice_code%3d999999%26service_region%3dT0&token=" + getBfToken(token);
    }


    /**
     * 获取web url成员中心
     *
     * @return {@link String}
     */
    @Override
    public String getWebUrlMemberCenter(String token) {
        return "https://hk.beanfun.com/beanfun_web_ap/auth.aspx?channel=member&page_and_query=default.aspx%3fservice_code%3d999999%26service_region%3dT0&token=" + getBfToken(token);
    }


    /**
     * 获取web url服务中心
     *
     * @return {@link String}
     */
    @Override
    public String getWebUrlServiceCenter() {
        return "http://hk.games.beanfun.com/faq/service.asp";
    }

    /**
     * 获取web url账号注册
     *
     * @return {@link String}
     */
    @Override
    public String getWebUrlRegister() {
        return "http://hk.beanfun.com/beanfun_web_ap/signup/preregistration.aspx";
    }


    /**
     * 获取web url忘记密码
     *
     * @return {@link String}
     */
    @Override
    public String getWebUrlForgotPwd() {
        return "http://hk.beanfun.com/member/forgot_pwd.aspx";
    }


    /**
     * 心跳
     */
    @Override
    public boolean heartbeat(String token) {
        try {
            ReqParams reqParams = ReqParams.getInstance().addParam("token", getBfToken(token));
            String url = "http://hk.beanfun.com/beanfun_block/generic_handlers/echo_token.ashx";
            QsHttpResponse httpResponse = HttpClient.get(url, reqParams);
            if (!httpResponse.getSuccess()) {
                return false;
            }
            String result = httpResponse.getContent();
            return result.indexOf("valid") != -1;
        } catch (Exception e) {
            log.error("心跳异常 e={}", e.getMessage(), e);
            return false;
        }
    }


}
