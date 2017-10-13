package com.konsung.bean;

import com.konsung.utils.constant.ReferenceValue;

/**
 * 该类用来转换bean类数据的切换
 * @param <T> 数据的切换
 */

public class BeanUtil<T> {
    private T mBean;

    /**
     * 数据转换对应的bean
     * @param bean 转换对应的bean
     */
    public BeanUtil(T bean) {
        mBean = bean;
    }

    /**
     * 获取参考值
     * @return 参考值
     */
    public static ReferenceValue getReferenceValue() {
        return new ReferenceValue(){};
    }

}
