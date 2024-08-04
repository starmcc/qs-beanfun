package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.constant.FXPageEnum;
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
    public synchronized static void delActPwd(String account, LoginType loginType) {
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
            if (StringUtils.equals(AesTools.dncode(key, actPwd.getAct()), account)) {
                iterator.remove();
                break;
            }
        }
        QsConstant.config.setActPwds(actPwds);
        FileTools.saveConfig(QsConstant.config);
    }

    /**
     * 记录账密配置
     *
     * @param account   账户
     * @param password  密码
     * @param loginType type
     */
    public synchronized static void recordActPwd(String account, String password, LoginType loginType) {
        delActPwd(account, loginType);
        try {
            ConfigModel.ActPwd actPwd = new ConfigModel.ActPwd();
            final String key = DataTools.getComputerUniqueId();

            actPwd.setAct(AesTools.encode(key, account));
            actPwd.setPwd(AesTools.encode(key, password));
            actPwd.setType(loginType.getType());
            QsConstant.config.getActPwds().add(0, actPwd);
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
                FrameManager.getInstance().messageMaster(pwdResult.getMsg(), Alert.AlertType.ERROR, FXPageEnum.MAIN);
                return;
            }
            log.debug("动态密码 ={}", pwdResult.getData());
            if (Objects.isNull(account)) {
                runnable.accept(null, pwdResult.getData());
            } else {
                runnable.accept(account.getId(), pwdResult.getData());
            }
        } catch (Exception e) {
            log.error("error={}", e, e.getMessage());
            FrameManager.getInstance().messageMaster("自动输入异常", Alert.AlertType.ERROR, FXPageEnum.MAIN);
        }
    }
}
