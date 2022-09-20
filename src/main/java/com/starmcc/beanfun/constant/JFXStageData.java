package com.starmcc.beanfun.constant;

import com.starmcc.beanfun.model.JFXStage;

import java.util.HashMap;

public class JFXStageData extends HashMap<String, JFXStage> {

    public void put(FXPages page, JFXStage jfxStage) {
        this.put(page.getFileName(), jfxStage);
    }


    public JFXStage get(FXPages page) {
        return this.get(page.getFileName());
    }

    public void remove(FXPages page) {
        this.remove(page.getFileName());
    }
}
