package com.starmcc.beanfun.windows;

import com.starmcc.beanfun.windows.impl.MapleStoryAPIServiceImpl;

import java.util.function.Consumer;

public interface MapleStoryAPIService {

    /**
     * 关闭新枫之谷启动窗口
     *
     * @return int
     */
    void closeMapleStoryStart();


    /**
     * 阻止游戏自动更新
     *
     * @param consumer 消费者
     */
    void stopAutoPatcher(Consumer<Process> consumer);

    /**
     * 新枫之谷前景窗口
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
     * 获得实例
     *
     * @return {@link MapleStoryAPIService}
     */
    public static MapleStoryAPIService getInstance() {
        return new MapleStoryAPIServiceImpl();
    }
}
