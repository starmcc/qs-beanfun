package com.starmcc.beanfun.entity.param;

import com.starmcc.beanfun.constant.CalcConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * 装备自动calc参数
 *
 * @author starmcc
 * @date 2022/04/02
 */
@Data
public class EquipmentAutoCalcParam implements Serializable {

    private static final long serialVersionUID = 6176134515397826464L;
    private Integer totalAtk;
    private Integer originAtk;
    private Integer starAtk;
    private Integer reelNum;
    private Integer starLevel;
    private Integer level;
    private CalcConstant.EquipmentType equipmentType;
}
