package com.konsung.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.konsung.utils.UiUtils.byteArrayToHex;

/**
 * 文本字符串的工具类
 */

public class TextUtils {

    /**
     * 删除浮点数字符串尾数后的0或者小数点
     * @param numStr 待处理的数字字符串
     * @return 处理后的数字字符串
     */
    public static String deleteEnd0(String numStr) {

        if (numStr != null && numStr.length() > 0) {
            if (numStr.contains(".")) { //包含小数点则为浮点数
                if (numStr.endsWith("0")) {
                    return deleteEnd0(numStr.substring(0, numStr.length() - 1));
                } else if (numStr.endsWith(".")) {
                    return numStr.substring(0, numStr.length() - 1);
                }
            }
        }
        return numStr;
    }

    /**
     * 判断字符串是否为空 包括null 长度为空 字符“null”
     * @param str 字符串
     * @return 是否为空
     */
    public boolean isEmpty(String str) {
        return str == null || str.length() == 0 || "null".equals(str);
    }


    /**
     * 判断点击输入的是否是表情
     * @param source String
     * @return 是否是表情
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是否是Emoji
     * @param codePoint 比较的单个字符
     * @return 是否是Emoji
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }


    /**
     * 检验端口是否合法
     * @param ipPort 端口号
     * @return 端口是否合法
     */
    public static boolean checkIsPort(String ipPort) {
        try {
            int port = Integer.valueOf(ipPort);
            if (port >= 0 && port <= 65535) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }


    /**
     * 过滤特殊字符
     * @param str 目标字符串
     * @return 过滤后的字符串
     * @throws PatternSyntaxException 异常
     */
    public static String stringFilter(String str) throws PatternSyntaxException {

        // 只允许字母和数字
        // String   regEx  =  "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\]" +
                ".<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 获取加密后的字符串
     *
     * @param pw 要转换md5的字符串
     * @return 返回已经转换的md5字符串
     */
    public static String stringMD5(String pw) {
        try {

            // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 输入的字符串转换成字节数组
            byte[] inputByteArray = pw.getBytes();
            // inputByteArray是输入字符串转换得到的字节数组
            messageDigest.update(inputByteArray);
            // 转换并返回结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 字符数组转换成字符串返回
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
