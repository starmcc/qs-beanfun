package com.starmcc.beanfun.entity.thread;

/**
 * 消费者
 *
 * @author starmcc
 * @date 2022/10/08
 */
@FunctionalInterface
public interface ThrowConsumer<T> {

    /**
     * 运行
     *
     * @param t t
     * @throws Exception 异常
     */
    void run(T t) throws Exception;
}
