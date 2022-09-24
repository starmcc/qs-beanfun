package com.starmcc.beanfun.manager.impl;

import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 高级调度线程池执行类
 *
 * @author starmcc
 * @date 2022/09/21
 */
public class AdvancedScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    public AdvancedScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        if (runnable instanceof AdvancedTimerTask) {
            ((AdvancedTimerTask) runnable).scheduledFuture = task;
        }
        return task;
    }
}