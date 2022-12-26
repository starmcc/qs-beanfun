package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.constant.CalcConstant;
import com.starmcc.beanfun.entity.model.CalcModel;
import com.starmcc.beanfun.entity.param.EquipmentAutoCalcParam;
import com.starmcc.beanfun.entity.param.EquipmentCalcParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 装备卷轴计算器处理程序
 *
 * @author starmcc
 * @date 2022/09/21
 */
@Slf4j
public class EquipmentHandler {

    private static final String DEFAULT_TEXT = "自动模式";

    public static String autoCalc(EquipmentAutoCalcParam param) {
        if (param.getReelNum() == 0 || param.getTotalAtk() == 0) {
            // 上卷次数 = 0，或总攻击数 = 0 则不计算
            return "请填写装备数据";
        }
        if (verifyIsReel(param, CalcConstant.Reel.RED卷)) {
            return "Red卷";
        }
        if (verifyIsReel(param, CalcConstant.Reel.X卷)) {
            return "X卷";
        }
        if (verifyIsReel(param, CalcConstant.Reel.V卷)) {
            return "V卷";
        }
        if (verifyIsReel(param, CalcConstant.Reel.黑卷)) {
            return "黑卷";
        }

        double reedAtk = verifyIsGlory(param);
        if (reedAtk > Double.valueOf(0)) {
            return "命运/荣耀:" + reedAtk;
        }

        return "未知类型";
    }

    /**
     * 验证是否该卷
     *
     * @param param 参数
     * @param reel  卷
     * @return boolean
     */
    private static boolean verifyIsReel(EquipmentAutoCalcParam param, CalcConstant.Reel reel) {
        int reelAtk = 0;
        if (param.getEquipmentType() == CalcConstant.EquipmentType.武器
                || param.getEquipmentType() == CalcConstant.EquipmentType.心脏) {
            reelAtk = param.getReelNum() * reel.getWeapons();
        } else {
            reelAtk = param.getReelNum() * reel.getArmor();
        }
        Double totalAtk = new Double(param.getOriginAtk());
        if (param.getEquipmentType() == CalcConstant.EquipmentType.武器) {
            totalAtk += reelAtk;
        }
        totalAtk = calcStarAtk(totalAtk, param.getStarLevel(), param.getLevel(), param.getEquipmentType()) + param.getStarAtk();
        if (param.getEquipmentType() != CalcConstant.EquipmentType.武器) {
            totalAtk += reelAtk;
        }
        return param.getTotalAtk() != 0 && param.getTotalAtk().compareTo(totalAtk.intValue()) == 0;
    }

    /**
     * 验证是否荣耀，如果不是荣耀，返回-1
     *
     * @param param 参数
     * @return int
     */
    private static double verifyIsGlory(EquipmentAutoCalcParam param) {
        Double totalAtk = 0D;
        TrainingModel trainingModel = getTrainingModel(param);
        if (trainingModel == null) {
            return -1;
        };
        if (param.getEquipmentType() == CalcConstant.EquipmentType.武器
                || param.getEquipmentType() == CalcConstant.EquipmentType.心脏) {
            if (trainingModel.getAtk() > 20) {
                return -1;
            }
        } else {
            if (trainingModel.getAtk() > 15) {
                return -1;
            }
        }
        if (trainingModel.getAtk() != 0D) {
            return getTrainingModel2(param,  trainingModel);
        }
        return trainingModel.getReelAtk();
    }

    /**
     * 获取训练模型
     *
     * @param param 参数
     * @return {@link TrainingModel}
     */
    private static TrainingModel getTrainingModel(EquipmentAutoCalcParam param) {
        Double totalAtk;
        List<TrainingModel> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            double reelAtk = 0d;
            if (param.getEquipmentType() == CalcConstant.EquipmentType.武器
                    || param.getEquipmentType() == CalcConstant.EquipmentType.心脏) {
                reelAtk = 10 + i;
            } else {
                reelAtk = 5 + i;
            }
            totalAtk = new Double(param.getOriginAtk());
            if (param.getEquipmentType() == CalcConstant.EquipmentType.武器) {
                // 武器需要加上卷的攻击去算
                totalAtk += param.getReelNum() * reelAtk;
            }
            totalAtk = calcStarAtk(totalAtk, param.getStarLevel(), param.getLevel(), param.getEquipmentType());
            if (param.getEquipmentType() != CalcConstant.EquipmentType.武器) {
                totalAtk += param.getReelNum() * reelAtk;
            }
            Double contrast = param.getTotalAtk() - param.getStarAtk() - totalAtk;
            list.add(new TrainingModel(contrast, reelAtk));
        }

        list.sort((o1, o2) -> o1.getAtk().compareTo(o2.atk));
        Optional<TrainingModel> first = list.stream().filter(item -> item.getAtk() >= 0).findFirst();

