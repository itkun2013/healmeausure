package com.konsung.utils;

import android.content.res.Resources;
import android.text.TextUtils;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.sqlite.DBManager;
import com.konsung.utils.constant.GlobalConstant;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YYX on 2016/6/2 0002.
 * 数据处理的类
 */
public class DBDataUtil {
    /**
     * 根据输入的身份证号查询测量数据
     *
     * @param idCard 身份证id
     * @return  List<MeasureDataBean>
     */
    public static List<MeasureDataBean> getMeasures(String idCard) {
        return DBManager.getDBHelper(UiUtils.getContent())
                .getMeasureDataDao().queryForEq("idcard", idCard);
    }

    /**
     * 尿常规值转换
     *
     * @param value 模块传递过来的值
     * @return 显示测量值
     */
    public static String valueToString(int value) {

        switch (value) {
            case -1:
                return "-";
            case 0:
                return "+-";
            case 1:
                return "+1";
            case 2:
                return "+2";
            case 3:
                return "+3";
            case 4:
                return "+4";
            case 5:
                return "+";
            case 6:
                return "Normal";
            default:
                return String.valueOf(value);
        }
    }

    /**
     * 类型转化
     *
     * @param type 类型
     * @return 病人类型
     */
    public static String getPatientType(int type) {
        Resources res = UiUtils.getContent().getResources();
        String[] patientType = res.getStringArray(R.array.patient_type_array);
        switch (type) {
            case 0:
                return patientType[0];
            case 1:
                return patientType[1];
            case 2:
                return patientType[2];
            default:
                return patientType[0];
        }
    }

    /**
     * 删除病人记录
     *
     * @param bean 测量记录
     */
    public static void deleteMeasure(MeasureDataBean bean) {
        getMeasureDao().delete(bean);
    }

    /**
     * 转换血糖类型，与服务器对应
     * 0 - 0 餐前血糖
     * 1 - 2 餐后血糖
     *
     * @param gluType 血糖类型
     * @return 血糖类型
     */
    public static String parseGluType(String gluType) {
        if (!TextUtils.isEmpty(gluType) && gluType.equals("1")) {
            return "2";
        }
        return "0";
    }

    /**
     * 根据key的值查询数据
     * @param key 条件
     * @param value 条件值
     * @return 测量数据
     * @throws SQLException sql异常
     */
    public static List<MeasureDataBean> getMeasures(String key, Object value) throws SQLException {
        RuntimeExceptionDao<MeasureDataBean, Integer> dao = getMeasureDao();
        QueryBuilder qb = dao.queryBuilder();
        qb.where().eq(key, value);
        PreparedQuery prepare = qb.prepare();
        List<MeasureDataBean> query = dao.query(prepare);
        return query;
    }

    /**
     * 根据idcard查询病人
     * @param idcard 身份证号
     * @return idcard查询病人
     */
    public static List<PatientBean> getPatient(String idcard) {
        return DBManager.getDBHelper(UiUtils.getContent()).getPatientDao()
                .queryForEq(GlobalConstant.IDCARD, idcard);
    }

    /**
     * 根据测量信息保存病人
     * @param bean 测量信息
     */
    public static void saveMeasure(MeasureDataBean bean) {
        getMeasureDao().update(bean);
    }

    /**
     * 获取操作数据库的dao
     * @return MeasureDataDao
     */
    public static RuntimeExceptionDao<MeasureDataBean, Integer> getMeasureDao() {
        return DBManager.getDBHelper(UiUtils.getContent()).getMeasureDataDao();
    }

    /**
     * 根据身份证判断当前病人有多少条测量记录
     * @param idcard 身份证
     * @return 多少条测量记录
     */
    public static int getMeasuresSize(String idcard) {
        QueryBuilder<MeasureDataBean, Integer>
                qb = getMeasureDao()
                .queryBuilder();
        try {
            qb.where().eq("idcard", idcard);
            return (int) qb.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 截取查询测量记录
     * @param idcard  身份证号码
     * @param offset  从什么地方开始截取，包括自己
     * @param viewAmount 截取多少位数据
     * @return 查询测量记录
     */
    public static List<MeasureDataBean> getMeasureSub(String idcard, long
            offset, long viewAmount) {
        RuntimeExceptionDao<MeasureDataBean, Integer> dao =
                getMeasureDao();
        QueryBuilder<MeasureDataBean, Integer>
                qb = dao.queryBuilder();
        try {
            qb.limit(viewAmount);
            qb.offset(offset);
            qb.orderBy("check_day", false);
            qb.where().eq("idcard", idcard);
            PreparedQuery pd = qb.prepare();
            List<MeasureDataBean>  list = dao.query(pd);
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * 根据身份证号查询该病人的所有测量记录
     * @param idCard  身份证号码
     * @return List<MeasureDataBean> 测量记录
     */
    public static List<MeasureDataBean> queryMeasureData(String idCard) {
        try {
            RuntimeExceptionDao<MeasureDataBean, Integer> measureDao =
                    getMeasureDao();
            QueryBuilder<MeasureDataBean, Integer> qb = measureDao
                    .queryBuilder();
            qb.orderBy("check_day", false);
            qb.where().eq("idcard", idCard);
            PreparedQuery prepare = qb.prepare();
            return measureDao.query(prepare);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
