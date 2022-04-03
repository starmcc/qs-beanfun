package com.starmcc.beanfun.utils;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.ReqParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

;

/**
 * http工具
 * https://blog.csdn.net/zhongzh86/article/details/84070561
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public class HttpUtils {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
    public static final CookieStore COOKIE_STORE = new BasicCookieStore();

    /**
     * GET
     *
     * @param url       url
     * @param reqParams 要求参数
     * @return {@link String}
     */
    public static String get(String url, ReqParams reqParams) throws Exception {
        return request(() -> {
            // 参数
            StringBuilder params = new StringBuilder(url.replace("?", ""));
            if (Objects.nonNull(reqParams) && DataTools.collectionIsNotEmpty(reqParams.getParams())) {
                for (int i = 0; i < reqParams.getParams().size(); i++) {
                    ReqParams.Param param = reqParams.getParams().get(i);
                    StringBuilder valParam = new StringBuilder();
                    valParam = i < 1 ? valParam.append("?") : valParam.append("&");
                    valParam.append(param.getKey()).append("=").append(URLEncoder.encode(param.getVal(), "utf-8"));
                    params.append(valParam.toString());
                }
            }
            log.info("请求URL = {}", params.toString());
            HttpGet get = new HttpGet(params.toString());
            get.setHeader("Accept-Encoding", "identity");
            return get;
        });
    }


    /**
     * POST
     *
     * @param url    url
     * @param params 参数
     * @return {@link String}
     * @throws Exception 异常
     */
    public static String post(String url, Map<String, String> params) throws Exception {
        return request(() -> {
            HttpPost post = new HttpPost(url);
            post.setHeader("Accept-Encoding", "identity");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            ArrayList<BasicNameValuePair> list = new ArrayList<>();
            params.forEach((key, value) -> list.add(new BasicNameValuePair(key, value)));
            post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
            return post;
        });
    }


    /**
     * 请求
     *
     * @param supplier 供应
     * @return {@link String}
     */
    private static <T> String request(SupplierCustom supplier) throws Exception {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept-Encoding", "identity"));

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultHeaders(headers)
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultCookieStore(COOKIE_STORE)
                .setUserAgent(USER_AGENT)
                .setProxy(QsConstant.proxy)
                .build();
        CloseableHttpResponse response = null;
        String result = "";
        try {
            // 由客户端执行(发送)Get请求
            HttpUriRequest httpUriRequest = supplier.build();
            response = httpClient.execute(httpUriRequest);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            log.debug("响应状态为: {}", response.getStatusLine());
            if (Objects.isNull(responseEntity)) {
                return result;
            }
            log.debug("响应内容长度为: {}", responseEntity.getContentLength());
            result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            log.debug("响应结果: {}", result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                // 释放资源
                if (Objects.nonNull(httpClient)) {
                    httpClient.close();
                }
                if (Objects.nonNull(response)) {
                    response.close();
                }
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
        return result;
    }


    @FunctionalInterface
    public static interface SupplierCustom {

        HttpUriRequest build() throws Exception;
    }

}
