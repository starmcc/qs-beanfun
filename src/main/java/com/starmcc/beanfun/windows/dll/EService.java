package com.starmcc.beanfun.windows.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface EService extends Library {

    EService INSTANCE = Native.load("lib/base/EService.dll", EService.class);


    void openBrowser(String url, String agent, String cookies);
}
