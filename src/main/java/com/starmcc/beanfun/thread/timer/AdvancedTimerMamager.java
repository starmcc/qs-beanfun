package com.starmcc.beanfun.thread.timer;

import com.starmcc.beanfun.utils.DataTools;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 高级定时任务
 *
 * @author xm
 * @date 2022/09/20
 */
public class AdvancedTimerMamager {
    private ScheduledExecutorService scheduledExecutorService;
    private List<AdvancedTimerTask> taskList;
    private static AdvancedTimerMamager advancedTimerMamager;

    /**
     * 获得单例
     *
     * @return {@link AdvancedTimerMamager}
     */
    public synchronized static AdvancedTimerMamager getInstance() {
        if (Objects.isNull(advancedTimerMamager)) {
            advancedTimerMamager = new AdvancedTimerMamager();
        }
        return advancedTimerMamager;
    }


    private AdvancedTimerMamager() {
        int coreSize = Runtime.getRuntime().availableProcessors() * 2;
        coreSize = Math.min(8, Math.max(4, coreSize));
        this.scheduledExecutorService = new AdvancedScheduledThreadPoolExecutor(coreSize);
        this.taskList = new ArrayList<>();
    }

    /**
     * 添加任务
     *
     * @param task     任务
     * @param waitTime 等待时间
     * @param delay    延迟
     */
    public synchronized String addTask(AdvancedTimerTask task, long waitTime, long delay) {
        String taskName = "TASK-" + System.currentTimeMillis();
        task.setTaskName(taskName);
        advancedTimerMamager.taskList.add(task);
        advancedTimerMamager.scheduledExecutorService.scheduleWithFixedDelay(task, waitTime, delay, TimeUnit.MILLISECONDS);
        return taskName;
    }


    /**
     * 删除任务
     *
     * @param taskName 任务名称
     */
    public synchronized boolean removeTask(String taskName) {
        if (StringUtils.isBlank(taskName)) {
            return false;
        }
        Iterator<AdvancedTimerTask> iterator = advancedTimerMamager.taskList.iterator();
        while (iterator.hasNext()) {
            AdvancedTimerTask task = iterator.next();
            if (StringUtils.equals(task.getTaskName(), taskName)) {
                task.cancel(false);
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * 删除任务
     *
     * @param taskName 任务名称
     */
    public synchronized int removeTask(List<String> taskNames) {
        if (DataTools.collectionIsEmpty(taskNames)) {
            return 0;
        }
        int result = 0;
        Iterator<AdvancedTimerTask> iterator = advancedTimerMamager.taskList.iterator();
        while (iterator.hasNext()) {
            AdvancedTimerTask task = iterator.next();
            if (taskNames.contains(task.getTaskName())) {
                task.cancel(false);
                iterator.remove();
                result++;
            }
        }
        return result;
    }

    /**
     * 删除所有任务
     */
    public synchronized int removeAllTask() {
        int result = advancedTimerMamager.taskList.size();
        advancedTimerMamager.taskList.forEach(task -> task.cancel(false));
        advancedTimerMamager.taskList.clear();
        return result;
    }


    /**
     * 关闭
     */
    public synchronized static void shutdown() {
        advancedTimerMamager.removeAllTask();
        advancedTimerMamager.scheduledExecutorService.shutdownNow();
        advancedTimerMamager = null;
    }
}
