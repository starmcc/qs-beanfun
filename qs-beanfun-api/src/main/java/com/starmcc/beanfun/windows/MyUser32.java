package com.starmcc.beanfun.windows;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.W32APIOptions;

public interface MyUser32 extends User32 {

    MyUser32 INSTANCE = Native.load("user32", MyUser32.class, W32APIOptions.DEFAULT_OPTIONS);

    byte MapVirtualKey(byte wCode, int wMap);

    int ClientToScreen(HWND hwnd, POINT point);

}
