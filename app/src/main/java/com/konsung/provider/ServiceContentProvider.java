package com.konsung.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.konsung.sqlite.DBHelper;
import com.konsung.utils.constant.GlobalConstant;

import java.util.Set;

public class ServiceContentProvider extends ContentProvider {

    private static String AUTHORITY = "com.konsung.healthmeasure.provider";
    private static String[] arrStr = new String[]{"id", "name"};
    private static final Uri NOTIFY_URI = Uri.parse("content://" + AUTHORITY + "/path");

    //创建两个常量来区分不同的URI请求
    private static final int ALLROWS = 1;
    private static final int SINGLE_ROW = 2;

    private static final UriMatcher URI_MATCHER;

    //填充UriMatcher对象，其中以path结尾请求所有数据
    // path/[rowID]结尾的请求单行数据
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, "path", ALLROWS);
        URI_MATCHER.addURI(AUTHORITY, "path/id", SINGLE_ROW);
    }

    public static final String KEY_ID = "id";

    public static final String KEY_COLUMN_1_NAME = "KEY_COLUMN_1_NAME";

    private DBHelper myOpenHelper;

    @Override
    public boolean onCreate() {
        //构造底层数据库
        //延迟打开数据库，直到需要执行一个查询或者事务时再打开
        myOpenHelper = new DBHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        //打开数据库
        SQLiteDatabase db;
        try {
            db = myOpenHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = myOpenHelper.getReadableDatabase();
        }
        Cursor cursor = null;
        switch (URI_MATCHER.match(uri)) {

            case ALLROWS: //如果匹配成功，就根据条件查询数据并将查询出的cursor返回
                cursor = db.query(GlobalConstant.DATABASE_TABLE_NAME, null, null, null, null, null,
                        null);
                break;
            case SINGLE_ROW: // 根据条件查询一条数据。。。。
                MatrixCursor matrixCursor = new MatrixCursor(arrStr, 1);
                matrixCursor.addRow(new Object[]{520, "zhangDaShuai"});
                cursor = matrixCursor;
                break;
            default: //匹配失败 返回空。
                cursor = null;
        }
        //返回结果Cursor
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //打开一个可读可写的数据库来支持事务
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        //限定删除的行为为指定的行
        switch (URI_MATCHER.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            default:
                break;
        }

        //要想返回删除的项的数量，必须制定一条where字句。要删除所有行并返回一个值，则传入1
        if (selection == null) {
            selection = "1";
        }

        //执行删除
        int deleteCount = db.delete(GlobalConstant.DATABASE_TABLE_NAME,
                selection, selectionArgs);

        //通知所有观察者，数据集已经改变
        getContext().getContentResolver().notifyChange(uri, null);

        //返回删除的项的数量
        return deleteCount;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //打开一个可读可写的数据库来支持事务
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        //要想通过传入一个空content value对象来向数据库添加一个空行，
        // 必须使用nullColumnHack参数来制定可以设置为null的列名
        String nullColumnHack = null;

        //向表中插入数据
        long id = db.insert(GlobalConstant.DATABASE_TABLE_NAME,
                nullColumnHack, values);
        Set<String> strings = values.keySet();
        for (String str : strings) {
            Log.e("数据库共享数据", str);
        }
        // 构造并返回新插入的行的URI
        if (id > -1) {
            Uri insertedId = ContentUris.withAppendedId(NOTIFY_URI, id);

            getContext().getContentResolver().notifyChange(insertedId, null);
            return insertedId;
        } else {
            return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {

        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        switch (URI_MATCHER.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            default:
                break;
        }

        int updateCount = db.update(GlobalConstant.DATABASE_TABLE_NAME,
                values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }
}