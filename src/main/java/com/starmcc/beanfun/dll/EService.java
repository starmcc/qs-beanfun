//package com.starmcc.beanfun.dll;
//
//import com.sun.jna.Library;
//import com.sun.jna.Native;
//
///**
// * eservice
// *
// * @author starmcc
// * @date 2022/09/25
// */
//public interface EService extends Library {
//
//    EService INSTANCE = Native.load("lib/base/EService.dll", EService.class);
//
//
//    /**
//     * 打开浏览器
//     *
//     * @param url     url
//     * @param agent   代理
//     * @param cookies 饼干
//     */
//    void openBrowser(String url, String agent, String cookies);
//
//
//    /**
//     * 获取PAC代理
//     *
//     * @param url url
//     * @return {@link String}
//     */
//    String getPACScriptAgent(String url);
//}
