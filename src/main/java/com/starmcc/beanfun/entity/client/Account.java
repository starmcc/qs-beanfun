package com.starmcc.beanfun.entity.client;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 账户
 *
 * @author starmcc
 * @date 2022/10/01
 */
@Data
public class Account implements Serializable {

    private static final long serialVersionUID = -5173758017991977906L;

    private String id;
    private Boolean status;
    private String sn;
    private String name;
    private Date createTime;
    private String authType;


    @Override
    public String toString() {
        return this.getName();
    }
}
