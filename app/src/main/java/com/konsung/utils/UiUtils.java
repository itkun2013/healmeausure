package com.konsung.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.StatFs;
import android.support.annotation.ColorRes;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.data.ProviderReader;
import com.konsung.exception.ReferenceValueException;
import com.konsung.network.EchoServerEncoder;
import com.konsung.utils.ParamDefine.EcgDefine;
import com.konsung.utils.constant.GlobalConstant;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 常用工具类
 */
public class UiUtils {

    /**
     * 无参考值
     */
    public static final int REFERENCE_TYPE_NONE = -1;
    /**
     * 数值范围
     */
    public static final int REFERENCE_TYPE_NUM = 0;
    /**
     * 阴阳性
     * P-positive
     * N-negative
     */
    public static final int REFERENCE_TYPE_PN = 1;

    private static Context mContext;
    private static Handler mHandler;

    /**
     * 初始化类变量
     * @param context 上下文
     */
    public static void initData(Context context) {
        mContext = context;
        mHandler = new Handler();
    }

    /**
     * @param byteArray 要转换的字符串
     * @return 返回的字符
     */
    public static String byteArrayToHex(byte[] byteArray) {

        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        // new一个字符数组，这个就是用来组成结果字符串的
        // （解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }

    /**
     * 获取包名
     * @return 包名
     */
    public static String getPackName() {
        return mContext.getPackageName();
    }

    /**
     * 获取本应用应用程序的软件版本号
     * @return 软件版本号
     */
    public static int getAppVersion() {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 安装应用程序的方法
     * @param file File
     */
    public static void install(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android" +
                ".package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                .FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        mContext.startActivity(intent);
    }

    /**
     * 关闭数据流的方法
     * @param is 数据流
     */
    public static void close(Closeable is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            is = null;
        }
    }

    /**
     * 转到主线程执行
     * @param task 线程
     */
    public static void post(Runnable task) {
        mHandler.post(task);
    }

    /**
     * 根据包名获取应用程序的版本号
     * @param packageName 包名
     * @return 版本号
     */
    public static int getAppViersion(String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = mContext.getPackageManager()
                    .getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return 0;
        }
        int code = packageInfo.versionCode;
        return code;
    }

