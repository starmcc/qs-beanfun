package com.starmcc.beanfun.entity.client;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Objects;

@Data
public class ReqParams {

    private LinkedHashMap<String, String> params;
    private LinkedHashMap<String, String> headers;


    public static ReqParams getInstance() {
        return new ReqParams();
    }

    public ReqParams(){
        this.params = new LinkedHashMap<>();
        this.headers = new LinkedHashMap<>();
    }

    public ReqParams addParam(String key, String val) {
        if (Objects.isNull(this.params)) {
            this.params = new LinkedHashMap<>();
        }
        this.params.put(key, val);
        return this;
    }

    public ReqParams addHeader(String key, String val) {
        if (Objects.isNull(this.headers)) {
            this.headers = new LinkedHashMap<>();
        }
        this.headers.put(key, val);
        return this;
    }


    @Data
    @AllArgsConstructor
    public static class Param {
        private String key;
        private String val;
    }
}
