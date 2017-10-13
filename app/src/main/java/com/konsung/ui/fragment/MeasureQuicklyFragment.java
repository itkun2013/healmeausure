package com.konsung.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.data.ProviderReader;
import com.konsung.exception.ReferenceValueException;
import com.konsung.network.EchoServerEncoder;
import com.konsung.presenter.QuicklyPresenter;
import com.konsung.presenter.impl.QuicklyPresenterImpl;
import com.konsung.ui.activity.MeasureActivity;
import com.konsung.ui.base.BaseFragment;
import com.konsung.ui.defineview.RoundProgressBar;
import com.konsung.ui.defineview.WaveFormSpo2;
import com.konsung.ui.holder.QuickHeightHolder;
import com.konsung.utils.KParamType;
import com.konsung.utils.ProtocolDefine;
import com.konsung.utils.ReferenceUtils;
import com.konsung.utils.TextWatcherGenerator;
import com.konsung.utils.ToastUtils;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 快速检测测量页面
 **/
public class MeasureQuicklyFragment extends BaseFragment<QuicklyPresenterImpl>
        implements QuicklyPresenter.View, View.OnClickListener {
    private static final int LAYOUT_ZERO = 0; //百捷三合一+血红蛋白
    private static final int LAYOUT_ONE = 1; //百捷三合一+身高体重
    private static final int LAYOUT_TWO = 2; //单血糖+血红蛋白+身高体重
    private static final int LAYOUT_THREE = 3; //单血糖+血脂
    private static final int AFTER_GLU = 0; //餐后血糖
    private static final int BEFOR_GLU = 1; //空腹血糖
    public static final int DEFAULT_TIME = 20; //默认血氧测量倒计时20秒

    @InjectView(R.id.tv_bp_stress)
    TextView tvBpStress;
    @InjectView(R.id.tv_cuff)
    TextView tvCuff;
    @InjectView(R.id.tv_sys_value)
    TextView tvSysValue;
    @InjectView(R.id.tv_nibp_slash)
    TextView tvNibpSlash;
    @InjectView(R.id.tv_dia_value)
    TextView tvDiaValue;
    @InjectView(R.id.tv_sbp_reference)
    TextView tvSbpReference;
    @InjectView(R.id.tv_dbp_reference)
    TextView tvDbpReference;
    @InjectView(R.id.btn_bp_start_measure)
    Button btnBpStartMeasure;
    @InjectView(R.id.tv_spo2_max)
    TextView tvSpo2Max;
    @InjectView(R.id.tv_spo2_min)
    TextView tvSpo2Min;
    @InjectView(R.id.tv_spo2_value)
    TextView tvSpo2Value;
    @InjectView(R.id.tv_spo2_count_down)
    TextView tvSpo2CountDown;
    @InjectView(R.id.progress_bar_spo2)
    RoundProgressBar progressBarSpo2;
    @InjectView(R.id.tv_pr_max)
    TextView tvPrMax;
    @InjectView(R.id.tv_pr_min)
    TextView tvPrMin;
    @InjectView(R.id.tv_pr_value)
    TextView tvPrValue;
    @InjectView(R.id.btn_spo2_start_measure)
    Button btnSpo2StartMeasure;
    @InjectView(R.id.spo2_wave)
    WaveFormSpo2 spo2Wave;
    @InjectView(R.id.tv_temp_max)
    TextView tvTempMax;
    @InjectView(R.id.tv_temp_min)
    TextView tvTempMin;
    @InjectView(R.id.tv_temp_value)
    TextView tvTempValue;
    @InjectView(R.id.tv_leu_value)
    TextView tvLeuValue;
    @InjectView(R.id.tv_nit_value)
    TextView tvNitValue;
    @InjectView(R.id.tv_ubg_value)
    TextView tvUbgValue;
    @InjectView(R.id.tv_pro_value)
    TextView tvProValue;
    @InjectView(R.id.tv_ph_value)
    TextView tvPhValue;
    @InjectView(R.id.tv_ph_min_max)
    TextView tvPhMinMax;
    @InjectView(R.id.tv_sg_value)
    TextView tvSgValue;
    @InjectView(R.id.tv_sg_min_max)
    TextView tvSgMinMax;
    @InjectView(R.id.tv_bld_value)
    TextView tvBldValue;
    @InjectView(R.id.tv_ket_value)
    TextView tvKetValue;
    @InjectView(R.id.tv_bil_value)
    TextView tvBilValue;
    @InjectView(R.id.tv_urine_glu_value)
    TextView tvUrineGluValue;
    @InjectView(R.id.tv_uri_bld_name)
    TextView tvBldName;
    @InjectView(R.id.tv_vc_name)
    TextView tvVcName;
    @InjectView(R.id.tv_vc_value)
    TextView tvVcValue;
    @InjectView(R.id.tv_ma_value)
    TextView tvMaValue;
    @InjectView(R.id.tv_ma_reference)
    TextView tvMaReference;

    @InjectView(R.id.tr_urine_ma)
    TableRow trUrineMa;
    @InjectView(R.id.tv_cr_value)
    TextView tvCrValue;
    @InjectView(R.id.tv_cr_reference)
    TextView tvCrReference;

    @InjectView(R.id.tr_urine_cr)
    TableRow trUrineCr;

    @InjectView(R.id.tv_ca_value)
    TextView tvCaValue;
    @InjectView(R.id.tv_ca_reference)
    TextView tvCaReference;

    @InjectView(R.id.tr_urine_ca)
    TableRow trUrineCa;
    @InjectView(R.id.tr_urine_ac)
    TableRow trUrineAc;

    @InjectView(R.id.tv_ac_value)
    TextView tvAcValue;
    @InjectView(R.id.tv_ac_reference)
    TextView tvAcReference;

    @InjectView(R.id.tv_bene_before_meal)
    TextView tvBeneBeforeMeal;
    @InjectView(R.id.tv_bene_after_meal)
    TextView tvBeneAfterMeal;
    @InjectView(R.id.tv_before_glu)
    TextView tvBeforeGlu;
    @InjectView(R.id.tv_after_glu)
    TextView tvAfterGlu;
    @InjectView(R.id.v_before_meal)
    View vBeforeMeal;
    @InjectView(R.id.v_after_meal)
    View vAfterMeal;
    @InjectView(R.id.tv_bene_glu_max)
    TextView tvBeneGluMax;
    @InjectView(R.id.tv_bene_glu_min)
    TextView tvBeneGluMin;
    @InjectView(R.id.tv_bene_glu_value)
    TextView tvBeneGluValue;
    @InjectView(R.id.tv_bene_ua_max)
    TextView tvBeneUaMax;
    @InjectView(R.id.tv_bene_ua_min)
    TextView tvBeneUaMin;
    @InjectView(R.id.tv_bene_ua_value)
    TextView tvBeneUaValue;
    @InjectView(R.id.tv_bene_total_cho_max)
    TextView tvBeneChoMax;
    @InjectView(R.id.tv_bene_total_cho_min)
    TextView tvBeneChoMin;
    @InjectView(R.id.tv_bene_total_cho_value)
    TextView tvBeneTotalChoValue;
    @InjectView(R.id.tv_hb_max)
    TextView tvHbMax;
    @InjectView(R.id.tv_hb_min)
    TextView tvHbMin;
    @InjectView(R.id.tv_hb_value)
    TextView tvHbValue;
    @InjectView(R.id.tv_htc_max)
    TextView tvHctMax;
    @InjectView(R.id.tv_htc_min)
    TextView tvHctMin;
    @InjectView(R.id.tv_htc_value)
    TextView tvHctValue;
    //        @InjectView(R.id.btn_save_height)
//    Button btnSaveHeight;
//    @InjectView(R.id.et_height_value)
//    EditText etHeightValue;
    @InjectView(R.id.v_before_glu)
    View vBeforeGlu;
    @InjectView(R.id.v_after_glu)
    View vAfterGlu;
    @InjectView(R.id.tv_glu_max)
    TextView tvGluMax;
    @InjectView(R.id.tv_glu_min)
    TextView tvGluMin;
    @InjectView(R.id.tv_glu_value)
    TextView tvGluValue;
    @InjectView(R.id.tv_max_total_cho)
    TextView tvMaxTotalCho;
    @InjectView(R.id.tv_min_total_cho)
    TextView tvMinTotalCho;
    @InjectView(R.id.tv_total_cho_value)
    TextView tvTotalChoValue;
    @InjectView(R.id.tv_max_trig)
    TextView tvMaxTrig;
    @InjectView(R.id.tv_min_trig)
    TextView tvMinTrig;
    @InjectView(R.id.tv_trig_value)
    TextView tvTrigValue;
    @InjectView(R.id.tv_max_hdl)
    TextView tvMaxHdl;
    @InjectView(R.id.tv_min_hdl)
    TextView tvMinHdl;
    @InjectView(R.id.tv_hdl_value)
    TextView tvHdlValue;
    @InjectView(R.id.tv_max_ldl)
    TextView tvMaxLdl;
    @InjectView(R.id.tv_min_ldl)
    TextView tvMinLdl;
    @InjectView(R.id.tv_ldl_value)
    TextView tvLdlValue;
    @InjectView(R.id.ll_group_layout)
    LinearLayout llGroupLayout;
    @InjectView(R.id.ll_bene_layout)
    LinearLayout llBeneLayout;
    @InjectView(R.id.ll_hb_layout)
    LinearLayout llHbLayout;
    @InjectView(R.id.ll_height_layout)
    LinearLayout llHeightLayout;
    @InjectView(R.id.ll_weight_layout)
    LinearLayout llWeightLayout;
    @InjectView(R.id.ll_glu_layout)
    LinearLayout llGluLayout;
    @InjectView(R.id.ll_blood_fat_layout)
    LinearLayout llBloodFatLayout;
    @InjectView(R.id.ll_nibp)
    LinearLayout llNibp;
    @InjectView(R.id.ll_spo2)
    LinearLayout llSpo2;
    @InjectView(R.id.ll_temp)
    LinearLayout llTemp;
    @InjectView(R.id.ll_urt)
    LinearLayout llUrt;

    @InjectView(R.id.include_height)
    FrameLayout includeHeight;
    @InjectView(R.id.include_weight)
    FrameLayout includeWeight;

    private View view;
    private MeasureDataBean measureBean;
    private int currentGlu = BEFOR_GLU;
    private boolean nibpChecking; //是否启动血压测量
    private boolean spo2Checking; //是否启动血氧测量

    //血压异常次数，当异常为2时，分割线变红
    private int nibpErrTimes = 0;

    private int deviceConfig = 0; //设备配置项
    private int measureConfig = 0; //身高体重是否配置，配置项
    private int urineConfig = 0;

    private QuickHeightHolder heightHolder;
    private QuickHeightHolder weightHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_measure_quickly, container, false);
        ButterKnife.inject(this, view);
