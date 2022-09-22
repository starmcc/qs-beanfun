package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.ConfigModel;
import com.starmcc.beanfun.model.client.Account;
import com.starmcc.beanfun.model.client.BeanfunStringResult;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.utils.AesTools;
import com.starmcc.beanfun.utils.FileTools;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.windows.FrameService;
import com.sun.istack.internal.NotNull;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
        List<ConfigModel.ActPwd> actPwds = QsConstant.config.getActPwds();
        if (DataTools.collectionIsNotEmpty(actPwds)) {
            Iterator<ConfigModel.ActPwd> iterator = actPwds.iterator();
            while (iterator.hasNext()) {
                ConfigModel.ActPwd next = iterator.next();
                if (StringUtils.equals(next.getAct(), account)) {
                    iterator.remove();
                    break;
                }
            }
        }
        try {
            ConfigModel.ActPwd actPwd = new ConfigModel.ActPwd();
            // 加密储存
            final String key = DataTools.getComputerUniqueId();
            account = AesTools.encode(key, account);
            password = AesTools.encode(key, password);
            actPwd.setAct(account);
            actPwd.setPwd(password);
            actPwds.add(0, actPwd);
            QsConstant.config.setActPwds(actPwds);
            FileTools.saveConfig(QsConstant.config);
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
        ThreadPoolManager.execute(() -> {
            try {
                BeanfunStringResult pwdResult = BeanfunClient.run().getDynamicPassword(account, QsConstant.beanfunModel.getToken());
                if (!pwdResult.isSuccess()) {
                    FrameService.getInstance().runLater(() -> QsConstant.alert(pwdResult.getMsg(), Alert.AlertType.ERROR));
                }
                log.debug("动态密码 ={}", pwdResult.getData());
                if (Objects.isNull(account)) {
                    runnable.accept(null, pwdResult.getData());
                } else {
                    runnable.accept(account.getId(), pwdResult.getData());
                }

            } catch (Exception e) {
                log.error("获取密码失败 e={}", e.getMessage(), e);
                FrameService.getInstance().runLater(() -> QsConstant.alert("获取动态密码异常:" + e.getMessage(), Alert.AlertType.ERROR));
            }
        });
    }
}
