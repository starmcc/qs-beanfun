package com.starmcc.beanfun.client;

import com.starmcc.beanfun.client.impl.DownloadClientImpl;
import com.starmcc.beanfun.client.impl.HttpClientImpl;

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

        public static enum State {
            请求状态码异常(-3, false),
            连接超时(-2, false),
            未知异常(-1, false),
            准备开始(0, true),
            正在连接(1, true),
            创建文件(2, true),
            下载中(3, true),
            速度回显(4, true),
            下载完毕(5, true),
            ;
            private final int state;
            private final boolean normal;

            State(int state, boolean normal) {
                this.state = state;
                this.normal = normal;
            }

            public int getState() {
                return state;
            }

            public boolean isNormal() {
                return normal;
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
