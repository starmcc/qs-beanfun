package com.starmcc.beanfun.model;

import com.starmcc.beanfun.constant.QsConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新模型
 *
 * @author starmcc
 * @date 2022/04/08
 */
@Data
public class UpdateModel implements Serializable {
    private static final long serialVersionUID = -7379298032869785110L;

    private String updateVersion;
    private String nowVersion;
    private String downloadUrl;
    private String updateText;
    private State state;


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

    public static UpdateModel build(State state) {
        UpdateModel model = new UpdateModel();
        model.setState(state);
        model.setNowVersion(QsConstant.APP_VERSION);
        return model;
    }


}
