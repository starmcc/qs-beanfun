package com.starmcc.beanfun.entity.model;

import lombok.Data;
import lombok.Getter;

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
    private Version version;
    private String url;
    private String content;
    private String hash;

    @Getter
    public static enum State {
        /**
         * 获取失败
         */
        GET_ERROR(0),
        /**
         * 已是最新版本
         */
        LATEST_VERSION(1),
        /**
         * 有新版本
         */
        NEW_VERSION(2),
        ;

        private final int state;

        State(int state) {
            this.state = state;
        }

    }


}
