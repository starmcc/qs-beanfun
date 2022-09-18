package com.starmcc.beanfun.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
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
public class ThreadUtils {

    public static ExecutorService THREAD_POOL = null;


    public static void executeThread(Runnable runnable) {
        if (Objects.isNull(THREAD_POOL) || THREAD_POOL.isShutdown()) {
            THREAD_POOL = new ThreadPoolExecutor(3, 6,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(2048),
                    new BasicThreadFactory.Builder().namingPattern("FrameUtils-schedule-pool-%d").daemon(true).build(),
                    new ThreadPoolExecutor.AbortPolicy());
        }
        THREAD_POOL.execute(runnable);
    }


}
