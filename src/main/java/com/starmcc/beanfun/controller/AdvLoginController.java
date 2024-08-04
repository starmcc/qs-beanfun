package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.client.QsHttpResponse;
import com.starmcc.beanfun.entity.client.ReqParams;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.utils.FileTools;
import com.starmcc.beanfun.utils.RegexUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

@Slf4j
public class AdvLoginController implements Initializable {

    @FXML
    private TextField textFieldPhone;
    @FXML
    private CheckBox checkBoxSave;
    @FXML
    private TextField textFieldCode;
    @FXML
    private ImageView imageViewCode;
    @FXML
    private Label labelPhoneTips;

    private String viewstate;
    private String eventvalidation;
    private String viewstategenerator;
    private String samplecaptcha;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initUi();
        openAdvanceCheck();
    }

    private void initUi() {
        imageViewCode.setOnMouseClicked(event -> this.openAdvanceCheck());
        checkBoxSave.setSelected(QsConstant.config.getAdvVerifyLoginPhoneSave());
        checkBoxSave.setOnAction(event -> {
            QsConstant.config.setAdvVerifyLoginPhoneSave(checkBoxSave.isSelected());
            FileTools.saveConfig(QsConstant.config);
        });
    }

    private void openAdvanceCheck() {
        try {
            QsHttpResponse rsp = HttpClient.getInstance().get("https://tw.newlogin.beanfun.com/LoginCheck/AdvanceCheck.aspx");
            if (!rsp.getSuccess()) {
                return;
            }
            String content = rsp.getContent();

            List<List<String>> dataList = RegexUtils.regex(RegexUtils.Constant.TW_VIEWSTATE, content);
            viewstate = RegexUtils.getIndex(0, 1, dataList);
            dataList = RegexUtils.regex(RegexUtils.Constant.TW_EVENTVALIDATION, content);
            eventvalidation = RegexUtils.getIndex(0, 1, dataList);
            dataList = RegexUtils.regex(RegexUtils.Constant.TW_VIEWSTATEGENERATOR, content);
            viewstategenerator = RegexUtils.getIndex(0, 1, dataList);
            dataList = RegexUtils.regex(Pattern.compile("id=\"LBD_VCID_c_logincheck_advancecheck_samplecaptcha\" value=\"(.*)\""), content);
            samplecaptcha = RegexUtils.getIndex(0, 1, dataList);

            dataList = RegexUtils.regex(Pattern.compile("<span id=\"lblAuthType\">(.*)</span>"), content);
            String text = RegexUtils.getIndex(0, 1, dataList);
            labelPhoneTips.setText(text);
            loadImage();
        } catch (Exception e) {
            log.error("error={}", e.getMessage(), e);
        }
    }


    private void loadImage() {
        try {
            String url = "https://tw.newlogin.beanfun.com/LoginCheck/BotDetectCaptcha.ashx";
            ReqParams reqParams = ReqParams.getInstance()
                    .addParam("get", "image")
                    .addParam("c", "c_logincheck_advancecheck_samplecaptcha")
                    .addParam("t", samplecaptcha);
            QsHttpResponse qsHttpResponse = HttpClient.getInstance().get(url, reqParams);
            Image image = new Image(new ByteArrayInputStream(qsHttpResponse.getByteData()));
            imageViewCode.setImage(image);
        } catch (Exception e) {
            log.error("error={}", e.getMessage(), e);
        }
    }


    @FXML
    public void continueLogin() {
        ReqParams reqParams = ReqParams.getInstance()
                .addParam("__VIEWSTATE", viewstate)
                .addParam("__EVENTVALIDATION", eventvalidation)
                .addParam("__VIEWSTATEGENERATOR", viewstategenerator)
                .addParam("txtVerify", textFieldPhone.getText())
                .addParam("CodeTextBox", textFieldCode.getText())
                .addParam("imgbtnSubmit.x", "73")
                .addParam("imgbtnSubmit.y", "23")
                .addParam("LBD_VCID_c_logincheck_advancecheck_samplecaptcha", samplecaptcha);

        try {
            QsHttpResponse req = HttpClient.getInstance().post("https://tw.newlogin.beanfun.com/LoginCheck/AdvanceCheck.aspx", reqParams);
            if (!req.getSuccess()) {
                return;
            }

            //
            List<List<String>> dataList = RegexUtils.regex(Pattern.compile("<span id=\"lblMessage\" style=\"color:Red;\">(.*?)</span>"), req.getContent());
            String msg = RegexUtils.getIndex(0, 1, dataList);
            if (StringUtils.isNotBlank(msg)) {
                FrameManager.getInstance().messageMaster(msg, Alert.AlertType.ERROR, FXPageEnum.ADV_LOGIN);
                openAdvanceCheck();
                return;
            }
            // alert('感謝您的配合，您的資料已驗證成功，\n請您於五分鐘內再次輸入您的帳號密碼進行驗證登入。！');window.top.location='https://tw.beanfun.com/'//]]>
            dataList = RegexUtils.regex(Pattern.compile("alert\\('([^']*)'\\)"), req.getContent());
            msg = RegexUtils.getIndex(0, 1, dataList);
            if (StringUtils.isNotBlank(msg)) {
                FrameManager.getInstance().messageMaster(msg, Alert.AlertType.INFORMATION, FXPageEnum.ADV_LOGIN);
            }
            //记录
            if (checkBoxSave.isSelected()) {
                QsConstant.config.setAdvVerifyLoginPhone(textFieldPhone.getText());
                FileTools.saveConfig(QsConstant.config);
            }

            FrameManager.getInstance().closeWindow(FXPageEnum.ADV_LOGIN);


        } catch (Exception e) {
            log.error("error={}", e.getMessage(), e);
        }


    }
}
