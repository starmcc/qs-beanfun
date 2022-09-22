package com.starmcc.beanfun.client;

import com.starmcc.beanfun.client.impl.HttpClientImpl;
import com.starmcc.beanfun.model.client.QsHttpResponse;
import com.starmcc.beanfun.model.client.ReqParams;
import org.apache.http.client.CookieStore;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

/**
 * http工具
 * https://blog.csdn.net/zhongzh86/article/details/84070561
 *
 * @author starmcc
 * @date 2022/03/19
 */
public abstract class HttpClient {

    private static HttpClient httpClient;

    /**
     * 获得实例
     *
     * @return {@link HttpClient}
     */
    public static HttpClient getInstance() {
        httpClient = Objects.isNull(httpClient) ? new HttpClientImpl() : httpClient;
        return httpClient;
    }


    /**
     * 获取URI cookies
     *
     * @param URI URI
     * @return {@link Map}<{@link String}, {@link String}>
     */
    public abstract Map<String, String> getCookie(URI uri);

    /**
     * 获取所有cookies
     *
     * @return {@link CookieStore}
     */
    public abstract CookieStore getCookieStore();

    /**
     * 设置cookie
     *
     * @param URI    URI
     * @param cookie cookie
     */
    public abstract void setCookie(URI uri, Map<String, String> cookie);


    /**
     * GET
     *
     * @param url url
     * @return {@link QsHttpResponse}
     * @throws Exception 异常
     */
    public abstract QsHttpResponse get(String url) throws Exception;

    /**
     * GET
     *
     * @param url       url
     * @param reqParams 要求参数
     * @return {@link QsHttpResponse}
     * @throws Exception 异常
     */
    public abstract QsHttpResponse get(String url, ReqParams reqParams) throws Exception;


    /**
     * POST
     *
     * @param url url
     * @return {@link QsHttpResponse}
     * @throws Exception 异常
     */
    public abstract QsHttpResponse post(String url) throws Exception;

    /**
     * POST
     *
     * @param url    url
     * @param params 参数个数
     * @return {@link QsHttpResponse}
     * @throws Exception 异常
     */
    public abstract QsHttpResponse post(String url, ReqParams params) throws Exception;


    /**
     * 下载文件
     *
     * @param url      url
     * @param saveFile 保存文件
     * @param process  下载回调函数
     */
    public abstract void downloadFile(URL url, File saveFile, HttpClient.Process process);

    /**
     * 读取http文件
     *
     * @param urlAddress url地址
     * @return {@link String}
     */
    public abstract String readHttpFile(String urlAddress);


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
            下载完毕(4, true),
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
         * @param e       e
         */
        void call(HttpClient.Process.State state, File file, Integer process, Exception e);
    }
}
