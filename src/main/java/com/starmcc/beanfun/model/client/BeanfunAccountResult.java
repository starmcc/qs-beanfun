package com.starmcc.beanfun.model.client;

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

    private List<Account> accountList;
    private Boolean newAccount;

    protected BeanfunAccountResult(CodeEnum codeEnum) {
        super(codeEnum);
    }

    public static BeanfunAccountResult success(List<Account> accountList, boolean newAccount) {
        BeanfunAccountResult result = new BeanfunAccountResult(CodeEnum.SUCCESS);
        result.setAccountList(accountList);
        result.setNewAccount(newAccount);
        return result;
    }

    public static BeanfunAccountResult error(CodeEnum codeEnum) {
        log.error("错误信息={}", codeEnum);
        BeanfunAccountResult result = new BeanfunAccountResult(codeEnum);
        result.setAccountList(new ArrayList<>());
        result.setNewAccount(false);
        return result;
    }


}
