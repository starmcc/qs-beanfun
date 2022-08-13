package com.starmcc.beanfun.controller.page;

import com.starmcc.beanfun.constant.CalcConstant;
import com.starmcc.beanfun.handler.EquipmentHandler;
import com.starmcc.beanfun.listener.FocusedTextZeroChangeListener;
import com.starmcc.beanfun.listener.TextValChangeListener;
import com.starmcc.beanfun.model.CalcModel;
import com.starmcc.beanfun.model.EquipmentAutoCalcParam;
import com.starmcc.beanfun.model.EquipmentCalcParam;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 装备控制器
 *
 * @author starmcc
 * @date 2022/04/02
 */
@Slf4j
public class EquipmentController implements Initializable {

    @FXML
    private ComboBox<Integer> levelSelectBox;
    @FXML
    private TextField totalAtkText;  // 总攻击
    @FXML
    private TextField originAtkText; // 原攻击
    @FXML
    private TextField starAtkText; // 星火攻击
    @FXML
    private TextField starLevelText; // 星力数
    @FXML
    private Label reelNumLabel; // 上卷次数文字提示
    @FXML
    private TextField reelNumText; // 上卷次数
    @FXML
    private Label appendAtkLabel; // 附加攻击
    @FXML
    private TextField gloryReelNumText; // 荣耀卷
    @FXML
    private TextField gloryValText; // 荣耀均值
    @FXML
    private TextField blackReelNumText; // 黑卷
    @FXML
    private TextField vReelNumText; // V卷
    @FXML
    private TextField xReelNumText; // X卷
    @FXML
    private TextField redReelNumText; // Red卷
    @FXML
    private ToggleGroup typeToggleGroup; // 装备类型选择
    @FXML
    private CheckBox autoCheckBox; // 自动识别开关

