package com.starmcc.beanfun.dll;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.W32APIOptions;

public interface CustomUser32 extends User32 {

    CustomUser32 INSTANCE = Native.load("user32", CustomUser32.class, W32APIOptions.DEFAULT_OPTIONS);

    byte MapVirtualKey(byte wCode, int wMap);

    int ClientToScreen(HWND hwnd, POINT point);

}
