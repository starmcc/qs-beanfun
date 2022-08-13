package com.starmcc.beanfun.model.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.cookie.Cookie;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * qs http响应
 *
 * @author starmcc
 * @date 2022/04/26
 */
@Data
@Slf4j
public class QsHttpResponse implements Serializable {

    private static final long serialVersionUID = -3691166575079674458L;

    private String content;
    private List<URI> redirectLocations;
    private int code;
    private long contentLength;
    private Boolean success;
    private Map<String, String> cookieMap;
    private List<Cookie> cookieScore;


    public QsHttpResponse build() {
        this.success = this.code == 200;
        log.info("HTTP response code = {} success = {} contentLength = {}",
                this.code, this.success, this.contentLength);
        log.debug("HTTP response content = {}", this.content);
        return this;
    }


}
