package com.starmcc.beanfun.model;

import com.starmcc.beanfun.constant.QsConstant;
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
        // 不置顶
        this.passInput = false;
        // 录像配置
        this.recordVideo = new RecordVideo();
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

        public RecordVideo() {
            // 默认配置
            // 自身目录/video
            this.folder = new File(QsConstant.PATH_PLUGINS + "video").getPath();
            // FPS 30
            this.setFps(30);
            // 码率 1800
            this.setCodeRate(1800);
            // 类型 默认捕捉游戏窗口
            this.setCaptureType(1);
        }


        /**
         * 捕捉枚举类型
         *
         * @author starmcc
         * @date 2022/09/22
         */
        public static enum CaptureTypeEnum {
            游戏窗口(1),
            桌面(2),
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
        private Float port;

        @Override
        public String toString() {
            return ip + ":" + port;
        }
    }
}
