package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.utils.FileTools;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * lrhandler
 *
 * @author starmcc
 * @date 2023/01/12
 */
@Slf4j
public class LocaleRemulatorHandler {

    private final static String LR_CONFIG_XML_PATH = QsConstant.PluginEnum.LOCALE_REMULATOR.getTargetPath() + "\\LRConfig.xml";

    /**
     * 设置输入法钩子
     *
     * @param hook 钩
     * @return boolean
     */
    public static boolean settingHookInput(boolean hook) {
        try {
            String xmlContent = FileTools.readFile(new File(LR_CONFIG_XML_PATH));
            String replaceContent = "<HookIME>" + String.valueOf(hook) + "</HookIME>";
            xmlContent = xmlContent.replaceAll("<HookIME>(.*)</HookIME>", replaceContent);
            FileTools.writeFile(xmlContent, LR_CONFIG_XML_PATH);
            return true;
        } catch (Exception e) {
            log.error("error e={}", e.getMessage(), e);
        }
        return false;
    }
}
