package com.starmcc.beanfun.client.impl;

import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.model.ReqParams;
import com.starmcc.beanfun.model.client.*;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * @Author starmcc
 * @Date 2022/6/2 12:30
 */
@Slf4j
public class BeanfunNewHongKongClientImpl extends BeanfunClient {
    @Override
    public BeanfunStringResult login(String account, String password, Consumer<Double> process) throws Exception {
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            return BeanfunStringResult.error(BeanfunStringResult.CodeEnum.ACT_PWD_IS_NULL);
        }
        process.accept(0D);
        // 1. 请求获取OTP
        String url = "";
        ReqParams params = ReqParams.getInstance().addParam("service", "999999_T0");
        url = "https://bfweb.hk.beanfun.com/beanfun_block/bflogin/default.aspx";
        QsHttpResponse qsHttpResponse = HttpClient.get(url, params);
        if (!qsHttpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        process.accept(0.2D);

        List<URI> uris = qsHttpResponse.getRedirectLocations();
        Optional<String> otp1 = uris.stream().map(uri -> {
            List<List<String>> regex = RegexUtils.regex(RegexUtils.PatternHongKong.OTP.getPattern(), uri.getRawQuery());
            return RegexUtils.getIndex(0, 1, regex);
        }).filter(StringUtils::isNotBlank).findFirst();
        if (!otp1.isPresent()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.OTP_GET_EMPTY);
        }
        // 2. 获取签名信息
        url = "https://login.hk.beanfun.com/login/id-pass_form_newBF.aspx";
        params = ReqParams.getInstance().addParam("otp1", otp1.get());
        qsHttpResponse = HttpClient.get(url, params);
        if (!qsHttpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        process.accept(0.4D);
        String content = qsHttpResponse.getContent();

        List<List<String>> dataList = new ArrayList<>();
        dataList = RegexUtils.regex(RegexUtils.PatternHongKong.VIEWSTATE.getPattern(), content);
        String viewstate = RegexUtils.getIndex(0, 1, dataList);
        dataList = RegexUtils.regex(RegexUtils.PatternHongKong.EVENTVALIDATION.getPattern(), content);
        String eventvalidation = RegexUtils.getIndex(0, 1, dataList);
        dataList = RegexUtils.regex(RegexUtils.PatternHongKong.VIEWSTATEGENERATOR.getPattern(), content);
        String viewstateGenerator = RegexUtils.getIndex(0, 1, dataList);
        log.info("BeanfunClient.login viewstate={} eventvalidation={} viewstateGenerator={}",
                viewstate, eventvalidation, viewstateGenerator);
        if (StringUtils.isEmpty(viewstate) || StringUtils.isEmpty(eventvalidation) || StringUtils.isEmpty(viewstateGenerator)) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.OTP_SIGN_GET_ERROR);
        }

        // 3. 登录接口 获取authKey
        Map<String, String> param = new HashMap<>(16);
        param.put("__EVENTTARGET", "");
        param.put("__EVENTARGUMENT", "");
        param.put("__VIEWSTATEENCRYPTED", "");
        param.put("__VIEWSTATE", viewstate);
        param.put("__VIEWSTATEGENERATOR", viewstateGenerator);
        param.put("__EVENTVALIDATION", eventvalidation);
        param.put("t_AccountID", account);
        param.put("t_Password", password);
        param.put("token1", "");
        param.put("btn_login", "登入");
        param.put("checkbox_remember_account", "on");
        url = "https://login.hk.beanfun.com/login/id-pass_form_newBF.aspx?otp1=" + otp1.get();
        qsHttpResponse = HttpClient.post(url, param);
        if (!qsHttpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        process.accept(0.6D);

        uris = qsHttpResponse.getRedirectLocations();
        if (DataTools.collectionIsEmpty(uris)) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.OTP_SIGN_GET_ERROR);
        }
        String aKey = uris.get(0).getRawQuery();
        aKey = aKey.split("=")[1];

        // 4. 通过akey请求获取webToken
        param = new HashMap<>(16);
        param.put("SessionKey", otp1.get());
        param.put("AuthKey", aKey);
        param.put("ServiceCode", "");
        param.put("ServiceRegion", "");
        param.put("ServiceAccountSN", "0");

        url = "https://bfweb.hk.beanfun.com/beanfun_block/bflogin/return.aspx";
        qsHttpResponse = HttpClient.post(url, param);
        if (!qsHttpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        process.accept(0.8D);
        uris = qsHttpResponse.getRedirectLocations();

        if (DataTools.collectionIsEmpty(uris)) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        // 5. 通过return重定向js获取地址，请求主页，返回bfWebToken
        url = "https://bfweb.hk.beanfun.com" + uris.get(0).getRawPath();
        qsHttpResponse = HttpClient.get(url, null);
        if (!qsHttpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        process.accept(0.9D);
        Map<String, String> cookieMap = qsHttpResponse.getCookieMap();
        String bfWebToken = cookieMap.get("bfWebToken");
        return BeanfunStringResult.success(bfWebToken);
    }

    @Override
    public BeanfunAccountResult getAccountList(String token) throws Exception {
        // 1. 授权接口访问，携带cookies和token信息，信任该次请求
        String url = "https://bfweb.hk.beanfun.com/beanfun_block/auth.aspx";
        ReqParams params = ReqParams.getInstance()
                .addParam("channel", "game_zone")
                .addParam("page_and_query", "game_start.aspx?service_code_and_region=610074_T9")
                .addParam("web_token", token);
        QsHttpResponse httpResponse = HttpClient.get(url, params);
        if (!httpResponse.getSuccess()) {
            return BeanfunAccountResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }

        // 2. 查询账号列表
        url = "https://bfweb.hk.beanfun.com/beanfun_block/game_zone/game_server_account_list.aspx";
        ReqParams reqParams = ReqParams.getInstance()
                .addParam("sc", "610074")
                .addParam("sr", "T9")
                .addParam("dt", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        httpResponse = HttpClient.get(url, reqParams);
        if (!httpResponse.getSuccess()) {
            return BeanfunAccountResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }

        String content = httpResponse.getContent();
        List<List<String>> dataList = RegexUtils.regex(RegexUtils.PatternHongKong.LOGIN_ACCOUNT_LIST.getPattern(), content);
        if (DataTools.collectionIsEmpty(dataList)) {
            if (RegexUtils.test(RegexUtils.PatternOldHongKong.LOGIN_CREATE_ACCOUNT.getPattern(), content)) {
                // 新账号，没有账号
                return BeanfunAccountResult.success(new ArrayList<>(), true);
            }
            // 找不到账号信息，且不是新账号
            return BeanfunAccountResult.error(AbstractBeanfunResult.CodeEnum.GET_ACT_LIST_EMPTY);
        }
        List<Account> accountList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            Account account = new Account();
            account.setStatus(true);
            account.setId(RegexUtils.getIndex(i, 1, dataList));
            account.setSn(RegexUtils.getIndex(i, 2, dataList));
            account.setName(RegexUtils.getIndex(i, 3, dataList));
            // 新港号需要手动查询创建时间
            account.setCreateTime();
            accountList.add(account);
        }
        return BeanfunAccountResult.success(accountList, false);
    }

    @Override
    public BeanfunStringResult getDynamicPassword(Account account, String token) throws Exception {
        if (Objects.isNull(account) || StringUtils.isBlank(account.getId())) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.GET_DYNAMIC_PWD_ACT_INFO_EMPTY);
        }
        String url = "https://bfweb.hk.beanfun.com/beanfun_block/game_zone/game_start_step2.aspx";
        ReqParams params = ReqParams.getInstance();
        params.addParam("service_code", "610074");
        params.addParam("service_region", "T9");
        params.addParam("sotp", account.getSn());
        Date d = new Date();
        String strDateTime = "" + d.getYear() + d.getMonth() + d.getDate() + d.getHours() + d.getMinutes() + d.getSeconds() + d.getMinutes();
        params.addParam("dt", strDateTime);

        QsHttpResponse httpResponse = HttpClient.get(url, params);
        if (!httpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        String content = httpResponse.getContent();
        List<List<String>> dataList = RegexUtils.regex(RegexUtils.PatternHongKong.GET_PWD_OTP_KEY.getPattern(), content);
        String pollingKey = RegexUtils.getIndex(0, 1, dataList);

        if (Objects.isNull(account.getCreateTime())) {
            dataList = RegexUtils.regex(RegexUtils.PatternHongKong.GET_SERVICE_CREATE_TIME.getPattern(), content);
            String time = RegexUtils.getIndex(0, 1, dataList);
            Date createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
            account.setCreateTime(createTime);
        }
        String url2 = "https://login.hk.beanfun.com/generic_handlers/get_cookies.ashx";
        httpResponse = HttpClient.get(url2, null);
        if (!httpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        content = httpResponse.getContent();
        dataList = RegexUtils.regex(RegexUtils.PatternHongKong.GET_PWD_OTP_SECRET.getPattern(), content);
        String secret = RegexUtils.getIndex(0, 1, dataList);

        url = "https://bfweb.hk.beanfun.com/beanfun_block/generic_handlers/record_service_start.ashx";
        Map<String, String> payload = new HashMap<>();
        payload.put("service_code", "610074");
        payload.put("service_region", "T9");
        payload.put("service_account_id", account.getId());
        payload.put("sotp", account.getSn());
        payload.put("service_account_display_name", account.getName());
        payload.put("service_account_create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(account.getCreateTime()));

        HttpClient.post(url, payload);

        // TODO 暂时不知道这个接口有啥用，不请求也能正常走通，请求返回错误，验证了域名，望有能之人解决。
//        String url = "https://bfweb.hk.beanfun.com/generic_handlers/get_result.ashx";
//        params = ReqParams.getInstance();
//        params.addParam("meth", "GetResultByLongPolling");
//        params.addParam("key", key);
//        params.addParam("_", String.valueOf(System.currentTimeMillis()));
//        HttpClient.get(url4, params);


        url = "https://bfweb.hk.beanfun.com/beanfun_block/generic_handlers/get_webstart_otp.ashx";
        params = ReqParams.getInstance();
        params.addParam("sn", pollingKey);
        params.addParam("WebToken", token);
        params.addParam("SecretCode", secret);
        params.addParam("ppppp", "F9B45415B9321DB9635028EFDBDDB44BC355421524D6AAAD45F2EB9C030F326DA537FB1C2A00540CE27DAC783CF16D54B917AE8371D3547DC06F3AF499B9EC14");
        params.addParam("ServiceCode", "610074");
        params.addParam("ServiceRegion", "T9");
        params.addParam("ServiceAccount", account.getId());
        params.addParam("CreateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(account.getCreateTime()));
        params.addParam("d", String.valueOf(System.currentTimeMillis()));
        httpResponse = HttpClient.get(url, params);

        if (!httpResponse.getSuccess()) {
            return BeanfunStringResult.error(AbstractBeanfunResult.CodeEnum.REQUEST_ERROR);
        }
        content = httpResponse.getContent();
        String pwd = decrDesPkcs5Hex(content);
        return BeanfunStringResult.success(pwd);
    }

    @Override
    public void loginOut(String token) throws Exception {

    }

    @Override
    public void uninitialize() {

    }

    @Override
    public int getGamePoints(String token) throws Exception {
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
    public String getWebUrlMemberTopUp(String token) {
        return null;
    }

    @Override
    public String getWebUrlMemberCenter(String token) {
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
    public boolean heartbeat(String token) {
        return false;
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

        QsHttpResponse httpResponse = HttpClient.get(url, params);
        if (!httpResponse.getSuccess()) {
            return null;
        }
        String content = httpResponse.getContent();
        List<List<String>> dataList = RegexUtils.regex(RegexUtils.PatternHongKong.GET_SERVICE_CREATE_TIME.getPattern(), content);
        String time = RegexUtils.getIndex(0, 1, dataList);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
    }
}
