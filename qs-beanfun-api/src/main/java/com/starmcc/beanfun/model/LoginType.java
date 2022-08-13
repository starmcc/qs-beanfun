package com.starmcc.beanfun.model;

import lombok.Data;
import lombok.Getter;

/**
 * 登录类型
 *
 * @author starmcc
 * @date 2022/06/03
 */
@Data
public class LoginType {

    private String name;
    private Integer type;

    public LoginType() {

    }

    public LoginType(TypeEnum typeEnum) {
        this.name = typeEnum.getName();
        this.type = typeEnum.getType();
    }

    @Getter
    public static enum TypeEnum {
        HK("香港账号", 1),
        TW("台号账号", 2),
        ;

        private final String name;
        private final Integer type;

        TypeEnum(String name, Integer type) {
            this.name = name;
            this.type = type;
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
