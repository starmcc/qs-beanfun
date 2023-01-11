package com.starmcc.beanfun.entity.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新模型
 *
 * @author starmcc
 * @date 2022/04/08
 */
@Data
public class UpdateModel implements Serializable {
    private static final long serialVersionUID = -7379298032869785110L;

    private State state;
    private String version;
    private String url;
    private String content;
    private String hash;

    public static enum State {
        /**
         * 获取失败
         */
        获取失败(0),
        /**
         * 已是最新版本
         */
        已是最新版本(1),
        /**
         * 有新版本
         */
        有新版本(2),
        ;

        private final int state;

        State(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }


}
