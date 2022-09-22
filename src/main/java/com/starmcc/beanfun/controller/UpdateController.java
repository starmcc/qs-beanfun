package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.client.UpdateModel;
import com.starmcc.beanfun.utils.FileTools;
import com.starmcc.beanfun.windows.FrameService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textAreaContent.setText(model.getContent());
        try {
            this.download();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载
     *
     * @throws Exception 异常
     */
    private void download() throws Exception {
        String fileName = QsConstant.PATH_APP + QsConstant.APP_NAME + ".exe.tmp";
        HttpClient.getInstance().downloadFile(new URL(model.getUrl()), new File(fileName), (state, file, process, e) -> {
            if (!state.isNormal()) {
                FrameService.getInstance().runLater(() -> {
                    QsConstant.alert("更新失败-" + state.toString(), Alert.AlertType.WARNING);
                });
                return;
            }
            progressBar.setProgress(process / 100);
            if (state != HttpClient.Process.State.下载完毕) {
                return;
            }
            this.downloadComplete(file);
        });
    }


    /**
     * 下载完成
     *
     * @param file 文件
     */
    private void downloadComplete(File file) {
        // 构建Bat文件
        StringBuffer bat = new StringBuffer();
        bat.append("@echo off\n");
        bat.append("taskkill /f /im ").append(QsConstant.APP_NAME).append("\n");
        bat.append("ping 1.1.1.1 -n 1 -w 3000\n");
        bat.append("del /f \"").append(QsConstant.APP_NAME).append(".exe\"\n");
        bat.append("ren \"").append(file.getPath()).append("\" \"").append(QsConstant.APP_NAME).append(".exe\"\n");
        bat.append("start \"").append(QsConstant.APP_NAME).append(".exe\"\n");
        bat.append("del %0");
        // 写入bat
        String batPath = QsConstant.PATH_APP + "/update.bat";
        FileTools.writeFile(bat.toString(), batPath);
        try {
            // 执行bat
            new ProcessBuilder().command("cmd", "/c", batPath);
        } catch (Exception e) {
            log.error("运行异常 e={}", e.getMessage(), e);
        }
    }
}
