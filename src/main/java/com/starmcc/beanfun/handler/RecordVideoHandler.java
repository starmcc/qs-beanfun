package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.thread.ThreadPoolManager;
import com.starmcc.beanfun.thread.timer.AdvancedTimerMamager;
import com.starmcc.beanfun.thread.timer.AdvancedTimerTask;
import com.starmcc.beanfun.windows.RecordVideoManager;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 录像处理程序
 *
 * @author starmcc
 * @date 2022/09/21
 */
@Slf4j
public class RecordVideoHandler {

    /**
     * 时间间隔 5分钟保存一次录制
     */
    private static final long TIME_INTERVAL = 1000 * 60 * 5;

    private static List<String> taskNames = new ArrayList<>();

    public static boolean run(boolean status) {
        // 检查ffmpeg.exe是否存在
        File file = new File(QsConstant.config.getRecordVideo().getFfmpegPath());
        if (!file.exists()) {
            return false;
        }

        if (!status) {
            // 停止录像，则删除任务
            AdvancedTimerMamager.getInstance().removeTask(taskNames);
            taskNames.clear();
            // 停止录像
            RecordVideoManager.getInstance().stop();
            return true;
        }

        // 定时任务，5分钟自动保存一次,然后再次录制
        String taskName = AdvancedTimerMamager.getInstance().addTask(new AdvancedTimerTask() {
            @Override
            public void start() throws Exception {
                RecordVideoManager.getInstance().stop();
                // 停止后继续执行
                ThreadPoolManager.execute(() -> {
                    RecordVideoManager.getInstance().start(
                            QsConstant.config.getRecordVideo(), (str) -> log.debug("ffmpeg - {}", str));
                });
            }
        }, TIME_INTERVAL, TIME_INTERVAL);
        taskNames.add(taskName);

        // 开始执行
        ThreadPoolManager.execute(() -> {
            RecordVideoManager.getInstance().start(
                    QsConstant.config.getRecordVideo(), (str) -> log.debug("ffmpeg - {}", str));
        });
        return true;
    }


}
