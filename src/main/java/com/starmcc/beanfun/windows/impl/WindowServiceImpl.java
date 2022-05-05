package com.starmcc.beanfun.windows.impl;

import com.starmcc.beanfun.windows.WindowService;
import com.sun.jna.Library;
import com.sun.jna.Native;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 窗口服务impl
 *
 * @author starmcc
 * @date 2022/05/04
 */
@Slf4j
public class WindowServiceImpl implements WindowService {


    @Override
    public void closeMapleStoryStart() {
        final long time = System.currentTimeMillis();
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2,
                new BasicThreadFactory.Builder().namingPattern("starGame-schedule-pool-%d").daemon(true).build());
        executorService.scheduleAtFixedRate(() -> {
            int hwnd = 0;
            try {
                hwnd = User32.RUN.FindWindowA("StartUpDlgClass", "MapleStory");
                if (hwnd != 0) {
                    User32.RUN.SetForegroundWindow(hwnd);
                    new Robot().keyPress(KeyEvent.VK_ESCAPE);
                }
            } catch (Exception e) {
                log.error("自动关闭Play窗口发生异常! e={}", e.getMessage(), e);
            } finally {
                if (hwnd != 0 || System.currentTimeMillis() - time > 10000) {
                    // 进程句柄找到了，超过10秒，结束该线程池
                    executorService.shutdownNow();
                }
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean setMapleStoryForegroundWindow() {
        int hwnd = User32.RUN.FindWindowA(null, "MapleStory");
        if (hwnd == 0) {
            return false;
        }
        return User32.RUN.SetForegroundWindow(hwnd);
    }


    /**
     * User32.dll win原生调用
     *
     * @author starmcc
     * @date 2022/05/04
     */
    public interface User32 extends Library {

        User32 RUN = (User32) Native.load("User32", User32.class);

        /**
         * 找到window窗口句柄
         *
         * @param lpClassName  lp类名
         * @param lpWindowName lp窗口名称
         * @return int 返回进程句柄
         */
        int FindWindowA(String lpClassName, String lpWindowName);

        /**
         * 帖子messagea
         *
         * @param hWnd   进程句柄
         * @param wMsg   消息类型
         * @param wParam 消息参数1
         * @param lParam 消息参数2
         * @return int
         */
        int PostMessageA(int hWnd, int wMsg, int wParam, int lParam);

        /**
         * 设置前景窗口
         *
         * @param hwnd 窗口句柄
         * @return boolean 是否成功
         */
        boolean SetForegroundWindow(int hwnd);
    }
}
