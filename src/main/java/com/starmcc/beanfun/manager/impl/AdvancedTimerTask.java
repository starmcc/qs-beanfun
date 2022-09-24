package com.starmcc.beanfun.manager.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * 高级计时器任务
 *
 * @author starmcc
 * @date 2022/09/21
 */
@Slf4j
@Data
public abstract class AdvancedTimerTask<T> implements Runnable {

    volatile ScheduledFuture<?> scheduledFuture = null;

    private T resources;

    private String taskName;

    public abstract void start() throws Exception;

    public AdvancedTimerTask() {
    }


    public AdvancedTimerTask(T resources) {
        this.resources = resources;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        try {
            start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            log.info("task run time consuming {}/ms", System.currentTimeMillis() - startTime);
        }

    }

    /**
     * 取消定时任务
     */
    public void cancel(boolean running) {
        if (Objects.isNull(scheduledFuture)) {
            return;
        }
        scheduledFuture.cancel(running);
    }
}
