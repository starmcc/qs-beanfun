package com.starmcc.beanfun.model.thread;

/**
 * runnable2
 *
 * @author starmcc
 * @date 2022/09/21
 */
@FunctionalInterface
public interface ThrowRunnable {
    /**
     * 运行
     *
     * @throws Exception 异常
     */
    void run() throws Exception;
}