package com.konsung.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 单位转换工具类
 *
 * @author ouyangfan
 * @version 0.0.1
 */
public class UnitConvertUtil {

    /**
     * 私有构造
     */
    private UnitConvertUtil() {

    }

    /**
     * Dp转换Px的方法
     * @param context 上下文
     * @param dp dp
     * @return 转换成px
     */
    public static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString
     * (int)来转换成16进制字符串。
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 去最高位
     * @param hexString String
     * @return byte[]
     */
    public static byte[] getByteformHexString(String hexString) {
        //去最高位
        byte[] bytes = hexStringToBytes(hexString);
        return changeByte(bytes);
    }

    /**
     *
     * @param a 数据源
     * @return 处理过的数据
     */
    public static byte[] changeByte(byte[] a) {
        List<Integer> iList = new ArrayList<Integer>();
        int len = 0;
        int length = 0;
        if (a != null) {
            length = a.length;
        } else {
            length = 0;
        }
        if (length % 2 == 1) {
            len = 1;
        }
        for (int i = 4; i < length - len; i++) {
            byte[] f = new byte[2];
            f[0] = a[i + 0];
            f[1] = a[i + 1];
            int v = toInt(f);
            if (v > 4096) {
                v = v % 4096;
            }
            iList.add(v);
            i++;
        }

        byte[] d = new byte[4 + iList.size() * 2];
        byte[] fb = toByteArray(iList.size(), 4);
        d[0] = fb[3];
        d[1] = fb[2];
        d[2] = fb[1];
        d[3] = fb[0];
        for (int i = 0; i < iList.size(); i++) {
            fb = toByteArray(iList.get(i), 2);
            d[4 + i * 2] = fb[1];
            d[4 + i * 2 + 1] = fb[0];
        }
        return d;
    }

    /**
     * 字节转int
     * @param bRefArr 字节
     * @return int
     */
    public static int toInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;
        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }

    /**
     *int转字节数组
     * @param iSource 数据源
     * @param iArrayLen 数据源
     * @return 字节数组
     */
    public static byte[] toByteArray(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
        for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
        }
        return bLocalArr;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 心电数据转换成float[]字符串
     * @param sixteenbytes 数据
     * @return float[]字符串
     */
    public static String ecgwaveToString(String sixteenbytes) {
        //转化的float[]字符串
        String s = "";

        //float[]的size
        int size = 0;
        byte[] bytes = hexStringToBytes(sixteenbytes);
        if (bytes != null) {
            size = bytes.length / 4;
        } else {
            return "";
        }
        ArrayList<Byte> data = new ArrayList<>();
        for (int i = 0; i < bytes.length; i++) {
            data.add(bytes[i]);
        }
        float[] point = new float[size];
        for (int i = 0; ; ) {
            if (data.size() > 16) {
                point[i++] = (data.get(0) & 0xFF) + ((data.get(1) & 0x0F) << 8);
                point[i++] = (data.get(8) & 0xFF) + ((data.get(9) & 0x0F) << 8);
                for (int j = 0; j < 8; j++) {
                    data.remove(0);
                }
            } else {
                break;
            }
        }
        s = Arrays.toString(point);
        return s;
    }

}
