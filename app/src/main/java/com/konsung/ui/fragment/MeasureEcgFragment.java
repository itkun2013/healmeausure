package com.konsung.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.adapter.StatisticalDataAdapter;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.MeasureEcgBean;
import com.konsung.bean.PatientBean;
import com.konsung.data.ProviderReader;
import com.konsung.network.EchoServerEncoder;
import com.konsung.presenter.EcgPresenter;
import com.konsung.presenter.impl.EcgPresenterImpl;
import com.konsung.service.AIDLServer;
import com.konsung.ui.activity.MeasureActivity;
import com.konsung.ui.base.BaseFragment;
import com.konsung.ui.defineview.DropEditText;
import com.konsung.ui.defineview.EcgReportDrawable;
import com.konsung.ui.defineview.EcgViewFor12;
import com.konsung.ui.defineview.StatisticalDataDialog;
import com.konsung.ui.holder.EcgMeasureHolder;
import com.konsung.ui.holder.EcgTutorialHolder;
import com.konsung.utils.KParamType;
import com.konsung.utils.ParamDefine.EcgDefine;
import com.konsung.utils.ProtocolDefine;
import com.konsung.utils.ReferenceUtils;
import com.konsung.utils.StatisticalDialogController;
import com.konsung.utils.ToastUtils;
import com.konsung.utils.UiUtils;
import com.konsung.utils.VideoUtil;
import com.konsung.utils.constant.GlobalConstant;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.senab.photoview.PhotoView;

/**
 * 心电测量界面
 */
