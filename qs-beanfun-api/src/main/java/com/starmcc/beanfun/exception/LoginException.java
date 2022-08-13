package com.starmcc.beanfun.exception;

/**
 * 登录异常
 *
 * @author starmcc
 * @date 2022/06/11
 */
public class LoginException extends RuntimeException {

    public LoginException() {
        super("login err");
    }
}
