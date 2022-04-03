package com.starmcc.beanfun.windows;

public interface BaseBFService {

    /**
     * “HK;Production”, “”, “”, 0, “”
     * 初始化
     *
     * @return int
     */
    boolean initialize2();


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
     * 获得实例
     *
     * @return {@link BaseBFService}
     */
    public static BaseBFService getInstance() {
        return new BFServiceJnaImpl();
    }

}
