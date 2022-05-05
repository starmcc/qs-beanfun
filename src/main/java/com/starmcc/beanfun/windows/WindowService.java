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
     * 新枫之谷前景窗口
     *
     * @return boolean
     */
    boolean setMapleStoryForegroundWindow();


    /**
     * 获得实例
     *
     * @return {@link WindowService}
     */
    public static WindowService getInstance() {
        return new WindowServiceImpl();
    }
}
