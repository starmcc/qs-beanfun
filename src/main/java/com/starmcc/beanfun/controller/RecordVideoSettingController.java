package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.model.ConfigModel;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.utils.FileTools;
import com.starmcc.beanfun.utils.RegexUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class RecordVideoSettingController implements Initializable
{
    @FXML
    private ChoiceBox<Integer> choiceBoxVideoFps;
    @FXML
    private ComboBox<String> comboBoxVideoCodeRate;
    @FXML
    private RadioButton radioButtonGame;
    @FXML
    private RadioButton radioButtonScreen;
    @FXML
    private TextField textFieldFFmpegPath;
    @FXML
    private TextField textFieldVideoPath;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        FrameManager.getInstance().runLater(() ->
        {
            // 录像配置
            ConfigModel.RecordVideo recordVideo = QsConstant.config.getRecordVideo();
            ObservableList<Integer> fpsItems = FXCollections.observableArrayList();
            fpsItems.add(30);
            fpsItems.add(60);
            choiceBoxVideoFps.setItems(fpsItems);
            choiceBoxVideoFps.getSelectionModel().select(recordVideo.getFps());
            textFieldVideoPath.setText(recordVideo.getFolder());

            ObservableList<String> codeRateItems = FXCollections.observableArrayList();
            codeRateItems.add("1800");
            codeRateItems.add("2500");
            codeRateItems.add("3500");
            comboBoxVideoCodeRate.setItems(codeRateItems);
            comboBoxVideoCodeRate.getSelectionModel().select(recordVideo.getCodeRate());
            comboBoxVideoCodeRate.setValue(String.valueOf(recordVideo.getCodeRate()));

            Integer captureType = recordVideo.getCaptureType();
            if (Objects.equals(captureType, ConfigModel.RecordVideo.CaptureTypeEnum.游戏窗口.getType()))
            {
                radioButtonGame.setSelected(true);
            }
            else
            {
                radioButtonScreen.setSelected(true);
            }
            textFieldFFmpegPath.setText(recordVideo.getFfmpegPath());
            // =================== 录像控件事件 =====================
            comboBoxVideoCodeRate.valueProperty().addListener((obsVal, oldVal, newVal) ->
            {
                // 只能输入数字
                if (!RegexUtils.test(RegexUtils.Constant.COMMON_NUMBER, newVal))
                {
                    comboBoxVideoCodeRate.setValue(oldVal);
                    return;
                }
                ConfigModel.RecordVideo recordVideoTemp = QsConstant.config.getRecordVideo();
                int number = Integer.valueOf(newVal);
                if (number == 0)
                {
                    number = recordVideoTemp.getCodeRate();
                }
                recordVideoTemp.setCodeRate(number);
                comboBoxVideoCodeRate.setValue(String.valueOf(number));
                QsConstant.config.setRecordVideo(recordVideoTemp);
                FileTools.saveConfig(QsConstant.config);
            });

            ToggleGroup group = new ToggleGroup();
            radioButtonGame.setToggleGroup(group);
            radioButtonScreen.setToggleGroup(group);
            radioButtonGame.setOnAction(event ->
            {
                QsConstant.config.getRecordVideo().setCaptureType(ConfigModel.RecordVideo.CaptureTypeEnum.游戏窗口.getType());
                FileTools.saveConfig(QsConstant.config);
            });
            radioButtonScreen.setOnAction(event ->
            {
                QsConstant.config.getRecordVideo().setCaptureType(ConfigModel.RecordVideo.CaptureTypeEnum.全屏.getType());
                FileTools.saveConfig(QsConstant.config);
            });
        });
    }

    @FXML
    public void videoPathOpenAction(ActionEvent actionEvent) throws Exception
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("录像目录");
        File selectedfolder = directoryChooser.showDialog(QsConstant.JFX_STAGE_DATA.get(FXPageEnum.主页).getStage());
        if (Objects.isNull(selectedfolder))
        {
            return;
        }
        String path = selectedfolder.getPath();
        if (StringUtils.isBlank(path))
        {
            return;
        }
        textFieldVideoPath.setText(path);
        QsConstant.config.getRecordVideo().setFolder(path);
        FileTools.saveConfig(QsConstant.config);
    }

    @FXML
    public void selectVideoFpsAction(ActionEvent actionEvent)
    {
        Integer value = choiceBoxVideoFps.getValue();
        QsConstant.config.getRecordVideo().setFps(value);
        FileTools.saveConfig(QsConstant.config);
    }

    @FXML
    public void ffmpegOpenAction(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("FFmpeg.exe(*.exe)", "*.exe");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(QsConstant.JFX_STAGE_DATA.get(FXPageEnum.主页).getStage());
        if (Objects.isNull(file))
        {
            return;
        }
        String path = file.getPath();
        if (StringUtils.isBlank(path))
        {
            return;
        }
        textFieldFFmpegPath.setText(path);
        QsConstant.config.getRecordVideo().setFfmpegPath(path);
        FileTools.saveConfig(QsConstant.config);
    }

    @FXML
    public void clearRecordVideoAction(ActionEvent actionEvent)
    {
        String folder = QsConstant.config.getRecordVideo().getFolder();
        File file = new File(folder);
        if (!file.exists())
        {
            FrameManager.getInstance().messageSync("该目录不存在!", Alert.AlertType.WARNING);
            return;
        }
        File[] files = file.listFiles(pathname -> pathname.getName().endsWith(".mp4"));
        if (ArrayUtils.isEmpty(files))
        {
            FrameManager.getInstance().messageSync("没有录像可清空!", Alert.AlertType.WARNING);
            return;
        }
        int delNum = 0;
        for (File f : files)
        {
            delNum = f.delete() ? delNum++ : delNum;
        }
        FrameManager.getInstance().messageSync("已清理" + delNum + "个录像", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void downloadFFmpegAction(ActionEvent actionEvent) {
        FrameManager.getInstance().openWebUrl("https://ffmpeg.org/download.html");
    }
}
