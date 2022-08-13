package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.ConfigJson;
import com.starmcc.beanfun.utils.AesUtil;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

/**
 * 账户处理程序
 *
 * @author starmcc
 * @date 2022/03/21
 */
@Slf4j
@Component
public class AccountHandler {


    /**
     * 记录账密配置
     *
     * @param account  账户
     * @param password 密码
     */
    public void recordActPwd(String account, String password) {
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
            FileUtils.writeConfig(QsConstant.config);
        } catch (Exception e) {
            log.error("异常 e={}", e.getMessage(), e);
        }
    }


}
