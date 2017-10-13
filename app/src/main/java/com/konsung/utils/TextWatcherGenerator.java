package com.konsung.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.konsung.R;

/**
 * 文本监控创建者
 */

public class TextWatcherGenerator {

    /**
     * 获取TextWatcher
     * @param param 参数
     * @param max 最大值
     * @return TextWatcher
     * @see KParamType
     */
    public static TextWatcher getTextWatcher(final int param, final int max) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                insertZero(s);
                valueRange(param, s, max);
            }
        };
        return watcher;
    }

    /**
     * 获取TextWatcher
     * @param param 参数
     * @param max 最大值
     * @param widget 控件
     * @return TextWatcher
     */
    public static TextWatcher getTextWatcher(final int param, final int max, final View widget) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                insertZero(s);
                limitValueNoStartWithZero(s);
                valueRange(param, s, max);
                controlWidget(s, widget);
            }
        };
        return watcher;
    }

    /**
     * 当输入‘.’开头时，自动补0
     * @param s 文本
     */
    private static void insertZero(Editable s) {
        if (s.toString().startsWith(".")) {
            s.insert(0, "0");
        }
    }

    /**
     * 不允许非小数，以0开头
     * @param s 文本
     */
    private static void limitValueNoStartWithZero(Editable s) {
        if (s.toString().startsWith("0")) {
            if (s.length() >= 2) {
                if (s.charAt(1) != '.') {
                    s.delete(0, 1);
                }
            }
        }
    }

    /**
     * 超出范围判断方法
     * @param param 参数
     * @param s 文本
     * @param max 最大值
     */
    private static void valueRange(int param, Editable s, int max) {
        if (s.length() > 0) {
            try {
                float value = Float.valueOf(s.toString());
                if (value > max) {
                    s.delete(s.length() - 1, s.length());
                    ToastUtils.showShortToast(UiUtils.getParamString(param) + UiUtils.
                            getString(R.string.can_not_higher) + max);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据文本长度控制控件是否可用
     * @param s 文本
     * @param widget 控件
     */
    private static void controlWidget(Editable s, View widget) {
        if (s.length() > 0) {
            widget.setEnabled(true);
        } else {
            widget.setEnabled(false);
        }
    }
}
