package com.starmcc.beanfun.entity.client;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @Author starmcc
 * @Date 2022/6/2 10:28
 */
@Slf4j
@Data
public abstract class BeanfunResult {

    private String msg;
    private Integer code;

    protected BeanfunResult() {
        this(CodeEnum.SUCCESS);
    }

    protected BeanfunResult(CodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getDesc();
    }

    protected BeanfunResult(CodeEnum codeEnum, String errorMsg) {
        this.code = codeEnum.getCode();
        this.msg = errorMsg;
    }

    public boolean isSuccess() {
        return Objects.equals(this.code, CodeEnum.SUCCESS.getCode())
                || Objects.equals(this.code, CodeEnum.LOGIN_ADV_VERIFY.getCode());
    }

    public <T extends BeanfunResult> T success() {
        this.setMsg(CodeEnum.SUCCESS.getDesc());
        this.setCode(CodeEnum.SUCCESS.getCode());
        return (T) this;
    }

    public <T extends BeanfunResult> T success(CodeEnum codeEnum) {
        this.setMsg(codeEnum.getDesc());
        this.setCode(codeEnum.getCode());
        return (T) this;
    }
        public <T extends BeanfunResult> T success(CodeEnum codeEnum, String msg) {
        this.setMsg(msg);
        this.setCode(codeEnum.getCode());
        return (T) this;
    }

    public <T extends BeanfunResult> T error(CodeEnum codeEnum) {
        return error(codeEnum.getCode(), codeEnum.getDesc());
    }

    public <T extends BeanfunResult> T error(CodeEnum codeEnum, String errMsg) {
        return error(codeEnum.getCode(), errMsg);
    }

    public <T extends BeanfunResult> T error(Integer code, String errMsg) {
        this.setMsg(errMsg);
        this.setCode(code);
        return (T) this;
    }

    /**
     * 枚举代码
     *
     * @author starmcc
     * @date 2022/06/02
     */
    @Getter
    public static enum CodeEnum {
        /**
         * 请求错误
         */
        REQUEST_ERROR(-2, "网络请求失败,请关闭加速器或更换节点再重试!"),
        /**
         * 错误
         */
        ERROR(-1, "未知错误!"),
        /**
         * 成功
         */
        SUCCESS(0, ""),
        /**
         * 账密为空
         */
        ACT_PWD_IS_NULL(1, "登录账号和密码不能为空!"),
        /**
         * 账密为空
         */
        IP_BAN(2, "IP被锁定,请关闭加速器或更换节点再重试!"),
        /**
         * otp得到空
         */
        OTP_GET_EMPTY(3, "OTP获取失败,请关闭加速器或更换节点再重试!"),
        /**
         * otp签名错误
         */
        OTP_SIGN_GET_ERROR(4, "OTP签名获取失败,请关闭加速器或更换节点再重试!"),
        /**
         * beanfun请求错误
         */
        BEANFUN_ERROR(5, "beanfun请求错误"),
        /**
         * 账密错误
         */
        ACT_PWD_ERROR(6, "登录失败,请检查账号或密码是否正确!"),
        /**
         * 获取的账号数据为空
         */
        GET_ACT_LIST_EMPTY(7, "获取的账号数据为空!"),
        /**
         * 获取动态密码失败!账号信息不存在
         */
        GET_DYNAMIC_PWD_ACT_INFO_EMPTY(8, "获取动态密码失败,账号信息不存在!"),
        /**
         * 账号操作异常
         */
        ACCOUNT_OPT_EXCEPTION(9, "账号操作异常!"),
        /**
         * 登录错误
         */
        LOGIN_ERROR_MSG(10, "登录错误"),
        /**
         * 获取动态密码失败!账号信息不存在
         */
        GET_DYNAMIC_PWD_ERROR(11, "获取动态密码失败,解密失败!"),
        /**
         * 需要进阶认证
         */
        CERT_VERIFY(12, "账号需要进阶认证才可以使用,请在菜单栏进入用户中心->会员中心进行认证!"),
        /**
         * 禁止区域
         */
        AREA_BAN(13, "您所在的区域不允许登录!"),
        /**
         * 双重验证失败
         */
        DUAL_VERIFICATIONS_ERROR(14, "双重验证失败!"),
        LOGIN_ADV_VERIFY(15, "进阶登录验证"),
        LOGIN_ADV_VERIFY_ERROR(16, "进阶登录验证"),
        ;

        private final int code;
        private final String desc;

        CodeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        CodeEnum(int code) {
            this.code = code;
            this.desc = "";
        }


    }
}
