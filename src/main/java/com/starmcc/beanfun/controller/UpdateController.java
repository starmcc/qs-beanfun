package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.DownloadClient;
import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.client.UpdateModel;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.utils.FileTools;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * 进度控制器
 *
 * @author starmcc
 * @date 2022/09/22
 */
@Slf4j
public class UpdateController implements Initializable {

    public static UpdateModel model;
    @FXML
    private TextArea textAreaContent;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label labelProcess;
    @FXML
    private Label labelSpeed;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textAreaContent.setText(model.getContent());
        labelProcess.setText("0%");
        labelSpeed.setText("-KB/s");
        try {
            this.download();
        } catch (Exception e) {
            log.error("Runtime error e={}", e.getMessage(), e);
        }
    }

    /**
     * 下载
     *
     * @throws Exception 异常
     */
    private void download() throws Exception {
        String fileName = QsConstant.PATH_APP + "\\" + QsConstant.APP_NAME + ".exe.tmp";
        ThreadPoolManager.execute(() -> {
            DownloadClient.getInstance().execute(new URL(model.getUrl()), new File(fileName), (state, file, process, speed, e) -> {
                if (!state.isNormal()) {
                    FrameManager.getInstance().runLater(() -> {
                        FrameManager.getInstance().message("更新失败-" + state.toString(), Alert.AlertType.WARNING);
                        if (Objects.nonNull(e)) {
                            log.error("更新失败 e={}", e.getMessage(), e);
                        }
                        FrameManager.getInstance().closeWindow(FXPageEnum.更新页);
                    });
                    return;
                }
                if (state == DownloadClient.Process.State.下载中) {
                    progressBar.setProgress(process / 100D);
                    FrameManager.getInstance().runLater(() -> labelProcess.setText(process + "%"));
                } else if (state == DownloadClient.Process.State.速度回显) {
                    FrameManager.getInstance().runLater(() -> {
                        String speedTxt = "";
                        BigDecimal b1024 = new BigDecimal(1024);
                        if (speed.compareTo(b1024) >= 0) {
                            speedTxt = speed.divide(b1024, 2, RoundingMode.HALF_UP) + "MB/s";
                        } else {
                            speedTxt = speed + "KB/s";
                        }
                        labelSpeed.setText(speedTxt);
                    });
                }

                if (state != DownloadClient.Process.State.下载完毕) {
                    return;
                }

                this.downloadComplete(file);
                FrameManager.getInstance().exit();
            });
        });

    }

    /**
     * 下载完成
     *
     * @param file 文件
     */
    private void downloadComplete(File file) {
        // 构建Bat文件
        String appExe = QsConstant.APP_NAME + ".exe";
        StringBuffer bat = new StringBuffer();
        bat.append("@echo off").append("\n");
        bat.append("timeout /t 5 /nobreak\n");
        // 先刪除旧的
        bat.append("if exist ").append(appExe).append(".old (\n");
        bat.append("  del /f ").append(appExe).append(".old").append("\n");
        bat.append(")\n");
        // 在将当前运行的改成旧的
        bat.append("ren ").append(appExe).append(" ").append(appExe).append(".old").append("\n");
        // 新下载的改成QsBeanfun.exe
        bat.append("ren ").append(file.getName()).append(" ").append(appExe).append("\n");
        // 重新启动
        bat.append("start ").append(appExe).append("\n");
        bat.append("exit");
        String batPath = QsConstant.PATH_APP + "\\update.bat";
        // 写入bat
        FileTools.writeFile(bat.toString(), batPath);
        try {
            // 执行update.bat
            String[] cmd = {"cmd", "/c", "start", ".\\update.bat"};
            Runtime.getRuntime().exec(cmd, null, new File(batPath).getParentFile());
        } catch (Exception e) {
            log.error("Runtime error e={}", e.getMessage(), e);
        }
    }
}