package com.starmcc.beanfun.manager;

import com.starmcc.beanfun.manager.impl.WindowManagerImpl;
import com.sun.jna.platform.win32.WinDef;
import org.apache.http.HttpHost;

import java.net.URI;
import java.util.function.Consumer;

/**
 * 窗口管理器
 *
 * @author starmcc
 * @date 2022/09/23
 */
public interface WindowManager {

    /**
     * 检查vc运行环境
     *
     * @return boolean
     */
    boolean checkVcRuntimeEnvironment();

    /**
     * 关闭新枫之谷启动窗口
     *
     * @return int
     */
    void closeMapleStoryStart();


    /**
     * 阻止游戏自动更新
     *
     * @param callback 回调
     */
    void stopAutoPatcher(Consumer<Process> callback);

    /**
     * 新枫之谷前置窗口
     *
     * @return boolean
     */
    boolean setMapleStoryForegroundWindow();

    /**
     * 获取新枫之谷窗口句柄
     *
     * @return {@link WinDef.HWND}
     */
    WinDef.HWND getMapleStoryHawd();

    /**
     * 检查新枫之谷是否已启动
     *
     * @return {@link WinDef.HWND}
     */
    boolean checkMapleStoryRunning();

    /**
     * 自动输入账号密码
     *
     * @param act 行为
     * @param pwd 松材线虫病
     * @throws Exception 异常
     */
    void autoInputActPwd(String act, String pwd) throws Exception;

    /**
     * 获取pac脚本代理
     *
     * @param uri uri
     * @return {@link HttpHost}
     */
    HttpHost getPacScriptProxy(URI uri);

    /**
     * 结束 NGS
     * 命令：taskkill /f /im BlackXchg.aes
     *
     * @return boolean
     */
    boolean killBlackXchg();

    /**
     * 打开系统计算器
     *
     * @return boolean
     */
    boolean openSystemCalc();

    /**
     * 获得实例
     *
     * @return {@link WindowManager}
     */
    public static WindowManager getInstance() {
        return new WindowManagerImpl();
    }
}
