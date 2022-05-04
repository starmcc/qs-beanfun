package com.starmcc.beanfun.windows.impl;

import com.starmcc.beanfun.windows.BaseBFService;
import com.sun.jna.Library;
import com.sun.jna.Native;

public class BFServiceJnaImpl implements BaseBFService {


    @Override
    public boolean initialize2() {
        if (!BFServiceX.RUN.loadState()){
            return false;
        }
        BFServiceX.RUN.initialize2("HK;Production", "", "", 0, "");
        return true;
    }

    @Override
    public String loadData(String zzKey) {
        return BFServiceX.RUN.loadData(zzKey);
    }

    @Override
    public int saveData(String szKey, String szValuue) {
        return BFServiceX.RUN.saveData(szKey, szValuue);
    }

    @Override
    public int getActivexObjId() {
        return BFServiceX.RUN.getActivexObjId();
    }

    @Override
    public int checkRegionInfo() {
        return BFServiceX.RUN.checkRegionInfo();
    }

    @Override
    public void uninitialize() {
        BFServiceX.RUN.uninitialize();
    }

    @Override
    public void setUpdatorVersion(String szVersion) {
        BFServiceX.RUN.setUpdatorVersion(szVersion);
    }

    @Override
    public String getVersion() {
        return BFServiceX.RUN.getVersion();
    }


    /**
     * bfservice dl
     *
     * @author starmcc
     * @date 2022/03/19
     */
    private static interface BFServiceX extends Library {

        BFServiceX RUN = (BFServiceX) Native.load("lib/BFService", BFServiceX.class);

        /**
         * “HK;Production”, “”, “”, 0, “”
         * 初始化
         *
         * @param szRegionVersion 地区版本
         * @param szNewerVersion  新版本
         * @param szChksum        chksum
         * @param dwTimeout       dw超时
         * @param szClientSite    客户网站
         * @return int
         */
        int initialize2(String szRegionVersion, String szNewerVersion, String szChksum, int dwTimeout, String szClientSite);


        /**
         * 加载数据
         *
         * @param zzKey zz关键
         * @return int
         */
        String loadData(String zzKey);


        /**
         * 保存数据
         *
         * @param szKey    深圳键
         * @param szValuue 深圳valuue
         * @return int
         */
        int saveData(String szKey, String szValuue);


        /**
         * 得到activex obj id
         *
         * @return int
         */
        int getActivexObjId();


        /**
         * 检查区域信息
         *
         * @return int
         */
        int checkRegionInfo();


        /**
         * uninitialize
         */
        void uninitialize();


        /**
         * 设置更新版本
         *
         * @param szVersion 深圳版本
         */
        void setUpdatorVersion(String szVersion);


        /**
         * 获得版本
         *
         * @return {@link String}
         */
        String getVersion();

        /**
         * 加载状态
         *
         * @return boolean
         */
        boolean loadState();
    }

}
