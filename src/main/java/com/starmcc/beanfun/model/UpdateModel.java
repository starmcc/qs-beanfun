package com.starmcc.beanfun.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新模型
 *
 * @author starmcc
 * @date 2022/04/08
 */
@Data
@Builder
public class UpdateModel implements Serializable {
    private static final long serialVersionUID = -7379298032869785110L;

    private String nowVersion;
    private String downloadUrl;
    private String tips;
    private State state;


    public static enum State {
        /**
         * 获取失败
         */
        获取失败,
        /**
         * 已是最新版本
         */
        已是最新版本,
        /**
         * 有新版本
         */
        有新版本,
        ;
    }

}
