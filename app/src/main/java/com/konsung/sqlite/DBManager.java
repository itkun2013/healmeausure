package com.konsung.sqlite;

import android.app.Activity;
import android.app.Service;
import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;

/**
 * 数据库管理类
 * Activity和Service都要用到数据库
 * 为防止重复创建数据库对象,抽象到此类进行管理
 * 因为Service远程调用是在不同的进程中
 * 所以Activity和Service各持有一个数据库对象
 * @author ouyangfan
 * @version 0.0.1
 */
public class DBManager {
    // 数据库帮助类
    private static DBHelper databaseHelper = null;
    // 使用范围
    private static boolean isUseInActivity = false;
    private static boolean isUseInService = false;

    /**
     * 私有构造器
     */
    private DBManager() {

    }

    /**
     * 在Activity中获取DBHelper对象
     * @param context 上下文对象
     * @param activity activity类
     * @return 数据库帮助类
     */
    public static DBHelper getDBHelper(Context context, Activity activity) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DBHelper
                    .class);
        }
        isUseInActivity = true;
        return databaseHelper;
    }

    /**
     * 在Activity中获取DBHelper对象
     * @param context 上下文对象
     * @return 数据库帮助类
     */
    public static DBHelper getDBHelper(Context context) {
        if (databaseHelper == null) {
            UiUtils.createFile(GlobalConstant.DATABASE_FILE);
            databaseHelper = OpenHelperManager.getHelper(context, DBHelper.class);
        }
        isUseInActivity = true;
        return databaseHelper;
    }

    /**
     * 在Service中获取DBHelper对象
     * @param context 上下文
     * @param service 服务
     * @return DBHelper对象
     */
    public static DBHelper getDBHelper(Context context, Service service) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DBHelper
                    .class);
        }
        isUseInService = true;
        return databaseHelper;
    }

    /**
     * 释放数据库
     * 只有当activity和service中都不使用数据库的时候才释放
     * @param activity 不在activity销毁时请置@null
     * @param service 不在service销毁时请置@null
     */
    public static void releaseHelper(Activity activity, Service service) {
        if (null != activity) {
            isUseInActivity = false;
        }
        if (null != service) {
            isUseInService = false;
        }
        if (service == null && !isUseInActivity) {
            return;
        }
        // 释放DBHelper
        if ((databaseHelper != null) && (!isUseInActivity) &&
                (!isUseInService)) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
