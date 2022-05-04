package com.starmcc.beanfun.windows;

import com.starmcc.beanfun.windows.impl.WindowServiceImpl;

public interface WindowService {

    /**
     * 关闭新枫之谷启动窗口
     *
     * @return int
     */
    void closeMapleStoryStart();

    /**
     * 窗口设置焦点
     *
     * @param hwnd 窗口句柄
     * @return boolean
     */
    boolean setForegroundWindow(int hwnd);


    /**
     * 获得实例
     *
     * @return {@link WindowService}
     */
    public static WindowService getInstance() {
        return new WindowServiceImpl();
    }
}
