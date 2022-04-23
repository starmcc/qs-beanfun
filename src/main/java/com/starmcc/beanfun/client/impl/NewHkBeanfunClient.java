package com.starmcc.beanfun.client.impl;

import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.model.Account;

/**
 * 新香港beanfun客户端
 *
 * @author starmcc
 * @date 2022/04/23
 */
public class NewHkBeanfunClient extends BeanfunClient {

    @Override
    public boolean login(String account, String password) throws Exception {
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
