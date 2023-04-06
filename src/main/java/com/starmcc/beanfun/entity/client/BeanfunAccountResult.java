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
public class BeanfunAccountResult extends BeanfunResult implements Serializable {
    private static final long serialVersionUID = -7146203696662330129L;

    /**
     * 账户列表
     */
    private List<Account> accountList = new ArrayList<>();
    /**
     * 新帐户
     */
    private Boolean newAccount = false;
    /**
     * 进阶认证状态
     */
    private Boolean certStatus = true;
    /**
     * 最大创建账号数量
     */
    private Integer maxActNumber = 0;

}
