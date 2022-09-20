package com.starmcc.beanfun.thread;

import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 窗口工具类
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public class ThreadPoolManager {

    private static ThreadPoolExecutor THREAD_POOL;

    /**
     * 初始化
     */
    public synchronized static void init() {
        if (!isShutdown()) {
            return;
        }
        THREAD_POOL = new ThreadPoolExecutor(2, 4,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new BasicThreadFactory.Builder().namingPattern("FrameUtils-schedule-pool-%d").daemon(true).build(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 执行
     *
     * @param runnable 可运行
     */
    public synchronized static void execute(@NotNull Runnable2 runnable) {
        execute(runnable, true);
    }

    /**
     * 执行
     *
     * @param runnable 可运行
     * @param init     初始化
     */
    public synchronized static void execute(@NotNull Runnable2 runnable, boolean init) {
        if (init) {
            init();
        }
        if (isShutdown()) {
            return;
        }
        THREAD_POOL.execute(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 关闭
     */
    public synchronized static void shutdown() {
        if (Objects.isNull(THREAD_POOL)) {
            return;
        }
        THREAD_POOL.shutdownNow();
        THREAD_POOL = null;
    }


    /**
     * 是否关闭
     *
     * @return boolean
     */
    public synchronized static boolean isShutdown() {
        if (Objects.isNull(THREAD_POOL)) {
            return true;
        }
        return THREAD_POOL.isTerminated();
    }

}
