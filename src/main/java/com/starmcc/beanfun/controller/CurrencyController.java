package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.ThirdPartyApiClient;
import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.model.LoadPage;
import com.starmcc.beanfun.handler.CellHandler;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class CurrencyController implements Initializable {
    @FXML
    private TextField textFieldRmbInput;
    @FXML
    private TextField textFieldXtbInput;
    @FXML
    private Label labelExchangeNow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // =========================== 汇率控件事件 ========================
        textFieldRmbInput.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (textFieldRmbInput.isFocused()) {
                textFieldXtbInput.setText(CellHandler.cellHuiLv(newValue, 1).toString());
            }
        });
        textFieldXtbInput.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (textFieldXtbInput.isFocused()) {
                textFieldRmbInput.setText(CellHandler.cellHuiLv(newValue, 2).toString());
            }
        });

        // 获取汇率
        ThreadPoolManager.execute(() -> {
            QsConstant.currentRateChinaToTw = ThirdPartyApiClient.getCurrentRateChinaToTw();
            FrameManager.getInstance().runLater(() -> labelExchangeNow.setText(QsConstant.currentRateChinaToTw.toString()));
        });
    }


    /**
     * 更新汇率
     */
    @FXML
    public void updateRateAction() {
        // 获取汇率
        LoadPage.task(FXPageEnum.CURRENCY, label -> {
            label.setText("更新汇率..");
            QsConstant.currentRateChinaToTw = ThirdPartyApiClient.getCurrentRateChinaToTw();
            FrameManager.getInstance().runLater(() -> labelExchangeNow.setText(QsConstant.currentRateChinaToTw.toString()));
        });
    }
}
