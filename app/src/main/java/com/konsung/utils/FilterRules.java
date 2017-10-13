package com.konsung.utils;

/**
 * 一些界面操作的规则，发送了广播对应的变化
 * 源码来自StartManager,本人水平有限，未做太多兼容，切勿手动修改，请直接copy。
 * 如在其它地方使用，请注明出处，避免误会
 */
public class FilterRules {
    public static final String WINDOW_SHOW = "com.konsung.startmanager.window.show";
    public static final String WINDOW_DISMISS = "com.konsung.startmanager.window.dismiss";

    /**
     * 健康测量界面正在显示
     */
    public static final String ON_MEASURE_DATA_SHOW = "com.konsung.startmanager.window.measure";

}
