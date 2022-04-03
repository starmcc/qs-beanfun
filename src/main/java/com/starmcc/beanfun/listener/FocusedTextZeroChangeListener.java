package com.starmcc.beanfun.listener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

/**
 * 焦点改变后，初始化值监听器
 *
 * @author starmcc
 * @date 2022/04/02
 */
public class FocusedTextZeroChangeListener implements ChangeListener<Boolean> {

    private final TextField textField;

    public FocusedTextZeroChangeListener(TextField textField) {
        this.textField = textField;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            // 失去焦点
            if (StringUtils.isBlank(textField.getText())) {
                textField.setText("0");
            }
        }
    }
}
