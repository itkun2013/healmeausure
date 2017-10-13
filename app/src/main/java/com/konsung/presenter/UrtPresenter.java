package com.konsung.presenter;

import android.content.Context;
import android.view.ViewGroup;

import com.konsung.bean.MeasureDataBean;
import com.konsung.network.LifeSubscription;
import com.konsung.presenter.impl.UrtPresenterImpl;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 尿常规逻辑
 **/
public class UrtPresenter {
    /**
     * 界面回显的接口
     */
    public interface View extends LifeSubscription {

        /**
         * 加载尿常规项目布局
         * @return 布局集合 ，返回的集合键，见KparamType
         * @see com.konsung.utils.KParamType
         */
        HashMap<Integer, UrtPresenterImpl.UrtHolder> loadUrtValueLayout();

        /**
         * 设置值
         * @param param 参数
         * @param value 参数值
         */
        void setValue(int param, int value);

        /**
         * 数据回显
         * @param bean 数据集
         */
        void setDataBean(MeasureDataBean bean);

        /**
         * 获取上下文
         * @return 上下文
         */
        Context getContent();
    }

    /**
     * 逻辑处理的接口
     */
    public interface Presenter {
        /**
         * 绑定服务
         */
        void bindAidlService();

        /**
         * 初始化尿常规显示项
         * @return 需要显示的项  见KParamType
         */
        ArrayList<Integer> initUrtData();

        /**
         * 初始化项目代码
         * @return 显示的项目代码
         */
        HashMap<Integer, String> initUrtCodes();

        /**
         * 初始化项目名称
         * @return 显示的项目名称
         */
        HashMap<Integer, String> initUrtNames();

        /**
         * 获取参数布局
         * @param context 上下文
         * @param root 父布局
         * @return 布局持有者
         */
        UrtPresenterImpl.UrtHolder getParamLayout(Context context, ViewGroup root);
    }
}
