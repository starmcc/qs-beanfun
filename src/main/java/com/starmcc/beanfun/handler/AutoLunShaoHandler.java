package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.manager.WindowManager;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
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
public class AutoLunShaoHandler {

    private static ScheduledExecutorService EXECUTOR_SERVICE = null;

    private static final long LUN_HUI = 1001 * 60 * 2;
    private static final long RAN_SHAO = 1002 * 60 * 3;

    private static Date runTime;

    private static final String RUN_TIPS = "是否启动自动轮烧？\n禁止商业用途\n" +
            "轮回技能放置在[{0}]键\n" +
            "燃烧技能放置在[{1}]键\n" +
            "点击确定后，将在5秒后启动...\n" +
            "再次点击会停止,会显示使用时长";

    /**
     * 开始
     */
    public static boolean start() {
        Integer lunHuiKey = QsConstant.config.getLunHuiKey();
        Integer ranShaoKey = QsConstant.config.getRanShaoKey();
        if (Objects.isNull(lunHuiKey) || Objects.isNull(ranShaoKey)) {
            // 没有设置键位
            QsConstant.alert("没有设置自动轮烧键位", Alert.AlertType.WARNING);
            return false;
        }
        String lunHuiKeyStr = KeyEvent.getKeyText(lunHuiKey);
        String ranShaoKeyStr = KeyEvent.getKeyText(ranShaoKey);
        String tips = MessageFormat.format(RUN_TIPS, lunHuiKeyStr, ranShaoKeyStr);
        if (!QsConstant.confirmDialog("自动轮烧", tips)) {
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
                WindowManager.getInstance().setMapleStoryForegroundWindow();
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
                WindowManager.getInstance().setMapleStoryForegroundWindow();
                new Robot().keyPress(ranShaoKey);
                log.info("自动轮烧按下了[{}]键", ranShaoKeyStr);
            } catch (Exception e) {
                log.error("按键B异常 e={}", e.getMessage(), e);
            }
        }, 7000, RAN_SHAO, TimeUnit.MILLISECONDS);
        return true;
    }

    /**
     * 停止
     */
    public static boolean stop() {
        if (Objects.isNull(runTime)) {
            return false;
        }
        long time = System.currentTimeMillis() - runTime.getTime();

        // 构建开始时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = sdf.format(runTime.getTime());
        StringBuffer tips = new StringBuffer();
        tips.append("开始时间:").append(startTime).append("\n");
        tips.append("当前已运行:").append(getDateDHMS(time)).append("\n");
        tips.append("是否现在停止？");
        String title = "自动轮烧";
        if (!QsConstant.confirmDialog(title, tips.toString())) {
            return false;
        }
        // 停止轮烧
        EXECUTOR_SERVICE.shutdown();
        EXECUTOR_SERVICE = null;
        runTime = null;
        log.info("关闭自动轮烧");
        return true;
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
