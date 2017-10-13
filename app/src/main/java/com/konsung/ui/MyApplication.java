package com.konsung.ui;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.konsung.service.AIDLServer;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;
import com.tencent.bugly.crashreport.CrashReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 进程启动
 */
public class MyApplication extends Application {

    private Context context;
    private ArrayList<String> infos = new ArrayList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
    //内存中日志文件最大值，1M
    private static final int MEMORY_LOG_FILE_MAX_SIZE = 1024 * 1024;
    String versionName;
    String versionCode;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        UiUtils.initData(context);
        // 初始化service
        initServer();
        Context context = getApplicationContext();
        String packageName = getPackageName();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        // 获取当前进程名
        String processName = null;
        List<ActivityManager.RunningAppProcessInfo> appList = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : appList) {
            if (info.pid == android.os.Process.myPid()) {
                processName = info.processName;
            }
        }
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(context, GlobalConstant.APPID, isApkDebugable(), strategy);
    }

    /**
     * 初始化service,在Launcher中直接开启service
     */
    private void initServer() {
        // intent的action为康尚aidl服务器
        Intent mIntent = new Intent(this, AIDLServer.class);
        // 一启动程序就绑定aidl service服务
        // 保证service只运行一次，一直在后台运行
        // 如果去掉startService只是bindService话,当所有的调用者退出时，即可消除service.
        // 但是本程序中,调用者在Lanucher中，为了防止用户不断的点击参数，重复调用service,
        // 就在这也使用上了startService, 保证不会所有参数都会接收到值,并且不会重复调用service。
        startService(mIntent);
    }

    /**
     * 当我们没在AndroidManifest.xml中设置其debug属性时:
     * 使用Eclipse运行这种方式打包时其debug属性为true,使用Eclipse导出这种方式打包时其debug属性为false.
     * 在使用ant打包时，其值就取决于ant的打包参数是release还是debug.
     * 因此在AndroidMainifest.xml中最好不设置android:debuggable属性置，而是由打包方式来决定其值.
     * @return true, 程序为debug版本；false,程序为release版本
     */
    private boolean isApkDebugable() {
        try {
            ApplicationInfo info = getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            Log.e("HealthOne", "Apk debugable check failed!");
        }
        return false;
    }
}
