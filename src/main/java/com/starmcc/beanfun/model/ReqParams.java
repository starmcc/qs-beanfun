package com.starmcc.beanfun.model;


import com.starmcc.beanfun.utils.DataTools;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReqParams {

    private List<Param> params;


    public static ReqParams getInstance() {
        return new ReqParams();
    }

    public ReqParams addParam(String key, String val) {
        if (DataTools.collectionIsEmpty(this.params)) {
            this.params = new ArrayList<>();
        }
        this.params.add(new Param(key, val));
        return this;
    }


    @Data
    @AllArgsConstructor
    public static class Param {
        private String key;
        private String val;
    }
}
