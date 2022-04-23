package com.starmcc.beanfun.client;

import com.starmcc.beanfun.client.impl.HkBeanfunClient;
import com.starmcc.beanfun.model.Account;
import com.starmcc.beanfun.utils.DesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * beanfun客户端
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public abstract class BeanfunClient {

    public static String token;
    public static String errorMsg;
    public static List<Account> accountList;
    public static boolean isNewAccount = false;
    public static double loginProcess = 0;

    private static BeanfunClient beanfunClient = null;

    public synchronized static BeanfunClient getInstance() {
        if (Objects.nonNull(beanfunClient)) {
            return beanfunClient;
        }
        beanfunClient = new HkBeanfunClient();
        return beanfunClient;
    }


    /**
     * 登录
     *
     * @param account  账户
     * @param password 密码
     * @return boolean
     * @throws Exception 异常
     */
    public abstract boolean login(String account, String password) throws Exception;

    /**
     * 获得账户列表
     *
     * @return {@link List}<{@link Account}>
     * @throws Exception 异常
     */
    public abstract boolean getAccountList() throws Exception;


    /**
     * 获取BfToken
     *
     * @param token 令牌
     * @return {@link String}
     */
    public abstract String getBfToken();


    /**
     * 获取动态密码
     *
     * @param account 账户
     * @return {@link String}
     * @throws Exception 异常
     */
    public abstract String getDynamicPassword(Account account) throws Exception;


    /**
     * 登出
     *
     * @throws Exception 异常
     */
    public abstract void loginOut() throws Exception;

    /**
     * 退出Beanfun元件
     */
    public abstract void uninitialize();


    /**
     * 获取游戏点数
     *
     * @return int
     * @throws Exception 异常
     */
    public abstract int getGamePoints() throws Exception;


    /**
     * 添加账户
     *
     * @param accountId 帐户id
     * @param newName   新名字
     * @return boolean
     * @throws Exception 异常
     */
    public abstract boolean addAccount(String newName) throws Exception;

    /**
     * 更改账户名称
     *
     * @param accountId 帐户id
     * @param newName   新名字
     * @return boolean
     * @throws Exception 异常
     */
    public abstract boolean changeAccountName(String accountId, String newName) throws Exception;

    /**
     * 获取web url会员充值
     *
     * @return {@link String}
     */
    public abstract String getWebUrlMemberTopUp();

    /**
     * 获取web url成员中心
     *
     * @return {@link String}
     */
    public abstract String getWebUrlMemberCenter();


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
    public abstract boolean heartbeat();

    // ========================================= 公共方法 ==============================================


    /**
     * 解密 des pkcs5 hex
     *
     * @param text 文本
     * @return {@link String}
     */
    protected String decrDesPkcs5Hex(String text) {
        log.debug("开始解密 val={}", text);
        if (StringUtils.isBlank(text)) {
            errorMsg = "解密失败 空的解密值!";
            log.error("解密失败 val is null");
            return "";
        }
        String[] split = text.split(";");
        if (ArrayUtils.isEmpty(split) || split.length < 2) {
            errorMsg = "解密失败 解析错误!";
            log.error("解密失败 val arr is empty or arr length < 2");
            return "";
        }
        String key = split[1].substring(0, 8);
        String deVal = split[1].substring(8);
        try {
            return DesUtils.decrypt(deVal, key);
        } catch (Exception e) {
            log.error("解密失败 e={}", e.getMessage());
            errorMsg = "解密异常:" + e.getMessage();
            return "";
        }
    }


}
