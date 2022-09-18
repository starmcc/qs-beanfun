package com.starmcc.beanfun.model;

import lombok.Data;
import lombok.Getter;

import java.util.Objects;

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
        HK("香港", 1),
        TW("台湾", 2),
        ;

        private final String name;
        private final Integer type;

        TypeEnum(String name, Integer type) {
            this.name = name;
            this.type = type;
        }


        public static TypeEnum getData(Integer type) {
            for (TypeEnum value : values()) {
                if (Objects.equals(value.getType(), type)) {
                    return value;
                }
            }
            return null;
        }

    }

    @Override
    public String toString() {
        return this.getName();
    }
}
