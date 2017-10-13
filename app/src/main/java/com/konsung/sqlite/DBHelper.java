package com.konsung.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.konsung.utils.constant.GlobalConstant;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;

import java.sql.SQLException;

/**
 * 数据库DBHelper类
 * @author ouyangfan
 * @version 0.0.1
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = GlobalConstant.DATABASE_FILE + "konsung.db";
    private static final String TAG = "DBHelper";
    private Context mContext;

    // 创建 dao
    private RuntimeExceptionDao<PatientBean, Integer> patientRuntimeDao = null;
    private RuntimeExceptionDao<MeasureDataBean, Integer>
            measureDataRuntimeDao = null;

    /**
     * 构造
     * @param context 上下文
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, GlobalConstant.DATABASE_VERSION);
        this.mContext = context;
    }

    /*
     * 创建Sqlite数据库
     * @param db
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        // 创建表
        try {
            TableUtils.createTable(connectionSource, PatientBean.class);
            TableUtils.createTable(connectionSource, MeasureDataBean.class);

            patientRuntimeDao = getPatientDao();
            measureDataRuntimeDao = getMeasureDataDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * 更新Sqlite数据库
     * 当应用程序升级，数据库改变时调用
     * @param db 数据库名
     * @param connectionSource 连接源
     * @param oldVersion 老版本号
     * @param newVersion 新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource
            connectionSource, int oldVersion, int newVersion) {

    }

    /**
     * 病人管理Dao
     * @return 病人管理Dao
     */
    public RuntimeExceptionDao<PatientBean, Integer> getPatientDao() {
        if (patientRuntimeDao == null) {
            patientRuntimeDao = getRuntimeExceptionDao(PatientBean.class);
        }
        return patientRuntimeDao;
    }

    /**
     * 管理测量数据Dao
     * @return 管理测量数据Dao
     */
    public RuntimeExceptionDao<MeasureDataBean, Integer> getMeasureDataDao() {
        if (measureDataRuntimeDao == null) {
            measureDataRuntimeDao = getRuntimeExceptionDao(MeasureDataBean
                    .class);
        }
        return measureDataRuntimeDao;
    }

    /**
     * 创建操作数据库的dao
     * @param clz 数据库类名
     * @param <T> 类型
     * @return 数据库的dao
     */
    public <T> RuntimeExceptionDao<T, Integer> createDao(Class<T> clz) {
        return getRuntimeExceptionDao(clz);
    }

    /**
     * 修改数据库，对某个库进行增加字段
     * @param db SQLiteDatabase
     * @param table 数据库名
     * @param line 名字
     * @param var 类型
     */
    private void addLine(SQLiteDatabase db, String table, String line, String var) {
        String sql = "alter table " + table + " " + "add " + line + " " + var;
        db.execSQL(sql);
    }

    /**
     * 关闭数据库
     */
    @Override
    public void close() {
        super.close();
        patientRuntimeDao = null;
        measureDataRuntimeDao = null;
    }
}