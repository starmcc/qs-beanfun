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


    public static enum PatternHongKong {
        OTP(Pattern.compile("otp1=(.*)&p")),
        VIEWSTATE(Pattern.compile("id=\"__VIEWSTATE\"\\svalue=\"(.*?)\"\\s/>")),
        EVENTVALIDATION(Pattern.compile("id=\"__EVENTVALIDATION\"\\svalue=\"(.*?)\"\\s/>")),
        VIEWSTATEGENERATOR(Pattern.compile("id=\"__VIEWSTATEGENERATOR\"\\svalue=\"(.*?)\"\\s/>")),
        LOGIN_ACCOUNT_LIST(Pattern.compile("<div\\sid=\"(.*?)\"\\ssn=\"(\\d+)\"\\sname=\"(.*?)\"\\sinherited=\".*\"\\svisible=\"\\d\"")),
        LOGIN_CREATE_ACCOUNT(Pattern.compile("<div\\sid\\=\"divServiceInstruction\">請先創立新帳戶</div>")),
        LOGIN_ERROR_MSG(Pattern.compile("<script>ShowMsgBox\\('(.*?)'\\);</script>")),
        GET_PWD_OTP_KEY(Pattern.compile("GetResultByLongPolling&key=(.*?)\"")),
        GET_SERVICE_CREATE_TIME(Pattern.compile("ServiceAccountCreateTime: \\\"([^\\\"]+)\\\"")),
        GET_PWD_OTP_SECRET(Pattern.compile("var m_strSecretCode = '(.*)'")),
        GAME_POINTS(Pattern.compile("\"RemainPoint\"\\s:\\s\"(\\d+)\"")),
        ;
        private final Pattern pattern;

        PatternHongKong(Pattern pattern){
            this.pattern = pattern;
        }

        public Pattern getPattern() {
            return pattern;
        }

    }

    public static final Pattern PTN_CHINA_STRING = Pattern.compile("[\u4E00-\u9FFF]+");
    public static final Pattern PTN_NUMBER = Pattern.compile("^[0-9]*$");
    public static final Pattern PTN_RATE_POINTS = Pattern.compile("<p></td><td><p>(.*?)</p></td><td><p>");


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

    public static boolean test(Pattern pattern, String content) {
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }


}
