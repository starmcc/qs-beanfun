package com.starmcc.beanfun.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * calc模型
 *
 * @author starmcc
 * @date 2022/04/02
 */
@Data
@Builder
public class CalcModel implements Serializable {
    private static final long serialVersionUID = -8615808741207121881L;

    private Integer totalAtk;
    private Integer appendAtk;


}
