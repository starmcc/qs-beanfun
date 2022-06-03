package com.starmcc.beanfun.model.client;

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
public class BeanfunStringResult extends AbstractBeanfunResult implements Serializable {
    private static final long serialVersionUID = 8770080225604858074L;

    private String data;

    protected BeanfunStringResult(CodeEnum codeEnum) {
        super(codeEnum);
    }

    protected BeanfunStringResult(CodeEnum codeEnum, String errorMsg) {
        super(codeEnum, errorMsg);
    }

    public static BeanfunStringResult success() {
        BeanfunStringResult result = new BeanfunStringResult(CodeEnum.SUCCESS);
        result.setData("");
        return result;
    }

    public static BeanfunStringResult success(String data) {
        BeanfunStringResult result = new BeanfunStringResult(CodeEnum.SUCCESS);
        result.setData(data);
        return result;
    }

    public static BeanfunStringResult error(CodeEnum codeEnum) {
        log.error("错误信息={}", codeEnum);
        BeanfunStringResult result = new BeanfunStringResult(codeEnum);
        result.setData("");
        return result;
    }

    public static BeanfunStringResult error(CodeEnum codeEnum, String errorMsg) {
        log.error("错误信息={} {}", codeEnum, errorMsg);
        BeanfunStringResult result = new BeanfunStringResult(codeEnum, errorMsg);
        result.setData("");
        return result;
    }
}