    /**
     * 获取当前应用的版本名字
     * @return 版本名字
     */
    public static String getappVersionName() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = mContext.getPackageManager()
                    .getPackageInfo(getPackName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return "未知版本";
        }
        String name = packageInfo.versionName;
        return name;
    }

    /**
     * 获取上下文
     * @return Context
     */
    public static Context getContent() {
        return mContext;
    }

    /**
     * 获取颜色值
     * @param colorId 颜色id
     * @return 对应的颜色
     */
    public static int getColor(int colorId) {
        return mContext.getResources().getColor(colorId);
    }

    /**
     * 判断是否含特殊字符
     * @param string 字符
     * @return 是否含特殊字符
     */
    public static boolean isConSpeCharacters(String string) {
        if (string.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*[\\u4e00-\\u9fa5]*", "")
                .length() == 0) {

            //不包含特殊字符
            return false;
        }
        return true;
    }

    /**
     * 判断是否连接了wifi
     * @return 是否连接了wifi
     */
    public static boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }

        return false;
    }

    /**
     * 根据id获取字符串
     * @param id id
     * @return 字符串
     */
    public static String getString(int id) {
        return mContext.getString(id);
    }

    /**
     * 根据id获取字符串
     * @param id id
     * @return 字符串
     */
    public static String[] getStringArr(int id) {
        return mContext.getResources().getStringArray(id);
    }

    /**
     * 通过未销毁的上下文获取 Drawable
     * @param id id
     * @return Drawable
     */
    public static Drawable getDrawable(int id) {
        return mContext.getResources().getDrawable(id);
    }

    /**
     * 通过未销毁的上下文获取 Dimens
     * @param id id
     * @return Dimens
     */
    public static float getDimens(int id) {
        return mContext.getResources().getDimension(id);
    }

    /**
     * 根据用户输入的信息，算出年龄
     * @param dateOfBirth 日期
     * @return 年龄
     */
    public static int getAge(Date dateOfBirth) {
        int age = 0;
        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        if (dateOfBirth != null) {
            now.setTime(new Date());
            born.setTime(dateOfBirth);
            if (born.after(now)) {
                throw new IllegalArgumentException(
                        "Can't be born in the future");
            }
            age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
            if (now.get(Calendar.DAY_OF_YEAR) < born.get(Calendar
                    .DAY_OF_YEAR)) {
                age -= 1;
            }
        }
        return age;
    }

    /**
     * 根据传入的枚举数据返回不同的时间值
     * @param state 事件类型枚举
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat getDateFormat(DateState state) {
        SimpleDateFormat dateFormat = null;
        switch (state) {
            case LONG:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                break;
            case SHORT:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case CHINESE:
                dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                break;
            case STATISTICAL:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");
                break;
            default:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                break;
        }
        return dateFormat;
    }

    /**
     * 用于区分时间格式的枚举
     * LONG 表示长时间
     * SHORT 表示短时间
     */
    public enum DateState {
        LONG,
        SHORT,
        CHINESE,
        STATISTICAL
    }

    /**
     * 获得可用的内存
     * @return 可用的内存
     */
    public static long getmemCurrent() {
        long menUnused;
        // 得到ActivityManager
        ActivityManager am = (ActivityManager) mContext.getSystemService(
                Context.ACTIVITY_SERVICE);
        // 创建ActivityManager.MemoryInfo对象
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // 取得剩余的内存空间
        menUnused = mi.availMem / 1024;
        return menUnused;
    }

    /**
     * 获得总内存
     * @return 总内存
     */
    public static long getmemTOTAL() {
        long mTotal;
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // beginIndex
        int begin = content.indexOf(':');
        // endIndex
        int end = content.indexOf('k');
        // 截取字符串信息

        content = content.substring(begin + 1, end).trim();
        mTotal = Integer.parseInt(content);
        return mTotal;
    }

    /**
     * 获取CPU占用率
     * @return CPU占用率
     */
    public static float getProcessCpuRate() {

        float totalCpuTime1 = getTotalCpuTime();
        float processCpuTime1 = getAppCpuTime();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }

        float totalCpuTime2 = getTotalCpuTime();
        float processCpuTime2 = getAppCpuTime();

        float cpuRate = 100 * (processCpuTime2 - processCpuTime1)
                / (totalCpuTime2 - totalCpuTime1);

        return cpuRate;
    }

    /**
     * 获取系统总CPU使用时间
     * @return CPU使用时间
     */
    public static long getTotalCpuTime() {
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long totalCpu = Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        return totalCpu;
    }

    /**
     * 获取应用占用的CPU时间
     * @return 占用的CPU时间
     */
    public static long getAppCpuTime() {
        String[] cpuInfos = null;
        try {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long appCpuTime = Long.parseLong(cpuInfos[13])
                + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                + Long.parseLong(cpuInfos[16]);
        return appCpuTime;
    }

    /**
     * 根据路径获取内存状态
     * @param path 路径
     * @return 内存状态
     */
    public static String getSizeInfo(File path) {
        // 获得一个磁盘状态对象
        StatFs stat = new StatFs(path.getPath());

        long blockSize = stat.getBlockSize();   // 获得一个扇区的大小

        long totalBlocks = stat.getBlockCount();    // 获得扇区的总数

        long availableBlocks = stat.getAvailableBlocks();   // 获得可用的扇区数量

        // 总空间
        String totalMemory = Formatter.formatFileSize(mContext, totalBlocks *
                blockSize);
        // 可用空间
        String availableMemory = Formatter.formatFileSize(mContext,
                availableBlocks * blockSize);
        return "可用空间: " + availableMemory + "  总空间: " + totalMemory;
    }

    /**
     * dp转换成像素
     * @param dp dp
     * @param resources Resources
     * @return 转换成px
     */
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                resources.getDisplayMetrics());
        return (int) px;
    }

    /**
     * px转换成dp
     * @param context 上下文
     * @param px px
     * @return 转换成dp
     */
    public static int convertPxToDip(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f * (px >= 0 ? 1 : -1));
    }

    /**
     * 压缩文件-由于out要在递归调用外,所以封装一个方法用来
     * 调用ZipFiles(ZipOutputStream out,String path,File... srcFiles)
     * @param zip 要输出的zip文件
     * @param path 源文件目录
     * @param srcFiles 文件集合
     * @throws IOException 异常
     * @author isea533
     */
    public static void zipFiles(File zip, String path, List<File> srcFiles) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));
        zipFiles(out, path, srcFiles);
        out.close();
    }

    /**
     * 压缩文件-File
     * @param out 压缩流
     * @param path 输出目录
     * @param srcFiles 被压缩源文件
     */
    public static void zipFiles(ZipOutputStream out, String path, List<File> srcFiles) {
        path = path.replaceAll("\\*", "/");
        if (!path.endsWith("/")) {
            path += "/";
        }
        byte[] buf = new byte[1024];
        try {
            for (int i = 0; i < srcFiles.size(); i++) {
                if (!srcFiles.get(i).isDirectory()) {
                    FileInputStream in = new FileInputStream(srcFiles.get(i));
                    out.putNextEntry(new ZipEntry(path + srcFiles.get(i).
                            getName()));
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.closeEntry();
                    in.close();
                } else {
                    File[] files = srcFiles.get(i).listFiles();
                    ArrayList<File> fileList = new ArrayList<File>();
                    for (File file : files) {
                        fileList.add(file);
                    }
                    String srcPath = srcFiles.get(i).getName();
                    srcPath = srcPath.replaceAll("\\*", "/");
                    if (!srcPath.endsWith("/")) {
                        srcPath += "/";
                    }
                    out.putNextEntry(new ZipEntry(path + srcPath));
                    zipFiles(out, path + srcPath, fileList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据sex的int类型返回它的string类型
     * @param sex int类型性别
     * @return string类型性别
     */
    public static String getSexString(int sex) {
        switch (sex) {
            case 0:
                return getString(R.string.sex_unknown);
            case 1:
                return getString(R.string.sex_man);
            case 2:
                return getString(R.string.sex_woman);
            case 3:
                return getString(R.string.sex_unsay);
            default:
                return getString(R.string.sex_unknown);
        }
    }

    /**
     * 获取软件名称的方法
     * @return 软件名称
     */
    public static String getAreaName() {
        String s = UiUtils.getappVersionName();
        return s.substring(s.indexOf("-") + 1).replace("-dev", "");
    }

    /**
     * 删除整个目录下的所有文件
     * @param root 目标文件夹/文件
     */
    public static void deleteAllFiles(File root) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化设备配置
     */
    public static void initDeviceConfig() {
        int value;

        //注意：需要优先进行设备配置初始化
        value = SpUtils.getSpInt(mContext, GlobalConstant.SYS_CONFIG,
                GlobalConstant.DEVICE_CONFIG_TAG, GlobalConstant.DEVICE_CONFIG);
        //当收到AppDeivce发来的数据后，才发生设备配置命令。
        EchoServerEncoder.setDeviceConfig(ProtocolDefine.NET_DEVICE_CONFIG, value);
        EchoServerEncoder.setDeviceConfig(ProtocolDefine.NET_DEVICE_CONFIG, value);
        EchoServerEncoder.setPatientConfig((short) 0, (short) 1, (short) 0, 65f, 170f, (short) 0);
        value = SpUtils.getSpInt(UiUtils.getContent(), GlobalConstant.SYS_CONFIG,
                GlobalConstant.SP_DEVICE_CONFIG,
                GlobalConstant.DEVICE_CONFIG);
        EchoServerEncoder.setDeviceConfig(ProtocolDefine.NET_DEVICE_CONFIG, value);
//        TODO 当实现3-5导联时在获取。
//        value = SpUtils.getSpInt(UiUtils.getContent(), GlobalConstant.SYS_CONFIG
//                , EcgDefine.ECG_LEAD_SYSTEM, EcgDefine.ECG_12_LEAD);
        EchoServerEncoder.setEcgConfig(ProtocolDefine.NET_ECG_LEAD_SYSTEM, EcgDefine.ECG_12_LEAD);
    }

    /**
     * 设置测量结果,尿常规不能用，该方法不支持没有小数点的显示
     * 如果需要兼容血压，脉率，心率 {@link #setMeasureResult(int, int, TextView, TextView, boolean)}
     * @param param 参数
     * @param bean 结果集
     * @param tvName 参数名TextView
     * @param tvValue 结果TextView
     */
    public static void setMeasureResult(int param, MeasureDataBean bean, TextView tvName,
            TextView tvValue) {
        float value = bean.getTrendValue(param);
        if (value == GlobalConstant.INVALID_DATA) {
            tvValue.setText(R.string.invalid_data);
            return;
        }
        if (value == OverCheckUtil.FLAG_OVER_MAX) {
            tvValue.setText(OverCheckUtil.getOverMaxString(param, value));
        } else if (value == OverCheckUtil.FLAG_BELOW_MIN) {
            tvValue.setText(OverCheckUtil.getOverMinString(param, value));
        } else {
            tvValue.setText(getValueAfterFactor(param, (int) value));
        }
        UiUtils.setMeasureValueColor(tvValue, tvName, ReferenceUtils.getMinReference(param),
                ReferenceUtils.getMaxReference(param));
    }

    /**
     * 设置测量结果,尿常规不能用，该方法不支持没有小数点的显示
     * 如果需要兼容血压，脉率，心率 {@link #setMeasureResult(int, int, TextView, TextView, boolean)}
     * @param param 参数
     * @param value 结果
     * @param tvName 参数名TextView
     * @param tvValue 结果TextView
     */
    public static void setMeasureResult(int param, float value, TextView tvName, TextView tvValue) {
        if (value == GlobalConstant.INVALID_DATA) {
            tvValue.setText(R.string.invalid_data);
            return;
        }
        if (value == OverCheckUtil.FLAG_OVER_MAX) {
            tvValue.setText(OverCheckUtil.getOverMaxString(param, value));
        } else if (value == OverCheckUtil.FLAG_BELOW_MIN) {
            tvValue.setText(OverCheckUtil.getOverMinString(param, value));
        } else {
            tvValue.setText(getValueAfterFactor(param, (int) value));
        }
        UiUtils.setMeasureValueColor(tvValue, tvName, ReferenceUtils.getMinReference(param),
                ReferenceUtils.getMaxReference(param));
    }

    /**
     * 设置测量结果,尿常规不能用
     * @param param 参数
     * @param value 结果
     * @param tvName 参数名TextView
     * @param tvValue 结果TextView
     * @param hasPoint 值是否有小数点
     */
    public static void setMeasureResult(int param, int value, TextView tvName, TextView tvValue,
            boolean hasPoint) {
        if (value == GlobalConstant.INVALID_DATA) {
            tvValue.setText(R.string.invalid_data);
            return;
        }
        if (value == OverCheckUtil.FLAG_OVER_MAX) {
            tvValue.setText(OverCheckUtil.getOverMaxString(param, value));
        } else if (value == OverCheckUtil.FLAG_BELOW_MIN) {
            tvValue.setText(OverCheckUtil.getOverMinString(param, value));
        } else {
            if (hasPoint) {
                tvValue.setText(String.valueOf(value / getFactor(param)));
            } else {
                tvValue.setText(String.valueOf((int) (value / getFactor(param))));
            }
        }
        UiUtils.setMeasureValueColor(tvValue, tvName, ReferenceUtils.getMinReference(
                param), ReferenceUtils.getMaxReference(param));
    }

    /**
     * 获取参数比例
     * @param param 参数
     * @return 比例
     */
    public static float getFactor(int param) {
        switch (param) {
            case KParamType.URINERT_SG:
                return GlobalConstant.SG_FACTOR;
            case KParamType.BLOOD_WBC:
                return GlobalConstant.WBC_FACTOR;
            default:
                return GlobalConstant.FACTOR;
        }
    }

    /**
     * 返回参数显示的值的字符串
     * @param param 参数
     * @param value 原始值
     * @return 比例
     */
    public static String getValueAfterFactor(int param, int value) {
        if (value == GlobalConstant.INVALID_DATA) {
            return UiUtils.getString(R.string.invalid_data);
        }
        switch (param) {
            case KParamType.GHB_EAG:
            case KParamType.GHB_HBA1C_NGSP:
            case KParamType.GHB_HBA1C_IFCC:
                if (value == OverCheckUtil.FLAG_OVER_MAX) {
                    return OverCheckUtil.getOverMaxString(param, value);
                } else if (value == OverCheckUtil.FLAG_BELOW_MIN) {
                    return OverCheckUtil.getOverMinString(param, value);
                } else {
                    return String.valueOf(value / GlobalConstant.FACTOR);
                }
            case KParamType.BLOOD_FAT_TRIG:
            case KParamType.BLOOD_FAT_CHO:
            case KParamType.BLOOD_FAT_HDL:
            case KParamType.BLOOD_FAT_LDL:
                if (value == OverCheckUtil.FLAG_OVER_MAX) {
                    return OverCheckUtil.getOverMaxString(param, value);
                } else if (value == OverCheckUtil.FLAG_BELOW_MIN) {
                    return OverCheckUtil.getOverMinString(param, value);
                } else {
                    return String.format(getString(R.string.rule_limit_2_after_point),
                            value / GlobalConstant.FACTOR);
                }
                //下面这几项，都是整数
            case KParamType.ECG_HR:
            case KParamType.SPO2_PR:
            case KParamType.SPO2_TREND:
            case KParamType.NIBP_DIA:
            case KParamType.NIBP_SYS:
            case KParamType.BLOOD_HCT:
            case KParamType.BLOOD_HGB:
                return String.valueOf(value / GlobalConstant.TREND_FACTOR);
            default:
                return String.valueOf(value / getFactor(param));
        }
    }

    /**
     * 对应参数结果是否是整型
     * @param param 参数
     * @return 是否是整型
     */
    public static boolean whetherResultIsInt(int param) {
        if (!getValueAfterFactor(param, 10000).contains(".")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 测量值根据参考值范围设置颜色
     * @param tvValue 测量值控件
     * @param tvName 测量值名字的控件
     * @param max 最大值
     * @param min 最小值
     */
    public static void setMeasureValueColor(TextView tvValue, TextView tvName, float min,
            float max) {
        String valueStr = tvValue.getText().toString();
        if (!valueStr.equals(getString(R.string.invalid_data))) {
            try {
                if (valueStr.contains(">") || valueStr.contains("<")) {
                    tvValue.setTextColor(getColor(R.color.error_value_color));
                    tvName.setTextColor(getColor(R.color.error_value_color));
                    return;
                }
                Float value = Float.valueOf(valueStr);
                if (value > max || value < min) {
                    tvValue.setTextColor(getColor(R.color.error_value_color));
                    tvName.setTextColor(getColor(R.color.error_value_color));
                    return;
                }
            } catch (Exception e) {
                tvValue.setTextColor(getColor(R.color.measure_value_text_color));
                tvName.setTextColor(getColor(R.color.measure_value_text_color));
            }
        }
        tvValue.setTextColor(getColor(R.color.measure_value_text_color));
        tvName.setTextColor(getColor(R.color.measure_value_text_color));
    }

    /**
     * 根据参数值获取单位字符串
     * @param param 参数
     * @return 单位
     */
    public static String getValueUnit(int param) {
        switch (param) {
            case KParamType.ECG_HR:
            case KParamType.SPO2_PR:
                return UiUtils.getString(R.string.health_unit_bpm);

            case KParamType.NIBP_SYS:
            case KParamType.NIBP_DIA:
                return UiUtils.getString(R.string.unit_mmhg);

            case KParamType.TEMP_T1:
            case KParamType.IRTEMP_TREND:
                return UiUtils.getString(R.string.unit_temp);

            case KParamType.BLOOD_HCT:
            case KParamType.SPO2_TREND:
            case KParamType.GHB_HBA1C_NGSP:
                return UiUtils.getString(R.string.unit_percent);

            case KParamType.BLOODGLU_AFTER_MEAL:
            case KParamType.BLOODGLU_BEFORE_MEAL:
            case KParamType.URICACID_TREND:
            case KParamType.CHOLESTEROL_TREND:
            case KParamType.BLOOD_FAT_CHO:
            case KParamType.BLOOD_FAT_HDL:
            case KParamType.BLOOD_FAT_LDL:
            case KParamType.BLOOD_FAT_TRIG:
            case KParamType.URINERT_KET:
            case KParamType.URINERT_GLU:
            case KParamType.URINERT_VC:
                return UiUtils.getString(R.string.unit_mmol_l);

            case KParamType.GHB_HBA1C_IFCC:
                return UiUtils.getString(R.string.unit_mmol_mol);
            case KParamType.URINERT_LEU:
            case KParamType.URINERT_BLD:
                return UiUtils.getString(R.string.unit_cells);

            case KParamType.URINERT_UBG:
            case KParamType.URINERT_BIL:
                return UiUtils.getString(R.string.unit_umol);
            case KParamType.URINERT_PRO:
            case KParamType.BLOOD_HGB:
                return UiUtils.getString(R.string.unit_gl);
            case KParamType.BLOOD_WBC:
                return UiUtils.getString(R.string.unit_index);
            case KParamType.GHB_EAG:
                return UiUtils.getString(R.string.unit_mg_dl);
            case KParamType.WEIGHT:
                return getString(R.string.unit_kg);
            case KParamType.HEIGHT:
                return getString(R.string.unit_cm);
            case KParamType.URINE_AC:
                return getString(R.string.unit_mg_l);
            default:
                return "";
        }
    }

    /**
     * 根据参数值获取测量项名称
     * @param param 参数
     * @return 名称
     */
    public static String getParamString(int param) {
        switch (param) {
            case KParamType.ECG_HR:
                return UiUtils.getString(R.string.ecg_hr);
            case KParamType.SPO2_TREND:
                return UiUtils.getString(R.string.spo2);
            case KParamType.SPO2_PR:
                return UiUtils.getString(R.string.pr_cn);
            case KParamType.NIBP_SYS:
                return UiUtils.getString(R.string.nibp_sys);
            case KParamType.NIBP_DIA:
                return UiUtils.getString(R.string.nibp_dia);
            case KParamType.IRTEMP_TREND:
                return UiUtils.getString(R.string.temp);
            case KParamType.BLOOD_HCT:
                return UiUtils.getString(R.string.red_blood_cells_backlog_value);
            case KParamType.BLOOD_HGB:
                return UiUtils.getString(R.string.hb);
            case KParamType.BLOODGLU_AFTER_MEAL:
                return UiUtils.getString(R.string.after_glu);
            case KParamType.BLOODGLU_BEFORE_MEAL:
                return UiUtils.getString(R.string.before_glu);
            case KParamType.URICACID_TREND:
                return UiUtils.getString(R.string.ua);

            case KParamType.CHOLESTEROL_TREND:
                return UiUtils.getString(R.string.total_cho);

            case KParamType.BLOOD_FAT_CHO:
                return UiUtils.getString(R.string.total_cho);

            case KParamType.BLOOD_FAT_HDL:
                return UiUtils.getString(R.string.hdl);

            case KParamType.BLOOD_FAT_LDL:
                return UiUtils.getString(R.string.ldl);

            case KParamType.BLOOD_FAT_TRIG:
                return UiUtils.getString(R.string.trig);
            case KParamType.GHB_HBA1C_NGSP:
                return UiUtils.getString(R.string.ghb_ngsp);
            case KParamType.GHB_HBA1C_IFCC:
                return UiUtils.getString(R.string.ghb_ifcc);
            case KParamType.GHB_EAG:
                return UiUtils.getString(R.string.ghb_eag);
            case KParamType.BLOOD_WBC:
                return UiUtils.getString(R.string.wbc);
            case KParamType.HEIGHT:
                return UiUtils.getString(R.string.height);
            case KParamType.WEIGHT:
                return UiUtils.getString(R.string.weight);
            default:
                return "";
        }
    }

    /**
     * 尿常规值转换
     * @param value 模块传递过来的值
     * @return 显示测量值
     */
    public static String urtValueToString(int value) {
        if (value == GlobalConstant.INVALID_DATA) {
            return getString(R.string.invalid_data);
        }
        String[] cas;
        if (getU120UseStatus()) {
            //U120
            cas = getStringArr(R.array.uri_value_u120);
        } else {
            cas = getStringArr(R.array.uri_value);
        }
        value = value / GlobalConstant.TREND_FACTOR;
        switch (value) {
            case -1:
                return cas[0];
            case 0:
                return cas[1];
            case 1:
                return cas[2];
            case 2:
                return cas[3];
            case 3:
                return cas[4];
            case 4:
                return cas[5];
            case 5:
                return cas[6];
            case 6:
                return cas[7];
            case 7:
                return cas[8];
            case 8:
                return cas[9];
            case 9:
                return cas[10];
            case 10:
                return cas[11];
            case 11:
                return cas[12];
            case 12:
                return cas[13];
            default:
                return String.valueOf(value);
        }
    }

    /**
     * 获取艾康U120使用情况
     * @return 是否使用U120
     */
    public static boolean getU120UseStatus() {
        int config = ProviderReader.getDeviceConfig(getContent());
        if ((config & (0x01 << 10)) != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 艾康U120尿常规肌酐，尿钙，微量白蛋白 三项显示值
     * @param param 参数
     * @param value 值
     * @return 需要显示的尿常规数值
     */
    private static String getUrtSpecialValue(int param, int value) {
        String[] valueString;
        //艾康U120
        if (getU120UseStatus()) {
            switch (param) {
                case KParamType.URINERT_ALB:
                    valueString = getStringArr(R.array.alb_value_u120);
                    break;
                case KParamType.URINERT_CRE:
                    valueString = getStringArr(R.array.cre_value_u120);
                    break;
                case KParamType.URINERT_CA:
                    valueString = getStringArr(R.array.ca_value_u120);
                    break;
                default:
                    return String.valueOf(value / getFactor(param));
            }
            value = value / GlobalConstant.TREND_FACTOR;
            switch (value) {
                case -1:
                    return valueString[0];
                case 1:
                    return valueString[1];
                case 2:
                    return valueString[2];
                case 3:
                    return valueString[3];
                case 4:
                    //微量白蛋白没有+4
                    if (param == KParamType.URINERT_ALB) {
                        return String.valueOf(value);
                    } else {
                        return valueString[4];
                    }
                default:
                    return String.valueOf(value);
            }
        } else {
            return urtValueToString(value);
        }
    }

    /**
     * 获取参考值类型 ，除了酸碱度，比重，都用半定量显示了
     * @param param 测量类型参数
     * @return 参考值类型，默认无参考值
     */
    public static int getReferenceType(int param) {
        switch (param) {
            case KParamType.URINERT_LEU:
            case KParamType.URINERT_NIT:
            case KParamType.URINERT_UBG:
            case KParamType.URINERT_PRO:
            case KParamType.URINERT_BLD:
            case KParamType.URINERT_KET:
            case KParamType.URINERT_BIL:
            case KParamType.URINERT_GLU:
            case KParamType.URINERT_VC:
            case KParamType.URINERT_ASC:
            case KParamType.URINERT_ALB:
            case KParamType.URINERT_CRE:
            case KParamType.URINERT_CA:
                return REFERENCE_TYPE_PN;

            case KParamType.URINERT_PH:
            case KParamType.URINERT_SG:
                return REFERENCE_TYPE_NUM;

            default:
                return REFERENCE_TYPE_NONE;
        }
    }

    /**
     * 获取转换后的测量值用于界面显示(尿常规需要显示+-，尿酸总胆固醇需要保留小数，血压心率需要整数)
     * @param bean 测量数据
     * @param param 参数
     * @return 转换后的测量值
     */
    public static String measureValueShowUi(MeasureDataBean bean, int param) {
        int value = bean.getTrendValue(param);
        if (value != GlobalConstant.INVALID_DATA) {
            switch (param) {
                case KParamType.ECG_HR:
                case KParamType.SPO2_PR:
                case KParamType.SPO2_TREND:
                case KParamType.NIBP_SYS:
                case KParamType.NIBP_DIA:
                case KParamType.NIBP_PR:
                case KParamType.NIBP_MAP:
                case KParamType.RESP_RR:
                case KParamType.BLOOD_HCT:
                case KParamType.BLOOD_HGB:
                    return value / GlobalConstant.TREND_FACTOR + "";
                case KParamType.URINERT_BIL:
                case KParamType.URINERT_BLD:
                case KParamType.URINERT_GLU:
                case KParamType.URINERT_KET:
                case KParamType.URINERT_LEU:
                case KParamType.URINERT_NIT:
                case KParamType.URINERT_PRO:
                case KParamType.URINERT_UBG:
                case KParamType.URINERT_ASC:
                    return urtValueToString(value);
                case KParamType.URINERT_ALB:
                case KParamType.URINERT_CRE:
                case KParamType.URINERT_CA:
                    return getUrtSpecialValue(param, value);
                case KParamType.BLOOD_WBC:
                case KParamType.BLOODGLU_AFTER_MEAL:
                case KParamType.BLOODGLU_BEFORE_MEAL:
                case KParamType.URICACID_TREND:
                case KParamType.URINERT_PH:
                case KParamType.CHOLESTEROL_TREND:
                case KParamType.WEIGHT:
                case KParamType.HEIGHT:
                case KParamType.TEMP_T1:
                case KParamType.IRTEMP_TREND:
                    return value / GlobalConstant.FACTOR + "";
                case KParamType.URINERT_SG:
                    return String.format(getString(R.string.rule_limit_3_after_point),
                            value / GlobalConstant.SG_FACTOR);
                case KParamType.GHB_EAG:
                case KParamType.GHB_HBA1C_NGSP:
                case KParamType.GHB_HBA1C_IFCC:
                case KParamType.BLOOD_FAT_CHO:
                case KParamType.BLOOD_FAT_HDL:
                case KParamType.BLOOD_FAT_LDL:
                case KParamType.BLOOD_FAT_TRIG:
                    if (value == OverCheckUtil.FLAG_OVER_MAX) {
                        return OverCheckUtil.getOverMaxString(param, value);
                    } else if (value == OverCheckUtil.FLAG_BELOW_MIN) {
                        return OverCheckUtil.getOverMinString(param, value);
                    } else {
                        return String.valueOf(value / GlobalConstant.FACTOR);
                    }
                default:
                    return getString(R.string.invalid_data);
            }
        }

        return getString(R.string.invalid_data);
    }

    /**
     * 获取转换后的测量值用于界面显示(尿常规需要显示+-，尿酸总胆固醇需要保留小数，血压心率需要整数)
     * @param param 参数
     * @param value 结果
     * @return 转换后的测量值
     */
    public static String measureValueShowUi(int param, int value) {
        if (value != GlobalConstant.INVALID_DATA) {
            switch (param) {
                case KParamType.ECG_HR:
                case KParamType.SPO2_PR:
                case KParamType.SPO2_TREND:
                case KParamType.NIBP_SYS:
                case KParamType.NIBP_DIA:
                case KParamType.NIBP_PR:
                case KParamType.NIBP_MAP:
                case KParamType.RESP_RR:
                case KParamType.BLOOD_HGB:
                case KParamType.BLOOD_HCT:
                    return value / GlobalConstant.TREND_FACTOR + "";
                case KParamType.URINERT_BIL:
                case KParamType.URINERT_BLD:
                case KParamType.URINERT_GLU:
                case KParamType.URINERT_KET:
                case KParamType.URINERT_LEU:
                case KParamType.URINERT_NIT:
                case KParamType.URINERT_PRO:
                case KParamType.URINERT_UBG:
                case KParamType.URINERT_ASC:
                    return urtValueToString(value);
                case KParamType.URINERT_ALB:
                case KParamType.URINERT_CRE:
                case KParamType.URINERT_CA:
                    return getUrtSpecialValue(param, value);
                case KParamType.BLOOD_WBC:
                case KParamType.BLOODGLU_AFTER_MEAL:
                case KParamType.BLOODGLU_BEFORE_MEAL:
                case KParamType.URICACID_TREND:
                case KParamType.URINERT_PH:
                case KParamType.CHOLESTEROL_TREND:

                case KParamType.WEIGHT:
                case KParamType.HEIGHT:
                case KParamType.TEMP_T1:
                case KParamType.IRTEMP_TREND:
                    return value / GlobalConstant.FACTOR + "";
                case KParamType.URINERT_SG:
                    return String.format(getString(R.string.rule_limit_3_after_point),
                            value / GlobalConstant.SG_FACTOR);
                case KParamType.GHB_EAG:
                case KParamType.GHB_HBA1C_NGSP:
                case KParamType.GHB_HBA1C_IFCC:
                case KParamType.BLOOD_FAT_CHO:
                case KParamType.BLOOD_FAT_HDL:
                case KParamType.BLOOD_FAT_LDL:
                case KParamType.BLOOD_FAT_TRIG:
                    if (value == OverCheckUtil.FLAG_OVER_MAX) {
                        return OverCheckUtil.getOverMaxString(param, value);
                    } else if (value == OverCheckUtil.FLAG_BELOW_MIN) {
                        return OverCheckUtil.getOverMinString(param, value);
                    } else {
                        return String.valueOf(value / GlobalConstant.FACTOR);
                    }
                default:
                    return getString(R.string.invalid_data);
            }
        }

        return getString(R.string.invalid_data);
    }

    /**
     * 根据测量值设置控件的颜色
     * @param tvValue 测量值控件
     * @param param 参数
     * @param value 测量数据
     * @param defaultColor 默认颜色
     */
    public static void setTextColor(TextView tvValue, int param, int value,
            @ColorRes int defaultColor) {
        try {
            if (value == GlobalConstant.INVALID_DATA) {
                tvValue.setTextColor(UiUtils.getColor(defaultColor));
                return;
            }
            int compare = ReferenceUtils.compareWithReference(param, value);
            if (compare == ReferenceUtils.FLAG_VALUE_NORMAL) {
                tvValue.setTextColor(UiUtils.getColor(defaultColor));
            } else {
                tvValue.setTextColor(UiUtils.getColor(R.color.error_value_color));
            }
        } catch (ReferenceValueException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据测量值设置控件的颜色
     * @param tvValue 测量值控件
     * @param param 参数
     * @param measureData 测量数据
     */
    public static void setTextColor(TextView tvValue, int param,
            MeasureDataBean measureData) {
        try {
            int value = measureData.getTrendValue(param);
            if (value == GlobalConstant.INVALID_DATA) {
                tvValue.setTextColor(UiUtils.getColor(R.color.measure_name_text_color));
                return;
            }
            int compare = ReferenceUtils.compareWithReference(param, value);
            if (compare == ReferenceUtils.FLAG_VALUE_NORMAL) {
                tvValue.setTextColor(UiUtils.getColor(R.color.measure_name_text_color));
            } else {
                tvValue.setTextColor(UiUtils.getColor(R.color.error_value_color));
            }
        } catch (ReferenceValueException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建文件
     * @param path 文件路径
     */
    public static void createFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 根据测量值设置控件的颜色
     * @param tvValue 测量值控件
     * @param tvName 测量值控件
     * @param param 参数
     * @param measureData 测量数据
     */
    public static void setTextColor(TextView tvValue, TextView tvName, int param,
            MeasureDataBean measureData) {
        try {
            int value = measureData.getTrendValue(param);
            if (value == GlobalConstant.INVALID_DATA) {
                tvValue.setTextColor(UiUtils.getColor(R.color.measure_name_text_color));
                tvName.setTextColor(UiUtils.getColor(R.color.measure_name_text_color));
                return;
            }
            int compare = ReferenceUtils.compareWithReference(param, value);
            if (compare == ReferenceUtils.FLAG_VALUE_NORMAL) {
                tvValue.setTextColor(UiUtils.getColor(R.color.measure_name_text_color));
                tvName.setTextColor(UiUtils.getColor(R.color.measure_name_text_color));
            } else {
                tvValue.setTextColor(UiUtils.getColor(R.color.error_value_color));
                tvName.setTextColor(UiUtils.getColor(R.color.error_value_color));
            }
        } catch (ReferenceValueException e) {
            e.printStackTrace();
        }
    }
}
