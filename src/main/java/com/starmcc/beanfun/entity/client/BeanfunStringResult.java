package com.starmcc.beanfun.entity.client;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @Author starmcc
 * @Date 2022/6/2 9:48
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class BeanfunStringResult extends BeanfunResult implements Serializable {
    private static final long serialVersionUID = 8770080225604858074L;

    private String data;
}
