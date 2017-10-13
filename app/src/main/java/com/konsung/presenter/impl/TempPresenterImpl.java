package com.konsung.presenter.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.data.ProviderReader;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.TempPresenter;
import com.konsung.service.AIDLServer;
import com.konsung.utils.KParamType;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 体温逻辑实现
 **/

public class TempPresenterImpl extends BasePresenter<TempPresenter.View>
        implements TempPresenter.Presenter {

    public static final int FLAG_TEMP_STATUS = 0x00;

    //是否再测量中
    private int tempValue = GlobalConstant.INVALID_DATA;

    AIDLServer aidlServer;
    TempPresenter.View view;

    /**
     * 构造函数
     * @param view 布局操作
     */
    public TempPresenterImpl(TempPresenter.View view) {
        this.view = view;
    }

    @Override
    public void bindAidlService() {
        Intent intent = new Intent(UiUtils.getContent(), AIDLServer.class);
        UiUtils.getContent().startService(intent);
        UiUtils.getContent().bindService(intent, serviceConnection, Context
                .BIND_AUTO_CREATE);
    }

    @Override
    public List<Integer> getStatisticalTableItem() {
        List<Integer> paramList = new ArrayList<>();
        paramList.add(KParamType.IRTEMP_TREND); //趋势表需要显示多少个参数，吧相对应的参数传进来即可
        return paramList;
    }

    @Override
    public StatisticalPicBean[] getStatisticalPic(Context context) {
        String currentIdCard = ProviderReader.readCurrentPatient(context).getIdCard();
        StatisticalPicBean picBean = new StatisticalPicBean();
        picBean.setIdCard(currentIdCard);
        picBean.setDataSize(10); //数据长度10个
        picBean.setStartCount(0); //数据库查询起始位置0
        picBean.setMaxValue(50); //y刻度值最大300
        picBean.setMinValue(30); //y刻度值最小0
        picBean.setySize(10); //y轴10个刻度
        picBean.setParameter(KParamType.IRTEMP_TREND);
        picBean.setUnit(UiUtils.getString(R.string.unit_temp));
        StatisticalPicBean[] beans = {picBean};
        return beans;
    }

/**
 * 获取安装引导界面
 * @param context 上下文
 * @param onClickListener 观看操作视频文本点击事件
 * @return 引导界面的View
 */
    /**
     * 获取设备安装引导界面
     * @param context 上下文
     * @param root 父布局
     * @param onClickListener 观看视频按钮时间
     * @return 显示布局
     */
    public View getInstallGuideView(Context context, ViewGroup root, View.OnClickListener
            onClickListener) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_temp_tutorial, root, false);
        TextView tvIntroduction = (TextView) view.findViewById(R.id.tv_introduction);
        TextView tvLookVideo = (TextView) view.findViewById(R.id.tv_look);
        tvLookVideo.setOnClickListener(onClickListener);
        return view;
    }

    /**
     * 获取测量成功界面
     * @param context 上下文
     * @param root 父布局
     * @return 测量界面的View
     */
    public View getSuccessLayout(Context context, ViewGroup root) {
        return LayoutInflater.from(context).inflate(R.layout.layout_spo2_mesure_success, root,
                false);
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlServer = ((AIDLServer.MsgBinder) service).getService();
            view.setDataBean(aidlServer.getMeasureDataBean());
            aidlServer.setSendMSGToFragment(new AIDLServer.SendMSGToFragment() {

                @Override
                public void sendParaStatus(String name, String version) {
                }

                @Override
                public void sendWave(int param, byte[] bytes) {
                }

                @Override
                public void sendTrend(int param, int value) {
                    switch (param) {
                        //接触式
                        case KParamType.TEMP_T1:
                        case KParamType.TEMP_T2:
                            break;
                        //额温
                        case KParamType.IRTEMP_TREND:
                            if (value != GlobalConstant.INVALID_DATA) {
                                view.measureSuccess(value);
                                aidlServer.saveTrend(KParamType.IRTEMP_TREND, value);
                                aidlServer.saveToDb2();
                            }
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void sendConfig(int param, int value) {
                }

                @Override
                public void sendPersonalDetail(String name, String idcard, int sex, int type) {

                }

                @Override
                public void send12LeadDiaResult(byte[] bytes) {

                }
            });
        }
    };
}
