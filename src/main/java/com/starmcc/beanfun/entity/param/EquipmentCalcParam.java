package com.starmcc.beanfun.entity.param;

import com.starmcc.beanfun.constant.CalcConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * 装备模型
 *
 * @author starmcc
 * @date 2022/04/02
 */
@Data
public class EquipmentCalcParam implements Serializable {

    private static final long serialVersionUID = 7695200904868131355L;
    private Integer originAtk;
    private Integer starAtk;
    private Integer starLevel;
    private Integer level;
    private Integer gloryNum;
    private Integer gloryValNum;
    private Integer blackNum;
    private Integer vNum;
    private Integer xNum;
    private Integer redNum;
    private CalcConstant.EquipmentType equipmentType;

}
