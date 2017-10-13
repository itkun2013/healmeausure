package com.konsung.presenter;

import android.text.TextWatcher;

import com.konsung.bean.MeasureDataBean;
import com.konsung.network.LifeSubscription;

/**
 * 手动输入推送者接口
 **/
public class HandInputPresenter {

    /**
     * 界面回显的接口
     */
    public interface View extends LifeSubscription {
        /**
         * 数据回显
         * @param bean 数据集
         */
        void setDataBean(MeasureDataBean bean);
    }

    /**
     * 逻辑处理的接口
     */
    public interface Presenter {

        /**
         * 保存值
         * @param param 参数
         * @param value 值
         * @return 是否保存成功
         */
        boolean save(int param, int value);

        /**
         * 获取小数限制Watcher
         * @param limit 限制长度
         * @return 文本Watcher
         */
        TextWatcher getDotLimitTextWatcher(final int limit);

        /**
         * 绑定服务
         */
        void bindAidlService();
    }
}
