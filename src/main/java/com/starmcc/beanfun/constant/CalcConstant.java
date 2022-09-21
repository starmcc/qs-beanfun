package com.starmcc.beanfun.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 装备计算常量
 *
 * @author starmcc
 * @date 2022/09/21
 */
public class CalcConstant {
    // 武器星力攻击系数加成
    public static final Map<Integer, Integer> WEAPON_STAR_200_ATK = new HashMap<>();
    public static final Map<Integer, Integer> WEAPON_STAR_160_ATK = new HashMap<>();
    public static final Map<Integer, Integer> WEAPON_STAR_150_ATK = new HashMap<>();

    // 装备星力攻击系数加成
    public static final Map<Integer, Integer> ARMOR_STAR_200_ATK = new HashMap<>();
    public static final Map<Integer, Integer> ARMOR_STAR_160_ATK = new HashMap<>();
    public static final Map<Integer, Integer> ARMOR_STAR_150_ATK = new HashMap<>();

    // 手套星力攻击系数加成
    public static final Map<Integer, Integer> GLOVES_STAR_200_ATK = new HashMap<>();
    public static final Map<Integer, Integer> GLOVES_STAR_160_ATK = new HashMap<>();
    public static final Map<Integer, Integer> GLOVES_STAR_150_ATK = new HashMap<>();

    static {
        initCalcData();
    }


    public static enum Reel {
        黑卷(9, 14),
        V卷(8, 13),
        X卷(7, 12),
        RED卷(5, 10),
        ;

        private final int armor;
        private final int weapons;

        Reel(int armor, int weapons) {
            this.armor = armor;
            this.weapons = weapons;
        }

        public int getArmor() {
            return armor;
        }

        public int getWeapons() {
            return weapons;
        }
    }


    public static enum EquipmentType {
        武器(1),
        防具(2),
        心脏(3),
        手套(4),
        ;

        private final int type;

        EquipmentType(int type) {
            this.type = type;
        }

        public static EquipmentType build(int type) {
            for (EquipmentType value : values()) {
                if (Objects.equals(value.getType(), type)) {
                    return value;
                }
            }
            return null;
        }

        public int getType() {
            return type;
        }
    }


