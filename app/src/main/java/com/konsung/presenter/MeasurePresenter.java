package com.konsung.presenter;

import android.app.Fragment;
import android.app.Service;
import android.content.Context;

import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.MeasureItemBean;
import com.konsung.network.LifeSubscription;

import java.util.List;
import java.util.Map;

/**
 * 测量界面的逻辑
 */
public class MeasurePresenter {
    /**
     * \
     * 测量界面回显接口
     */
    public interface View extends LifeSubscription {
        /**
         * 切换Fragment
         * @param position 被添加显示的索引
         */
        void switchToFragment(int position);

        /**
         * 初始化测量数据
         * @param bean 测量数据
         */
        void setMeasureData(MeasureDataBean bean);

        /**
         * 更新测量数据
         * @param param 参数
         */
        void updateMeasureData(int param);
    }

    /**
     * 测量界面的逻辑接口
     */
    public interface Presenter {
        /**
         * 获取测量项
         * @return 测量项集合
         */
        List<MeasureItemBean> getMeasureItems();

        /**
         * 默认显示的fragment
         * @return fragment集合里的索引
         */
        int defaultFragment();

        /**
         * 获取所有测量项的fragment
         * @param measureItems 已加载的测量项
         * @return 所有测量项的fragment
         */
        Map<Integer, Fragment> getAllFragment(List<MeasureItemBean> measureItems);

        /**
         * 绑定aidlService
         */
        void bindService();

        /**
         * 取消绑定服务
         * @param context
         */
        void unbindService(Context context);

        /**
         * 获取服务
         * @return 服务
         */
        Service getAidlService();

        /**
         * 获取需要测量的用户信息
         */
        void getPatient();

        /**
         * 关闭远程服务
         */
        void stopService();
    }
}
