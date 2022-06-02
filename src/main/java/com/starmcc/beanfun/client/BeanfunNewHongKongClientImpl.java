package com.starmcc.beanfun.client;

import com.starmcc.beanfun.client.model.Account;
import com.starmcc.beanfun.client.model.BeanfunAccountResult;
import com.starmcc.beanfun.client.model.BeanfunStringResult;
import com.starmcc.beanfun.model.ReqParams;
import com.starmcc.beanfun.utils.HttpUtils;
import com.starmcc.beanfun.windows.BaseBFService;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

/**
 * @Author starmcc
 * @Date 2022/6/2 12:30
 */
public class BeanfunNewHongKongClientImpl extends BeanfunClient {
    @Override
    public BeanfunStringResult login(String account, String password, Consumer<Double> process) throws Exception {
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            return BeanfunStringResult.error(BeanfunStringResult.CodeEnum.ACT_PWD_IS_NULL);
        }
        process.accept(0D);
        boolean load = BaseBFService.getInstance().initialize2();
        if (!load) {
            return BeanfunStringResult.error(BeanfunStringResult.CodeEnum.PLUGIN_NOT_INSTALL);
        }
        process.accept(0.2D);
        // 1. 请求获取OTP
        String url = "";
        ReqParams params = ReqParams.getInstance().addParam("service", "999999_T0");
        url = "https://bfweb.hk.beanfun.com/beanfun_block/bflogin/default.aspx";
        String html = HttpUtils.get(url, params);
        return null;
    }

    @Override
    public BeanfunAccountResult getAccountList(String token) throws Exception {
        return null;
    }

    @Override
    public BeanfunStringResult getDynamicPassword(Account account, String token) throws Exception {
        return null;
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
}