    private static void initCalcData() {
        // 200级武器攻击力
        final int starTotal = 25;
        for (int i = 0; i < starTotal; i++) {
            int atk = 0;
            if ((i + 1) == 16) {
                atk = 13;
            } else if ((i + 1) == 17) {
                atk = 13;
            } else if ((i + 1) == 18) {
                atk = 14;
            } else if ((i + 1) == 19) {
                atk = 14;
            } else if ((i + 1) == 20) {
                atk = 15;
            } else if ((i + 1) == 21) {
                atk = 16;
            } else if ((i + 1) == 22) {
                atk = 17;
            } else if ((i + 1) == 23) {
                atk = 34;
            } else if ((i + 1) == 24) {
                atk = 36;
            } else if ((i + 1) == 25) {
                atk = 38;
            }
            WEAPON_STAR_200_ATK.put(i, atk);
        }

        for (int i = 0; i < starTotal; i++) {
            int atk = 0;
            if ((i + 1) == 16) {
                atk = 9;
            } else if ((i + 1) == 17) {
                atk = 9;
            } else if ((i + 1) == 18) {
                atk = 10;
            } else if ((i + 1) == 19) {
                atk = 11;
            } else if ((i + 1) == 20) {
                atk = 12;
            } else if ((i + 1) == 21) {
                atk = 13;
            } else if ((i + 1) == 22) {
                atk = 14;
            } else if ((i + 1) == 23) {
                atk = 32;
            } else if ((i + 1) == 24) {
                atk = 34;
            } else if ((i + 1) == 25) {
                atk = 36;
            }
            WEAPON_STAR_160_ATK.put(i, atk);
        }

        for (int i = 0; i < starTotal; i++) {
            int atk = 0;
            if (i == 16) {
                atk = 8;
            } else if ((i + 1) == 17) {
                atk = 9;
            } else if ((i + 1) == 18) {
                atk = 9;
            } else if ((i + 1) == 19) {
                atk = 10;
            } else if ((i + 1) == 20) {
                atk = 11;
            } else if ((i + 1) == 21) {
                atk = 12;
            } else if ((i + 1) == 22) {
                atk = 13;
            } else if ((i + 1) == 23) {
                atk = 31;
            } else if ((i + 1) == 24) {
                atk = 33;
            } else if ((i + 1) == 25) {
                atk = 35;
            }
            WEAPON_STAR_150_ATK.put(i, atk);
        }

        for (int i = 0; i < starTotal; i++) {
            int atk = 0;
            if ((i + 1) == 16) {
                atk = 12;
            } else if ((i + 1) == 17) {
                atk = 13;
            } else if ((i + 1) == 18) {
                atk = 14;
            } else if ((i + 1) == 19) {
                atk = 15;
            } else if ((i + 1) == 20) {
                atk = 16;
            } else if ((i + 1) == 21) {
                atk = 17;
            } else if ((i + 1) == 22) {
                atk = 19;
            } else if ((i + 1) == 23) {
                atk = 21;
            } else if ((i + 1) == 24) {
                atk = 23;
            } else if ((i + 1) == 25) {
                atk = 25;
            }
            ARMOR_STAR_200_ATK.put(i, atk);
        }

        for (int i = 0; i < starTotal; i++) {
            int atk = 0;
            if ((i + 1) == 16) {
                atk = 10;
            } else if ((i + 1) == 17) {
                atk = 11;
            } else if ((i + 1) == 18) {
                atk = 12;
            } else if ((i + 1) == 19) {
                atk = 13;
            } else if ((i + 1) == 20) {
                atk = 14;
            } else if ((i + 1) == 21) {
                atk = 15;
            } else if ((i + 1) == 22) {
                atk = 17;
            } else if ((i + 1) == 23) {
                atk = 19;
            } else if ((i + 1) == 24) {
                atk = 21;
            } else if ((i + 1) == 25) {
                atk = 23;
            }
            ARMOR_STAR_160_ATK.put(i, atk);
        }

        for (int i = 0; i < starTotal; i++) {
            int atk = 0;
            if ((i + 1) == 16) {
                atk = 9;
            } else if ((i + 1) == 17) {
                atk = 10;
            } else if ((i + 1) == 18) {
                atk = 11;
            } else if ((i + 1) == 19) {
                atk = 12;
            } else if ((i + 1) == 20) {
                atk = 13;
            } else if ((i + 1) == 21) {
                atk = 14;
            } else if ((i + 1) == 22) {
                atk = 16;
            } else if ((i + 1) == 23) {
                atk = 18;
            } else if ((i + 1) == 24) {
                atk = 20;
            } else if ((i + 1) == 25) {
                atk = 22;
            }
            ARMOR_STAR_150_ATK.put(i, atk);
        }

        for (int i = 0; i < starTotal; i++) {
            int atk = 0;
            if ((i + 1) == 5 || (i + 1) == 7 || (i + 1) == 9 || (i + 1) == 11 || ((i + 1) >= 13 && (i + 1) <= 15)) {
                atk = 1;
            } else if ((i + 1) == 16) {
                atk = 12;
            } else if ((i + 1) == 17) {
                atk = 13;
            } else if ((i + 1) == 18) {
                atk = 14;
            } else if ((i + 1) == 19) {
                atk = 15;
            } else if ((i + 1) == 20) {
                atk = 16;
            } else if ((i + 1) == 21) {
                atk = 17;
            } else if ((i + 1) == 22) {
                atk = 19;
            } else if ((i + 1) == 23) {
                atk = 21;
            } else if ((i + 1) == 24) {
                atk = 23;
            } else if ((i + 1) == 25) {
                atk = 25;
            }
            GLOVES_STAR_200_ATK.put(i, atk);
        }
        for (int i = 0; i < starTotal; i++) {
            int atk = 0;
            if ((i + 1) == 5 || (i + 1) == 7 || (i + 1) == 9 || (i + 1) == 11 || ((i + 1) >= 13 && (i + 1) <= 15)) {
                atk = 1;
            } else if ((i + 1) == 16) {
                atk = 10;
            } else if ((i + 1) == 17) {
                atk = 11;
            } else if ((i + 1) == 18) {
                atk = 12;
            } else if ((i + 1) == 19) {
                atk = 13;
            } else if ((i + 1) == 20) {
                atk = 14;
            } else if ((i + 1) == 21) {
                atk = 15;
            } else if ((i + 1) == 22) {
                atk = 17;
            } else if ((i + 1) == 23) {
                atk = 19;
            } else if ((i + 1) == 24) {
                atk = 21;
            } else if ((i + 1) == 25) {
                atk = 23;
            }
            GLOVES_STAR_160_ATK.put(i, atk);
        }
        for (int i = 0; i < starTotal; i++) {
            int atk = 0;
            if ((i + 1) == 5 || (i + 1) == 7 || (i + 1) == 9 || (i + 1) == 11 || ((i + 1) >= 13 && (i + 1) <= 15)) {
                atk = 1;
            } else if ((i + 1) == 16) {
                atk = 9;
            } else if ((i + 1) == 17) {
                atk = 10;
            } else if ((i + 1) == 18) {
                atk = 11;
            } else if ((i + 1) == 19) {
                atk = 12;
            } else if ((i + 1) == 20) {
                atk = 13;
            } else if ((i + 1) == 21) {
                atk = 14;
            } else if ((i + 1) == 22) {
                atk = 16;
            } else if ((i + 1) == 23) {
                atk = 18;
            } else if ((i + 1) == 24) {
                atk = 20;
            } else if ((i + 1) == 25) {
                atk = 22;
            }
            GLOVES_STAR_150_ATK.put(i, atk);
        }
    }

}
