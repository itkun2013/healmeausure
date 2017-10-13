package com.konsung.presenter.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.data.ProviderReader;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.Spo2Presenter;
import com.konsung.service.AIDLServer;
import com.konsung.ui.activity.MeasureActivity;
import com.konsung.ui.base.ViewHolder;
import com.konsung.ui.defineview.RoundProgressBar;
import com.konsung.ui.defineview.WaveFormSpo2;
import com.konsung.ui.fragment.MeasureSpo2Fragment;
import com.konsung.ui.holder.Spo2TutorialHolder;
import com.konsung.utils.KParamType;
import com.konsung.utils.ProtocolDefine;
import com.konsung.utils.UiUtils;
import com.konsung.utils.UnitConvertUtil;
import com.konsung.utils.constant.GlobalConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 血氧逻辑实现
 **/

public class Spo2PresenterImpl extends BasePresenter<Spo2Presenter.View>
        implements Spo2Presenter.Presenter {

    /**
     * 测量时间
     */
    public static final int MEASURE_TIMES = 20;
    //是否再测量中
    public boolean checking = false;
    private int spo2Value = GlobalConstant.INVALID_DATA;
    private int prValue = GlobalConstant.INVALID_DATA;

    private int measureCount = 0;
    private int countDown = 0; //默认倒计时时间

    AIDLServer aidlServer;
    Spo2Presenter.View view;

    /**
     * 构造函数
     * @param view 布局操作
     */
    public Spo2PresenterImpl(Spo2Presenter.View view) {
        this.view = view;
    }

    @Override
    public void startMeasure() {
        countDown = MEASURE_TIMES;
        checking = true;
        view.refresh(MeasureSpo2Fragment.MEASURE_LAYOUT);
        startCountDown();
    }

    @Override
    public void stopMeasure() {
        checking = false;
        view.refresh(MeasureSpo2Fragment.SPO2_LAYOUT);
        stopCountDown();
        measureCount = 0;
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
        paramList.add(KParamType.SPO2_TREND); //趋势表需要显示多少个参数，吧相对应的参数传进来即可
        paramList.add(KParamType.SPO2_PR);
        return paramList;
    }

    @Override
    public StatisticalPicBean[] getStatisticalPic(Context context) {
        String currentIdCard = ProviderReader.readCurrentPatient(context).getIdCard();
        StatisticalPicBean picBean = new StatisticalPicBean();
        picBean.setIdCard(currentIdCard);
        picBean.setDataSize(10); //数据长度10个
        picBean.setStartCount(0); //数据库查询起始位置0
        picBean.setMaxValue(100); //y刻度值最大300
        picBean.setMinValue(0); //y刻度值最小0
        picBean.setySize(10); //y轴10个刻度
        picBean.setParameter(KParamType.SPO2_TREND);
        picBean.setUnit(UiUtils.getString(R.string.unit_percent));
        StatisticalPicBean picBean2 = new StatisticalPicBean();
        picBean2.setIdCard(currentIdCard);
        picBean2.setDataSize(10); //数据长度10个
        picBean2.setStartCount(0); //数据库查询起始位置0
        picBean2.setMaxValue(300); //y刻度值最大300
        picBean2.setMinValue(0); //y刻度值最小0
        picBean2.setySize(10); //y轴10个刻度
        picBean2.setParameter(KParamType.SPO2_PR);
        picBean2.setUnit(UiUtils.getString(R.string.health_unit_bpm));
        StatisticalPicBean[] beans = {picBean, picBean2};
        return beans;
    }

    /**
     * 获取安装引导界面
     * @param context 上下文
     * @param root 父布局
     * @return 引导界面持有者
     */
    public Spo2TutorialHolder getInstallGuideView(Context context, ViewGroup root) {
        Spo2TutorialHolder holder = new Spo2TutorialHolder(context,
                R.layout.layout_spo2_tutorial, root);
        return holder;
    }

    /**
     * 获取测量界面
     * @param context 上下文
     * @param root 父布局
     * @return 测量界面的View
     */
    public CheckViewHolder getMeasureLayout(Context context, ViewGroup root) {
        CheckViewHolder holder = new CheckViewHolder(context, R.layout.layout_spo2_measure, root);
        return holder;
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

    /**
     * 测量页面持有者
     */
    public class CheckViewHolder extends ViewHolder {
        @InjectView(R.id.spo2_wave)
        public WaveFormSpo2 spo2Wave;
        @InjectView(R.id.tv_status)
        public TextView tvStatus;
        @InjectView(R.id.progress_bar)
        public RoundProgressBar progressBar;

        /**
         * 构造器
         * @param context 上下文
         * @param id 布局资源文件id
         * @param root 父布局
         */
        public CheckViewHolder(Context context, @LayoutRes int id, ViewGroup root) {
            super(context, id, root);
            ButterKnife.inject(this, view);
            progressBar.setMax(MEASURE_TIMES);
        }
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlServer = ((AIDLServer.MsgBinder) service).getService();
            //绑定服务成功时，刷新血氧界面
            if (MeasureActivity.probeSpo2Status != GlobalConstant.INVALID_DATA) {
                view.setSpo2Status(MeasureActivity.probeSpo2Status);
            }
            view.setDateBean(aidlServer.getMeasureDataBean());
            aidlServer.setSendMSGToFragment(new AIDLServer.SendMSGToFragment() {

                @Override
                public void sendParaStatus(String name, String version) {
                }

                @Override
                public void sendWave(int param, byte[] bytes) {
                    switch (param) {
                        case KParamType.SPO2_WAVE:
                            view.spo2WaveData(bytes);
                            if (checking) {
                                try {
                                    aidlServer.savedWave(param, UnitConvertUtil
                                            .bytesToHexString(bytes));
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                //测量超时
                                if (countDown >= MEASURE_TIMES) {
                                    checking = false;
                                    stopCountDown();
                                    view.refresh(MeasureSpo2Fragment.MEASURE_FAILED_LAYOUT);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void sendTrend(int param, int value) {
                    switch (param) {
                        case KParamType.SPO2_TREND:
                            if (checking) {
                                if ((Math.abs(spo2Value - value / GlobalConstant
                                        .TREND_FACTOR) < 4) && value !=
                                        GlobalConstant.INVALID_DATA) {
                                    if ((measureCount++) == 6) {
                                        stopCountDown();
                                        view.measureSuccess(spo2Value *
                                                GlobalConstant.TREND_FACTOR, prValue);
                                        measureCount = 0;
                                        aidlServer.saveTrend(KParamType
                                                .SPO2_TREND, spo2Value *
                                                GlobalConstant.TREND_FACTOR);
                                        aidlServer.saveTrend(KParamType
                                                .SPO2_PR, prValue);
                                        // 保存到数据库
                                        aidlServer.saveToDb2();
                                        stopMeasure();
                                        view.refresh(MeasureSpo2Fragment.OVER_LAYOUT);
                                    }
                                } else {
                                    spo2Value = value / GlobalConstant.TREND_FACTOR;
                                    measureCount = 0;
                                }
                            }
                            break;
                        case KParamType.SPO2_PR:
                            if (checking) {
                                prValue = value;
                            }
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void sendConfig(int param, int value) {
                    switch (param) {
                        case ProtocolDefine.NET_SPO2_SENSOR_STATUS:
                            view.setSpo2Status(value);
                            checking = false;
                            stopCountDown();
                            measureCount = 0;
                            countDown = 0;
                            break;
                        default:
                            break;
                    }
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

    /**
     * 开始倒计时
     */
    private void startCountDown() {
        countDown = 0;
        handler.removeCallbacks(timeRunnable);
        handler.post(timeRunnable);
    }

    /**
     * 停止倒计时
     */
    private void stopCountDown() {
        handler.removeCallbacks(timeRunnable);
    }

    /**
     * 倒计时实时改变界面
     */
    private Handler handler = new Handler();
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            countDown++;
            view.refreshProgress(countDown);
            if (countDown < MEASURE_TIMES) {
                handler.postDelayed(this, 1000);
            }
        }
    };
}
