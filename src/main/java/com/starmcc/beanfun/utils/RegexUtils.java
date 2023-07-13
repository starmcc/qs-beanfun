package com.starmcc.beanfun.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 *
 * @author starmcc
 * @date 2022/03/19
 */
public class RegexUtils {


    public static enum Constant {
        HK_SESSION_KEY(Pattern.compile("otp1=(.*)&p")),
        HK_VIEWSTATE(Pattern.compile("id=\"__VIEWSTATE\"\\svalue=\"(.*?)\"\\s/>")),
        HK_EVENTVALIDATION(Pattern.compile("id=\"__EVENTVALIDATION\"\\svalue=\"(.*?)\"\\s/>")),
        HK_VIEWSTATEGENERATOR(Pattern.compile("id=\"__VIEWSTATEGENERATOR\"\\svalue=\"(.*?)\"\\s/>")),
        HK_ACCOUNT_MAX(Pattern.compile("<div\\sid=\\\"divServiceAccountAmountLimitNotice\\\".*>.*:(\\d+)</div>")),
        HK_ACCOUNT_LIST(Pattern.compile("onclick=\\\"([^\\\"]*)\\\"><div id=\\\"(\\w+)\\\" sn=\\\"(\\d+)\\\" name=\\\"([^\\\"]+)\\\"")),
        HK_CREATE_ACCOUNT(Pattern.compile("<div\\sid\\=\"divServiceInstruction\">請先創立新帳戶</div>")),
        HK_LOGIN_ERROR_MSG(Pattern.compile("<script>ShowMsgBox\\('(.*?)'\\);</script>")),
        HK_LOGIN_AKEY(Pattern.compile("AuthKey\\.value\\s=\\s\"(.*?)\";parent")),
        HK_GET_PWD_OTP_KEY(Pattern.compile("GetResultByLongPolling&key=(.*?)\"")),
        HK_GET_SERVICE_CREATE_TIME(Pattern.compile("ServiceAccountCreateTime:\\s\\\"([^\\\"]+)\\\"")),
        HK_GET_PWD_OTP_SECRET(Pattern.compile("var\\sm_strSecretCode\\s=\\s'(.*)'")),
        HK_GAME_POINTS(Pattern.compile("\"RemainPoint\"\\s:\\s\"(\\d+)\"")),
        HK_CERT_VERIFY(Pattern.compile("<div\\sid=\\\"divServiceAccountAmountLimitNotice\\\"\\sclass=\\\"InnerContent\\\">(.*)</div>")),


        // =========================== tw ========================
        TW_SESSION_KEY(Pattern.compile("var\\sstrSessionKey\\s=\\s\"(.*)\";var\\sstrClientID")),
        TW_VIEWSTATE(Pattern.compile("id=\"__VIEWSTATE\"\\svalue=\"(.*?)\"\\s/>")),
        TW_EVENTVALIDATION(Pattern.compile("id=\"__EVENTVALIDATION\"\\svalue=\"(.*?)\"\\s/>")),
        TW_VIEWSTATEGENERATOR(Pattern.compile("id=\"__VIEWSTATEGENERATOR\"\\svalue=\"(.*?)\"\\s/>")),
        TW_LOGIN_ERROR_MSG(Pattern.compile("\\$\\(function\\(\\)\\{MsgBox\\.Show\\('(.*?)'\\);}\\);")),
        TW_LOGIN_AKEY(Pattern.compile("AuthKey\\.value\\s=\\s\"(.*?)\";parent")),
        TW_ACCOUNT_MAX(Pattern.compile("<div\\sid=\\\"divServiceAccountAmountLimitNotice\\\".*>.*:(\\d+)</div>")),
        TW_ACCOUNT_LIST(Pattern.compile("onclick=\\\"([^\\\"]*)\\\"><div id=\\\"(\\w+)\\\" sn=\\\"(\\d+)\\\" name=\\\"([^\\\"]+)\\\"")),
        TW_CREATE_ACCOUNT(Pattern.compile("<div\\sid\\=\"divServiceInstruction\">請先創立新帳戶</div>")),
        TW_CERT_VERIFY(Pattern.compile("<div\\sid=\\\"divServiceAccountAmountLimitNotice\\\"\\sclass=\\\"InnerContent\\\">(.*)</div>")),
        TW_GET_SERVICE_CREATE_TIME(Pattern.compile("ServiceAccountCreateTime:\\s\\\"([^\\\"]+)\\\"")),
        TW_GET_PWD_OTP_KEY(Pattern.compile("GetResultByLongPolling&key=(.*?)\"")),
        TW_GET_PWD_OTP_SECRET(Pattern.compile("var\\sm_strSecretCode\\s=\\s'(.*)'")),
        TW_GAME_POINTS(Pattern.compile("\"RemainPoint\"\\s:\\s\"(\\d+)\"")),
        TW_QRCODE_AKEY(Pattern.compile("akey=(.*)&authkey=(.*)&")),

        // =============
        COMMON_IP_ADDRESS(Pattern.compile("PROXY\\s(\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+)")),
        COMMON_CHINA_STRING(Pattern.compile("[\u4E00-\u9FFF]+")),
        COMMON_NUMBER(Pattern.compile("^[0-9]*$")),
        COMMON_RATE_POINTS(Pattern.compile("<p></td><td><p>(.*?)</p></td><td><p>")),
        COMMON_SIX_NUMBER(Pattern.compile("^\\d{6}$")),
        ;
        private final Pattern pattern;

        Constant(Pattern pattern) {
            this.pattern = pattern;
        }

        public Pattern getPattern() {
            return pattern;
        }

    }

    public static String getIndex(int group, int children, List<List<String>> list) {

        if (DataTools.collectionIsEmpty(list) || DataTools.collectionIsEmpty(list.get(0))) {
            return "";
        }

        if (list.size() < group + 1) {
            return "";
        }

        if (list.get(group).size() < children + 1) {
            return "";
        }

        return list.get(group).get(children);
    }


    public static List<List<String>> regex(RegexUtils.Constant pattern, String content) {
        return regex(pattern.getPattern(), content);
    }

    public static List<List<String>> regex(Pattern pattern, String content) {
        Matcher matcher = pattern.matcher(content);
        List<List<String>> result = new ArrayList<>();
        while (matcher.find()) {
            List<String> list = new ArrayList<>();
            list.add(matcher.group(0));
            for (int i = 0; i < matcher.groupCount(); i++) {
                list.add(matcher.group(i + 1));
            }
            result.add(list);
        }
        return result;
    }


    public static boolean test(RegexUtils.Constant pattern, String content) {
        return test(pattern.getPattern(), content);
    }

    public static boolean test(Pattern pattern, String content) {
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }


}
