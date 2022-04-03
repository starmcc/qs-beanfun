package com.starmcc.beanfun.model;

import lombok.Data;

import java.util.Date;

@Data
public class Account {

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
