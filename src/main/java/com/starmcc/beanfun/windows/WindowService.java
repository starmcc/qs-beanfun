package com.starmcc.beanfun.windows;

import com.starmcc.beanfun.windows.impl.WindowServiceImpl;
import org.apache.http.HttpHost;

import java.util.function.Consumer;

public interface WindowService {

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
     * @param url url
     * @return {@link HttpHost}
     */
    HttpHost getPacScriptProxy(String url);

    /**
     * 获得实例
     *
     * @return {@link WindowService}
     */
    public static WindowService getInstance() {
        return new WindowServiceImpl();
    }
}
