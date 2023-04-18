package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.DownloadClient;
import com.starmcc.beanfun.client.QrCodeClient;
import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.client.BeanfunModel;
import com.starmcc.beanfun.entity.client.BeanfunQrCodeResult;
import com.starmcc.beanfun.entity.client.BeanfunStringResult;
import com.starmcc.beanfun.manager.AdvancedTimerMamager;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.manager.impl.AdvancedTimerTask;
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
        DownloadClient.getInstance().execute(
                new URL(beanfunQrCodeResult.getQrImageUrl()),
                new File(QsConstant.PATH_APP_PLUGINS + "onlineQrCode.jpg"),
                (state, file, process, speed, e) -> {
                    if (state == DownloadClient.Process.State.下载完毕) {
                        if (Objects.isNull(file)) {
                            try {
                                // 下载二维码出现问题 重新下载
                                Thread.sleep(1000);
                                ThreadPoolManager.execute(this::loadQrCode, false);
                            } catch (Exception ex) {
                                log.error("error={}", e.getMessage(), e);
                            }
                            return;
                        }
                        FrameManager.getInstance().runLater(() -> {
                            Image image = new Image(file.toURI().toString());
                            imageViewQrCode.setImage(image);
                            labelQrCodeTips.setText("请扫描二维码登录");
                            labelQrCodeTips.setTextFill(Color.GREEN);
                        });
                        // 开始心跳检查
                        AdvancedTimerMamager.getInstance().addTask(new AdvancedTimerTask() {
                            @Override
                            public void start() throws Exception {
                                checkQrCodeStatus();
                            }
                        }, 1000, 2000);

                    }
                });
    }

    /**
     * 检查二维码状态
     *
     * @throws Exception 异常
     */
    public void checkQrCodeStatus() throws Exception {
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
                    FrameManager.getInstance().openWindow(FXPageEnum.主页);
                    FrameManager.getInstance().closeWindow(FXPageEnum.登录页);
                    FrameManager.getInstance().closeWindow(FXPageEnum.二维码登录);
                } catch (Exception e) {
                    log.error("error = {}", e.getMessage(), e);
                }
            });
        } else if (result == 2) {
            ThreadPoolManager.execute(this::loadQrCode, false);
        }
    }

}
