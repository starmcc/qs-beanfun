package com.starmcc.beanfun.thread;

@FunctionalInterface
public interface Runnable2 {
    /**
     * 运行
     *
     * @throws Exception 异常
     */
    void run() throws Exception;
}