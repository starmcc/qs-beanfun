package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.client.QrCodeClient;
import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.client.BeanfunModel;
import com.starmcc.beanfun.entity.client.BeanfunQrCodeResult;
import com.starmcc.beanfun.entity.client.BeanfunStringResult;
import com.starmcc.beanfun.entity.client.QsHttpResponse;
import com.starmcc.beanfun.manager.AdvancedTimerManager;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.manager.impl.AdvancedTimerTask;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 二维码控制器
 *
 * @author starmcc
 * @date 2022/09/21
 */
@Slf4j
public class QrCodeController implements Initializable {


    @FXML
    private ImageView imageViewQrCode;
    @FXML
    private Label labelQrCodeTips;

    private static BeanfunQrCodeResult beanfunQrCodeResult;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ThreadPoolManager.execute(this::loadQrCode);
    }


    @FXML
    public void qrCodeClickAction() {
        ThreadPoolManager.execute(this::loadQrCode);
    }


    /**
     * 加载二维码
     *
     * @throws Exception 异常
     */
    private void loadQrCode() throws Exception {
        AdvancedTimerManager.getInstance().removeAllTask();
        if (ThreadPoolManager.isShutdown()) {
            return;
        }
        FrameManager.getInstance().runLater(() -> {
            Image image = new Image("static/images/loading.gif");
            imageViewQrCode.setImage(image);
            labelQrCodeTips.setText("二维码加载中");
            labelQrCodeTips.setTextFill(Color.GREEN);
        });

        try {
            beanfunQrCodeResult = QrCodeClient.run().getQrCodeImage();
        } catch (Exception e) {
            log.error("error={}", e.getMessage(), e);
            Thread.sleep(1000);
            ThreadPoolManager.execute(this::loadQrCode, false);
            return;
        }
        if (!beanfunQrCodeResult.isSuccess()) {
            // 请求错误，重新加载
            log.error("error={}", beanfunQrCodeResult.getMsg());
            Thread.sleep(1000);
            ThreadPoolManager.execute(this::loadQrCode, false);
            return;
        }
        QsHttpResponse rsp = HttpClient.getInstance().get(beanfunQrCodeResult.getQrImageUrl());
        if (!rsp.getSuccess()) {
            this.loadQrCode();
            return;
        }
        FrameManager.getInstance().runLater(() -> {
            Image image = new Image(new ByteArrayInputStream(rsp.getByteData()));
            imageViewQrCode.setImage(image);
            labelQrCodeTips.setText("请扫描二维码登录");
            labelQrCodeTips.setTextFill(Color.GREEN);
        });

        // 开始心跳检查
        AdvancedTimerManager.getInstance().addTask(new AdvancedTimerTask() {
            @Override
            public void start() throws Exception {
                checkQrCodeStatus();
            }
        }, 1000, 2000);
    }

    /**
     * 检查二维码状态
     *
     * @throws Exception 异常
     */
    synchronized void checkQrCodeStatus() throws Exception {
        if (!beanfunQrCodeResult.isSuccess()) {
            return;
        }
        int result = QrCodeClient.run().verifyQrCodeSuccess(QrCodeController.beanfunQrCodeResult.getStrEncryptData());
        log.debug("扫码心跳 返回值 res={}", result);
        if (result == 1) {
            // 扫码成功
            FrameManager.getInstance().runLater(() -> {
                labelQrCodeTips.setText("扫码成功,正在登录..");
                labelQrCodeTips.setTextFill(Color.BLUE);
            });
            BeanfunStringResult loginResult = QrCodeClient.run().login(QrCodeController.beanfunQrCodeResult.getSessionKey());
            if (!loginResult.isSuccess()) {
                ThreadPoolManager.execute(this::loadQrCode, false);
                return;
            }

            QsConstant.beanfunModel = new BeanfunModel();
            QsConstant.beanfunModel.setToken(loginResult.getData());
            FrameManager.getInstance().runLater(() -> {
                try {
                    FrameManager.getInstance().openWindow(FXPageEnum.MAIN);
                    FrameManager.getInstance().closeWindow(FXPageEnum.LOGIN);
                    FrameManager.getInstance().closeWindow(FXPageEnum.QR_CODE);
                } catch (Exception e) {
                    log.error("error = {}", e.getMessage(), e);
                }
            });
        } else if (result == 2) {
            ThreadPoolManager.execute(this::loadQrCode, false);
        }
    }

}
