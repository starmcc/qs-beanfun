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

    public static final Pattern PTN_OPT = Pattern.compile("var\\sotp1\\s=\\s\"(.*?)\";");
    public static final Pattern PTN_VIEWSTATE = Pattern.compile("id=\"__VIEWSTATE\"\\svalue=\"(.*?)\"\\s/>");
    public static final Pattern PTN_EVENTVALIDATION = Pattern.compile("id=\"__EVENTVALIDATION\"\\svalue=\"(.*?)\"\\s/>");
    public static final Pattern PTN_VIEWSTATEGENERATOR = Pattern.compile("id=\"__VIEWSTATEGENERATOR\"\\svalue=\"(.*?)\"\\s/>");
    public static final Pattern PTN_LOGIN_TOKEN = Pattern.compile("ProcessLoginV2\\(\\{token:\\\\\"(.*?)\\\\\",");
    public static final Pattern PTN_LOGIN_ERRMSG = Pattern.compile("<div\\sid=\"divMsg\"\\sclass=\"divMsg\">(.*?)\\</div\\>");
    public static final Pattern PTN_LOGIN_ACCOUNT_LOCATION = Pattern.compile("document.location\\s=\\s\"(.*?)\";");
    public static final Pattern PTN_LOGIN_ACCOUNT_LIST = Pattern.compile("<li class=\\\"(.*?)\\\"\\stitle=\\\".*\\\"\\sonclick=\\\"StartGame\\('(.*?)',\\s'(.*?)',\\s'(.*?)',\\s'(.*?)',\\s'(.*?)',\\s'(.*?)',\\s'(.*?)'\\)");
    public static final Pattern PTN_LOGIN_CREATE_ACCOUNT = Pattern.compile("<div\\sid\\=\"divServiceInstruction\">請先創立新帳戶</div>");
    public static final Pattern PTN_LOGIN_GAME_POINTS = Pattern.compile("\"RemainPoint\"\\s:\\s\"(.*?)\"");
    public static final Pattern PTN_CHINA_PATH = Pattern.compile("[\u4E00-\u9FFF]+");
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
