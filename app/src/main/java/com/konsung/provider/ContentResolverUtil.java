package com.konsung.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

import java.util.Map;

/**
 * 内容提供者工具类
 */
public class ContentResolverUtil {
    private Uri uri; //与app交互的唯一标示
    ContentResolver contentResolver; //用于访问内容提供者

    /**
     * 构造器
     * @param context 上下文
     * @param uri 与内容提供者对接的唯一标示
     * @param observable 注册内容观察者，内容提供者数据变化后的回调
     */
    public ContentResolverUtil(Context context, Uri uri, ContentObserver observable) {
        this.contentResolver = context.getContentResolver();
        this.uri = uri;
        contentResolver.registerContentObserver(uri, true, observable);
    }

    /**
     * 插入数据
     * @param map 数据集
     */
    public void insert(Map<String, String> map) {
        //实例化一个ContentValues对象
        ContentValues insertContentValues = new ContentValues();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            insertContentValues.put(entry.getKey(), entry.getValue());
        }

        //这里的uri和ContentValues对象经过一系列处理之后会传到ContentProvider中的insert方法中，
        //在我们自定义的ContentProvider中进行匹配操作
        contentResolver.insert(uri, insertContentValues);
    }

    /**
     * 查询所有的数据
     * @return 查询结果集
     */
    public Cursor query() {
        Cursor query = contentResolver.query(uri, null, null, null, null);
        return query;
    }

    /**
     * 查询最新的数据
     * @return 最新的数据
     */
    public Cursor queryLatestData() {
        String s = uri.toString();
        s = s + "/path";
        Uri parse = Uri.parse(s);
        return contentResolver.query(parse, null, null, null, null);
    }

    /**
     * 更新数据
     * @param map 更新的内容
     */
    public void update(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            contentValues.put(entry.getKey(), entry.getValue());
        }
        contentResolver.update(uri, contentValues, null, null);
    }
}
