package com.starmcc.beanfun.model.client;

import lombok.Data;
import lombok.Getter;

import java.util.Objects;

/**
 * @Author starmcc
 * @Date 2022/6/2 10:28
 */
@Data
public abstract class AbstractBeanfunResult {

    private String msg;
    private Integer code;

    protected AbstractBeanfunResult() {

    }

    protected AbstractBeanfunResult(CodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getDesc();
    }

    protected AbstractBeanfunResult(CodeEnum codeEnum, String errorMsg) {
        this.code = codeEnum.getCode();
        this.msg = errorMsg;
    }

    public boolean isSuccess() {
        return Objects.equals(this.code, CodeEnum.SUCCESS.getCode());
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
        REQUEST_ERROR(-2, "网络请求失败!"),
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
         * 插件没有安装
         */
        PLUGIN_NOT_INSTALL(2, "未安装Beanfun插件!"),
        /**
         * otp得到空
         */
        OTP_GET_EMPTY(3, "OTP获取失败!"),
        /**
         * otp签名错误
         */
        OTP_SIGN_GET_ERROR(4, "OTP签名获取失败!"),
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
