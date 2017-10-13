package com.konsung.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.sqlite.DBManager;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;

import java.sql.SQLException;
import java.util.List;

/**
 * 内容提供写入者
 * 主要用于向健康档案写入数据
 */
public class ProviderWriter {

    /**
     * 写入测量信息,都以insert的方式保存，健康档案会自动区分处理
     * @param measureDataBean 测量数据
     */
    public static void writeMeasureData(Context context, MeasureDataBean measureDataBean) {
        //新增记录
        if (measureDataBean.isHaveData()) {
            ContentResolver contentResolver = context.getContentResolver();
            Uri insertUri = Uri.parse(GlobalConstant.URI_ADD_MEASURE_DATA);
            ContentValues values = new ContentValues();
            values.put(GlobalConstant.KEY_SAVE_MEASURE_DATA, new Gson().toJson(measureDataBean));
            contentResolver.insert(insertUri, values);
        }
    }

    /**
     * 向contentProvider写入数据（JSON）
     * @param context 上下文
     * @param uriStr Uri
     * @param key 键
     * @param measureDataBean 需要写入的测量数据
     */
    private void writeToContent(Context context, String uriStr, String key, MeasureDataBean
            measureDataBean) {

    }
}
