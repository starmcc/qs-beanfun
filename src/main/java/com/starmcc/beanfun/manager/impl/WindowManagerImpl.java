package com.starmcc.beanfun.manager.impl;

import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.dll.CustomUser32;
import com.starmcc.beanfun.entity.client.QsHttpResponse;
import com.starmcc.beanfun.entity.model.ConfigModel;
import com.starmcc.beanfun.manager.AdvancedTimerMamager;
import com.starmcc.beanfun.manager.WindowManager;
import com.sun.jna.platform.win32.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * 窗口管理器实现
 *
 * @author starmcc
 * @date 2022/09/23
 */
@Slf4j
public class WindowManagerImpl implements WindowManager {

    /**
     * 扫描秒数
     */
    private static final int SCANNER_SECONDS = 1000 * 10;
    private static final int WM_KEYDOWN = 0X100;
    private static final int WM_LBUTTONDOWN = 0x0201;
    private static final byte VK_BACK = 0x0008;
    private static final byte VK_TAB = 0x0009;
    private static final byte VK_ENTER = 0x000D;
    private static final byte VK_ESCAPE = 0x001B;
    private static final byte VK_END = 0x0023;

    @Override
    public WinDef.HWND getMapleStoryHawd() {
        return User32.INSTANCE.FindWindow("MapleStoryClassTW", "MapleStory");
    }

    @Override
    public boolean checkMapleStoryRunning() {
        return Objects.nonNull(this.getMapleStoryHawd());
    }

    @Override
    public boolean checkVcRuntimeEnvironment() {
        String path = "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\";
        boolean exists = Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE, path, WinNT.KEY_READ);
        if (!exists) {
            // 如果找不到该key，则不进行校验
            return true;
        }
        String[] softwares = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, path, WinNT.KEY_READ);
        if (ArrayUtils.isEmpty(softwares)) {
            // 如果获取不到软件列表,则不进行校验
            return true;
        }
        for (String software : softwares) {
            Map<String, Object> map = Advapi32Util.registryGetValues(WinReg.HKEY_LOCAL_MACHINE, path + software, WinNT.KEY_READ);
            Object val = map.get("DisplayName");
            if (Objects.isNull(val)) {
                continue;
            }
            String valStr = (String) val;
            if (StringUtils.indexOf(valStr, "Microsoft Visual C++ 20") >= 0) {
                return true;
            }
        }
        return false;
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
                    if (hwnd != null || System.currentTimeMillis() - time > SCANNER_SECONDS) {
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
                    if (patcherPid != 0 || System.currentTimeMillis() - time > SCANNER_SECONDS) {
                        // 进程PID找到了或超过设置的等待秒数，结束该线程池
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
        WinDef.HWND hwnd = this.getMapleStoryHawd();
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
        WinDef.HWND hwnd = this.getMapleStoryHawd();
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

    @Override
    public HttpHost getPacScriptProxy(URI uri) {
        // 如果有自定义配置的代理，优先使用配置代理
        ConfigModel.ProxyConfig proxyConfig = QsConstant.config.getProxyConfig();
        if (Objects.nonNull(proxyConfig)) {
            if (BooleanUtils.isTrue(proxyConfig.getBan())) {
                log.info("禁止使用PAC代理");
                return null;
            }
            if (StringUtils.isNotBlank(proxyConfig.getIp()) && Objects.nonNull(proxyConfig.getPort())) {
                log.info("use proxy my custom value = {}", proxyConfig.toString());
                return new HttpHost(proxyConfig.getIp(), proxyConfig.getPort(), "http");
            }
        }
        String agent = getPACScriptAgent(uri.toString());
        if (StringUtils.isBlank(agent)) {
            return null;
        }
        String[] split = agent.split(":");
        if (ArrayUtils.isEmpty(split) || split.length < 2) {
            return null;
        }
        log.info("使用PAC代理={}", agent);
        return new HttpHost(split[0], Integer.valueOf(split[1]), "http");
    }


    private static String getPACScriptAgent(String url) {
        try {
            String path = "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
            boolean exists = Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, path, "AutoConfigURL", WinNT.KEY_READ);
            if (!exists) {
                return "";
            }
            String proxyUrl = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, path, "AutoConfigURL", WinNT.KEY_READ);
            if (StringUtils.isBlank(proxyUrl)) {
                log.info("not pac proxy");
                return "";
            }
            CloseableHttpClient httpClient =  HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(proxyUrl);
            CloseableHttpResponse execute = httpClient.execute(httpGet);
            String pacScript = EntityUtils.toString(execute.getEntity());
            if (StringUtils.isBlank(pacScript)) {
                log.info("read pac proxy file is null");
                return "";
            }
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
            engine.eval(pacScript);
            //调用js中的方法
            URI uri = new URI(url);
            Object runResult = ((Invocable) engine).invokeFunction("FindProxyForURL", uri.toString(), uri.getHost());
            if (Objects.isNull(runResult)) {
                log.info("url:{} runing FindProxyForURL is null result", url);
                return "";
            }
            String pac = String.valueOf(runResult);
            log.info("pac={}", pac);
            // DIRECT 不代理
            if (StringUtils.indexOf(pac, "DIRECT") >= 0) {
                return "";
            }
            String[] split = pac.split(";");
            for (String pxy : split) {
                String[] pxyArr = pxy.trim().split(" ");
                if (ArrayUtils.isEmpty(pxyArr) || pxyArr.length < 2) {
                    continue;
                }
                if (!StringUtils.equals(pxyArr[0].trim(), "PROXY")) {
                    continue;
                }
                return pxyArr[1].trim();
            }
        } catch (Exception e) {
            log.error("proxy error = {}", e.getMessage(), e);
        }
        return "";
    }


    @Override
    public boolean killBlackXchg() {
        try {
            Runtime.getRuntime().exec("taskkill /f /im BlackXchg.aes");
        } catch (IOException e) {
            log.error("ngs error={}", e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean openSystemCalc() {
        try {
            Runtime.getRuntime().exec("calc");
        } catch (IOException e) {
            log.error("ngs error={}", e.getMessage(), e);
            return false;
        }
        return true;
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
