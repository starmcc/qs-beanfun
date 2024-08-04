package com.starmcc.beanfun.entity.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class WindowXyParam implements Serializable {
    private Double x;
    private Double y;

    public WindowXyParam(Double x, Double y) {
        this.x = x;
        this.y = y;
    }


}
