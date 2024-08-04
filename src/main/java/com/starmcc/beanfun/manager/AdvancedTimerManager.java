package com.starmcc.beanfun.manager;

import com.starmcc.beanfun.manager.impl.AdvancedScheduledThreadPoolExecutor;
import com.starmcc.beanfun.manager.impl.AdvancedTimerTask;
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
 * @author starmcc
 * @date 2022/09/20
 */
public class AdvancedTimerManager {

    private final ScheduledExecutorService scheduledExecutorService;
    private final List<AdvancedTimerTask> taskList;
    private static AdvancedTimerManager advancedTimerManager;

    /**
     * 获得单例
     *
     * @return {@link AdvancedTimerManager}
     */
    public static AdvancedTimerManager getInstance() {
        if (Objects.isNull(advancedTimerManager)) {
            advancedTimerManager = new AdvancedTimerManager();
        }
        return advancedTimerManager;
    }


    private AdvancedTimerManager() {
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
    public String addTask(AdvancedTimerTask task, long waitTime, long delay) {
        String taskName = "TASK-" + System.currentTimeMillis();
        task.setTaskName(taskName);
        advancedTimerManager.taskList.add(task);
        advancedTimerManager.scheduledExecutorService.scheduleWithFixedDelay(task, waitTime, delay, TimeUnit.MILLISECONDS);
        return taskName;
    }


    /**
     * 删除任务
     *
     * @param taskName 任务名称
     */
    public void removeTask(String taskName) {
        if (StringUtils.isBlank(taskName)) {
            return;
        }
        Iterator<AdvancedTimerTask> iterator = advancedTimerManager.taskList.iterator();
        while (iterator.hasNext()) {
            AdvancedTimerTask task = iterator.next();
            if (StringUtils.equals(task.getTaskName(), taskName)) {
                task.cancel(false);
                iterator.remove();
                return;
            }
        }
    }

    /**
     * 删除任务
     *
     * @param taskNames 任务名称
     */
    public void removeTask(List<String> taskNames) {
        if (DataTools.collectionIsEmpty(taskNames)) {
            return;
        }
        Iterator<AdvancedTimerTask> iterator = advancedTimerManager.taskList.iterator();
        while (iterator.hasNext()) {
            AdvancedTimerTask task = iterator.next();
            if (taskNames.contains(task.getTaskName())) {
                task.cancel(false);
                iterator.remove();
            }
        }
    }

    /**
     * 删除所有任务
     */
    public void removeAllTask() {
        advancedTimerManager.taskList.forEach(task -> task.cancel(false));
        advancedTimerManager.taskList.clear();
    }


    /**
     * 关闭
     */
    public static void shutdown() {
        advancedTimerManager.removeAllTask();
        advancedTimerManager.scheduledExecutorService.shutdownNow();
        advancedTimerManager = null;
    }
}