public class MeasureEcgFragment extends BaseFragment<EcgPresenterImpl>
        implements EcgPresenter.View, View.OnClickListener {
    private static final int LEAD_OFF_LAYOUT = 0; //引导界面
    //    private static final int LEFF_OFF_LAYOUT = 0; //导联脱落
    private static final int ELECTRODE_OFF_LAYOUT = 7; //电极脱落

    private static final int ECG_LAYOUT = 1; //实时心电界面
    private static final int MEASURE_LAYOUT = 2; //测量界面
    private static final int OVER_LAYOUT = 3; //测量结束界面
    private static final int GUIDE_BEING = 4; //提示引导
    private static final int GUIDE_FINISH = 5; //完成引导
    private static final int GUIDE_STEP = 6; //引导步骤
    private static final int SAVE_ECG_WAVE = 101; //保存心电标记

    @InjectView(R.id.ecg_setAmplitude)
    DropEditText ecgSetAmplitude; //波幅设置
    @InjectView(R.id.ecg_set_lead)
    DropEditText ecgSetLead;
    @InjectView(R.id.ecg_set_velocity)
    DropEditText ecgSetVelocity;
    @InjectView(R.id.tv_measure_name)
    TextView tvMeasureName;
    @InjectView(R.id.tv_measure_max)
    TextView tvMeasureMax;
    @InjectView(R.id.tv_measure_min)
    TextView tvMeasureMin;
    @InjectView(R.id.tv_measure_unit)
    TextView tvMeasureUnit;
    @InjectView(R.id.tv_measure_value)
    TextView tvMeasureValue;
    @InjectView(R.id.tv_measure_template1)
    TextView tvMeasureTemplate1;
    @InjectView(R.id.tv_measure_template2)
    TextView tvMeasureTemplate2;
    @InjectView(R.id.tv_measure_template3)
    TextView tvMeasureTemplate3;
    @InjectView(R.id.btn_measure_template1)
    Button btnStartMeasure; //启动测量
    @InjectView(R.id.btn_measure_template2)
    Button btnHistoryData; //趋势统计
    @InjectView(R.id.btn_measure_template3)
    Button btnEcgViewReport; //查看报告
    @InjectView(R.id.ecg_wave)
    EcgViewFor12 ecgWave; //波形
    //    @InjectView(R.id.all_ecg_wave)
//    EcgViewFor12 allEcgWave; //全屏波形
    @InjectView(R.id.fl_ecg_content)
    FrameLayout layoutContain;
    @InjectView(R.id.ll_set_layout)
    LinearLayout llSetLayout; //心电设置布局
    @InjectView(R.id.ll_ecg_value)
    LinearLayout llEcgValue; //心电记录显示
    @InjectView(R.id.rl_ecg_measure_result)
    RelativeLayout rlEcgMeasureResult; //心电测量结果回显
    @InjectView(R.id.tv_ecg_result_hr)
    TextView tvHr;
    @InjectView(R.id.tv_ecg_result_pr)
    TextView tvPr;
    @InjectView(R.id.tv_ecg_result_p_qrs_t)
    TextView tvPQrsT; //PQrsT值
    @InjectView(R.id.tv_ecg_result_qrs)
    TextView tvQrs;
    @InjectView(R.id.tv_ecg_result_qt_qtc)
    TextView tvQtQtc;
    @InjectView(R.id.tv_ecg_result_rv5_sv1)
    TextView tvRv5SV1;
    @InjectView(R.id.tv_ecg_result_rv5Sv1Add)
    TextView tvRv5SV1Add;
    @InjectView(R.id.tv_ecg_result)
    TextView tvEcgResult; //心电测量结果
    @InjectView(R.id.root)
    RelativeLayout rootLayout;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (handler.hasMessages(0)) {
                    handler.removeMessages(0);
                }
            }
            if (msg.what == SAVE_ECG_WAVE) {
                byte[] data = (byte[]) msg.obj;
                presenter.saveWave(msg.arg1, data);
            }
        }
    };
    public AIDLServer aidlServer;
    private boolean isChecking; //是否测量
    private int countDown;
    private float zoom = 0.5f;
    private float pixelPerMm = (float) (1280 / 216.576 * zoom);
    private int currentLayout = LEAD_OFF_LAYOUT; //当前界面
    private int currentEcgLead = EcgDefine.ECG_12_LEAD; //当前导联默认12
    private boolean isEcgConnect; //心电是否连接
    private boolean isTimeOut; //是否超时
    private boolean isShowToast; // 是否要显示toast
    StatisticalDataDialog dataDialog;
    StatisticalDataAdapter statisticalTableAdapter;
    private MeasureDataBean measureBean;
    private PatientBean patient;
    //当前显示的界面
    private View currentView;

    /**
     * 心电引导界面持有者
     */
    private EcgTutorialHolder guideHolder;
    private EcgMeasureHolder ecgHolder;

    private WindowManager windowManager;

    private StatisticalDialogController statisticalController;
    private long lastClickTimestamp = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ecg_guide, container, false);
        ButterKnife.inject(this, view);
        bindEvent();
        initAdapter();
        ecgWave.setLayoutZoom(0.45f); //波形整体缩小0.5
        windowManager = (WindowManager) UiUtils.getContent()
                .getSystemService(Context.WINDOW_SERVICE);
        presenter.bindAidlService();
        //发送12导联指令。
        EchoServerEncoder.setEcgConfig(ProtocolDefine.NET_ECG_LEAD_SYSTEM, EcgDefine.ECG_12_LEAD);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        measureBean = new MeasureDataBean();
    }

    @Override
    public void onResume() {
        super.onResume();
        isEcgConnect = false;
        isChecking = false;
        initViewData();
        presenter.stopMeasure();
        patient = ProviderReader.readCurrentPatient(getActivity());

        if (MeasureActivity.probeEcgStatus != GlobalConstant.INVALID_DATA) {
            setEcgConnectStatus(MeasureActivity.probeEcgStatus);
        } else {
            refresh(LEAD_OFF_LAYOUT);
            refreshGuide(tvMeasureTemplate1, GUIDE_BEING);
            refreshGuide(tvMeasureTemplate2, GUIDE_STEP);
        }
    }

    /**
     * 初始化控件数据
     */
    private void initViewData() {

        if (ecgHolder == null) {
            ecgHolder = new EcgMeasureHolder(getActivity());
        }

        btnStartMeasure.setEnabled(false);
        tvMeasureTemplate3.setVisibility(View.GONE);
        tvMeasureTemplate1.setText(getString(R.string.please_lead_access));
        tvMeasureTemplate2.setText(getString(R.string.install_electrodes));
        tvMeasureName.setText(getString(R.string.ecg_hr));
        tvMeasureUnit.setText(getString(R.string.health_unit_bpm));
        ecgSetLead.setItemSelected(0); //现版本只有12导联功能
        ecgSetVelocity.setItemSelected(presenter.getConfigValue(EcgDefine.ECG_VELOCITY_SYSTEM));
        ecgSetAmplitude.setItemSelected(presenter.getConfigValue(EcgDefine.ECG_AMPLITUDE_SYSTEM));
        tvMeasureMax.setText(String.valueOf((int) ReferenceUtils.
                getMaxReference(KParamType.ECG_HR)));
        tvMeasureMin.setText(String.valueOf((int) ReferenceUtils.
                getMinReference(KParamType.ECG_HR)));
    }

    /**
     * 初始化测量数据
     */
    private void initMeasureData() {
        int hr = measureBean.getTrendValue(KParamType.ECG_HR);
        UiUtils.setMeasureResult(KParamType.ECG_HR, hr, tvMeasureName, tvMeasureValue, false);
    }

    /**
     * 绑定点击事件
     */
    private void bindEvent() {
        btnEcgViewReport.setVisibility(View.VISIBLE);
        btnEcgViewReport.setText(R.string.view_report);
        btnEcgViewReport.setEnabled(false);
        btnEcgViewReport.setOnClickListener(this);
        btnStartMeasure.setOnClickListener(this);
        btnHistoryData.setOnClickListener(this);
        ecgSetLead.setOnItemSelectedCallback(new DropEditText.OnItemSelectedCallback() {
            @Override
            public void onItemSelected(int position) {
//                presenter.saveConfigParam(EcgDefine.ECG_LEAD_SYSTEM, position);
//                ecgWave.setWaveSpeed();
            }
        });
        ecgSetVelocity.setOnItemSelectedCallback(new DropEditText.OnItemSelectedCallback() {
            @Override
            public void onItemSelected(int position) {
                presenter.saveConfigParam(EcgDefine.ECG_VELOCITY_SYSTEM, position);
                ecgWave.setWaveSpeed();
            }
        });
        ecgSetAmplitude.setOnItemSelectedCallback(new DropEditText.OnItemSelectedCallback() {
            @Override
            public void onItemSelected(int position) {
                presenter.saveConfigParam(EcgDefine.ECG_AMPLITUDE_SYSTEM, position);
                ecgWave.setWaveSpeed();
            }
        });
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        ecgSetAmplitude.setAdapter(presenter.getAmplitudeAdapter(UiUtils.getContent()));
        ecgSetLead.setAdapter(presenter.getLeadAdapter(UiUtils.getContent()));
        ecgSetVelocity.setAdapter(presenter.getVelocityAdapter(UiUtils.getContent()));
    }

    @Override
    public EcgPresenterImpl initPresenter() {
        return new EcgPresenterImpl(this);
    }

    @Override
    public void onClick(View v) {
        //预防点击过快
        long time = System.currentTimeMillis() - lastClickTimestamp;
        if (time < GlobalConstant.CLICK_TIMES && time > 0) {
            return;
        }
        lastClickTimestamp = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.btn_measure_template1: //启动测量
                if (!isChecking && isEcgConnect && currentEcgLead == EcgDefine.ECG_12_LEAD) {
                    //启动12导诊断
                    measureBean.initEcgWave();
                    presenter.startMeasure();
                    refresh(MEASURE_LAYOUT);
                    isChecking = true;
                    isShowToast = true;
                    MeasureActivity.isCheckingEcg = true;
                } else if (!isChecking && isEcgConnect) { //不在12导联的状态下就实时获取心率
                    isChecking = true;
                } else {
                    isChecking = false;
                    MeasureActivity.isCheckingEcg = false;
                }
                break;
            case R.id.btn_measure_template2: //趋势统计
                StatisticalDialogController statisticalController = new
                        StatisticalDialogController(getActivity(), presenter
                        .getStatisticalTableItem(), presenter.getStatisticalPic(getActivity()),
                        false);
                statisticalController.showDialog();
                break;
            case R.id.btn_measure_template3:

                final WindowManager.LayoutParams params = new WindowManager.
                        LayoutParams(WindowManager.LayoutParams.TYPE_PHONE);
                params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM; //可以获取back事件
                LayoutInflater flater = (LayoutInflater) UiUtils.getContent().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view = flater.inflate(R.layout.pop_ecg_report, null);
                ReportView reportView = new ReportView();
                ButterKnife.inject(reportView, view);
                initReportPatientData(reportView);
                initReportMeasureData(reportView);
                initReportEcgWave(reportView);
                windowManager.addView(view, params);
                reportView.llReortView.setOnKeyListener(new View.OnKeyListener() {

                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_BACK:
                                windowManager.removeView(view);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                reportView.btnStopReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        windowManager.removeView(view);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void refresh(int state) {
        currentLayout = state;
        switch (state) {
            case ECG_LAYOUT: //实时心电界面
                btnStartMeasure.setEnabled(true);
                ecgWave.setWaveSpeed();
                ecgHolder.remove(windowManager);
                rlEcgMeasureResult.setVisibility(View.GONE);
                ecgWave.setVisibility(View.VISIBLE);
                llSetLayout.setVisibility(View.VISIBLE);
                llEcgValue.setVisibility(View.VISIBLE);
                layoutContain.setVisibility(View.VISIBLE);
                if (currentView != null) {
                    layoutContain.removeView(currentView);
                    currentView = null;
                }
                break;
            case LEAD_OFF_LAYOUT: //导联脱落
                if (guideHolder == null) {
                    guideHolder = presenter.getInstallGuideView(getActivity(), layoutContain,
                            VideoUtil.getVideoListener(getActivity(),
                                    KParamType.ECG_HR));
                }
                if (currentView != guideHolder.view) {
                    layoutContain.removeView(currentView);
                    currentView = guideHolder.view;
                    layoutContain.addView(currentView);
                }
                guideHolder.setViewStatus(EcgTutorialHolder.STATUS_CONNECT);
                ecgHolder.remove(windowManager);
                ecgWave.setVisibility(View.GONE);
                rlEcgMeasureResult.setVisibility(View.GONE);
                llSetLayout.setVisibility(View.VISIBLE);
                layoutContain.setVisibility(View.VISIBLE);
                llEcgValue.setVisibility(View.VISIBLE);
                break;
            case ELECTRODE_OFF_LAYOUT:
                isEcgConnect = false;
                if (guideHolder == null) {
                    guideHolder = presenter.getInstallGuideView(getActivity(), layoutContain,
                            VideoUtil.getVideoListener(getActivity(),
                                    KParamType.ECG_HR));
                }
                if (currentView != guideHolder.view) {
                    layoutContain.removeView(currentView);
                    currentView = guideHolder.view;
                    layoutContain.addView(currentView);
                }
                guideHolder.setViewStatus(EcgTutorialHolder.STATUS_INSTALL);
                ecgHolder.remove(windowManager);
                ecgWave.setVisibility(View.GONE);
                rlEcgMeasureResult.setVisibility(View.GONE);
                llSetLayout.setVisibility(View.VISIBLE);
                layoutContain.setVisibility(View.VISIBLE);
                llEcgValue.setVisibility(View.VISIBLE);
                break;
            case MEASURE_LAYOUT: //测量界面
                ecgHolder.ecgView.setWaveSpeed();
                if (currentView != null) {
                    layoutContain.removeView(currentView);
                    currentView = null;
                }
                ecgWave.setVisibility(View.GONE);
                rlEcgMeasureResult.setVisibility(View.GONE);
                llSetLayout.setVisibility(View.GONE);
                llEcgValue.setVisibility(View.GONE);
                layoutContain.setVisibility(View.GONE);
                ecgHolder.addToWindow(windowManager);
                break;
            case OVER_LAYOUT: //测量结束界面
                ecgHolder.remove(windowManager);
                ecgWave.setVisibility(View.GONE);
                if (currentView != null) {
                    layoutContain.removeView(currentView);
                    currentView = null;
                }
                btnEcgViewReport.setEnabled(true);
                rlEcgMeasureResult.setVisibility(View.VISIBLE);
                layoutContain.setVisibility(View.VISIBLE);
                llSetLayout.setVisibility(View.VISIBLE);
                llEcgValue.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void measureSuccess(int ecgHr, MeasureEcgBean bean) {
        refresh(OVER_LAYOUT);
        isChecking = false;
        MeasureActivity.isCheckingEcg = false;
        String limit = UiUtils.getString(R.string.unit_limit);
        String mv = UiUtils.getString(R.string.unit_mv);
        String ms = UiUtils.getString(R.string.unit_ms);
        String bpm = UiUtils.getString(R.string.health_unit_bpm);
        tvPr.setText(bean.getPr() + ms);
        tvHr.setText(ecgHr + bpm);
        tvQrs.setText(bean.getQrs() + ms);
        tvQtQtc.setText(bean.getQtQtc() + ms);
        tvPQrsT.setText(bean.getpQrsT() + limit);
        tvRv5SV1.setText(bean.getRv5Sv1() + mv);
        tvRv5SV1Add.setText(bean.getRv5Sv1Add() + mv);
        tvEcgResult.setText(bean.getResult());

        UiUtils.setMeasureResult(KParamType.ECG_HR, ecgHr * GlobalConstant.TREND_FACTOR,
                tvMeasureName, tvMeasureValue, false);
    }

    @Override
    public void measureError() {
        isChecking = false;
        MeasureActivity.isCheckingEcg = false;
        refresh(ECG_LAYOUT);
    }

    @Override
    public void refreshGuide(TextView tv, int state) {
        switch (state) {
            case GUIDE_BEING: //提示引导
                tv.setTextSize(30);
                tv.setTextColor(getResources().getColor(R.color.measure_name_text_color));
                Drawable dra = getResources().getDrawable(R.mipmap.ic_doing);
                dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
                tv.setCompoundDrawables(dra, null, null, null);

                break;
            case GUIDE_FINISH: //已完成引导
                tv.setTextSize(18);
                tv.setTextColor(getResources().getColor(R.color.gray));
                Drawable dra2 = getResources().getDrawable(R.mipmap.ic_finished);
                dra2.setBounds(0, 0, dra2.getMinimumWidth(), dra2.getMinimumHeight());
                tv.setCompoundDrawables(dra2, null, null, null);
                break;
            case GUIDE_STEP: //引导步骤
                tv.setTextSize(18);
                tv.setTextColor(getResources().getColor(R.color.gray));
                Drawable dra3 = getResources().getDrawable(R.mipmap.ic_step2);
                dra3.setBounds(0, 0, dra3.getMinimumWidth(), dra3.getMinimumHeight());
                tv.setCompoundDrawables(dra3, null, null, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void addEcgWaveData(int param, int data) {
        //绘制心电波形
        if (currentLayout == ECG_LAYOUT) {
            ecgWave.addEcgData(param, data);
        } else if (currentLayout == MEASURE_LAYOUT) {
            ecgHolder.addEcgData(param, data);
        }
    }

    @Override
    public void setEcgConnectStatus(int leadOff) {
        //规避三导联时，提示导联线脱落问题
//        boolean ecg3Lead = currentEcgLead == EcgDefine.ECG_3_LEAD
//                && leadOff == KParamType.ECG_FALL_OFF;
        switch (leadOff) {
            case 0: //连接正常
                if (!isEcgConnect) {
                    if (currentView != null) {
                        layoutContain.removeView(currentView);
                        currentView = null;
                    }
                    refresh(ECG_LAYOUT);
                    btnStartMeasure.setEnabled(true);
                    isEcgConnect = true;
                    tvMeasureTemplate1.setText(UiUtils.getString(R.string.please_lead_access));
                    tvMeasureTemplate2.setText(UiUtils.getString(R.string.install_electrodes));
                    refreshGuide(tvMeasureTemplate1, GUIDE_FINISH);
                    refreshGuide(tvMeasureTemplate2, GUIDE_FINISH);
                }
                break;
            case GlobalConstant.INVALID_DATA:
                //服务没启动
                presenter.stopMeasure();
                btnStartMeasure.setEnabled(false);
                isEcgConnect = false;
                isChecking = false;
                MeasureActivity.isCheckingEcg = false;
                refresh(LEAD_OFF_LAYOUT);
                if (isShowToast) {
                    ToastUtils.showShortToast(R.string.please_start_service);
                    isShowToast = false;
                }
                break;
            case 511: //导联脱落
                presenter.stopMeasure();
                btnStartMeasure.setEnabled(false);
                isEcgConnect = false;
                isChecking = false;
                MeasureActivity.isCheckingEcg = false;
                refresh(LEAD_OFF_LAYOUT);
                refreshGuide(tvMeasureTemplate1, GUIDE_BEING);
                refreshGuide(tvMeasureTemplate2, GUIDE_STEP);
                tvMeasureTemplate2.setText(UiUtils.getString(R.string.install_electrodes));
                break;
            default: //电极脱落
                if (guideHolder != null) {
                    guideHolder.setLeadOffSignal(leadOff);
                }
                presenter.stopMeasure();
                btnStartMeasure.setEnabled(false);
                isEcgConnect = false;
                isChecking = false;
                MeasureActivity.isCheckingEcg = false;
                refresh(ELECTRODE_OFF_LAYOUT);
                refreshGuide(tvMeasureTemplate1, GUIDE_FINISH);
                refreshGuide(tvMeasureTemplate2, GUIDE_BEING);
                tvMeasureTemplate2.setText(UiUtils.getString(R.string.install_electrodes));
                if (currentEcgLead == EcgDefine.ECG_12_LEAD) {
                    btnStartMeasure.setText(UiUtils.getString(R.string.start_measure));
                } else {
                    btnStartMeasure.setText(UiUtils.getString(R.string.get_hr));
                }
                break;
        }
    }

    @Override
    public void saveEcgWave(int param, byte[] bytes) {
        //测量状态下，保存心电波形
        if (isChecking && isEcgConnect) {
            Message msg = Message.obtain();
            msg.what = SAVE_ECG_WAVE;
            msg.arg1 = param;
            msg.obj = bytes;
            //发送数据到Handler保存
            handler.sendMessage(msg);
        }
    }

    @Override
    public void setMeasureDataBean(MeasureDataBean measureDataBean) {
        this.measureBean = measureDataBean;
        initMeasureData();
        if (measureDataBean.getTrendValue(KParamType.ECG_HR) != GlobalConstant.INVALID_DATA) {
            btnEcgViewReport.setEnabled(true);
        } else {
            btnEcgViewReport.setEnabled(false);
        }
    }

    /**
     * 初始化病人信息
     * @param holder 体检报告控件容器
     */
    private void initReportPatientData(ReportView holder) {
        if (patient != null) {
            holder.tvReportAge.setText(patient.getAge() + UiUtils.getString(R.string.age));
            holder.tvReportName.setText(patient.getName());
            holder.tvReportIdcard.setText(UiUtils.getString(R.string.idcard_colon)
                    + patient.getIdCard());
            SimpleDateFormat dateFormat = UiUtils.getDateFormat(UiUtils.DateState.LONG);
            String measureTime = dateFormat.format(measureBean.getMeasureTime());
            holder.tvReportMeasureTime.setText(UiUtils.getString(R.string.measure_time_colon)
                    + measureTime);
            holder.tvReportSex.setText(UiUtils.getSexString(patient.getSex()));
        }
    }

    /**
     * 初始化测量数据
     * @param holder 体检报告控件容器
     */
    private void initReportMeasureData(ReportView holder) {
        if (measureBean != null) {
            String divide = UiUtils.getString(R.string.unit_divide);
            String ms = UiUtils.getString(R.string.unit_ms);
            String limit = UiUtils.getString(R.string.unit_limit);
            String mv = UiUtils.getString(R.string.unit_mv);
            String qtQtc = measureBean.getQt() + divide + measureBean.getQtc() + ms;
            String pQrsT = measureBean.getpAxis() + divide + measureBean.getQrsAxis() + divide +
                    measureBean.gettAxis() + limit;
            holder.tvReportHr.setText(measureBean.getTrendValue(KParamType.ECG_HR)
                    / GlobalConstant.TREND_FACTOR + UiUtils.getString(R.string.health_unit_bpm));
            holder.tvReportPr.setText(measureBean.getPr() + ms);
            holder.tvReportQrs.setText(measureBean.getQrs()
                    + UiUtils.getString(R.string.unit_ms));
            holder.tvReportQtqtc.setText(qtQtc);
            holder.tvReportPQrsT.setText(pQrsT);
            //单位为0.01mV.
            float rv5 = (float) measureBean.getRv5() / GlobalConstant.FACTOR;
            float sv1 = (float) measureBean.getSv1() / GlobalConstant.FACTOR;
            String rv5Sv1 = String.format("%.2f", rv5) + divide + String.format("%.2f", sv1) + mv;
            holder.tvReportRv5Sv1.setText(rv5Sv1);
            holder.tvRV5SV1Add.setText(String.format("%.2f", rv5 + sv1) + mv);
            holder.tvReportResult.setText(measureBean.getEcgDiagnoseResult());
        }
    }

    /**
     * 初始化心电波形
     * @param holder 体检报告控件容器
     */
    private void initReportEcgWave(ReportView holder) {
        //获取全屏的宽高
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        final int width = displayMetrics.widthPixels;
        final int height = displayMetrics.heightPixels;
        EcgReportDrawable drawable = new EcgReportDrawable(measureBean, height, width);
        // 1280屏幕宽度，575控件高度
        Bitmap bitmap = Bitmap.createBitmap(1280, 575, drawable.getOpacity() != PixelFormat.OPAQUE ?
                Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, 1280, 575);
        drawable.draw(canvas);
        holder.imgReport.setImageBitmap(bitmap);
    }

    /**
     * 体检报告控件容器
     */
    public class ReportView {

        @InjectView(R.id.ll_report_view)
        LinearLayout llReortView;
        @InjectView(R.id.tv_report_name)
        TextView tvReportName;
        @InjectView(R.id.tv_report_sex)
        TextView tvReportSex;
        @InjectView(R.id.tv_report_age)
        TextView tvReportAge;
        @InjectView(R.id.tv_report_idcard)
        TextView tvReportIdcard;
        @InjectView(R.id.tv_report_measure_time)
        TextView tvReportMeasureTime;
        @InjectView(R.id.btn_stop_report)
        Button btnStopReport;
        @InjectView(R.id.tv_report_hr)
        TextView tvReportHr;
        @InjectView(R.id.tv_report_pr)
        TextView tvReportPr;
        @InjectView(R.id.tv_report_qrs)
        TextView tvReportQrs;
        @InjectView(R.id.tv_report_qtqtc)
        TextView tvReportQtqtc;
        @InjectView(R.id.tv_report_pQrsT)
        TextView tvReportPQrsT;
        @InjectView(R.id.tv_report_rv5Sv1)
        TextView tvReportRv5Sv1;
        @InjectView(R.id.tv_report_result)
        TextView tvReportResult;
        @InjectView(R.id.fl_report_ecg_wave)
        FrameLayout reportEcgWave;
        @InjectView(R.id.tv_report_RV5SV1Add)
        TextView tvRV5SV1Add;
        @InjectView(R.id.img_report)
        PhotoView imgReport;
    }

    @Override
    public void onPause() {
        super.onPause();
        layoutContain.removeView(currentView);
        if (guideHolder != null) {
            guideHolder.guideView.stop();
        }
        currentView = null;
        guideHolder = null;
        isEcgConnect = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isChecking = false;
        MeasureActivity.isCheckingEcg = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        isChecking = false;
        presenter.stopMeasure();
    }
}
