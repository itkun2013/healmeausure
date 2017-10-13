package com.konsung.utils;

import java.util.UUID;

/**
 * uuid工具类
 */
public class UUIDGenerator {
    /**
     * 构造器
     */
    private UUIDGenerator() {
    }

    /**
     * 获取uuid
     * @return uuid
     */
    public static String getUUID() {
        String s = UUID.randomUUID().toString();
        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)
                + s.substring(19, 23) + s.substring(24);
    }
}
