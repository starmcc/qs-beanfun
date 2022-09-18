package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.ConfigJson;
import com.starmcc.beanfun.model.client.Account;
import com.starmcc.beanfun.model.client.BeanfunStringResult;
import com.starmcc.beanfun.utils.AesUtil;
import com.starmcc.beanfun.utils.ConfigFileUtils;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.ThreadUtils;
import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 账户处理程序
 *
 * @author starmcc
 * @date 2022/03/21
 */
@Slf4j
public class AccountHandler {


    /**
     * 记录账密配置
     *
     * @param account  账户
     * @param password 密码
     */
    public static void recordActPwd(String account, String password) {
        List<ConfigJson.ActPwd> actPwds = QsConstant.config.getActPwds();
        if (DataTools.collectionIsNotEmpty(actPwds)) {
            Iterator<ConfigJson.ActPwd> iterator = actPwds.iterator();
            while (iterator.hasNext()) {
                ConfigJson.ActPwd next = iterator.next();
                if (StringUtils.equals(next.getAct(), account)) {
                    iterator.remove();
                    break;
                }
            }
        }
        try {
            ConfigJson.ActPwd actPwd = new ConfigJson.ActPwd();
            // 加密储存
            final String key = DataTools.getComputerUniqueId();
            account = AesUtil.encode(key, account);
            password = AesUtil.encode(key, password);
            actPwd.setAct(account);
            actPwd.setPwd(password);
            actPwds.add(0, actPwd);
            QsConstant.config.setActPwds(actPwds);
            ConfigFileUtils.writeConfig(QsConstant.config);
        } catch (Exception e) {
            log.error("异常 e={}", e.getMessage(), e);
        }
    }

    /**
     * 获取动态密码
     *
     * @param runnable 可运行
     */
    public static void getDynamicPassword(Account account, @NotNull BiConsumer<String, String> runnable) {
        ThreadUtils.executeThread(() -> {
            try {
                BeanfunStringResult pwdResult = BeanfunClient.run().getDynamicPassword(account, QsConstant.beanfunModel.getToken());
                if (!pwdResult.isSuccess()) {
                    Platform.runLater(() -> QsConstant.alert(pwdResult.getMsg(), Alert.AlertType.ERROR));
                }
                log.debug("动态密码 ={}", pwdResult.getData());
                runnable.accept(account.getId(), pwdResult.getData());
            } catch (Exception e) {
                log.error("获取密码失败 e={}", e.getMessage(), e);
                Platform.runLater(() -> QsConstant.alert("获取动态密码异常:" + e.getMessage(), Alert.AlertType.ERROR));
            }
        });
    }
}
