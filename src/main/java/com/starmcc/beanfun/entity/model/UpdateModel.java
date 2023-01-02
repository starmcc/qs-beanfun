package com.starmcc.beanfun.entity.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 更新模型
 *
 * @author starmcc
 * @date 2022/04/08
 */
@Data
public class UpdateModel implements Serializable {
    private static final long serialVersionUID = -7379298032869785110L;

    private Type type;
    private State state;
    private String version;
    private String url;
    private String content;
    private Integer versionInt;
    private String hash;

    /**
     * 更新类型
     *
     * @author starmcc
     * @date 2023/01/03
     */
    public static enum Type {
        /**
         * 未知类型
         */
        未知类型((short) -1),
        /**
         * 无数据
         */
        无数据((short) 0),
        /**
         * github自动更新
         */
        GITHUB自动更新((short) 1),
        /**
         * github手动更新
         */
        GITHUB手动更新((short) 2),

        ;
        private final short type;

        Type(short type) {
            this.type = type;
        }

        public short getType() {
            return type;
        }

        public static Type get(short type) {
            for (Type value : values()) {
                if (Objects.equals(value.getType(), type)) {
                    return value;
                }
            }
            return null;
        }
    }

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
