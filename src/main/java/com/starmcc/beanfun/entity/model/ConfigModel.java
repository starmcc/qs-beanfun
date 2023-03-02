package com.starmcc.beanfun.entity.model;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.LoginType;
import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 全局配置数据模型
 *
 * @author starmcc
 * @date 2022/09/21
 */
@Data
public class ConfigModel implements Serializable {

    private static final long serialVersionUID = 7433412132480861916L;

    private Boolean recordActPwd;
    private String gamePath;
    private Integer lunHuiKey;
    private Integer ranShaoKey;
    private Boolean killStartPalyWindow;
    private Boolean checkAppUpdate;
    private Short updateChannel;
    private Boolean killGamePatcher;
    private Boolean autoInput;
    private Boolean passInput;
    private Integer loginType;
    private RecordVideo recordVideo;
    private List<ActPwd> actPwds;
    private ProxyConfig proxyConfig;
    private LRConfig lrConfig;
    private String vipSecrect;
    private Boolean minimizeMode;
    private Boolean expandSettingPane;

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
        // 不免密输入
        this.passInput = false;
        // 录像配置
        this.recordVideo = new RecordVideo();
        // 自动输入
        this.autoInput = true;
        // 代理配置
        this.proxyConfig = new ProxyConfig();
        // 检查应用更新
        this.checkAppUpdate = true;
        // 更新渠道 默认Gitee 走国内路线
        this.updateChannel = UpdateChannel.GITEE.getChannel();
        // LR配置
        this.lrConfig = new LRConfig();
        // vip密钥
        this.vipSecrect = "";
        // 最小化模式
        this.minimizeMode = true;
        // 展开工具设置面板
        this.expandSettingPane = false;
    }


    @Data
    public static class ActPwd implements Serializable {
        private static final long serialVersionUID = -2963079081098248669L;
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
    public static class RecordVideo implements Serializable {
        private static final long serialVersionUID = 4984160868883374046L;
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

    /**
     * lrconfig
     *
     * @author starmcc
     * @date 2023/01/12
     */
    @Data
    public static class LRConfig implements Serializable {
        private static final long serialVersionUID = 7199324595972542483L;

        /**
         * 钩子输入法
         */
        private Boolean hookInput;

        public LRConfig() {
            this.hookInput = true;
        }

    }

    @Data
    public static class ProxyConfig implements Serializable {

        private static final long serialVersionUID = -2103248701734060691L;
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


    /**
     * 更新频道
     *
     * @author starmcc
     * @date 2023/01/12
     */
    public static enum UpdateChannel {
        GITHUB((short) 1),
        GITEE((short) 2),
        ;

        private final short channel;

        UpdateChannel(short channel) {
            this.channel = channel;
        }

        public short getChannel() {
            return channel;
        }

        public static UpdateChannel get(short channel) {
            for (UpdateChannel value : values()) {
                if (Objects.equals(value.getChannel(), channel)) {
                    return value;
                }
            }
            // 默认Github
            return UpdateChannel.GITHUB;
        }
    }
}
