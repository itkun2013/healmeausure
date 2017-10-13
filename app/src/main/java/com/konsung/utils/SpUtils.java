package com.konsung.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具类
 * @author ouyangfan
 * @version 0.0.1
 */
public class SpUtils {
    /**
     * 私有构造
     */
    private SpUtils() {

    }

    /**
     * 保存布尔值
     * @param mContext mContext
     * @param name name
     * @param key key
     * @param value value
     */
    public static synchronized void saveToSp(Context mContext, String name, String key
            , boolean value) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context
                .MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 保存数字
     * @param mContext mContext
     * @param name name
     * @param key key
     * @param value value
     */
    public static synchronized void saveToSp(Context mContext, String name,
            String key, int value) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 保存数字
     * @param name name
     * @param key key
     * @param value value
     */
    public static synchronized void saveToSp(String name,
            String key, int value) {
        SharedPreferences sp = UiUtils.getContent().getSharedPreferences(name, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 保存字符串
     * @param mContext mContext
     * @param name name
     * @param key key
     * @param value value
     */
    public static synchronized void saveToSp(Context mContext, String name, String key
            , String value) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 保存字符串
     * @param name name
     * @param key key
     * @param value value
     */
    public static synchronized void saveToSp(String name, String key
            , String value) {
        SharedPreferences sp = UiUtils.getContent().getSharedPreferences(name, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 保存小数
     * @param name name
     * @param key key
     * @param value value
     */
    public static synchronized void saveToSp(String name,
            String key, float value) {
        SharedPreferences sp = UiUtils.getContent().getSharedPreferences(name, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * 获取指定key的int
     * @param mContext mContext
     * @param name name
     * @param key key
     * @param defValue defValue
     * @return 返回值
     */
    public static synchronized int getSpInt(Context mContext, String name,
            String key, int defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context
                .MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    /**
     * 获取指定key的int
     * @param name name
     * @param key key
     * @param defValue defValue
     * @return 返回值
     */
    public static synchronized int getSpInt(String name,
            String key, int defValue) {
        SharedPreferences sp = UiUtils.getContent().getSharedPreferences(name, Context
                .MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    /**
     * 获取指定key的int
     * @param name name
     * @param key key
     * @param defValue defValue
     * @return 返回值
     */
    public static synchronized long getSpLong(String name,
            String key, long defValue) {
        SharedPreferences sp = UiUtils.getContent().getSharedPreferences(name, Context
                .MODE_PRIVATE);
        return sp.getLong(key, defValue);
    }

    /**
     * 获取指定key的float
     * @param name name
     * @param key key
     * @param defValue defValue
     * @return 返回值
     */
    public static synchronized float getSpFloat(String
            name, String key, float defValue) {
        SharedPreferences sp = UiUtils.getContent().getSharedPreferences(name, Context
                .MODE_PRIVATE);
        return sp.getFloat(key, defValue);
    }

    /**
     * 获取指定key的boolean
     * @param name name
     * @param key key
     * @param defValue defValue
     * @return 返回值
     */
    public static synchronized boolean getSp(String name,
            String key, boolean defValue) {
        SharedPreferences sp = UiUtils.getContent().getSharedPreferences(name, Context
                .MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    /**
     * 获取指定key的String
     * @param mContext mContext
     * @param name name
     * @param key key
     * @param defValue defValue
     * @return 返回值
     */
    public static synchronized String getSp(Context mContext, String name,
            String key, String defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context
                .MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    /**
     * 获取指定key的String
     * @param name name
     * @param key key
     * @param defValue defValue
     * @return 返回值
     */
    public static synchronized String getSp(String name,
            String key, String defValue) {
        SharedPreferences sp = UiUtils.getContent().getSharedPreferences(name, Context
                .MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    /**
     * 删除指定的Key
     * @param name name
     * @param key name
     */

    public static synchronized void removeSp(String name, String key) {
        SharedPreferences sp = UiUtils.getContent().getSharedPreferences(name,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
    }
}
