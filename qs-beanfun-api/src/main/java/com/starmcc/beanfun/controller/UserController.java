package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.config.PassToken;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.handler.AccountHandler;
import com.starmcc.beanfun.model.client.Account;
import com.starmcc.beanfun.model.client.BeanfunAccountResult;
import com.starmcc.beanfun.model.client.BeanfunModel;
import com.starmcc.beanfun.model.client.BeanfunStringResult;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.qmframework.body.QmBody;
import com.starmcc.qmframework.controller.QmResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private AccountHandler accountHandler;

    @Autowired
    private ApplicationContext applicationContext;

    @PassToken
    @PostMapping("/user/login")
    public String login(@QmBody String account,
                        @QmBody String password) {
        try {
            BeanfunStringResult result = BeanfunClient.run().login(account, password);
            if (!result.isSuccess()) {
                return QmResult.fail(result.getMsg(), null);
            }
            if (BooleanUtils.isTrue(QsConstant.config.getRecordActPwd())) {
                accountHandler.recordActPwd(account, password);
            }
            QsConstant.beanfunModel = new BeanfunModel();
            QsConstant.beanfunModel.setToken(result.getData());

            BeanfunAccountResult actResult = this.refreshAccount();
            if (!actResult.isSuccess()) {
                return QmResult.fail(actResult.getMsg(), null);
            }

            return QmResult.success(QsConstant.beanfunModel.getToken());
        } catch (Exception e) {
            log.error("异常 e={}", e.getMessage(), e);
            return QmResult.error(e.getMessage(), null);
        }
    }


    /**
     * 更新帐户列表
     *
     * @return {@link BeanfunAccountResult}
     * @throws Exception 异常
     */
    private BeanfunAccountResult refreshAccount() throws Exception {
        BeanfunAccountResult actResult = BeanfunClient.run().getAccountList(QsConstant.beanfunModel.getToken());
        if (!actResult.isSuccess()) {
            return actResult;
        }
        QsConstant.beanfunModel.setAccountList(actResult.getAccountList());
        QsConstant.beanfunModel.setNewAccount(actResult.getNewAccount());
        if (DataTools.collectionIsNotEmpty(actResult.getAccountList())) {
            QsConstant.nowAccount = actResult.getAccountList().get(0);
        }
        return actResult;
    }


    @PostMapping("/user/select_account")
    public String selectAccount(@RequestParam String accountId) {
        List<Account> accountList = QsConstant.beanfunModel.getAccountList();
        Optional<Account> account = accountList.stream().filter(act -> StringUtils.equals(act.getId(), accountId)).findFirst();
        QsConstant.nowAccount = account.isPresent() ? account.get() : null;
        return QmResult.success();
    }

    @GetMapping("/user/get_account_info")
    public String getAccountInfo() {
        return QmResult.success(QsConstant.beanfunModel.getAccountList());
    }


    @GetMapping("/user/get_points")
    public String getPoints() {
        return QmResult.success(this.getPointsText());
    }

    @PostMapping("/user/get_dynamic_password")
    public String getDynamicPassword() {
        try {
            if (Objects.isNull(QsConstant.beanfunModel)) {
                return QmResult.loginNotIn();
            }
            BeanfunStringResult pwdResult = BeanfunClient.run().getDynamicPassword(QsConstant.nowAccount, QsConstant.beanfunModel.getToken());
            if (!pwdResult.isSuccess() || StringUtils.isBlank(pwdResult.getData())) {
                return QmResult.fail(pwdResult.getMsg(), null);
            }
            return QmResult.success(pwdResult.getData());
        } catch (Exception e) {
            log.error("异常 e={}", e.getMessage(), e);
            return QmResult.error();
        }
    }


    @PostMapping("/user/add_account")
    public String addAccount(@QmBody(required = false) String name) {
        try {
            if (StringUtils.isBlank(name)) {
                return QmResult.fail("账号名称不能为空!", null);
            }

            BeanfunStringResult result = BeanfunClient.run().addAccount(name);
            if (!result.isSuccess()) {
                return QmResult.fail(result.getMsg(), null);
            }
            BeanfunAccountResult actResult = this.refreshAccount();
            if (!actResult.isSuccess()) {
                return QmResult.fail(actResult.getMsg(), null);
            }
            return QmResult.success();
        } catch (Exception e) {
            log.error("异常 e={}", e.getMessage(), e);
            return QmResult.error();
        }
    }

    @PostMapping("/user/edit_account")
    public String editAccount(@QmBody(required = false) String name) {
        try {
            if (StringUtils.isBlank(name)) {
                return QmResult.fail("账号名称不能为空!", null);
            }

            BeanfunStringResult result = BeanfunClient.run().changeAccountName(QsConstant.nowAccount.getId(), name);
            if (!result.isSuccess()) {
                return QmResult.fail(result.getMsg(), null);
            }
            BeanfunAccountResult actResult = this.refreshAccount();
            if (!actResult.isSuccess()) {
                return QmResult.fail(actResult.getMsg(), null);
            }
            return QmResult.success();
        } catch (Exception e) {
            log.error("异常 e={}", e.getMessage(), e);
            return QmResult.error();
        }
    }

    @GetMapping("/user/login_out")
    public String loginOut() {
        try {
            if (Objects.nonNull(QsConstant.beanfunModel)) {
                BeanfunClient.run().loginOut(QsConstant.beanfunModel.getToken());
            }
        } catch (Exception e) {
            log.error("退出登录异常 e={}", e.getMessage(), e);
        }
        QsConstant.beanfunModel = null;
        QsConstant.nowAccount = null;
        return QmResult.success();
    }


    /**
     * 获取游戏点数文本
     *
     * @return {@link String}
     */
    private String getPointsText() {
        String template = "{0}点[游戏内:{1}]";
        int gamePoints = 0;
        try {
            gamePoints = BeanfunClient.run().getGamePoints(QsConstant.beanfunModel.getToken());
        } catch (Exception e) {
            log.error("获取游戏点数异常 e={}", e.getMessage(), e);
        }
        if (gamePoints == 0) {
            return MessageFormat.format(template, 0, 0);
        }
        BigDecimal points = new BigDecimal(gamePoints);
        BigDecimal divide = points.divide(new BigDecimal("2.5"), 2, BigDecimal.ROUND_DOWN);
        return MessageFormat.format(template, gamePoints, divide.intValue());
    }
}
