package com.starmcc.beanfun.model;

import com.starmcc.beanfun.constant.QsConstant;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局配置数据模型
 *
 * @author starmcc
 * @date 2022/09/21
 */
@Data
public class ConfigModel {

    private Boolean recordActPwd;
    private String gamePath;
    private Integer lunHuiKey;
    private Integer ranShaoKey;
    private Boolean killStartPalyWindow;
    private Boolean killGamePatcher;
    private Boolean autoInput;
    private Boolean passInput;
    private Integer loginType;
    private Video video;
    private List<ActPwd> actPwds;

    private ProxyConfig proxyConfig;

    public ConfigModel() {
        // 默认配置
        this.recordActPwd = false;
        // 游戏路径
        this.gamePath = "";
        // 账号
        this.actPwds = new ArrayList<>();
        // B
        this.lunHuiKey = 66;
        // N
        this.ranShaoKey = 78;
        // 关闭
        this.killStartPalyWindow = true;
        // 港号登录
        this.loginType = LoginType.TypeEnum.HK.getType();
        // 关闭
        this.killGamePatcher = true;
        // 不置顶
        this.passInput = false;
        // 录像配置
        this.video = new Video();
        // 自动输入
        this.autoInput = true;
        // 代理配置
        this.proxyConfig = null;
    }


    @Data
    public static class ActPwd {
        private String act;
        private String pwd;
    }

    @Data
    public static class Video {
        private String videoPath;
        private Integer videoFps;
        private Integer videoCodeRate;

        public Video() {
            // 默认配置
            // 自身目录/video
            this.videoPath = QsConstant.APP_PATH + "video";
            // FPS 60
            this.setVideoFps(60);
            // 码率 2500
            this.setVideoCodeRate(2500);
        }
    }


    @Data
    public static class ProxyConfig {

        private String ip;
        private Float port;

        @Override
        public String toString() {
            return ip + ":" + port;
        }
    }
}
