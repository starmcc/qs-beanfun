package com.starmcc.beanfun.listener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;


/**
 * 文本val改变监听器
 *
 * @author starmcc
 * @date 2022/04/02
 */
public class TextValDoubleChangeListener implements ChangeListener<String> {

    private final TextField textField;

    public TextValDoubleChangeListener(TextField textField) {
        this.textField = textField;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (textField.isFocused() && StringUtils.isNotBlank(newValue)) {
            if (!newValue.matches("^[0-9]+\\.{0,1}[0-9]{0,2}$")) {
                textField.setText("0");
            }
        }
    }
}
