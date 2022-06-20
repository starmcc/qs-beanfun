package com.starmcc.beanfun.client;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.ReqParams;
import com.starmcc.beanfun.model.client.QsHttpResponse;
import com.starmcc.beanfun.utils.DataTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

;

/**
 * http工具
 * https://blog.csdn.net/zhongzh86/article/details/84070561
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public class HttpClient {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
    private static final CookieStore COOKIE_STORE = new BasicCookieStore();

    public static Map<String, String> getCookies() {
        Map<String, String> cookieMap = new HashMap<>();
        List<Cookie> cookies = COOKIE_STORE.getCookies();
        for (Cookie cookie : cookies) {
            cookieMap.put(cookie.getName(), cookie.getValue());
        }
        return cookieMap;
    }


    /**
     * GET
     *
     * @param url       url
     * @param reqParams 要求参数
     * @return {@link String}
     */
    public static QsHttpResponse get(String url, ReqParams reqParams) throws Exception {
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
    public static QsHttpResponse post(String url, Map<String, String> params) throws Exception {
        return request(() -> {
            HttpPost post = new HttpPost(url);
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
    private static QsHttpResponse request(SupplierCustom supplier) throws Exception {
        QsHttpResponse qsHttpResponse = new QsHttpResponse();

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept-Encoding", "identity"));
//        headers.add(new BasicHeader("referer", ""));

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
            HttpClientContext context = HttpClientContext.create();
            response = httpClient.execute(httpUriRequest, context);
            // 从响应模型中获取响应实体
            qsHttpResponse.setRedirectLocations(context.getRedirectLocations());
            qsHttpResponse.setCode(response.getStatusLine().getStatusCode());
            List<Cookie> cookies = COOKIE_STORE.getCookies();
            qsHttpResponse.setCookieScore(cookies);
            if (DataTools.collectionIsNotEmpty(cookies)) {
                Map<String, String> cookieMap = cookies.stream().collect(Collectors.toMap(Cookie::getName, Cookie::getValue, (x, y) -> x));
                qsHttpResponse.setCookieMap(cookieMap);
            } else {
                qsHttpResponse.setCookieMap(new HashMap<>(1));
            }
            HttpEntity responseEntity = response.getEntity();
            if (Objects.isNull(responseEntity)) {
                return qsHttpResponse.build();
            }
            qsHttpResponse.setContentLength(responseEntity.getContentLength());
            String content = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            qsHttpResponse.setContent(StringEscapeUtils.unescapeHtml4(content));
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
        return qsHttpResponse.build();
    }


    @FunctionalInterface
    public static interface SupplierCustom {

        HttpUriRequest build() throws Exception;
    }

}
