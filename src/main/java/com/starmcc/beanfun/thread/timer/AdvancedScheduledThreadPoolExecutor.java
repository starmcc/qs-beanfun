package com.starmcc.beanfun.thread.timer;

import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

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