package com.starmcc.beanfun.client;

import com.starmcc.beanfun.client.impl.HKBeanfunClientImpl;
import com.starmcc.beanfun.client.impl.TWBeanfunClientImpl;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.LoginType;
import com.starmcc.beanfun.model.client.Account;
import com.starmcc.beanfun.model.client.BeanfunAccountResult;
import com.starmcc.beanfun.model.client.BeanfunStringResult;
import com.starmcc.beanfun.utils.DesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * beanfun客户端
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public abstract class BeanfunClient {

    private static BeanfunClient beanfunClient = null;

    public static BeanfunClient run() {
        LoginType.TypeEnum type = LoginType.TypeEnum.getData(QsConstant.config.getLoginType());
        if (type == LoginType.TypeEnum.HK) {
            if (Objects.isNull(beanfunClient) || !(beanfunClient instanceof HKBeanfunClientImpl)) {
                beanfunClient = new HKBeanfunClientImpl();
            }
        } else {
            if (Objects.isNull(beanfunClient) || !(beanfunClient instanceof TWBeanfunClientImpl)) {
                beanfunClient = new TWBeanfunClientImpl();
            }
        }
        return beanfunClient;
    }

    /**
     * 获得会话密钥
     *
     * @return {@link String}
     */
    public abstract String getSessionKey() throws Exception;

    /**
     * 登录
     *
     * @param account  账户
     * @param password 密码
     * @param process  登录进度
     * @throws Exception 异常
     */
    public abstract BeanfunStringResult login(String account, String password, Consumer<Double> process) throws Exception;


    /**
     * 获得账户列表
     *
     * @return {@link List}<{@link Account}>
     * @throws Exception 异常
     */
    public abstract BeanfunAccountResult getAccountList(String token) throws Exception;


    /**
     * 获取动态密码
     *
     * @param account 账户
     * @return {@link String}
     * @throws Exception 异常
     */
    public abstract BeanfunStringResult getDynamicPassword(Account account, String token) throws Exception;


    /**
     * 登出
     *
     * @throws Exception 异常
     */
    public abstract void loginOut(String token) throws Exception;


    /**
     * 获取游戏点数
     *
     * @return int
     * @throws Exception 异常
     */
    public abstract int getGamePoints(String token) throws Exception;


    /**
     * 添加账户
     *
     * @param accountId 帐户id
     * @param newName   新名字
     * @return boolean
     * @throws Exception 异常
     */
    public abstract BeanfunStringResult addAccount(String newName) throws Exception;

    /**
     * 更改账户名称
     *
     * @param accountId 帐户id
     * @param newName   新名字
     * @return boolean
     * @throws Exception 异常
     */
    public abstract BeanfunStringResult changeAccountName(String accountId, String newName) throws Exception;

    /**
     * 获取web url会员充值
     *
     * @return {@link String}
     */
    public abstract String getWebUrlMemberTopUp(String token);


    /**
     * 获取web url成员中心
     *
     * @return {@link String}
     */
    public abstract String getWebUrlMemberCenter(String token);


    /**
     * 获取web url服务中心
     *
     * @return {@link String}
     */
    public abstract String getWebUrlServiceCenter();

    /**
     * 获取web url账号注册
     *
     * @return {@link String}
     */
    public abstract String getWebUrlRegister();


    /**
     * 获取web url忘记密码
     *
     * @return {@link String}
     */
    public abstract String getWebUrlForgotPwd();


    /**
     * 心跳
     */
    public abstract boolean heartbeat(String token) throws Exception;


    /**
     * 解密des pkcs5 hex密文
     *
     * @param text 文本
     * @return {@link String}
     */
    protected String decrDesPkcs5Hex(String text) {
        log.debug("开始解密 val={}", text);
        if (StringUtils.isBlank(text)) {
            log.error("解密失败 val is null");
            return "";
        }
        String[] split = text.split(";");
        if (ArrayUtils.isEmpty(split) || split.length < 2) {
            log.error("解密失败 val arr is empty or arr length < 2");
            return "";
        }
        String key = split[1].substring(0, 8);
        String deVal = split[1].substring(8);
        try {
            return DesUtils.decrypt(deVal, key).trim();
        } catch (Exception e) {
            log.error("解密失败 e={}", e.getMessage());
            return "";
        }
    }
}
