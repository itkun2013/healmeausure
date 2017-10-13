package com.konsung.utils;

import android.content.Context;
import android.content.Intent;

/**
 * 广播管理类，让广播受控，所有广播都在这里实现
 */

public class BroadcastUtils {

    private static final String IDCARD = "idcard";
    private static final String NAME = "name";
    private static final String SEX = "sex";
    private static final String TYPE = "type";

    private static final String ACTION_HEALTH_FILE_CARDREADER = "com.konsung.healthmeasure" +
            ".cardreader"; //发送给健康档案的居民信息-内置读卡器

    /**
     * 应用启动管理浮窗显示
     */
    private static final String ACTION_WINDOW_SHOW = "com.konsung.startmanager.window.show";
    /**
     * 应用启动管理浮窗隐藏
     */
    private static final String ACTION_WINDOW_DISMISS = "com.konsung.startmanager.window.dismiss";

    /**
     * 健康测量界面正在显示
     */
    private static final String ACTION_ON_MEASURE_DATA_SHOW = "com.konsung.startmanager.window" +
            ".measure";

    /**
     * 健康档案界面显示
     */
    public static final String ACTION_ON_HEALTH_FILE_SHOW = "com.konsung.healthfile.onstart";

    /**
     * 健康档案界面停止
     */
    public static final String ACTION_ON_HEALTH_FILE_STOP = "com.konsung.healthfile.onstop";

    //触发健康档案上传机制的广播Action
    public static final String ACTION_UPLOAD_DATA = "com.konsung.healthfile.autouploadreceiver";

    /**
     * 发送居民信息到健康档案界面
     * @param context 上下文
     * @param name 姓名
     * @param idcard 身份证号码
     * @param sex 性别
     * @param type 类型
     */
    public static void sentCitizenDetailToHealthFile(Context context, String name, String idcard,
            int sex, int type) {
        Intent it = new Intent(ACTION_HEALTH_FILE_CARDREADER);
        it.putExtra(NAME, name);
        it.putExtra(IDCARD, idcard);
        it.putExtra(TYPE, type);
        it.putExtra(SEX, sex);
        context.sendBroadcast(it);
    }

    /**
     * 发送健康测量界面已经显示的状态到启动管理
     * @param context 上下文
     */
    public static void sentHealthMeasureStatusToStartManager(Context context) {
        Intent it = new Intent();
        it.setAction(ACTION_ON_MEASURE_DATA_SHOW);
        context.sendBroadcast(it);
    }

    /**
     * 通知健康档案上传记录
     * @param context 上下文
     */
    public static void sentUploadFlagToHealthFile(Context context) {
        Intent it = new Intent();
        it.setAction(ACTION_UPLOAD_DATA);
        context.sendBroadcast(it);
    }
}
