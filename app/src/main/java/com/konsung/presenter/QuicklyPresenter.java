package com.konsung.presenter;

import com.konsung.bean.MeasureDataBean;
import com.konsung.network.LifeSubscription;

/**
 * 快速检测逻辑
 **/
public class QuicklyPresenter {
    /**
     * 界面回显的接口
     */
    public interface View extends LifeSubscription {

        /**
         * 初始化加载的界面
         * @param state 0:百捷三合一+血红蛋白，1：百捷三合一+身高体重，2：单血糖+血红蛋白+身高体重，3单血糖+血脂
         */
        void refreshLayout(int state);

        /**
         * 设置测量值
         * @param param 测量参数
         * @param value 测量值（原始数据）
         */
        void setMeasureValue(int param, int value);

        /**
         * 绘制血氧波形
         * @param data 波形数据
         */
        void drawSpo2Wave(byte[] data);

        /**
         * 设置测量数据
         * @param bean 测量数据
         */
        void setMeasureDataBean(MeasureDataBean bean);

        /**
         * 刷新袖带压
         * @param value 袖带压数据
         */
        void refreshCuff(int value);

        /**
         * 测量血压状态提示
         * @param code 测量状态
         */
        void showNibpResult(int code);

        /**
         * 血氧状态
         * @param code 测量状态
         */
        void showSpo2Status(int code);
        /**
         * 测量状态提示
         * @param progress 测量状态
         */
        void refreshSpo2Ui(int progress);
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
         * 获取测量数据
         * @return 测量数据
         */
        MeasureDataBean getMeasureData();

        /**
         * 对当前的测量数据进行保存
         * @param param 参数
         * @param value 测量值
         */
        void saveDb(int param, int value);

        /**
         * 启动血氧测量
         */
        void startSpo2Measure();

        /**
         * 停止血氧测量
         */
        void stopSpo2Measure();
    }
}
