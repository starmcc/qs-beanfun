package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.client.QrCodeClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.client.BeanfunModel;
import com.starmcc.beanfun.model.client.BeanfunQrCodeResult;
import com.starmcc.beanfun.model.client.BeanfunStringResult;
import com.starmcc.beanfun.thread.ThreadPoolManager;
import com.starmcc.beanfun.thread.timer.AdvancedTimerMamager;
import com.starmcc.beanfun.thread.timer.AdvancedTimerTask;
import com.starmcc.beanfun.windows.FrameService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

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
    public void qrCodeClickAction(MouseEvent mouseEvent) {
        ThreadPoolManager.execute(this::loadQrCode);
    }


    /**
     * 加载二维码
     *
     * @throws Exception 异常
     */
    private void loadQrCode() throws Exception {
        AdvancedTimerMamager.getInstance().removeAllTask();
        if (ThreadPoolManager.isShutdown()) {
            return;
        }
        FrameService.getInstance().runLater(() -> {
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
        String savePath = QsConstant.APP_PATH + "onlineQrCode.jpg";
        File file = HttpClient.getInstance().downloadFile(beanfunQrCodeResult.getQrImageUrl(), savePath);
        if (Objects.isNull(file)) {
            // 下载二维码出现问题 重新下载
            return;
        }

        FrameService.getInstance().runLater(() -> {
            Image image = new Image(file.toURI().toString());
            imageViewQrCode.setImage(image);
            labelQrCodeTips.setText("请扫描二维码登录");
            labelQrCodeTips.setTextFill(Color.GREEN);
        });

        // 开始心跳检查
        AdvancedTimerMamager.getInstance().addTask(new AdvancedTimerTask<QrCodeController>(this) {
            @Override
            public void start() throws Exception {
                this.getResources().checkQrCodeStatus();
            }
        }, 1000, 2000);
    }

    /**
     * 检查二维码状态
     *
     * @throws Exception 异常
     */
    synchronized public void checkQrCodeStatus() throws Exception {
        if (!beanfunQrCodeResult.isSuccess()) {
            return;
        }
        int result = QrCodeClient.run().verifyQrCodeSuccess(QrCodeController.beanfunQrCodeResult.getStrEncryptData());
        log.debug("扫码心跳 返回值 res={}", result);
        if (result == 1) {
            // 扫码成功
            FrameService.getInstance().runLater(() -> {
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
            FrameService.getInstance().runLater(() -> {
                try {
                    FrameService.getInstance().closeWindow(QsConstant.loginJFXStage, true);
                    FrameService.getInstance().openWindow(QsConstant.Page.主界面);
                    FrameService.getInstance().closeWindow(QsConstant.qrCodeJFXStage, true);
                } catch (Exception e) {
                    log.error("error = {}", e.getMessage(), e);
                }
            });
        } else if (result == 2) {
            ThreadPoolManager.execute(this::loadQrCode, false);
        }
    }

}