        if (!first.isPresent()) {
            return null;
        }
        TrainingModel trainingModel = first.get();
        return trainingModel;
    }

    /**
     * 获取训练模型2
     *
     * @param param         参数
     * @param trainingModel 培训模式
     * @return double
     */
    private static double getTrainingModel2(EquipmentAutoCalcParam param, TrainingModel trainingModel) {
        List<TrainingModel> list2 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Double tt = new Double(param.getOriginAtk());
            if (param.getEquipmentType() == CalcConstant.EquipmentType.武器) {
                // 武器需要加上卷的攻击去算
                tt += param.getReelNum() * (trainingModel.getReelAtk() + ((i + 1) / 100D));
            }
            tt = calcStarAtk(tt, param.getStarLevel(), param.getLevel(), param.getEquipmentType());
            if (param.getEquipmentType() != CalcConstant.EquipmentType.武器) {
                tt += param.getReelNum() * (trainingModel.getReelAtk() + ((i + 1) / 100D));
            }
            Double contrast = param.getTotalAtk() - param.getStarAtk() - tt;
            list2.add(new TrainingModel(contrast, ((i + 1) / 100D) + trainingModel.getReelAtk()));
            if (contrast == 0D) {
                return Double.parseDouble(String.format("%.2f", (i + 1) / 100D + trainingModel.getReelAtk()));
            }
        }
        list2.sort((o1, o2) -> o1.getAtk().compareTo(o2.atk));
        Optional<TrainingModel> first2 = list2.stream().filter(item -> item.getAtk() >= 0).findFirst();
        if (!first2.isPresent()) {
            return -1;
        }
        TrainingModel trainingModel2 = first2.get();
        return trainingModel2.getReelAtk();
    }

    /**
     * calc
     *
     * @param param 参数
     * @return {@link CalcModel}
     */
    public static CalcModel calc(EquipmentCalcParam param) {
        // 获得卷轴攻击力
        Double reelAtk = param.getGloryNum() * param.getGloryValNum();
        if (param.getEquipmentType() == CalcConstant.EquipmentType.武器
                || param.getEquipmentType() == CalcConstant.EquipmentType.心脏) {
            reelAtk += param.getBlackNum() * CalcConstant.Reel.黑卷.getWeapons();
            reelAtk += param.getVNum() * CalcConstant.Reel.V卷.getWeapons();
            reelAtk += param.getXNum() * CalcConstant.Reel.X卷.getWeapons();
            reelAtk += param.getRedNum() * CalcConstant.Reel.RED卷.getWeapons();
        } else {
            reelAtk += param.getBlackNum() * CalcConstant.Reel.黑卷.getArmor();
            reelAtk += param.getVNum() * CalcConstant.Reel.V卷.getArmor();
            reelAtk += param.getXNum() * CalcConstant.Reel.X卷.getArmor();
            reelAtk += param.getRedNum() * CalcConstant.Reel.RED卷.getArmor();
        }

        // 使用原始攻击 + 卷轴攻击套用星力加成计算 武器吃加成! 获取总攻击数据
        Double totalAtk = new Double(param.getOriginAtk());
        if (param.getEquipmentType() == CalcConstant.EquipmentType.武器) {
            totalAtk += reelAtk;
        }
        totalAtk = calcStarAtk(totalAtk, param.getStarLevel(), param.getLevel(), param.getEquipmentType()) + param.getStarAtk();
        if (param.getEquipmentType() != CalcConstant.EquipmentType.武器) {
            totalAtk += reelAtk;
        }
        // 获取附加攻击值
        Double appendAtk = totalAtk - param.getOriginAtk() - param.getStarAtk();

        return CalcModel.builder().totalAtk(totalAtk).appendAtk(appendAtk).build();

    }


    /**
     * 计算星力攻击
     *
     * @param param 参数
     * @return int
     */
    private static Double calcStarAtk(Double totalAtk, int starLevel, int level, CalcConstant.EquipmentType equipmentType) {
        // 循环计算攻击力
        for (int i = 0; i < starLevel; i++) {
            if (equipmentType == CalcConstant.EquipmentType.武器) {
                if ((i + 1) <= 15) {
                    totalAtk += calcStarLevelDeduceAtk(totalAtk);
                }
                if (level == 200) {
                    totalAtk += CalcConstant.WEAPON_STAR_200_ATK.get(i);
                } else if (level == 160) {
                    totalAtk += CalcConstant.WEAPON_STAR_160_ATK.get(i);
                } else if (level == 150) {
                    totalAtk += CalcConstant.WEAPON_STAR_150_ATK.get(i);
                }
            } else if (equipmentType == CalcConstant.EquipmentType.心脏
                    || equipmentType == CalcConstant.EquipmentType.防具) {
                if (level == 200) {
                    totalAtk += CalcConstant.ARMOR_STAR_200_ATK.get(i);
                } else if (level == 160) {
                    totalAtk += CalcConstant.ARMOR_STAR_160_ATK.get(i);
                } else if (level == 150) {
                    totalAtk += CalcConstant.ARMOR_STAR_150_ATK.get(i);
                }
            } else if (equipmentType == CalcConstant.EquipmentType.手套) {
                if (level == 200) {
                    totalAtk += CalcConstant.GLOVES_STAR_200_ATK.get(i);
                } else if (level == 160) {
                    totalAtk += CalcConstant.GLOVES_STAR_160_ATK.get(i);
                } else if (level == 150) {
                    totalAtk += CalcConstant.GLOVES_STAR_150_ATK.get(i);
                }
            }
        }
        return totalAtk;
    }

    /**
     * 计算星级推演的攻击力累加值
     *
     * @param atk atk公司
     * @return int
     */
    private static int calcStarLevelDeduceAtk(Double atk) {
        if (atk == 0) {
            return 0;
        }
        double dAtk = atk / 50D;
        if (Double.compare(dAtk, new Double(0)) == 0) {
            return 1;
        }
        return (int) Math.floor(dAtk) + 1;
    }


    @Data
    public static class TrainingModel implements Serializable {

        private static final long serialVersionUID = 2311167728586773837L;

        private Double atk;
        private Double reelAtk;

        public TrainingModel(Double atk, Double reelAtk) {
            this.atk = atk;
            this.reelAtk = reelAtk;
        }

    }
}
