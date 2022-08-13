package com.starmcc.beanfun.model.client;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class BeanfunModel implements Serializable {

    private static final long serialVersionUID = 3333264336307425168L;
    /**
     * 令牌
     */
    private String token;
    /**
     * 账户列表
     */
    private List<Account> accountList;
    /**
     * 是新账户
     */
    private boolean isNewAccount;


    public BeanfunModel() {
        this.token = "";
        this.accountList = new ArrayList<>();
        this.isNewAccount = false;
    }
}
