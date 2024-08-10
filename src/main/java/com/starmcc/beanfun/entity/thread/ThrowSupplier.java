package com.starmcc.beanfun.entity.thread;

@FunctionalInterface
public interface ThrowSupplier<T> {

    /**
     * 获取
     *
     * @throws Exception 异常
     */
    T get() throws Exception;


}