//        refreshLayout(LAYOUT_ZERO);
        initEvent();
        initReferenceValue();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setLayoutVisiable();
        initWeightLayout();
        initHeightLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.bindAidlService();

        initView();
        setSpo2BtnStatus();
    }

    /**
     * 初始化体重布局
     */
    private void initWeightLayout() {
        weightHolder = new QuickHeightHolder(includeWeight);
        weightHolder.tvName.setText(getString(R.string.weight));
        weightHolder.tvUnit.setText(UiUtils.getValueUnit(KParamType.WEIGHT));

        weightHolder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueStr = weightHolder.etValue.getText().toString();
                if (!valueStr.contains(".") && UiUtils.isConSpeCharacters(valueStr)) {
                    weightHolder.etValue.setText("");
                    Toast.makeText(UiUtils.getContent(), UiUtils.getString(
                            R.string.pls_input_weight), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (valueStr.length() > 0 && !valueStr.equals("0")) {
                    Float valueF = Float.valueOf(valueStr);
                    int weight = (int) (valueF * GlobalConstant.TREND_FACTOR);
                    presenter.saveDb(KParamType.WEIGHT, weight);
                    //清除焦点
                    weightHolder.etValue.clearFocus();
                    hideKeyboard(weightHolder.etValue);
                    Toast.makeText(UiUtils.getContent(), UiUtils.getString(
                            R.string.save_success), Toast.LENGTH_SHORT).show();
                }
            }
        });
        weightHolder.etValue.setText(R.string.invalid_data);
        weightHolder.etValue.addTextChangedListener(TextWatcherGenerator.getTextWatcher(
                KParamType.WEIGHT, (int) ReferenceUtils.getMaxReference(KParamType.WEIGHT),
                weightHolder.btnSave));
        weightHolder.etValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    weightHolder.etValue.setText("");
                }
            }
        });
    }

    /**
     * 初始化身高布局
     */
    private void initHeightLayout() {
        heightHolder = new QuickHeightHolder(includeHeight);
        heightHolder.etValue.addTextChangedListener(TextWatcherGenerator.getTextWatcher(
                KParamType.HEIGHT, (int) ReferenceUtils.getMaxReference(KParamType.HEIGHT),
                heightHolder.btnSave));
        heightHolder.etValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    heightHolder.etValue.setText("");
                }
            }
        });
        heightHolder.etValue.setText(R.string.invalid_data);
        heightHolder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueStr = heightHolder.etValue.getText().toString();
                if (!valueStr.contains(".") && UiUtils.isConSpeCharacters(valueStr)) {
                    heightHolder.etValue.setText("");
                    Toast.makeText(UiUtils.getContent(), UiUtils.getString(
                            R.string.pls_input_height), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (valueStr.length() > 0 && !valueStr.equals("0")) {
                    Float valueF = Float.valueOf(valueStr);
                    int weight = (int) (valueF * GlobalConstant.TREND_FACTOR);
                    presenter.saveDb(KParamType.HEIGHT, weight);
                    //清除焦点
                    heightHolder.etValue.clearFocus();
                    hideKeyboard(heightHolder.etValue);
                    Toast.makeText(UiUtils.getContent(), UiUtils.getString(
                            R.string.save_success), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 初始化尿常规11-14项
     */
    private void initUrine() {
        urineConfig = ProviderReader.getUrtConfig(getActivity());
        //第一位11项
        if ((urineConfig & (0x01 << 0)) != 0) {
            trUrineMa.setVisibility(View.GONE);
            trUrineCa.setVisibility(View.GONE);
            trUrineAc.setVisibility(View.GONE);
            trUrineCr.setVisibility(View.GONE);
        } else if ((urineConfig & (0x01 << 1)) != 0) {
            //第一位11+2项
            trUrineMa.setVisibility(View.VISIBLE);
            trUrineAc.setVisibility(View.VISIBLE);
            trUrineCa.setVisibility(View.GONE);
            trUrineCr.setVisibility(View.VISIBLE);
        } else {
            //其它默认显示14项
            trUrineMa.setVisibility(View.VISIBLE);
            trUrineCa.setVisibility(View.VISIBLE);
            trUrineAc.setVisibility(View.GONE);
            trUrineCr.setVisibility(View.VISIBLE);
        }
        int config = ProviderReader.getDeviceConfig(getActivity());
        if ((config & (0x01 << 10)) != 0) {
            tvBldName.setText(R.string.bld_u120);
            tvVcName.setText(R.string.vc_u120);
        } else {
            tvBldName.setText(R.string.bld);
            tvVcName.setText(R.string.vc);
        }
    }

    /**
     * 处理页面显示逻辑
     */
    private void setLayoutVisiable() {
        int config = ProviderReader.getDeviceConfig(getActivity());
        deviceConfig = config;
        int page = ProviderReader.getQuickCheckConfig(getActivity());
        measureConfig = ProviderReader.getHeightWeightConfig(getActivity());
        initUrine();
        //先判断显示哪个页面
        for (int i = 0; i < 4; i++) {
            if ((page & (0x01 << i)) != 0) {
                refreshLayout(i);
            }
        }

        if ((config & (0x01 << 1)) != 0 || (config & (0x01 << 9)) != 0
                || (config & (0x01 << 10)) != 0) {
            llUrt.setVisibility(View.VISIBLE);
        } else {
            llUrt.setVisibility(View.GONE);
        }
        //体温
        if ((config & (0x01 << 4)) != 0 || (config & (0x01 << 5)) != 0) {
            llTemp.setVisibility(View.VISIBLE);
        } else {
            llTemp.setVisibility(View.GONE);
        }

        //多合一
        if ((deviceConfig & (0x01 << 7)) != 0) {
            llGluLayout.setVisibility(View.GONE);
            llBeneLayout.setVisibility(View.VISIBLE);
        }
        //再判断显示百捷三合一还是单血糖
        if ((deviceConfig & (0x01 << 2)) != 0 || (deviceConfig & (0x01 << 6)) != 0
                || (deviceConfig & (0x01 << 8)) != 0) {
            llGluLayout.setVisibility(View.VISIBLE);
            llBeneLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        tvBeneAfterMeal.setOnClickListener(this);
        tvBeneBeforeMeal.setOnClickListener(this);
        tvBeforeGlu.setOnClickListener(this);
        tvAfterGlu.setOnClickListener(this);
        btnBpStartMeasure.setOnClickListener(this);
        btnSpo2StartMeasure.setOnClickListener(this);
    }

    /**
     * 初始化参考值
     */
    private void initReferenceValue() {
        tvBeneGluMax.setText(ReferenceUtils.getMaxReference(KParamType.BLOODGLU_BEFORE_MEAL) + "");
        tvBeneGluMin.setText(ReferenceUtils.getMinReference(KParamType.BLOODGLU_BEFORE_MEAL) + "");

        String sys = UiUtils.getString(R.string.nibp_sys) + " ";
        String dia = UiUtils.getString(R.string.nibp_dia) + " ";
        tvSbpReference.setText(sys + ReferenceUtils.getReferenceRange(KParamType.NIBP_SYS));
        tvDbpReference.setText(dia + ReferenceUtils.getReferenceRange(KParamType.NIBP_DIA));
        tvSpo2Max.setText((int) ReferenceUtils.getMaxReference(KParamType.SPO2_TREND) + "");
        tvSpo2Min.setText((int) ReferenceUtils.getMinReference(KParamType.SPO2_TREND) + "");
        tvPrMax.setText((int) ReferenceUtils.getMaxReference(KParamType.SPO2_PR) + "");
        tvPrMin.setText((int) ReferenceUtils.getMinReference(KParamType.SPO2_PR) + "");
        tvTempMax.setText(ReferenceUtils.getMaxReference(KParamType.IRTEMP_TREND) + "");
        tvTempMin.setText(ReferenceUtils.getMinReference(KParamType.IRTEMP_TREND) + "");
        tvPhMinMax.setText(ReferenceUtils.getReferenceRange(KParamType.URINERT_PH));
        tvSgMinMax.setText(ReferenceUtils.getReferenceRange(KParamType.URINERT_SG));
        tvMaReference.setText(ReferenceUtils.getReferenceRangeInQuickCheck(KParamType.URINERT_ALB));
        tvCrReference.setText(ReferenceUtils.getReferenceRangeInQuickCheck(KParamType.URINERT_CRE));
        tvCaReference.setText(ReferenceUtils.getReferenceRangeInQuickCheck(KParamType.URINERT_CA));
        tvAcReference.setText(ReferenceUtils.getReferenceRangeInQuickCheck(KParamType.URINE_AC));
        String uaMax = String.format(getString(R.string.rule_limit_2_after_point),
                ReferenceUtils.getMaxReference(KParamType.URICACID_TREND));
        String uaMin = String.format(getString(R.string.rule_limit_2_after_point),
                ReferenceUtils.getMinReference(KParamType.URICACID_TREND));
        String choMax = String.format(getString(R.string.rule_limit_2_after_point),
                ReferenceUtils.getMaxReference(KParamType.CHOLESTEROL_TREND));
        String choMin = String.format(getString(R.string.rule_limit_2_after_point),
                ReferenceUtils.getMinReference(KParamType.CHOLESTEROL_TREND));
        tvBeneUaMax.setText(uaMax);
        tvBeneUaMin.setText(uaMin);
        tvBeneChoMax.setText(choMax);
        tvBeneChoMin.setText(choMin);
        tvHbMax.setText((int) ReferenceUtils.getMaxReference(KParamType.BLOOD_HGB) + "");
        tvHbMin.setText((int) ReferenceUtils.getMinReference(KParamType.BLOOD_HGB) + "");
        tvHctMax.setText((int) ReferenceUtils.getMaxReference(KParamType.BLOOD_HCT) + "");
        tvHctMin.setText((int) ReferenceUtils.getMinReference(KParamType.BLOOD_HCT) + "");
        tvGluMax.setText(ReferenceUtils.getMaxReference(KParamType.BLOODGLU_BEFORE_MEAL) + "");
        tvGluMin.setText(ReferenceUtils.getMinReference(KParamType.BLOODGLU_BEFORE_MEAL) + "");
        tvMaxTrig.setText(String.format("%.2f",
                ReferenceUtils.getMaxReference(KParamType.BLOOD_FAT_TRIG)));
        tvMinTrig.setText(ReferenceUtils.getMinReference(KParamType.BLOOD_FAT_TRIG) + "");
        tvMaxLdl.setText(ReferenceUtils.getMaxReference(KParamType.BLOOD_FAT_LDL) + "");
        tvMinLdl.setText(ReferenceUtils.getMinReference(KParamType.BLOOD_FAT_LDL) + "");
        tvMaxHdl.setText(ReferenceUtils.getMaxReference(KParamType.BLOOD_FAT_HDL) + "");
        tvMinHdl.setText(ReferenceUtils.getMinReference(KParamType.BLOOD_FAT_HDL) + "");
        tvMaxTotalCho.setText(ReferenceUtils.getMaxReference(KParamType.BLOOD_FAT_CHO) + "");
        tvMinTotalCho.setText(ReferenceUtils.getMinReference(KParamType.BLOOD_FAT_CHO) + "");
    }

    /**
     * 初始化布局
     */
    private void initView() {
        initViewData();
    }

    /**
     * 初始化导入的控件
     */
    private void initViewData() {
        tvCuff.setVisibility(View.GONE);
        tvBpStress.setVisibility(View.GONE);
        tvBpStress.setText(UiUtils.getString(R.string.zero));
        spo2Wave.setSampleRate(125); // 抽样率
        spo2Wave.setRatio(0.35f); // 降低波形高度，设配缩小版波形
        spo2Wave.reset();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_after_glu:
            case R.id.tv_bene_after_meal:
                setGlu(AFTER_GLU);
                break;
            case R.id.tv_before_glu:
            case R.id.tv_bene_before_meal:
                setGlu(BEFOR_GLU);
                break;
            case R.id.btn_bp_start_measure: //血压启动测量
                if (!nibpChecking) {
                    btnBpStartMeasure.setBackgroundResource(R.drawable.btn_selector_orange);
                    btnSpo2StartMeasure.setEnabled(false);
                    btnBpStartMeasure.setText(UiUtils.getString(R.string.stop_measure));
                    tvCuff.setVisibility(View.VISIBLE);
                    tvBpStress.setVisibility(View.VISIBLE);
                    tvSysValue.setText(R.string.invalid_data);
                    tvSysValue.setTextColor(ContextCompat.getColor(getActivity(),
                            R.color.value_default));
                    tvDiaValue.setText(R.string.invalid_data);
                    tvNibpSlash.setTextColor(ContextCompat.getColor(getActivity(),
                            R.color.value_default));
                    tvDiaValue.setTextColor(ContextCompat.getColor(getActivity(),
                            R.color.value_default));
                    nibpErrTimes = 0;
                    tvBpStress.setText(UiUtils.getString(R.string.zero));
                    EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_START_MEASURE
                            , ProtocolDefine.NIBP_MEASURE);
                    EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_START_MEASURE
                            , ProtocolDefine.NIBP_MEASURE);
                    nibpChecking = true;
                } else {
                    btnBpStartMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
                    nibpChecking = false;
                    setSpo2BtnStatus();
                    tvCuff.setVisibility(View.GONE);
                    tvBpStress.setVisibility(View.GONE);
                    nibpErrTimes = 0;
                    tvBpStress.setText(UiUtils.getString(R.string.zero));
                    btnBpStartMeasure.setText(UiUtils.getString(R.string.start_measure));
                    EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_STOP_MEASURE, 0);
                    EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_STOP_MEASURE, 0);
                }
                break;
            case R.id.btn_spo2_start_measure: //血氧启动测量
                if (!spo2Checking) {
                    btnBpStartMeasure.setEnabled(false);
                    btnSpo2StartMeasure.setBackgroundResource(R.drawable.btn_selector_orange);
                    progressBarSpo2.setMax(DEFAULT_TIME);
                    progressBarSpo2.setProgress(0);
                    btnSpo2StartMeasure.setText(UiUtils.getString(R.string.stop_measure));
                    presenter.startSpo2Measure();
                    tvSpo2Value.setText(R.string.invalid_data);
                    tvPrValue.setText(R.string.invalid_data);
                    progressBarSpo2.setVisibility(View.VISIBLE);
                    tvSpo2CountDown.setVisibility(View.VISIBLE);
                } else {
                    btnBpStartMeasure.setEnabled(true);
                    btnSpo2StartMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
                    progressBarSpo2.setMax(DEFAULT_TIME);
                    btnSpo2StartMeasure.setText(UiUtils.getString(R.string.start_measure));
                    presenter.stopSpo2Measure();
                    progressBarSpo2.setProgress(0);
                    tvSpo2CountDown.setVisibility(View.INVISIBLE);
                    progressBarSpo2.setVisibility(View.INVISIBLE);
                }
                spo2Checking = !spo2Checking;
                break;
            default:
                break;
        }
    }

    @Override
    public void refreshLayout(int state) {
        switch (state) {
            case LAYOUT_ZERO:
                if ((deviceConfig & (0x01 << 12)) != 0 || (deviceConfig & (0x01 << 13)) != 0
                        || (deviceConfig & (0x01 << 14)) != 0) {
                    llHbLayout.setVisibility(View.VISIBLE);
                } else {
                    llHbLayout.setVisibility(View.GONE);
                }

                //多合一
                if ((deviceConfig & (0x01 << 7)) != 0) {
                    llGluLayout.setVisibility(View.GONE);
                    llBeneLayout.setVisibility(View.VISIBLE);
                }
                //再判断显示百捷三合一还是单血糖
                if ((deviceConfig & (0x01 << 2)) != 0 || (deviceConfig & (0x01 << 6)) != 0
                        || (deviceConfig & (0x01 << 8)) != 0) {
                    llGluLayout.setVisibility(View.VISIBLE);
                    llBeneLayout.setVisibility(View.GONE);
                }

                llWeightLayout.setVisibility(View.GONE);
                llHeightLayout.setVisibility(View.GONE);
                llBloodFatLayout.setVisibility(View.GONE);
                break;
            case LAYOUT_ONE:
                //多合一
                if ((deviceConfig & (0x01 << 7)) != 0) {
                    llGluLayout.setVisibility(View.GONE);
                    llBeneLayout.setVisibility(View.VISIBLE);
                }
                //再判断显示百捷三合一还是单血糖
                if ((deviceConfig & (0x01 << 2)) != 0 || (deviceConfig & (0x01 << 6)) != 0
                        || (deviceConfig & (0x01 << 8)) != 0) {
                    llGluLayout.setVisibility(View.VISIBLE);
                    llBeneLayout.setVisibility(View.GONE);
                }
                //身高
                if ((measureConfig & (0x01 << 0)) != 0) {
                    llHeightLayout.setVisibility(View.VISIBLE);
                } else {
                    llHeightLayout.setVisibility(View.GONE);
                }
                //体重
                if ((measureConfig & (0x01 << 1)) != 0) {
                    llWeightLayout.setVisibility(View.VISIBLE);
                } else {
                    llWeightLayout.setVisibility(View.GONE);
                }
                llHbLayout.setVisibility(View.GONE);
                llBloodFatLayout.setVisibility(View.GONE);
                break;
            case LAYOUT_TWO:
                //再判断显示百捷三合一还是单血糖
                if ((deviceConfig & (0x01 << 2)) != 0 || (deviceConfig & (0x01 << 6)) != 0
                        || (deviceConfig & (0x01 << 8)) != 0 || (deviceConfig & (0x01 << 7)) != 0) {
                    llGluLayout.setVisibility(View.VISIBLE);
                    llBeneLayout.setVisibility(View.GONE);
                }
                if ((deviceConfig & (0x01 << 12)) != 0 || (deviceConfig & (0x01 << 13)) != 0
                        || (deviceConfig & (0x01 << 14)) != 0) {
                    llHbLayout.setVisibility(View.VISIBLE);
                } else {
                    llHbLayout.setVisibility(View.GONE);
                }
                if ((measureConfig & (0x01 << 0)) != 0) {
                    llHeightLayout.setVisibility(View.VISIBLE);
                } else {
                    llHeightLayout.setVisibility(View.GONE);
                }
                //体重
                if ((measureConfig & (0x01 << 1)) != 0) {
                    llWeightLayout.setVisibility(View.VISIBLE);
                } else {
                    llWeightLayout.setVisibility(View.GONE);
                }
                llBloodFatLayout.setVisibility(View.GONE);
                llBeneLayout.setVisibility(View.GONE);
                break;
            case LAYOUT_THREE:
                //血脂
                //再判断显示百捷三合一还是单血糖
                if ((deviceConfig & (0x01 << 2)) != 0 || (deviceConfig & (0x01 << 6)) != 0
                        || (deviceConfig & (0x01 << 8)) != 0 || (deviceConfig & (0x01 << 7)) != 0) {
                    llGluLayout.setVisibility(View.VISIBLE);
                    llBeneLayout.setVisibility(View.GONE);
                }

                if ((deviceConfig & (0x01 << 15)) != 0) {
                    llBloodFatLayout.setVisibility(View.VISIBLE);
                } else {
                    llBloodFatLayout.setVisibility(View.GONE);
                }
                llWeightLayout.setVisibility(View.GONE);
                llHeightLayout.setVisibility(View.GONE);
                llBeneLayout.setVisibility(View.GONE);
                break;
            default:
                if ((deviceConfig & (0x01 << 12)) != 0 || (deviceConfig & (0x01 << 13)) != 0
                        || (deviceConfig & (0x01 << 14)) != 0) {
                    llHbLayout.setVisibility(View.VISIBLE);
                } else {
                    llHbLayout.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void setMeasureValue(int param, int value) {
        boolean measureSuccess = true;
        if (value != GlobalConstant.INVALID_DATA) {
            measureBean = presenter.getMeasureData();
            switch (param) {
                case KParamType.SPO2_PR:
                    btnSpo2StartMeasure.setText(R.string.start_measure);
                    progressBarSpo2.setProgress(0);
                    btnSpo2StartMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
                    btnBpStartMeasure.setEnabled(true);
                    progressBarSpo2.setVisibility(View.INVISIBLE);
                    tvSpo2CountDown.setVisibility(View.INVISIBLE);
                    presenter.saveDb(param, value);
                    tvPrValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setTextColor(tvPrValue, param);
                    break;
                case KParamType.SPO2_TREND:
                    presenter.saveDb(param, value);
                    tvSpo2Value.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setTextColor(tvSpo2Value, param);
                    break;
                case KParamType.NIBP_DIA: //收缩压舒张压显示在一个控件上
                    presenter.saveDb(param, value);
                    btnBpStartMeasure.setText(R.string.start_measure);
                    btnBpStartMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
                    setSpo2BtnStatus();

                    String dia = UiUtils.measureValueShowUi(measureBean, param);
                    tvDiaValue.setText(dia);
                    setTextColor(tvDiaValue, param);
                    tvCuff.setVisibility(View.GONE);
                    tvBpStress.setVisibility(View.GONE);
                    tvBpStress.setText(UiUtils.getString(R.string.zero));
                    //单值异常+1，双值异常变红，默认灰色
                    if (nibpErrTimes == 2) {
                        tvNibpSlash.setTextColor(getResources().getColor(R.color
                                .error_value_color));
                    } else if (nibpErrTimes == 1) {
                        tvNibpSlash.setTextColor(getResources().getColor(R.color
                                .measure_value_text_color));
                    } else {
                        tvNibpSlash.setTextColor(getResources().getColor(R.color.value_default));
                    }
                    break;
                case KParamType.NIBP_SYS:
                    presenter.saveDb(param, value);
                    String sys = UiUtils.measureValueShowUi(measureBean, KParamType.NIBP_SYS);
                    tvSysValue.setText(sys);
                    setTextColor(tvSysValue, param);

                    break;
                case KParamType.IRTEMP_TREND:
                    presenter.saveDb(param, value);
                    tvTempValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setTextColor(tvTempValue, param);
                    break;
                case KParamType.BLOODGLU_AFTER_MEAL:
                case KParamType.BLOODGLU_BEFORE_MEAL:
                    tvGluValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    tvBeneGluValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    saveGlu(currentGlu, value);
                    setGlu(currentGlu);

                    break;
                case KParamType.URINERT_BIL:
                    presenter.saveDb(param, value);
                    tvBilValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setUrtTextColor(tvBilValue, param);
                    break;
                case KParamType.URINERT_BLD:
                    presenter.saveDb(param, value);
                    tvBldValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setUrtTextColor(tvBldValue, param);
                    break;
                case KParamType.URINERT_GLU:
                    presenter.saveDb(param, value);
                    tvUrineGluValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setUrtTextColor(tvUrineGluValue, param);
                    break;
                case KParamType.URINERT_KET:
                    presenter.saveDb(param, value);
                    tvKetValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setUrtTextColor(tvKetValue, param);
                    break;
                case KParamType.URINERT_LEU:
                    presenter.saveDb(param, value);
                    tvLeuValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setUrtTextColor(tvLeuValue, param);
                    break;
                case KParamType.URINERT_NIT:
                    presenter.saveDb(param, value);
                    tvNitValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setUrtTextColor(tvNitValue, param);
                    break;
                case KParamType.URINERT_PH:
                    presenter.saveDb(param, value);
                    tvPhValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setUrtTextColor(tvPhValue, param);
                    break;
                case KParamType.URINERT_PRO:
                    presenter.saveDb(param, value);
                    tvProValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setUrtTextColor(tvProValue, param);
                    break;
                case KParamType.URINERT_SG:
                    presenter.saveDb(param, value);
                    tvSgValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setUrtTextColor(tvSgValue, param);
                    break;
                case KParamType.URINERT_UBG:
                    presenter.saveDb(param, value);
                    tvUbgValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setUrtTextColor(tvUbgValue, param);
                    break;
                case KParamType.URINERT_ASC:
                case KParamType.URINERT_VC:
                    presenter.saveDb(KParamType.URINERT_ASC, value);
                    measureBean.setTrendValue(KParamType.URINERT_ASC, value);
                    tvVcValue.setText(UiUtils.measureValueShowUi(measureBean,
                            KParamType.URINERT_ASC));
                    setUrtTextColor(tvVcValue, KParamType.URINERT_ASC);
                    break;
                case KParamType.URINERT_ALB:
                    presenter.saveDb(param, value);
                    tvMaValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setUrtTextColor(tvMaValue, param);
                    break;
                case KParamType.URINERT_CRE:
                    presenter.saveDb(param, value);
                    tvCrValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setUrtTextColor(tvCrValue, param);
                    break;
                case KParamType.URINERT_CA:
                    presenter.saveDb(param, value);
                    tvCaValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setUrtTextColor(tvCaValue, param);
                    break;
                case KParamType.URINE_AC:
                    presenter.saveDb(param, value);
                    tvAcValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setUrtTextColor(tvAcValue, param);
                    break;
                case KParamType.BLOOD_HGB:
                    presenter.saveDb(param, value);
                    tvHbValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setTextColor(tvHbValue, param);
                    break;
                case KParamType.BLOOD_HCT:
                    presenter.saveDb(param, value);
                    tvHctValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setTextColor(tvHctValue, param);
                    break;
                case KParamType.URICACID_TREND:
                    presenter.saveDb(param, value);
                    tvBeneUaValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setTextColor(tvBeneUaValue, param);
                    break;
                case KParamType.CHOLESTEROL_TREND:
                    presenter.saveDb(param, value);
                    tvBeneTotalChoValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setTextColor(tvBeneTotalChoValue, param);
                    break;
                case KParamType.BLOOD_FAT_CHO:
                    presenter.saveDb(param, value);
                    tvTotalChoValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setTextColor(tvTotalChoValue, param);
                    break;
                case KParamType.BLOOD_FAT_HDL:
                    presenter.saveDb(param, value);
                    tvHdlValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setTextColor(tvHdlValue, param);
                    break;
                case KParamType.BLOOD_FAT_LDL:
                    presenter.saveDb(param, value);
                    tvLdlValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setTextColor(tvLdlValue, param);
                    break;
                case KParamType.BLOOD_FAT_TRIG:
                    presenter.saveDb(param, value);
                    tvTrigValue.setText(UiUtils.measureValueShowUi(measureBean, param));
                    setTextColor(tvTrigValue, param);
                    break;
                default:
                    measureSuccess = false;
                    break;
            }
            if (measureSuccess) {
                showNibpResult(-1); // 测量成功
            }
        }
    }

    @Override
    public void drawSpo2Wave(byte[] data) {
        spo2Wave.setData(data);
    }

    @Override
    public void refreshCuff(int value) { //设置血压值
        tvBpStress.setText(String.valueOf(value));
    }

    /**
     * 初始化测量值的所有控件。血糖在初始化餐前后状态赋值
     */
    private void initMeasureValueView() {
        if (measureBean == null || !measureBean.isHaveData()) { //没有测量数据则返回
            return;
        }
        nibpErrTimes = 0;
        tvBeneTotalChoValue.setText(UiUtils.measureValueShowUi(measureBean,
                KParamType.CHOLESTEROL_TREND));
        tvBeneUaValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URICACID_TREND));
        tvBilValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINERT_BIL));
        tvBldValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINERT_BLD));
        tvSysValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.NIBP_SYS));
        //收缩压舒张压连着显示
        tvDiaValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.NIBP_DIA));
        tvCaValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINERT_CA));
        tvAcValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINE_AC));
        tvCrValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINERT_CRE));
        tvHbValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.BLOOD_HGB));
        tvHdlValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.BLOOD_FAT_HDL));
        tvHctValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.BLOOD_HCT));
        tvKetValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINERT_KET));
        tvLdlValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.BLOOD_FAT_LDL));
        tvLeuValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINERT_LEU));
        tvMaValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINERT_ALB));
        tvNitValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINERT_NIT));
        tvPhValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINERT_PH));
        tvProValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINERT_PRO));
        tvPrValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.SPO2_PR));
        tvSgValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINERT_SG));
        tvSpo2Value.setText(UiUtils.measureValueShowUi(measureBean, KParamType.SPO2_TREND));
        tvTempValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.IRTEMP_TREND));
        tvTotalChoValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.BLOOD_FAT_CHO));
        tvTrigValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.BLOOD_FAT_TRIG));
        tvUbgValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINERT_UBG));
        tvUrineGluValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINERT_GLU));
        tvVcValue.setText(UiUtils.measureValueShowUi(measureBean, KParamType.URINERT_ASC));
        String glu = UiUtils.measureValueShowUi(measureBean, KParamType.BLOODGLU_AFTER_MEAL);
        if (UiUtils.getString(R.string.invalid_data).equals(glu)) { //如果餐后为空则取餐前。并且更新UI
            setGlu(BEFOR_GLU);
        } else {
            setGlu(AFTER_GLU);
        }
    }

    /**
     * 初始化所有测量值控件的颜色
     */
    private void initAllTextViewColor() {
        if (measureBean == null || !measureBean.isHaveData()) {
            return;
        }
        setTextColor(tvPrValue, KParamType.SPO2_PR);
        setTextColor(tvSpo2Value, KParamType.SPO2_TREND);
        setTextColor(tvSysValue, KParamType.NIBP_SYS);
        setTextColor(tvDiaValue, KParamType.NIBP_DIA);
        if (nibpErrTimes == 2) {
            tvNibpSlash.setTextColor(getResources().getColor(R.color.error_value_color));
        } else if (nibpErrTimes == 1) {
            tvNibpSlash.setTextColor(getResources().getColor(R.color.measure_value_text_color));
        } else {
            tvNibpSlash.setTextColor(getResources().getColor(R.color.value_default));
        }
        setTextColor(tvTempValue, KParamType.IRTEMP_TREND);

        setUrtTextColor(tvBilValue, KParamType.URINERT_BIL);
        setUrtTextColor(tvBldValue, KParamType.URINERT_BLD);
        setUrtTextColor(tvUrineGluValue, KParamType.URINERT_GLU);
        setUrtTextColor(tvKetValue, KParamType.URINERT_KET);
        setUrtTextColor(tvLeuValue, KParamType.URINERT_LEU);
        setUrtTextColor(tvNitValue, KParamType.URINERT_NIT);
        setUrtTextColor(tvPhValue, KParamType.URINERT_PH);
        setUrtTextColor(tvProValue, KParamType.URINERT_PRO);
        setUrtTextColor(tvSgValue, KParamType.URINERT_SG);
        setUrtTextColor(tvUbgValue, KParamType.URINERT_UBG);
        setUrtTextColor(tvVcValue, KParamType.URINERT_ASC);
        setUrtTextColor(tvMaValue, KParamType.URINERT_ALB);
        setUrtTextColor(tvCrValue, KParamType.URINERT_CRE);
        setUrtTextColor(tvCaValue, KParamType.URINERT_CA);
        setUrtTextColor(tvAcValue, KParamType.URINE_AC);
        setTextColor(tvHbValue, KParamType.BLOOD_HGB);
        setTextColor(tvHctValue, KParamType.BLOOD_HCT);
        setTextColor(tvBeneUaValue, KParamType.URICACID_TREND);
        setTextColor(tvBeneTotalChoValue, KParamType.CHOLESTEROL_TREND);
        setTextColor(tvTotalChoValue, KParamType.BLOOD_FAT_CHO);
        setTextColor(tvHdlValue, KParamType.BLOOD_FAT_HDL);
        setTextColor(tvLdlValue, KParamType.BLOOD_FAT_LDL);
        setTextColor(tvTrigValue, KParamType.BLOOD_FAT_TRIG);
    }

    /**
     * 设置餐前餐后血糖参考值范围
     * @param state 餐前餐后
     */
    private void setGlu(int state) {
        measureBean = presenter.getMeasureData();
        if (state == AFTER_GLU) {
            vBeforeGlu.setVisibility(View.INVISIBLE);
            vBeforeMeal.setVisibility(View.INVISIBLE);
            vAfterGlu.setVisibility(View.VISIBLE);
            vAfterMeal.setVisibility(View.VISIBLE);
            tvBeneGluMax.setText(ReferenceUtils.getMaxReference(KParamType.BLOODGLU_AFTER_MEAL) +
                    "");
            tvBeneGluMin.setText(ReferenceUtils.getMinReference(KParamType.BLOODGLU_AFTER_MEAL) +
                    "");
            tvGluMax.setText(ReferenceUtils.getMaxReference(KParamType.BLOODGLU_AFTER_MEAL) + "");
            tvGluMin.setText(ReferenceUtils.getMinReference(KParamType.BLOODGLU_AFTER_MEAL) + "");
            String glu = UiUtils.measureValueShowUi(measureBean, KParamType.BLOODGLU_AFTER_MEAL);
            tvBeneGluValue.setText(glu);
            tvGluValue.setText(glu);
            setTextColor(tvGluValue, KParamType.BLOODGLU_AFTER_MEAL);
            setTextColor(tvBeneGluValue, KParamType.BLOODGLU_AFTER_MEAL);
        } else if (state == BEFOR_GLU) {
            vBeforeGlu.setVisibility(View.VISIBLE);
            vBeforeMeal.setVisibility(View.VISIBLE);
            vAfterGlu.setVisibility(View.INVISIBLE);
            vAfterMeal.setVisibility(View.INVISIBLE);
            tvBeneGluMax.setText(ReferenceUtils.getMaxReference(KParamType.BLOODGLU_BEFORE_MEAL)
                    + "");
            tvBeneGluMin.setText(ReferenceUtils.getMinReference(KParamType.BLOODGLU_BEFORE_MEAL)
                    + "");
            tvGluMax.setText(ReferenceUtils.getMaxReference(KParamType.BLOODGLU_BEFORE_MEAL) + "");
            tvGluMin.setText(ReferenceUtils.getMinReference(KParamType.BLOODGLU_BEFORE_MEAL) + "");
            String glu = UiUtils.measureValueShowUi(measureBean, KParamType.BLOODGLU_BEFORE_MEAL);
            tvGluValue.setText(glu);
            tvBeneGluValue.setText(glu);
            setTextColor(tvGluValue, KParamType.BLOODGLU_BEFORE_MEAL);
            setTextColor(tvBeneGluValue, KParamType.BLOODGLU_BEFORE_MEAL);
        }
        currentGlu = state;
    }

    /**
     * 保存血糖
     * @param state 血糖状态
     * @param value 血糖值
     */
    private void saveGlu(int state, int value) {
        if (state == BEFOR_GLU) {
            measureBean.setTrendValue(KParamType.BLOODGLU_BEFORE_MEAL, value);
            presenter.saveDb(KParamType.BLOODGLU_BEFORE_MEAL, value);
            presenter.saveDb(KParamType.BLOODGLU_AFTER_MEAL, GlobalConstant.INVALID_DATA);
        } else {
            measureBean.setTrendValue(KParamType.BLOODGLU_AFTER_MEAL, value);
            presenter.saveDb(KParamType.BLOODGLU_AFTER_MEAL, value);
            presenter.saveDb(KParamType.BLOODGLU_BEFORE_MEAL, GlobalConstant.INVALID_DATA);
        }
    }

    /**
     * 根据测量值设置控件的颜色
     * @param tvValue 测量值控件
     * @param param 参数
     */
    private void setTextColor(TextView tvValue, int param) {
        try {
            int value = measureBean.getTrendValue(param);
            if (value == GlobalConstant.INVALID_DATA) {
                tvValue.setTextColor(UiUtils.getColor(R.color.value_default));
                return;
            }
            int compare = ReferenceUtils.compareWithReference(param, value);
            if (compare == ReferenceUtils.FLAG_VALUE_NORMAL) {
                tvValue.setTextColor(UiUtils.getColor(R.color.measure_value_text_color));
            } else {
                switch (param) {
                    case KParamType.NIBP_SYS:
                        nibpErrTimes = 1;
                        break;
                    case KParamType.NIBP_DIA:
                        nibpErrTimes += 1;
                        break;
                    default:
                        break;
                }
                tvValue.setTextColor(UiUtils.getColor(R.color.error_value_color));
            }
        } catch (ReferenceValueException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据测量值设置尿常规控件的颜色
     * @param tvValue 测量值控件
     * @param param 参数
     */
    private void setUrtTextColor(TextView tvValue, int param) {
        try {
            int value = measureBean.getTrendValue(param);
            int compare = ReferenceUtils.compareWithReference(param, value);
            if (compare == ReferenceUtils.FLAG_VALUE_NORMAL) {
                tvValue.setTextColor(UiUtils.getColor(R.color.value_default));
            } else {
                tvValue.setTextColor(UiUtils.getColor(R.color.error_value_color));
            }
        } catch (ReferenceValueException e) {
            tvValue.setTextColor(UiUtils.getColor(R.color.value_default));
            e.printStackTrace();
        }
    }

    /**
     * 测量状态提示
     * @param code 测量状态
     */
    public void showNibpResult(int code) {
        String result;
        switch (code) {
            case 1:
                result = getString(R.string.nibbp_result_1);
                break;
            case 2:
                result = getString(R.string.nibbp_result_2);
                break;
            case 3:
                result = getString(R.string.nibbp_result_3);
                break;
            case 4:
                result = getString(R.string.nibbp_result_4);
                break;
            case 5:
                result = getString(R.string.nibbp_result_5);
                break;
            case 6:
                result = getString(R.string.nibbp_result_6);
                break;
            case 7:
            case 8:
                result = getString(R.string.nibbp_result_7);
                break;
            case 9:
                result = getString(R.string.nibbp_result_8);
                break;
            case 10:
                result = getString(R.string.nibbp_result_9);
                break;
            case 11:
                result = getString(R.string.nibbp_result_10);
                break;
            case 12:
                result = getString(R.string.nibbp_result_11);
                break;
            case 13:
                result = getString(R.string.nibbp_result_12);
                break;
            case -1:
                result = getString(R.string.measure_success);
                break;
            default:
                return;
        }
        btnBpStartMeasure.setText(R.string.start_measure);
        if (nibpChecking) {
            ToastUtils.showShortToast(result);
        }
        nibpChecking = false;
        spo2Checking = false;
        btnBpStartMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
        tvCuff.setVisibility(View.GONE);
        tvBpStress.setVisibility(View.GONE);
        tvBpStress.setText(R.string.zero);
        setSpo2BtnStatus();
    }

    @Override
    public void showSpo2Status(int code) {
        switch (code) {
            case MeasureSpo2Fragment.STATUS_LEFF_ON:
                setSpo2BtnStatus();
                break;
            case MeasureSpo2Fragment.MEASURE_FAILED_LAYOUT:
                btnSpo2StartMeasure.setEnabled(true);
                btnBpStartMeasure.setEnabled(true);
                ToastUtils.showShortToast(R.string.measure_timeout);
                btnSpo2StartMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
                btnSpo2StartMeasure.setText(R.string.start_measure);
                spo2Checking = false;
                progressBarSpo2.setProgress(0);
                presenter.stopSpo2Measure();
                break;
            case MeasureSpo2Fragment.STATUS_LEFF_OFF:
            case MeasureSpo2Fragment.STATUS_FINGER:
                btnSpo2StartMeasure.setEnabled(false);
                btnBpStartMeasure.setEnabled(true);
                btnSpo2StartMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
                btnSpo2StartMeasure.setText(R.string.start_measure);
                spo2Checking = false;
                progressBarSpo2.setProgress(0);
                presenter.stopSpo2Measure();
                break;
            default:
                break;
        }
    }

    @Override
    public void refreshSpo2Ui(int progress) {
        if (progressBarSpo2 != null) {
            progressBarSpo2.setProgress(progress);
        }
    }

    @Override
    public QuicklyPresenterImpl initPresenter() {

        return new QuicklyPresenterImpl(this);
    }

    @Override
    public void setMeasureDataBean(MeasureDataBean bean) {
        this.measureBean = bean;
        initMeasureValueView();
        initAllTextViewColor();

        if (heightHolder != null) {
            if (bean.getTrendValue(KParamType.HEIGHT) != GlobalConstant.INVALID_DATA) {
                heightHolder.etValue.setText(String.valueOf(bean.getTrendValue(KParamType.HEIGHT) /
                        UiUtils.getFactor(KParamType.HEIGHT)));
            }
        }
        if (weightHolder != null) {
            if (bean.getTrendValue(KParamType.WEIGHT) != GlobalConstant.INVALID_DATA) {
                weightHolder.etValue.setText(String.valueOf(bean.getTrendValue(KParamType.WEIGHT) /
                        UiUtils.getFactor(KParamType.WEIGHT)));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        spo2Checking = false;
        nibpChecking = false;
        presenter.stopSpo2Measure();
        ButterKnife.reset(this);
    }

    /**
     * 血氧按钮状态AppDevice只发一次，后面如果要设置，需要自己去取
     */

    private void setSpo2BtnStatus() {
        switch (MeasureActivity.probeSpo2Status) {
            case MeasureSpo2Fragment.STATUS_LEFF_ON:
                if (!nibpChecking) {
                    btnSpo2StartMeasure.setEnabled(true);
                }
                break;
            case MeasureSpo2Fragment.STATUS_LEFF_OFF:
            case MeasureSpo2Fragment.STATUS_FINGER:
            case MeasureSpo2Fragment.MEASURE_FAILED_LAYOUT:
                btnSpo2StartMeasure.setEnabled(false);
                btnBpStartMeasure.setEnabled(true);
                btnSpo2StartMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
                btnSpo2StartMeasure.setText(R.string.start_measure);
                spo2Checking = false;
                progressBarSpo2.setProgress(0);
                presenter.stopSpo2Measure();
                progressBarSpo2.setVisibility(View.INVISIBLE);
                tvSpo2CountDown.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 隐藏键盘
     * @param v 需要隐藏键盘的view
     */
    public static void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_STOP_MEASURE, 0);
        EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_STOP_MEASURE, 0);
    }
}
