package com.starmcc.beanfun.entity.model;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.LoginType;
import lombok.Data;

import java.io.File;
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
    private RecordVideo recordVideo;
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
        // 不自动输入
        this.passInput = false;
        // 录像配置
        this.recordVideo = new RecordVideo();
        // 自动输入
        this.autoInput = true;
        // 代理配置
        this.proxyConfig = new ProxyConfig();
    }


    @Data
    public static class ActPwd {
        private String act;
        private String pwd;
        private Integer type;

        public static ActPwd build(String act, String pwd) {
            ActPwd actPwd = new ActPwd();
            actPwd.setAct(act);
            actPwd.setPwd(pwd);
            return actPwd;
        }

        @Override
        public String toString() {
            return act;
        }
    }

    /**
     * 录制视频
     *
     * @author starmcc
     * @date 2022/09/22
     */
    @Data
    public static class RecordVideo {
        /**
         * 文件夹
         */
        private String folder;
        /**
         * 帧/秒
         */
        private Integer fps;
        /**
         * 视频码率
         */
        private Integer codeRate;
        /**
         * 捕捉类型 1=游戏录制 2=全屏
         */
        private Integer captureType;
        /**
         * ffmpeg路径
         */
        private String ffmpegPath;

        public RecordVideo() {
            // 默认配置
            // 自身目录/video
            this.folder = new File(QsConstant.PATH_APP_PLUGINS + "recordVideo").getPath();
            // FPS 30
            this.fps = 30;
            // 码率 1800
            this.codeRate = 1800;
            // 类型 默认捕捉游戏窗口
            this.captureType = 1;
            // 默认ffmpeg路径
            this.ffmpegPath = "";
        }


        /**
         * 捕捉枚举类型
         *
         * @author starmcc
         * @date 2022/09/22
         */
        public static enum CaptureTypeEnum {
            游戏窗口(1), 全屏(2),
            ;
            private final int type;

            CaptureTypeEnum(int type) {
                this.type = type;
            }

            public int getType() {
                return type;
            }
        }
    }


    @Data
    public static class ProxyConfig {

        private String ip;
        private Integer port;
        private Boolean ban;

        public ProxyConfig() {
            this.ip = "";
            // 默认自动使用PAC代理
            this.ban = false;
        }

        @Override
        public String toString() {
            return ip + ":" + port;
        }
    }
}
