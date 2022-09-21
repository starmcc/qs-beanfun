package com.starmcc.beanfun.windows.impl;

import com.starmcc.beanfun.thread.timer.AdvancedTimerMamager;
import com.starmcc.beanfun.thread.timer.AdvancedTimerTask;
import com.starmcc.beanfun.windows.WindowService;
import com.starmcc.beanfun.windows.dll.CustomUser32;
import com.sun.jna.platform.win32.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 窗口服务impl
 *
 * @author starmcc
 * @date 2022/05/04
 */
@Slf4j
public class WindowServiceImpl implements WindowService {

    private static final int WM_KEYDOWN = 0X100;
    private static final int WM_LBUTTONDOWN = 0x0201;
    private static final byte VK_BACK = 0x0008;
    private static final byte VK_TAB = 0x0009;
    private static final byte VK_ENTER = 0x000D;
    private static final byte VK_ESCAPE = 0x001B;
    private static final byte VK_END = 0x0023;

    @Override
    public boolean checkVcRuntimeEnvironment() {
        boolean is = false;
        String path = "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\";
        String[] softwares = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, path, WinNT.KEY_READ);
        if (ArrayUtils.isEmpty(softwares)) {
            return is;
        }
        for (String software : softwares) {
            Map<String, Object> map = Advapi32Util.registryGetValues(WinReg.HKEY_LOCAL_MACHINE, path + software);
            Object val = map.get("DisplayName");
            if (Objects.isNull(val)) {
                continue;
            }
            String valStr = (String) val;
            log.debug("scan regedit uninstall app DisplayName={}", valStr);
            if (StringUtils.indexOf("Microsoft Visual C++ 20", valStr) >= 0 && StringUtils.indexOf("Runtime", valStr) >= 0) {
                is = true;
                break;
            }
        }
        return is;
    }

    @Override
    public void closeMapleStoryStart() {
        // 200毫秒检查一次
        final long time = System.currentTimeMillis();
        AdvancedTimerMamager.getInstance().addTask(new AdvancedTimerTask() {
            @Override
            public void start() throws Exception {
                WinDef.HWND hwnd = null;
                try {
                    hwnd = CustomUser32.INSTANCE.FindWindow("StartUpDlgClass", "MapleStory");
                    if (hwnd != null) {
                        CustomUser32.INSTANCE.SetForegroundWindow(hwnd);
                        CustomUser32.INSTANCE.PostMessage(hwnd, 0x10, new WinDef.WPARAM(0), new WinDef.LPARAM(0));
                    }
                } catch (Exception e) {
                    log.error("自动关闭Play窗口发生异常! e={}", e.getMessage(), e);
                } finally {
                    if (hwnd != null || System.currentTimeMillis() - time > 5000) {
                        // 进程句柄找到了或超过5秒，结束该任务
                        AdvancedTimerMamager.getInstance().removeTask(this.getTaskName());
                    }
                }
            }
        }, 0, 200);
    }

    @Override
    public void stopAutoPatcher(Consumer<Process> callback) {
        final long time = System.currentTimeMillis();
        AdvancedTimerMamager.getInstance().addTask(new AdvancedTimerTask() {
            @Override
            public void start() throws Exception {
                int patcherPid = 0;
                try {
                    // 查找更新程序
                    patcherPid = this.searchNameProcess("Patcher.exe");
                    if (patcherPid == 0) {
                        return;
                    }
                    String cmdScript = String.format("taskkill /F /PID %d", patcherPid);
                    callback.accept(Runtime.getRuntime().exec(cmdScript));
                } catch (Exception e) {
                    log.error("异常 e={}", e.getMessage(), e);
                } finally {
                    if (patcherPid != 0 || System.currentTimeMillis() - time > 5000) {
                        // 进程PID找到了或超过5秒，结束该线程池
                        AdvancedTimerMamager.getInstance().removeTask(this.getTaskName());
                    }
                }
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
        }, 0, 200);
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


    @Override
    public boolean setMapleStoryForegroundWindow() {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow("MapleStoryClassTW", "MapleStory");
        if (hwnd == null) {
            return false;
        }
        return CustomUser32.INSTANCE.SetForegroundWindow(hwnd);
    }

    @Override
    public void autoInputActPwd(String act, String pwd) throws Exception {
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        dpi = dpi / 96;
        // 获取句柄
        WinDef.HWND hwnd = CustomUser32.INSTANCE.FindWindow("MapleStoryClassTW", "MapleStory");
        // 前置游戏窗口
        CustomUser32.INSTANCE.SetForegroundWindow(hwnd);
        // 获取鼠标位置
        WinDef.POINT point = new WinDef.POINT();
        CustomUser32.INSTANCE.GetCursorPos(point);
        WinDef.POINT windowPoint = new WinDef.POINT();
        CustomUser32.INSTANCE.ClientToScreen(hwnd, windowPoint);
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
            CustomUser32.INSTANCE.PostMessage(hwnd, 0x102, new WinDef.WPARAM(vKey), new WinDef.LPARAM(0));
        }
    }

    private static void postEventKey(WinDef.HWND hwnd, int wMsg, byte wParam) {
        byte lp = CustomUser32.INSTANCE.MapVirtualKey(wParam, 0);
        CustomUser32.INSTANCE.PostMessage(hwnd, wMsg, new WinDef.WPARAM(wParam), new WinDef.LPARAM(lp));
    }
}