    @FXML
    private Label autoText; // 自动识别文本
    @FXML
    private Label gloryLabel; // 荣耀卷文案
    @FXML
    private Label gloryValLabel; // 荣耀均值文案
    @FXML
    private Label blackLabel; // 黑卷文案
    @FXML
    private Label vLabel; // V卷文案
    @FXML
    private Label xLabel; // X卷文案
    @FXML
    private Label redLabel; // Red卷文案

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化组件
        this.initComponents();
    }


    /**
     * 初始化组件
     */
    private void initComponents() {
        // 初始化等级
        ObservableList<Integer> items = levelSelectBox.getItems();
        items.add(200);
        items.add(160);
        items.add(150);
        levelSelectBox.getSelectionModel().selectFirst();
        this.autoViewModel(autoCheckBox.isSelected());
        // 初始化事件
        this.initListener();

    }

    private void initListener() {
        // 所有框框事件，避免输入其他内容
        initTextFieldListener(totalAtkText);
        initTextFieldListener(originAtkText);
        initTextFieldListener(starAtkText);
        initTextFieldListener(starLevelText);
        initTextFieldListener(reelNumText);
        initTextFieldListener(gloryReelNumText);
        initTextFieldListener(gloryValText);
        initTextFieldListener(blackReelNumText);
        initTextFieldListener(vReelNumText);
        initTextFieldListener(xReelNumText);
        initTextFieldListener(redReelNumText);
        levelSelectBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> this.calcData());
        typeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> this.calcData());
        autoCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> this.autoViewModel(newValue));
    }

    /**
     * 初始化文本域侦听器
     *
     * @param textField 文本字段
     */
    private void initTextFieldListener(TextField textField) {
        textField.focusedProperty().addListener(new FocusedTextZeroChangeListener(textField));
        textField.textProperty().addListener(new TextValChangeListener(textField));
        textField.setOnMouseClicked(event -> textField.setText(""));
        textField.textProperty().addListener((observable, oldValue, newValue) -> this.calcData());
    }

    private void calcData() {
        if (!autoCheckBox.isSelected()) {
            // 手动模式
            EquipmentCalcParam param = new EquipmentCalcParam();
            Integer originAtk = StringUtils.isBlank(originAtkText.getText()) ? 0 : Integer.parseInt(originAtkText.getText());
            param.setOriginAtk(originAtk);
            Integer starAtk = StringUtils.isBlank(starAtkText.getText()) ? 0 : Integer.parseInt(starAtkText.getText());
            param.setStarAtk(starAtk);
            Integer gloryNum = StringUtils.isBlank(gloryReelNumText.getText()) ? 0 : Integer.parseInt(gloryReelNumText.getText());
            param.setGloryNum(gloryNum);
            Integer gloryValNum = StringUtils.isBlank(gloryValText.getText()) ? 0 : Integer.parseInt(gloryValText.getText());
            param.setGloryValNum(gloryValNum);
            Integer blackNum = StringUtils.isBlank(blackReelNumText.getText()) ? 0 : Integer.parseInt(blackReelNumText.getText());
            param.setBlackNum(blackNum);
            Integer vNum = StringUtils.isBlank(vReelNumText.getText()) ? 0 : Integer.parseInt(vReelNumText.getText());
            param.setVNum(vNum);
            Integer xNum = StringUtils.isBlank(xReelNumText.getText()) ? 0 : Integer.parseInt(xReelNumText.getText());
            param.setXNum(xNum);
            Integer redNum = StringUtils.isBlank(redReelNumText.getText()) ? 0 : Integer.parseInt(redReelNumText.getText());
            param.setRedNum(redNum);
            Integer starLevel = StringUtils.isBlank(starLevelText.getText()) ? 0 : Integer.parseInt(starLevelText.getText());
            if (starLevel > 25) {
                param.setStarLevel(25);
            } else {
                param.setStarLevel(starLevel);
            }
            Integer level = levelSelectBox.getSelectionModel().getSelectedItem();
            param.setLevel(level);
            Toggle selectedToggle = typeToggleGroup.getSelectedToggle();
            String equipmentType = (String) selectedToggle.getUserData();
            param.setEquipmentType(CalcConstant.EquipmentType.build(Integer.parseInt(equipmentType)));
            CalcModel model = EquipmentHandler.calc(param);
            totalAtkText.setText(String.valueOf(model.getTotalAtk()));
            appendAtkLabel.setText(String.valueOf(model.getAppendAtk()));
        } else {
            // 自动模式
            EquipmentAutoCalcParam param = new EquipmentAutoCalcParam();
            Toggle selectedToggle = typeToggleGroup.getSelectedToggle();
            String equipmentType = (String) selectedToggle.getUserData();
            param.setEquipmentType(CalcConstant.EquipmentType.build(Integer.parseInt(equipmentType)));
            Integer originAtk = StringUtils.isBlank(originAtkText.getText()) ? 0 : Integer.parseInt(originAtkText.getText());
            param.setOriginAtk(originAtk);
            Integer totalAtk = StringUtils.isBlank(totalAtkText.getText()) ? 0 : Integer.parseInt(totalAtkText.getText());
            param.setTotalAtk(totalAtk);
            Integer starAtk = StringUtils.isBlank(starAtkText.getText()) ? 0 : Integer.parseInt(starAtkText.getText());
            param.setStarAtk(starAtk);
            Integer reelNum = StringUtils.isBlank(reelNumText.getText()) ? 0 : Integer.parseInt(reelNumText.getText());
            param.setReelNum(reelNum);
            Integer starLevel = StringUtils.isBlank(starLevelText.getText()) ? 0 : Integer.parseInt(starLevelText.getText());
            if (starLevel > 25) {
                param.setStarLevel(25);
            } else {
                param.setStarLevel(starLevel);
            }
            Integer level = levelSelectBox.getSelectionModel().getSelectedItem();
            param.setLevel(level);
            String value = EquipmentHandler.autoCalc(param);
            autoText.setText(value);
        }

    }


    private void autoViewModel(boolean auto) {
        totalAtkText.setDisable(!auto);
        reelNumLabel.setVisible(auto);
        reelNumText.setVisible(auto);
        gloryReelNumText.setVisible(!auto);
        gloryValText.setVisible(!auto);
        blackReelNumText.setVisible(!auto);
        vReelNumText.setVisible(!auto);
        xReelNumText.setVisible(!auto);
        redReelNumText.setVisible(!auto);
        autoText.setVisible(auto);
        if (auto) {
            autoText.setText("自动模式");
        }
        gloryLabel.setVisible(!auto);
        gloryValLabel.setVisible(!auto);
        blackLabel.setVisible(!auto);
        vLabel.setVisible(!auto);
        xLabel.setVisible(!auto);
        redLabel.setVisible(!auto);
    }

}
