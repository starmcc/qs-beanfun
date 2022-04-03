package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.ConfigJson;
import com.starmcc.beanfun.utils.ConfigFileUtils;
import com.starmcc.beanfun.utils.DataTools;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * 账户处理程序
 *
 * @author starmcc
 * @date 2022/03/21
 */
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
        ConfigJson.ActPwd actPwd = new ConfigJson.ActPwd();
        actPwd.setAct(account);
        actPwd.setPwd(password);
        actPwds.add(0, actPwd);
        QsConstant.config.setActPwds(actPwds);
        ConfigFileUtils.writeJsonFile(QsConstant.config, QsConstant.APP_CONFIG);
    }


}
