package com.starmcc.beanfun.windows.impl;

import com.starmcc.beanfun.windows.MapleStoryAPIService;
import com.starmcc.beanfun.windows.MyUser32;
import com.sun.jna.platform.win32.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 窗口服务impl
 *
 * @author starmcc
 * @date 2022/05/04
 */
@Slf4j
public class MapleStoryAPIServiceImpl implements MapleStoryAPIService {

    private static final int WM_KEYDOWN = 0X100;
    private static final int WM_LBUTTONDOWN = 0x0201;
    private static final byte VK_BACK = 0x0008;
    private static final byte VK_TAB = 0x0009;
    private static final byte VK_ENTER = 0x000D;
    private static final byte VK_ESCAPE = 0x001B;
    private static final byte VK_END = 0x0023;


    @Override
    public void closeMapleStoryStart() {
        final long time = System.currentTimeMillis();
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2,
                new BasicThreadFactory.Builder().namingPattern("closeMapleStoryStart-schedule-pool-%d").daemon(true).build());
        executorService.scheduleAtFixedRate(() -> {
            WinDef.HWND hwnd = null;
            try {
                hwnd = MyUser32.INSTANCE.FindWindow("StartUpDlgClass", "MapleStory");
                if (hwnd != null) {
                    MyUser32.INSTANCE.SetForegroundWindow(hwnd);
                    MyUser32.INSTANCE.PostMessage(hwnd, 0x10, new WinDef.WPARAM(0), new WinDef.LPARAM(0));
                }
            } catch (Exception e) {
                log.error("自动关闭Play窗口发生异常! e={}", e.getMessage(), e);
            } finally {
                if (hwnd != null || System.currentTimeMillis() - time > 5000) {
                    // 进程句柄找到了或超过5秒，结束该线程池
                    executorService.shutdownNow();
                }
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stopAutoPatcher(Consumer<Process> consumer) {
        final long time = System.currentTimeMillis();
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2,
                new BasicThreadFactory.Builder().namingPattern("closeMapleStoryStart-schedule-pool-%d").daemon(true).build());
        executorService.scheduleAtFixedRate(() -> {
            int patcherPid = 0;
            try {
                // 查找更新程序
                patcherPid = this.searchNameProcess("Patcher.exe");
                if (patcherPid == 0) {
                    return;
                }
                String cmdScript = String.format("taskkill /F /PID %d", patcherPid);
                consumer.accept(Runtime.getRuntime().exec(cmdScript));
            } catch (Exception e) {
                log.error("异常 e={}", e.getMessage(), e);
            } finally {
                if (patcherPid != 0 || System.currentTimeMillis() - time > 5000) {
                    // 进程PID找到了或超过5秒，结束该线程池
                    executorService.shutdownNow();
                }
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }


    @Deprecated
    private boolean closeProcess(int processId) {
        WinNT.HANDLE handle = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_QUERY_INFORMATION, false, processId);
        if (Objects.isNull(handle)) {
            return false;
        }
        boolean is = Kernel32.INSTANCE.TerminateProcess(handle, WinNT.PROCESS_TERMINATE);
        Kernel32.INSTANCE.CloseHandle(handle);
        return is;
    }

    private int searchNameProcess(String name) {
        WinNT.HANDLE hand = Kernel32.INSTANCE.CreateToolhelp32Snapshot(new WinDef.DWORD(2), new WinDef.DWORD(0));
        Tlhelp32.PROCESSENTRY32 processInfo = new Tlhelp32.PROCESSENTRY32();
        boolean exists = Kernel32.INSTANCE.Process32First(hand, processInfo);
        while (exists) {
            String processName = String.valueOf(processInfo.szExeFile);
            if (StringUtils.equals(processName.trim(), name.trim())) {
                Kernel32.INSTANCE.CloseHandle(hand);
                return processInfo.th32ProcessID.intValue();
            }
            exists = Kernel32.INSTANCE.Process32Next(hand, processInfo);
        }
        Kernel32.INSTANCE.CloseHandle(hand);
        return 0;
    }


    @Override
    public boolean setMapleStoryForegroundWindow() {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow("MapleStoryClassTW", "MapleStory");
        if (hwnd == null) {
            return false;
        }
        return MyUser32.INSTANCE.SetForegroundWindow(hwnd);
    }

    @Override
    public void autoInputActPwd(String act, String pwd) throws Exception {
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        dpi = dpi / 96;
        // 获取句柄
        WinDef.HWND hwnd = MyUser32.INSTANCE.FindWindow("MapleStoryClassTW", "MapleStory");
        // 前置游戏窗口
        MyUser32.INSTANCE.SetForegroundWindow(hwnd);
        // 获取鼠标位置
        WinDef.POINT point = new WinDef.POINT();
        MyUser32.INSTANCE.GetCursorPos(point);
        WinDef.POINT windowPoint = new WinDef.POINT();
        MyUser32.INSTANCE.ClientToScreen(hwnd, windowPoint);
        // 获取当前鼠标位置
        Point oldPoint = MouseInfo.getPointerInfo().getLocation();
        Thread.sleep(100);
        // 关闭错误提示框
        postEventKey(hwnd, WM_KEYDOWN, VK_ESCAPE);
        Robot rbt = new Robot();
        rbt.delay(100);
        rbt.mouseMove(windowPoint.x + (510 * dpi), windowPoint.y + (340 * dpi));
        rbt.delay(100);
        rbt.mousePress(16);
        rbt.delay(100);
        rbt.mouseRelease(16);
        rbt.delay(100);
        rbt.mouseMove(oldPoint.x, oldPoint.y);
        postEventKey(hwnd, WM_KEYDOWN, VK_END);
        for (int i = 0; i < 50; i++) {
            postEventKey(hwnd, WM_KEYDOWN, VK_BACK);
        }
        inputString(hwnd, act);
        postEventKey(hwnd, WM_KEYDOWN, VK_TAB);
        Thread.sleep(100);
        postEventKey(hwnd, WM_KEYDOWN, VK_END);
        for (int i = 0; i < 20; i++) {
            postEventKey(hwnd, WM_KEYDOWN, VK_BACK);
        }
        inputString(hwnd, pwd);
        Thread.sleep(100);
        postEventKey(hwnd, WM_KEYDOWN, VK_ENTER);
    }

    private static void inputString(WinDef.HWND hwnd, String val) {
        char[] chars = val.toCharArray();
        for (char ch : chars) {
            Integer vKey = Integer.valueOf(ch);
            MyUser32.INSTANCE.PostMessage(hwnd, 0x102, new WinDef.WPARAM(vKey), new WinDef.LPARAM(0));
        }
    }

    private static void postEventKey(WinDef.HWND hwnd, int wMsg, byte wParam) {
        byte lp = MyUser32.INSTANCE.MapVirtualKey(wParam, 0);
        MyUser32.INSTANCE.PostMessage(hwnd, wMsg, new WinDef.WPARAM(wParam), new WinDef.LPARAM(lp));
    }
}
