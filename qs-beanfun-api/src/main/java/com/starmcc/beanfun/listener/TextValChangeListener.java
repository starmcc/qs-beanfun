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
public class TextValChangeListener implements ChangeListener<String> {

    private final TextField textField;

    public TextValChangeListener(TextField textField) {
        this.textField = textField;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (textField.isFocused() && StringUtils.isNotBlank(newValue)) {
            if (!newValue.matches("[0-9]+(\\.[0-9]+)?")) {
                textField.setText("0");
            }
            if (newValue.length() > 4) {
                textField.setText(newValue.substring(0, 4));
            }
        }
    }
}
