package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.windows.MapleStoryAPIService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自动轮烧
 *
 * @author starmcc
 * @date 2022/03/29
 */
@Slf4j
@Component
public class AutoLunShaoHandler {

    private static ScheduledExecutorService EXECUTOR_SERVICE = null;

    private static final long LUN_HUI = 1001 * 60 * 2;
    private static final long RAN_SHAO = 1002 * 60 * 3;

    private static Date runTime;

    /**
     * 开始
     */
    public boolean start(final Integer lunHuiKey,final Integer ranShaoKey) {
        String lunHuiKeyStr = KeyEvent.getKeyText(lunHuiKey);
        String ranShaoKeyStr = KeyEvent.getKeyText(ranShaoKey);
        if (StringUtils.isBlank(lunHuiKeyStr) || StringUtils.isBlank(ranShaoKeyStr)) {
            return false;
        }
        // 启动轮烧
        log.info("正在启动自动轮烧 键位{}轮回，{}燃烧", lunHuiKeyStr, ranShaoKeyStr);
        runTime = new Date();
        createThreadPool();
        // 5秒后开始
        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            // 开始按键轮回
            try {
                // 自动聚焦游戏
                MapleStoryAPIService.getInstance().setMapleStoryForegroundWindow();
                new Robot().keyPress(lunHuiKey);
                log.info("自动轮烧按下了[{}]键", lunHuiKeyStr);
            } catch (Exception e) {
                log.error("按键B异常 e={}", e.getMessage(), e);
            }
        }, 5000, LUN_HUI, TimeUnit.MILLISECONDS);
        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            // 开始按键燃烧
            try {
                // 自动聚焦游戏
                MapleStoryAPIService.getInstance().setMapleStoryForegroundWindow();
                new Robot().keyPress(ranShaoKey);
                log.info("自动轮烧按下了[{}]键", ranShaoKeyStr);
            } catch (Exception e) {
                log.error("按键B异常 e={}", e.getMessage(), e);
            }
        }, 7000, RAN_SHAO, TimeUnit.MILLISECONDS);
        return true;
    }

    public long getRunTime() {
        if (Objects.isNull(runTime)) {
            return 0;
        }
        return runTime.getTime();
    }


    /**
     * 停止
     */
    public void stop() {
        // 停止轮烧
        EXECUTOR_SERVICE.shutdown();
        EXECUTOR_SERVICE = null;
        runTime = null;
        log.info("关闭自动轮烧");
    }


    /**
     * 创建线程池
     */
    private static void createThreadPool() {
        if (Objects.nonNull(EXECUTOR_SERVICE) && !EXECUTOR_SERVICE.isShutdown()) {
            return;
        }
        EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(2,
                new BasicThreadFactory.Builder().namingPattern("AutoLunShaoHandler-schedule-pool-%d").daemon(true).build());
    }


    /**
     * 获取日期dhms
     *
     * @param milliseconds 毫秒
     * @return {@link String}
     */
    private static String getDateDHMS(long milliseconds) {
        final long day = TimeUnit.MILLISECONDS.toDays(milliseconds);

        final long hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));

        final long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));

        final long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        return day + "天 " + hours + "时 " + minutes + "分 " + seconds + "秒 ";
    }
}
