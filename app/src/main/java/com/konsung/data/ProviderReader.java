package com.konsung.data;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.utils.ToastUtils;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 内容提供读取者
 * 主要用于读取健康档案中的数据
 */
public class ProviderReader {

    /**
     * 读取病人信息
     * @param context 上下文
     * @return 病人bean
     */
    public static PatientBean readCurrentPatient(Context context) {
        //获取配置项
        Uri uri = Uri.parse(GlobalConstant.QUICK_CURRENT_PATIENT);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            PatientBean bean;
            if (cursor.moveToNext()) {
                String patientDetail = cursor.getString(cursor.getColumnIndex(GlobalConstant
                        .CONFIG));
                if (patientDetail.equals("")) {
                    bean = new PatientBean();
                } else {
                    bean = new Gson().fromJson(patientDetail, PatientBean.class);
                }
            } else {
                Log.e("JustRush", "111");

                bean = new PatientBean();
            }
            cursor.close();
            return bean;
        } else {
            Log.e("JustRush", "222");

            return new PatientBean();
        }
    }

    /**
     * 读取病人信息
     * @param context 上下文
     * @return 病人bean
     */
    public static PatientBean readCurrentPatient(Activity context) {
        //获取配置项
        Uri uri = Uri.parse(GlobalConstant.QUICK_CURRENT_PATIENT);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            PatientBean bean;
            if (cursor.moveToNext()) {
                String patientDetail = cursor.getString(cursor.getColumnIndex(GlobalConstant
                        .CONFIG));
                if (patientDetail.equals("")) {
                    bean = new PatientBean();
                    ToastUtils.showLongToast(R.string.no_current_citizen);
                    context.finish();
                } else {
                    bean = new Gson().fromJson(patientDetail, PatientBean.class);
                }
            } else {
                bean = new PatientBean();
                ToastUtils.showLongToast(R.string.no_current_citizen);
                context.finish();
            }
            cursor.close();
            return bean;
        } else {
            ToastUtils.showLongToast(R.string.no_current_citizen);
            context.finish();
            return new PatientBean();
        }
    }

    /**
     * 读取最新的测量记录，如果已经上传，返回的测量记录将会时空字符串
     * @param context 上下文
     * @param idcard 身份证号码
     * @return 测量记录bean
     */
    public static MeasureDataBean readLatestMeasureData(Context context, String idcard) {
        Uri uri = Uri.parse(GlobalConstant.URI_QUERY_MEASURE_DATA);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            String measureDataDetail = cursor.getString(
                    cursor.getColumnIndex(GlobalConstant.CONFIG));
            MeasureDataBean bean = new Gson().fromJson(measureDataDetail, MeasureDataBean.class);
            cursor.close();
            return bean;
        } else {
            try {
                throw new Exception(context.getString(R.string.no_current_citizen));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MeasureDataBean bean = new MeasureDataBean();
        bean.setIdcard(idcard);
        return bean;
    }

    /**
     * TODO 需要向健康档案中读取
     * 读取病人最近十次测量数据
     * @param idcard 身份证
     * @return 测量数据
     * @throws SQLException SQL异常
     */
    public static List<MeasureDataBean> readTenLatestMeasureData(String idcard) throws
            SQLException {
        Uri uri = Uri.parse(GlobalConstant.URI_QUERY_LATEST_TEN_MEASURE_DATA);
        Cursor cursor = UiUtils.getContent().getContentResolver()
                .query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            String measureDataDetail = cursor.getString(
                    cursor.getColumnIndex(GlobalConstant.CONFIG));
            List<MeasureDataBean> been = new Gson().fromJson(measureDataDetail, new
                    TypeToken<List<MeasureDataBean>>() {
                    }.getType());
            cursor.close();
            return been;
        } else {
            try {
                throw new Exception(UiUtils.getContent().getString(R.string.no_measure_history));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    /**
     * 获取数据配置项-从厂家维护中获取
     * @param context 上下文
     * @return 配置项 32位
     */
    public static int getDeviceConfig(Context context) {
        //获取配置项
        Uri uri = Uri.parse(GlobalConstant.AUTHORITY_DEVICE_CONFIG);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null,
                null);
        if (cursor != null && cursor.moveToNext()) {
            int config = cursor.getInt(cursor.getColumnIndex(GlobalConstant.CONFIG));
            cursor.close();
            return config;
        }
        return GlobalConstant.DEVICE_CONFIG;
    }

    /**
     * 获取数据配置项(身高体重)-从厂家维护中获取
     * @param context 上下文
     * @return 配置项 32位
     */
    public static int getHeightWeightConfig(Context context) {
        //获取配置项
        Uri uri = Uri.parse(GlobalConstant.QUICK_CHECK_HEIGHT_WIGHT_CONFIG);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null,
                null);
        if (cursor != null && cursor.moveToNext()) {
            int config = cursor.getInt(cursor.getColumnIndex(GlobalConstant.CONFIG));
            cursor.close();
            return config;
        }
        return GlobalConstant.DEVICE_CONFIG;
    }

    /**
     * 获取界面配置项-从厂家维护中获取
     * @param context 上下文
     * @return 配置项 4位
     */
    public static int getFragmentDisplayConfig(Context context) {
        //获取配置项
        Uri uri = Uri.parse(GlobalConstant.AUTHORITY_MEASURE_CONFIG);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null,
                null);
        if (cursor != null && cursor.moveToNext()) {
            int config = cursor.getInt(cursor.getColumnIndex(GlobalConstant.CONFIG));
            cursor.close();
            return config;
        }
        return GlobalConstant.DEVICE_CONFIG;
    }

    /**
     * 获取尿常规测量项配置参数-从厂家维护中获取
     * @param context 上下文
     * @return 配置项 4位
     */
    public static int getUrtConfig(Context context) {
        //获取配置项
        //获取配置项
        Uri uri = Uri.parse(GlobalConstant.AUTHORITY_URT_CONFIG);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null,
                null);
        if (cursor != null && cursor.moveToNext()) {
            int config = cursor.getInt(cursor.getColumnIndex(GlobalConstant.CONFIG));
            cursor.close();
            return config;
        }
        return GlobalConstant.DEVICE_CONFIG;
    }

    /**
     * 获取快检页面配置-从厂家维护中获取
     * @param context 上下文
     * @return 配置项 4位
     */
    public static int getQuickCheckConfig(Activity context) {
        //获取配置项
        //获取配置项
        Uri uri = Uri.parse(GlobalConstant.QUCIK_CHECK_PAGE_URI_CONFIG);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null,
                null);
        if (cursor != null) {
            int config = GlobalConstant.QUICK_CONFIG;
            if (cursor.moveToNext()) {
                config = cursor.getInt(cursor.getColumnIndex(GlobalConstant.CONFIG));
            }
            cursor.close();
            return config;
        }
        return GlobalConstant.QUICK_CONFIG;
    }

    /**
     * 获取快检页面11-14项配置-从厂家维护中获取
     * @param context 上下文
     * @return 配置项 4位
     */
    public static int getQuickCheckUrineConfig(Activity context) {
        //获取配置项
        //获取配置项
        Uri uri = Uri.parse(GlobalConstant.QUICK_CHECK_URINE_CONFIG);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null,
                null);
        if (cursor != null && cursor.moveToNext()) {
            int config = cursor.getInt(cursor.getColumnIndex(GlobalConstant.CONFIG));
            cursor.close();
            return config;
        }
        return GlobalConstant.URINE_CONFIG;
    }
}
