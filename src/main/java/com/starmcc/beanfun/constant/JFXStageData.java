package com.starmcc.beanfun.constant;

import com.starmcc.beanfun.entity.model.JFXStage;

import java.util.HashMap;

/**
 * jfxstage数据存放对象
 *
 * @author starmcc
 * @date 2022/09/21
 */
public class JFXStageData extends HashMap<String, JFXStage> {

    public void put(FXPageEnum page, JFXStage jfxStage) {
        this.put(page.getFileName(), jfxStage);
    }


    public JFXStage get(FXPageEnum page) {
        return this.get(page.getFileName());
    }

    public void remove(FXPageEnum page) {
        this.remove(page.getFileName());
    }
}
