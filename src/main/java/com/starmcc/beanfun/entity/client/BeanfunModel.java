package com.starmcc.beanfun.entity.client;

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
    private boolean newAccount;
    /**
     * 进阶认证状态
     */
    private boolean certStatus;
    /**
     * 账号最大数量
     */
    private Integer maxActNumber;


    public BeanfunModel() {
        this.token = "";
        this.accountList = new ArrayList<>();
        this.newAccount = false;
        this.certStatus = true;
        this.maxActNumber = 0;
    }


    public BeanfunModel build(BeanfunAccountResult result) {
        this.accountList = result.getAccountList();
        this.newAccount = result.getNewAccount();
        this.maxActNumber = result.getMaxActNumber();
        this.certStatus = result.getCertStatus();
        return this;
    }
}
