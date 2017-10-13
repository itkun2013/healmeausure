package com.konsung.presenter.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import com.konsung.bean.MeasureDataBean;
import com.konsung.data.ProviderReader;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.QuicklyPresenter;
import com.konsung.service.AIDLServer;
import com.konsung.ui.fragment.MeasureSpo2Fragment;
import com.konsung.utils.KParamType;
import com.konsung.utils.ProtocolDefine;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;

/**
 * 快速检测逻辑实现
 **/

public class QuicklyPresenterImpl extends BasePresenter<QuicklyPresenter.View>
        implements QuicklyPresenter.Presenter {
    /**
     * 测量时间
     */
    public static final int MEASURE_TIMES = 20;
    //是否再测量中
    public boolean spo2Checking = false;
    private int countDown = 0; //默认倒计时时间
    private int measureCount = 0;
    private int spo2Value = GlobalConstant.INVALID_DATA; // 血氧
    private int prValue = GlobalConstant.INVALID_DATA; // 血压
    AIDLServer aidlServer;
    QuicklyPresenter.View view;

    /**
     * 构造函数
     * @param view 布局操作
     */
    public QuicklyPresenterImpl(QuicklyPresenter.View view) {
        this.view = view;
    }

    @Override
    public void bindAidlService() {
        Intent intent = new Intent(UiUtils.getContent(), AIDLServer.class);
        UiUtils.getContent().bindService(intent, serviceConnection, Context
                .BIND_AUTO_CREATE);
    }

    @Override
    public MeasureDataBean getMeasureData() {
        return aidlServer.getMeasureDataBean();
    }

    @Override
    public void saveDb(int param, int value) {
        aidlServer.saveTrend(param, value);
        aidlServer.saveToDb2();
    }

    @Override
    public void startSpo2Measure() {
        spo2Checking = true;
        startCountDown();
    }

    @Override
    public void stopSpo2Measure() {
        spo2Checking = false;
        stopCountDown();
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlServer = ((AIDLServer.MsgBinder) service).getService();
            /**
             * TODO 后面还需要添加一个弹窗，询问是否需要创建新记录，如果不创建新记录，
             * TODO 就需要读取健康档案记录
             */

            MeasureDataBean measureDataBean = aidlServer.getMeasureDataBean();
            if (measureDataBean == null) {
                measureDataBean = new MeasureDataBean();
                measureDataBean.setIdcard(ProviderReader.readCurrentPatient(UiUtils.getContent())
                        .getIdCard());
                aidlServer.initMeasureBean(measureDataBean);
            }
            view.setMeasureDataBean(measureDataBean);
            aidlServer.setSendMSGToFragment(new AIDLServer.SendMSGToFragment() {
                @Override
                public void sendParaStatus(String name, String version) {
                }

                @Override
                public void sendWave(int param, byte[] bytes) {
                    if (param == KParamType.SPO2_WAVE) {
                        view.drawSpo2Wave(bytes);
                        if (spo2Checking) {
                            //测量超时
                            if (countDown >= MEASURE_TIMES) {
                                spo2Checking = false;
                                stopCountDown();
                                view.showNibpResult(13); //测量超时
                            }
                        }
                    }
                }

                @Override
                public void sendTrend(int param, int value) {
                    if (value != GlobalConstant.INVALID_DATA) {
                        //血氧需要比较几个测量值稳定后才算测量成功
                        if (param == KParamType.SPO2_TREND || param == KParamType.SPO2_PR) {
                            if (param == KParamType.SPO2_TREND && spo2Checking) {
                                if ((Math.abs(spo2Value - value / GlobalConstant
                                        .TREND_FACTOR) < 4)) {
                                    if ((measureCount++) == 6) {
                                        stopCountDown();
                                        view.setMeasureValue(param, value);
                                        view.setMeasureValue(KParamType.SPO2_PR, prValue);
                                        measureCount = 0;
                                        aidlServer.saveTrend(KParamType
                                                .SPO2_TREND, spo2Value *
                                                GlobalConstant.TREND_FACTOR);
                                        aidlServer.saveTrend(KParamType
                                                .SPO2_PR, prValue);
                                    }
                                } else {
                                    spo2Value = value / GlobalConstant.TREND_FACTOR;
                                    measureCount = 0;
                                }
                            } else if (spo2Checking) {
                                prValue = value;
                            }
                        } else {
                            view.setMeasureValue(param, value);
                        }
                    }
                }

                @Override
                public void sendConfig(int param, int value) {
                    switch (param) {
                        case ProtocolDefine.NET_NIBP_CUFF: //袖带压
                            if (value != GlobalConstant.INVALID_DATA) {
                                view.refreshCuff(value);
                            }
                            break;
                        case ProtocolDefine.NET_NIBP_RESULT: //血压测量结果发送至界面显示
                            if (value > 0) {
                                view.showNibpResult(value);
                            }
                            break;
                        case ProtocolDefine.NET_SPO2_SENSOR_STATUS:
                            view.showSpo2Status(value);
                            spo2Checking = false;
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
        countDown = 0;
        handler.removeCallbacks(timeRunnable);
        spo2Checking = false;
        measureCount = 0;
    }

    /**
     * 倒计时实时改变界面
     */
    private Handler handler = new Handler();
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            countDown++;
            view.refreshSpo2Ui(countDown);
            if (countDown < MEASURE_TIMES) {
                handler.postDelayed(this, 1000);
            } else {
                view.showSpo2Status(MeasureSpo2Fragment.MEASURE_FAILED_LAYOUT);
                spo2Checking = false;
            }
        }
    };
}
