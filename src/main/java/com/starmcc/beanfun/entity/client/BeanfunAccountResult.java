package com.starmcc.beanfun.entity.client;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author starmcc
 * @Date 2022/6/2 11:00
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class BeanfunAccountResult extends AbstractBeanfunResult implements Serializable {
    private static final long serialVersionUID = -7146203696662330129L;

    /**
     * 账户列表
     */
    private List<Account> accountList;
    /**
     * 新帐户
     */
    private Boolean newAccount;
    /**
     * 进阶认证状态
     */
    private Boolean certStatus;
    /**
     * 最大创建账号数量
     */
    private Integer maxActNumber;

    public BeanfunAccountResult() {
        this(CodeEnum.SUCCESS);
    }

    protected BeanfunAccountResult(CodeEnum codeEnum) {
        super(codeEnum);
        this.newAccount = false;
        this.certStatus = true;
        this.maxActNumber = 0;
        this.accountList = new ArrayList<>();
    }


    public static BeanfunAccountResult error(CodeEnum codeEnum) {
        return new BeanfunAccountResult(codeEnum);
    }


}
