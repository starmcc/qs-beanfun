package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.LoginType;
import com.starmcc.beanfun.entity.client.Account;
import com.starmcc.beanfun.entity.client.BeanfunStringResult;
import com.starmcc.beanfun.entity.model.ConfigModel;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.utils.AesTools;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.FileTools;
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
     * 删除记录账号
     *
     * @param account   账户
     * @param loginType 登录类型
     */
    public static void delActPwd(String account, LoginType loginType) {
        List<ConfigModel.ActPwd> actPwds = QsConstant.config.getActPwds();
        if (DataTools.collectionIsEmpty(actPwds)) {
            return;
        }
        Iterator<ConfigModel.ActPwd> iterator = actPwds.iterator();
        while (iterator.hasNext()) {
            ConfigModel.ActPwd actPwd = iterator.next();
            if (!Objects.equals(actPwd.getType(), loginType.getType())) {
                continue;
            }
            final String key = DataTools.getComputerUniqueId();
            String act = AesTools.dncode(key, actPwd.getAct());
            if (StringUtils.equals(act, account)) {
                iterator.remove();
                break;
            }
        }
        FileTools.saveConfig(QsConstant.config);
    }

    /**
     * 记录账密配置
     *
     * @param account   账户
     * @param password  密码
     * @param loginType type
     */
    public static void recordActPwd(String account, String password, LoginType loginType) {
        // 加密储存
        final String key = DataTools.getComputerUniqueId();
        List<ConfigModel.ActPwd> actPwds = QsConstant.config.getActPwds();
        if (DataTools.collectionIsNotEmpty(actPwds)) {
            Iterator<ConfigModel.ActPwd> iterator = actPwds.iterator();
            while (iterator.hasNext()) {
                ConfigModel.ActPwd next = iterator.next();
                String dncodeAct = AesTools.dncode(key, next.getAct());
                if (StringUtils.equals(dncodeAct, account)
                        && Objects.equals(next.getType(), loginType.getType())) {
                    iterator.remove();
                }
            }
        }
        try {
            ConfigModel.ActPwd actPwd = new ConfigModel.ActPwd();

            account = AesTools.encode(key, account);
            password = AesTools.encode(key, password);
            actPwd.setAct(account);
            actPwd.setPwd(password);
            actPwd.setType(loginType.getType());
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
    public static void getDynamicPassword(Account account, BiConsumer<String, String> runnable) {
        try {
            BeanfunStringResult pwdResult = BeanfunClient.run().getDynamicPassword(account, QsConstant.beanfunModel.getToken());
            if (!pwdResult.isSuccess()) {
                FrameManager.getInstance().message(pwdResult.getMsg(), Alert.AlertType.ERROR);
            }
            log.debug("动态密码 ={}", pwdResult.getData());
            if (Objects.isNull(account)) {
                runnable.accept(null, pwdResult.getData());
            } else {
                runnable.accept(account.getId(), pwdResult.getData());
            }
        } catch (Exception e) {
            log.error("error={}", e, e.getMessage());
            FrameManager.getInstance().message("自动输入异常", Alert.AlertType.ERROR);
        }
    }
}
