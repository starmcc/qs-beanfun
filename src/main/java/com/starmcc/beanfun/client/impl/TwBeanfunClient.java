package com.starmcc.beanfun.client.impl;

import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.exception.BFServiceNotFondException;
import com.starmcc.beanfun.model.Account;
import com.starmcc.beanfun.model.QsHttpResponse;
import com.starmcc.beanfun.model.ReqParams;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.RegexUtils;
import com.starmcc.beanfun.windows.BaseBFService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * tw beanfun客户
 * TODO 未完成
 * @author starmcc
 * @date 2022/04/23
 */
@Slf4j
public class TwBeanfunClient extends BeanfunClient {

    @Override
    public boolean login(String account, String password) throws Exception {
        errorMsg = "";
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            errorMsg = "登录账号和密码不能为空!";
            return false;
        }
        loginProcess = 0;
        boolean load = BaseBFService.getInstance().initialize2();
        if (!load) {
            errorMsg = "未安装Beanfun插件!";
            throw new BFServiceNotFondException();
        }

        loginProcess = 0.2D;
        // 1. 请求获取OTP
        String url = "";
        ReqParams params = ReqParams.getInstance().addParam("service", "999999_T0");
        url = "https://tw.beanfun.com/beanfun_block/bflogin/default.aspx";
        QsHttpResponse response = HttpClient.get(url, params);
        if (!BooleanUtils.isTrue(response.getSuccess())) {
            errorMsg = "skey 获取失败!";
            return false;
        }
        URI uri = response.getRedirectLocations().get(0);
        List<List<String>> dataList = RegexUtils.regex(RegexUtils.PTN_TW_OPT, uri.toString());
        String skey = DataTools.collectionIsEmpty(dataList) ? "" : dataList.get(0).get(1);
        if (StringUtils.isEmpty(skey)) {
            log.info("BeanfunClient.login otp1 is empty");
            errorMsg = "skey 获取失败!";
            return false;
        }
        log.info("BeanfunClient.login skey={}", skey);
        loginProcess = 0.4D;
        params = ReqParams.getInstance().addParam("skey", skey);
        url = "https://tw.newlogin.beanfun.com/login/id-pass_form.aspx";
        response = HttpClient.get(url, params);
        if (!BooleanUtils.isTrue(response.getSuccess())) {
            errorMsg = "获取签名失败";
            return false;
        }

        dataList = RegexUtils.regex(RegexUtils.PTN_VIEWSTATE, response.getContent());
        String viewstate = RegexUtils.getIndex(0, 1, dataList);
        dataList = RegexUtils.regex(RegexUtils.PTN_EVENTVALIDATION, response.getContent());
        String eventvalidation = RegexUtils.getIndex(0, 1, dataList);
        dataList = RegexUtils.regex(RegexUtils.PTN_VIEWSTATEGENERATOR, response.getContent());
        String viewstateGenerator = RegexUtils.getIndex(0, 1, dataList);
        log.info("BeanfunClient.login viewstate={} eventvalidation={} viewstateGenerator={}",
                viewstate, eventvalidation, viewstateGenerator);
        if (StringUtils.isEmpty(viewstate) || StringUtils.isEmpty(eventvalidation) || StringUtils.isEmpty(viewstateGenerator)) {
            errorMsg = "签名获取失败!";
            return false;
        }
        loginProcess = 0.6D;
        // 3. 登录接口 login get token
        Map<String, String> param = new HashMap<>(16);
        param.put("__EVENTTARGET", "");
        param.put("__EVENTARGUMENT", "");
        param.put("__VIEWSTATE", viewstate);
        param.put("__VIEWSTATEGENERATOR", viewstateGenerator);
        param.put("__EVENTVALIDATION", eventvalidation);
        param.put("t_AccountID", account);
        param.put("t_Password", password);
        param.put("btn_login", "登入");
        url = "https://tw.newlogin.beanfun.com/login/id-pass_form.aspx?skey=" + skey;
        response = HttpClient.post(url, param);


        return false;
    }

    @Override
    public boolean getAccountList() throws Exception {
        return false;
    }

    @Override
    public String getBfToken() {
        return null;
    }

    @Override
    public String getDynamicPassword(Account account) throws Exception {
        return null;
    }

    @Override
    public void loginOut() throws Exception {

    }

    @Override
    public void uninitialize() {

    }

    @Override
    public int getGamePoints() throws Exception {
        return 0;
    }

    @Override
    public boolean addAccount(String newName) throws Exception {
        return false;
    }

    @Override
    public boolean changeAccountName(String accountId, String newName) throws Exception {
        return false;
    }

    @Override
    public String getWebUrlMemberTopUp() {
        return null;
    }

    @Override
    public String getWebUrlMemberCenter() {
        return null;
    }

    @Override
    public String getWebUrlServiceCenter() {
        return null;
    }

    @Override
    public String getWebUrlRegister() {
        return null;
    }

    @Override
    public String getWebUrlForgotPwd() {
        return null;
    }

    @Override
    public boolean heartbeat() {
        return false;
    }
}
