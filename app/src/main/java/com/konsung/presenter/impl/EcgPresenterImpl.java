package com.konsung.presenter.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.konsung.R;
import com.konsung.bean.EcgCacheBean;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.MeasureEcgBean;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.data.ProviderReader;
import com.konsung.network.EchoServerEncoder;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.EcgPresenter;
import com.konsung.service.AIDLServer;
import com.konsung.ui.activity.MeasureActivity;
import com.konsung.ui.holder.EcgTutorialHolder;
import com.konsung.utils.DiagCodeToText;
import com.konsung.utils.KParamType;
import com.konsung.utils.ParamDefine.EcgDefine;
import com.konsung.utils.SpUtils;
import com.konsung.utils.ToastUtils;
import com.konsung.utils.UiUtils;
import com.konsung.utils.UnitConvertUtil;
import com.konsung.utils.constant.GlobalConstant;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 心电逻辑处理类
 */

public class EcgPresenterImpl extends BasePresenter<EcgPresenter.View> implements EcgPresenter
        .Presenter {
    private static final int ONE_THOUSAND = 1000; //秒转毫秒
    private static final int ECG_OPERATION = 0xFF;
    private static final int ECG_OPERATION1 = 0x0F;
    private static final int DEFAULT_TIME = 20;

    //心电连接正常
    private static final int ECG_CONNECT_NORMAL = 0;
    AIDLServer aidlServer;
    EcgPresenter.View view;
    int ecgHr;
    private int countDown = DEFAULT_TIME; //默认倒计时时间
    //    private Timer timer;
//    private TimerTask timerTask;
    private boolean checking = false;

    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            countDown--;
            MeasureActivity.ecgCheckTimes = countDown;
            timeHandler.postDelayed(this, ONE_THOUSAND);
        }
    };

    EcgCacheBean waveCacheBean = new EcgCacheBean();

    /**
     * 构造器
     * @param view 对应的view
     */
    public EcgPresenterImpl(EcgPresenter.View view) {
        this.view = view;
    }

    @Override
    public void startMeasure() {
        waveCacheBean.initEcgWave();
        checking = true;
        EchoServerEncoder.setEcgConfig(KParamType.START_ECG_DIAGNOSE, 1);
        startCountDown();
    }

    @Override
    public void stopMeasure() {
        checking = false;
        stopCountDown();
    }

    @Override
    public BaseAdapter getLeadAdapter(Context context) {
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(context, R.layout.textview,
                context.getResources().getStringArray(R.array.ecg_lead));
        return stringArrayAdapter;
    }

    @Override
    public BaseAdapter getAmplitudeAdapter(Context context) {
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(context, R.layout.textview,
                context.getResources().getStringArray(R.array.ecg_amplitude));
        return stringArrayAdapter;
    }

    @Override
    public BaseAdapter getVelocityAdapter(Context context) {
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(context, R.layout.textview,
                context.getResources().getStringArray(R.array.ecg_velocity));
        return stringArrayAdapter;
    }

    @Override
    public void bindAidlService() {
        Intent intent = new Intent(UiUtils.getContent(), AIDLServer.class);
        UiUtils.getContent().startService(intent);
        UiUtils.getContent().bindService(intent, serviceConnection, Context
                .BIND_AUTO_CREATE);
    }

    @Override
    public String getEcgConnectStatus(int leadoff) {
        String off = UiUtils.getString(R.string.please_access);
        if (leadoff == 1) {
            off += UiUtils.getString(R.string.avf) + ",";
        }
        if ((leadoff & 0x2) == 0x2) {
            off += UiUtils.getString(R.string.avl) + ",";
        }
        if ((leadoff & 0x4) == 0x4) {
            off += UiUtils.getString(R.string.avr) + ",";
        }
        if ((leadoff & 0x8) == 0x8) {
            off += UiUtils.getString(R.string.V1) + ",";
        }
        if ((leadoff & 0x10) == 0x10) {
            off += UiUtils.getString(R.string.V2) + ",";
        }
        if ((leadoff & 0x20) == 0x20) {
            off += UiUtils.getString(R.string.V3) + ",";
        }
        if ((leadoff & 0x40) == 0x40) {
            off += UiUtils.getString(R.string.V4) + ",";
        }
        if ((leadoff & 0x80) == 0x80) {
            off += UiUtils.getString(R.string.V5) + ",";
        }
        if ((leadoff & 0x100) == 0x100) {
            off += UiUtils.getString(R.string.V6) + ",";
        }
        return off.substring(0, off.length() - 1) +
                UiUtils.getString(R.string.pole_off);
    }

    @Override
    public void saveConfigParam(String param, int value) {
        SpUtils.saveToSp(UiUtils.getContent(), GlobalConstant.APP_CONFIG, param, value);
    }

    @Override
    public int getConfigValue(String param) {
        if (EcgDefine.ECG_AMPLITUDE_SYSTEM.equals(param)) {
            return SpUtils.getSpInt(UiUtils.getContent(), GlobalConstant.APP_CONFIG, param,
                    EcgDefine.ECG_AMPLITUDE_DEFAULT);
        } else if (EcgDefine.ECG_LEAD_SYSTEM.equals(param)) {
            return SpUtils.getSpInt(UiUtils.getContent(), GlobalConstant.APP_CONFIG, param,
                    EcgDefine.ECG_LEAD_DEFAULT);
        } else {
            return SpUtils.getSpInt(UiUtils.getContent(), GlobalConstant.APP_CONFIG, param,
                    EcgDefine.ECG_VELOCITY_DEFAULT);
        }
    }

    @Override
    public List<Integer> getStatisticalTableItem() {
        //加载当前用户的历史测量记录
        List<Integer> paramList = new ArrayList<>();
        paramList.add(KParamType.ECG_HR); //趋势表需要显示多少个参数，吧相对应的参数传进来即可
        return paramList;
    }

    @Override
    public StatisticalPicBean[] getStatisticalPic(Context context) {
        String currentIdCard = ProviderReader.readCurrentPatient(context).getIdCard();
        StatisticalPicBean picBean = new StatisticalPicBean();
        picBean.setIdCard(currentIdCard);
        picBean.setDataSize(10); //数据长度10个
        picBean.setStartCount(0); //数据库查询起始位置0
        picBean.setMaxValue(350); //y刻度值最大300
        picBean.setMinValue(0); //y刻度值最小0
        picBean.setySize(10); //y轴10个刻度
        picBean.setParameter(KParamType.ECG_HR);
        picBean.setUnit(UiUtils.getString(R.string.health_unit_bpm));
        StatisticalPicBean[] beans = {picBean};
        return beans;
    }

    /**
     * 获取设备安装引导界面
     * @param context 上下文
     * @param root 父布局
     * @param onClickListener 观看视频按钮时间
     * @return 显示布局
     */
    public EcgTutorialHolder getInstallGuideView(Context context, ViewGroup root, View
            .OnClickListener
            onClickListener) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_ecg_tutorial, root, false);
        EcgTutorialHolder holder = new EcgTutorialHolder(view);
        if (onClickListener != null) {
            holder.tvLookVideo.setOnClickListener(onClickListener);
        }
        return holder;
    }

    @Override
    public void saveWave(int param, byte[] data) {
        waveCacheBean.setEcgWave(param, UnitConvertUtil.bytesToHexString(data));
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlServer = ((AIDLServer.MsgBinder) service).getService();
            final MeasureDataBean measureDataBean = aidlServer.getMeasureDataBean();
            view.setMeasureDataBean(measureDataBean);
            aidlServer.setSendMSGToFragment(new AIDLServer.SendMSGToFragment() {

                @Override
                public void sendParaStatus(String name, String version) {
                }

                @Override
                public void sendWave(int param, byte[] bytes) {
                    if (1 <= param && param <= 12) { //12导联心电波形参数 1-12
                        //对数据进行处理后返回给fragment
                        for (int i = 0; i < bytes.length / 2; i++) {
                            int data = (bytes[i * 2] & 0xFF) + ((bytes[i * 2 + 1] & 0x0F) << 8);
                            view.addEcgWaveData(param, data);
                        }
                        if (checking) {
                            view.saveEcgWave(param, bytes);
                        }
                        //测量超时
                        if (countDown <= 0) {
                            stopCountDown();
                            view.measureError();
                            ToastUtils.showShortToast(R.string.ecg_check_timeout);
                        }
                    }
                }

                @Override
                public void sendTrend(int param, int value) {
                    switch (param) {
                        case KParamType.ECG_HR:
                            // HR已改为从12导诊断结果获取，不再从此趋势值获取
                            //TODO 3-5导联下这里获取心率
                            ecgHr = value;
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void sendConfig(int param, int value) {
                    switch (param) {
                        case KParamType.ECG_CONNECTION_STATUS: //心电导联线连接状态码
                            view.setEcgConnectStatus(value);
                            if (value != ECG_CONNECT_NORMAL) {
                                checking = false;
                            }
                            break;

                        case KParamType.ECG_ABNORMAL: //心率失常状态监听
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
                    String diaResult = " "; // 12导诊断结果
                    //根据AppDevice中的协议，诊断结果最多有26个数据（不包括时间戳）
                    if (bytes.length != 52) {
                        Log.v("HealthOne", "12 Lead Dia Result len is not right!");
                    }
                    int[] result = new int[26];

                    for (int i = 0; i < 26; i++) {
                        result[i] = (short) (((bytes[i * 2 + 1] & 0x00FF)
                                << 8) | (0x00FF & bytes[i * 2]));
                    }
                    int hrValue = result[0]; // HR值
                    int prInterval = result[1]; // PR间期 单位ms
                    int qrsDuration = result[2]; // QRS间期, 单位ms
                    int qt = result[3]; // QT间期 单位ms
                    int qtc = result[4]; // QTC间期 单位ms
                    int pAxis = result[5]; // P 波轴 单位°
                    int qrsAxis = result[6]; // QRS波心电轴 单位°
                    int tAxis = result[7]; // T波心电轴 单位°
                    int rv5 = result[8]; // RV5, 单位0.01mV
                    int sv1 = result[9]; // SV1, 单位0.01mV
                    String divide = UiUtils.getString(R.string.unit_divide);
                    //诊断算法包无法计算出超过300的心率值，在此获取参数版上传的心率值显示
                    if (hrValue == 0 && ecgHr / GlobalConstant.TREND_FACTOR >= 300) {
                        hrValue = ecgHr / GlobalConstant.TREND_FACTOR;
                    }
                    measureDataBean.setPr(prInterval);
                    measureDataBean.setQrs(qrsDuration);
                    measureDataBean.setQt(qt);
                    measureDataBean.setQtc(qtc);
                    measureDataBean.setpAxis(pAxis);
                    measureDataBean.setQrsAxis(qrsAxis);
                    measureDataBean.settAxis(tAxis);
                    measureDataBean.setRv5(rv5);
                    measureDataBean.setSv1(sv1);
                    DecimalFormat format  =   new DecimalFormat("##0.00");

                    MeasureEcgBean ecgBean = new MeasureEcgBean();
                    ecgBean.setP(pAxis + "");
                    ecgBean.setPr(prInterval + "");
                    ecgBean.setQrs(qrsDuration + "");
                    ecgBean.setQtQtc(qt + divide + qtc);
                    ecgBean.setpQrsT(pAxis + divide + qrsAxis + divide + tAxis);
                    float rv5Float = (float) rv5 / GlobalConstant.FACTOR;
                    float sv1Float = (float) sv1 / GlobalConstant.FACTOR;
                    ecgBean.setRv5Sv1(String.format("%.2f", rv5Float) + divide
                            + String.format("%.2f", sv1Float));
                    ecgBean.setRv5Sv1Add(String.format("%.2f", rv5Float + sv1Float));
                    if (prInterval < 0) {
                        prInterval = (short) -prInterval;
                    }
                    diaResult = String.valueOf(hrValue) + "," + String
                            .valueOf(prInterval) + ","
                            + String.valueOf(qrsDuration) + "," + String
                            .valueOf(qt) + ","
                            + String.valueOf(qtc) + "," + String.valueOf(pAxis) + ","
                            + String.valueOf(qrsAxis) + "," + String.valueOf(tAxis) + ","
                            + String.format("%.2f", (float) rv5 / GlobalConstant.FACTOR) + ","
                            + String.format("%.2f", (float) sv1 / GlobalConstant.FACTOR) + ","
                            + String.format("%.2f", ((float) rv5 / GlobalConstant.FACTOR +
                            (float) sv1 / GlobalConstant.FACTOR)) + ",";

                    //根据AppDevice协议，诊断码有16个，但不是所有都有效
                    DiagCodeToText diagCodeToText = new DiagCodeToText();
                    for (int i = 0; i < 16; i++) {
                        for (int j = 0; j < diagCodeToText.ECG_12_LEAD_DIAG_TEXT.length; j++) {
                            String[] str = diagCodeToText.ECG_12_LEAD_DIAG_TEXT[j].split(":");
                            if (result[10 + i] == Integer.parseInt(str[0])) {
                                diaResult += str[1];
                                if ((str[1] != null) && (!"".equals(str[1]))) {
                                    diaResult += ";";
                                }
                            }
                        }
                    }

                    String[] split = diaResult.split(",");
                    String resultStr = split[split.length - 1];
                    measureDataBean.setEcgDiagnoseResult(resultStr);
                    ecgBean.setResult(resultStr);
                    // 心电测量完成
                    if (checking) {
                        aidlServer.saveTrend(KParamType.ECG_HR, hrValue * 100);
                        //保存心电数据
                        for (int i = 1; i <= 12; i++) {
                            try {
                                aidlServer.savedWave(i, waveCacheBean.getEcgWave(i));
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }

                        aidlServer.saveToDb2();
                        view.measureSuccess(hrValue, ecgBean);
                        view.setMeasureDataBean(measureDataBean);
                        stopMeasure();
                    }
                }
            });
        }
    };

    /**
     * 倒计时实时改变界面
     */
    private Handler timeHandler = new Handler();

    /**
     * 开始倒计时
     */
    private void startCountDown() {
        timeHandler.post(timeRunnable);
    }

    /**
     * 停止倒计时
     */
    private void stopCountDown() {
        checking = false;
        timeHandler.removeCallbacks(timeRunnable);
        countDown = DEFAULT_TIME;
        MeasureActivity.isCheckingEcg = false;
    }
}
