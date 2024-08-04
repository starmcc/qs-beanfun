package com.starmcc.beanfun.client;

import com.starmcc.beanfun.client.impl.DownloadClientImpl;
import lombok.Getter;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Objects;

/**
 * 下载客户端
 *
 * @author starmcc
 * @date 2022/12/11
 */
public abstract class DownloadClient {

    private static DownloadClient client;

    /**
     * 获得实例
     *
     * @return {@link HttpClient}
     */
    public static DownloadClient getInstance() {
        client = Objects.isNull(client) ? new DownloadClientImpl() : client;
        return client;
    }


    /**
     * 执行
     *
     * @param url      url
     * @param saveFile 保存文件
     * @param process  过程
     */
    public abstract void execute(URL url, File saveFile, Process process);

    @FunctionalInterface
    public interface Process {

        @Getter
        public static enum State {
            REQUEST_STATUS_ERR(-3, false),
            CONNECT_TIMEOUT(-2, false),
            UNKNOWN_ERR(-1, false),
            READY_START(0, true),
            CONNECT_RUNNING(1, true),
            CREATE_FILE(2, true),
            DOWNLOAD_RUNNING(3, true),
            SPEED_ECHO(4, true),
            DOWNLOAD_OK(5, true),
            ;
            private final int state;
            private final boolean normal;

            State(int state, boolean normal) {
                this.state = state;
                this.normal = normal;
            }

        }

        /**
         * 调用
         *
         * @param state   状态
         * @param file    文件
         * @param process 过程
         * @param speed   速度
         * @param e       e
         */
        void call(Process.State state, File file, Integer process, BigDecimal speed, Exception e);

    }
}
