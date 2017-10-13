package com.konsung.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.presenter.BeneTrinityPresenter;
import com.konsung.presenter.impl.BeneTrinityPresenterImpl;
import com.konsung.ui.base.BaseFragment;
import com.konsung.utils.KParamType;
import com.konsung.utils.ReferenceUtils;
import com.konsung.utils.StatisticalDialogController;
import com.konsung.utils.UiUtils;
import com.konsung.utils.VideoUtil;
import com.konsung.utils.constant.GlobalConstant;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 百捷三合一测量页面
 **/
public class MeasureBeneTrinityFragment extends BaseFragment<BeneTrinityPresenterImpl>
        implements BeneTrinityPresenter.View, View.OnClickListener {

    private static final int BEFORE_GLU = 0;
    private static final int AFTER_GLU = 1;

    //空腹血糖
    @InjectView(R.id.relative_before_glu)
    RelativeLayout rlBeforeGlu;
    //餐后血糖
    @InjectView(R.id.relative_after_glu)
    RelativeLayout rlAfterGlu;
    //尿酸
    @InjectView(R.id.relative_ua)
    RelativeLayout rlUa;
    //总胆固醇
    @InjectView(R.id.relative_total_cho)
    RelativeLayout rlTotalCho;
    //测量刷新布局
    @InjectView(R.id.layout_contain)
    RelativeLayout containLayout;
    @InjectView(R.id.tv_measure_template1)
    TextView tvLink; //连接设备
    @InjectView(R.id.tv_measure_template2)
    TextView tvSetout; // 准备试纸
    @InjectView(R.id.tv_measure_template3)
    TextView tvDetection; // 采血检测
    @InjectView(R.id.btn_measure_template1)
    Button btnMeasure;
    @InjectView(R.id.btn_measure_template2)
    Button btnTrend; // 趋势统计

    //空腹血糖
    private TextView tvBeforeGlu;
    private TextView tvBeforeGluName;
    private ImageView ivBeforeDining;
    TextView tvBeforeGluMax;
    TextView tvBeforeGluMin;
    TextView tvBeforeUnit;
    //餐后血糖
    private TextView tvAfterGlu;
    private TextView tvAfterGluName;
    private ImageView ivAfterDining;
    TextView tvAfterGluMax;
    TextView tvAfterGluMin;
    TextView tvAfterUnit;
    //尿酸
    private TextView tvUa;
    private TextView tvUaName;
    //总胆固醇
    private TextView tvTotalCho;
    private TextView tvTotalChoName;

    private View view;

    private int currentGlu;
    private MeasureDataBean measureData;
    private long lastClickTimestamp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bene_trinity, container, false);
        ButterKnife.inject(this, view);
        presenter.bindAidlService();
        return view;
    }

    /**
     * 初始化布局
     */
    private void initView() {
        initViewData();
        btnTrend.setOnClickListener(this);
    }

    /**
     * 初始化测量数据
     */
    private void initMeasureData() {
        if (measureData == null) {
            return;
        }
        tvBeforeGlu.setText(UiUtils.getValueAfterFactor(KParamType.BLOODGLU_BEFORE_MEAL,
                measureData.getTrendValue(KParamType.BLOODGLU_BEFORE_MEAL)));
        tvAfterGlu.setText(UiUtils.getValueAfterFactor(KParamType.BLOODGLU_AFTER_MEAL,
                measureData.getTrendValue(KParamType.BLOODGLU_AFTER_MEAL)));
        UiUtils.setMeasureResult(KParamType.URICACID_TREND, measureData,
                tvUaName, tvUa);
        UiUtils.setMeasureResult(KParamType.CHOLESTEROL_TREND, measureData,
                tvTotalChoName, tvTotalCho);
        int value = measureData.getTrendValue(KParamType.BLOODGLU_AFTER_MEAL);
        if (value == GlobalConstant.INVALID_DATA) {
            replaceGluStyle(rlBeforeGlu);
            UiUtils.setMeasureResult(KParamType.BLOODGLU_BEFORE_MEAL, measureData,
                    tvBeforeGluName, tvBeforeGlu);
        } else {
            replaceGluStyle(rlAfterGlu);
            UiUtils.setMeasureResult(KParamType.BLOODGLU_AFTER_MEAL, measureData,
                    tvAfterGluName, tvAfterGlu);
        }
    }

    /**
     * 初始化导入的控件
     */
    private void initViewData() {
        initMeasureValueView();
        initGuideView();
        initMeasureData();
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
            //趋势统计.百捷三合一只显示趋势表
            case R.id.btn_measure_template2:
                StatisticalDialogController statisticalController = new
                        StatisticalDialogController(getActivity(),
                        presenter.getStatisticalTableItem());
                statisticalController.showDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void measureSuccess(float gluValue, float uaValue, float choValue) {
        if (gluValue != GlobalConstant.INVALID_DATA) {
            if (currentGlu == AFTER_GLU) {
                UiUtils.setMeasureResult(KParamType.BLOODGLU_AFTER_MEAL, gluValue,
                        tvAfterGluName, tvAfterGlu);
                presenter.saveGlu(KParamType.BLOODGLU_AFTER_MEAL, (int) gluValue);
                presenter.saveGlu(KParamType.BLOODGLU_BEFORE_MEAL, GlobalConstant.INVALID_DATA);
                UiUtils.setMeasureResult(KParamType.BLOODGLU_BEFORE_MEAL, GlobalConstant
                        .INVALID_DATA, tvBeforeGluName, tvBeforeGlu);
                measureData.setTrendValue(KParamType.BLOODGLU_AFTER_MEAL, (int) gluValue);
            } else {
                UiUtils.setMeasureResult(KParamType.BLOODGLU_BEFORE_MEAL, gluValue,
                        tvBeforeGluName, tvBeforeGlu);
                presenter.saveGlu(KParamType.BLOODGLU_BEFORE_MEAL, (int) gluValue);
                presenter.saveGlu(KParamType.BLOODGLU_AFTER_MEAL, GlobalConstant.INVALID_DATA);
                UiUtils.setMeasureResult(KParamType.BLOODGLU_AFTER_MEAL, GlobalConstant
                        .INVALID_DATA, tvAfterGluName, tvAfterGlu);
                measureData.setTrendValue(KParamType.BLOODGLU_BEFORE_MEAL, (int) gluValue);
            }
        }
        if (uaValue != GlobalConstant.INVALID_DATA) {
            UiUtils.setMeasureResult(KParamType.URICACID_TREND, uaValue,
                    tvUaName, tvUa);
        }
        if (choValue != GlobalConstant.INVALID_DATA) {
            UiUtils.setMeasureResult(KParamType.CHOLESTEROL_TREND, choValue,
                    tvTotalChoName, tvTotalCho);
        }
    }

    @Override
    public void setMeasureData(MeasureDataBean measureDataBean) {
        this.measureData = measureDataBean;
        initMeasureData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public BeneTrinityPresenterImpl initPresenter() {
        return new BeneTrinityPresenterImpl(this);
    }

    /**
     * 初始化引导界面
     */
    private void initGuideView() {
        containLayout.addView(presenter.getInstallGuideView(getActivity(), containLayout,
                VideoUtil.getVideoListener(getActivity(), KParamType.BLOODGLU_BEFORE_MEAL)));
        btnMeasure.setVisibility(View.GONE);
        tvLink.setText(UiUtils.getString(R.string.connect_the_detector));
        tvSetout.setText(UiUtils.getString(R.string.blood_test));
        tvDetection.setText(UiUtils.getString(R.string.link_device));
        Drawable drawable1 = UiUtils.getDrawable(R.mipmap.ic_step1);
        Drawable drawable2 = UiUtils.getDrawable(R.mipmap.ic_step2);
        Drawable drawable3 = UiUtils.getDrawable(R.mipmap.ic_step3);
        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
        drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
        tvLink.setCompoundDrawables(drawable1, null, null, null);
        tvSetout.setCompoundDrawables(drawable2, null, null, null);
        tvDetection.setCompoundDrawables(drawable3, null, null, null);
        tvLink.setTextSize(UiUtils.getDimens(R.dimen.px20));
        tvSetout.setTextSize(UiUtils.getDimens(R.dimen.px20));
        tvDetection.setTextSize(UiUtils.getDimens(R.dimen.px20));
    }

    /**
     * 初始化测量值的所有控件
     */
    private void initMeasureValueView() {
        tvBeforeGluName = (TextView) rlBeforeGlu.findViewById(R.id.tv_measure_name);
        tvBeforeGluMax = (TextView) rlBeforeGlu.findViewById(R.id.tv_measure_max);
        tvBeforeGluMin = (TextView) rlBeforeGlu.findViewById(R.id.tv_measure_min);
        tvBeforeGluMax.setText(String.valueOf(ReferenceUtils.
                getMaxReference(KParamType.BLOODGLU_BEFORE_MEAL)));
        tvBeforeGluMin.setText(String.valueOf(ReferenceUtils.
                getMinReference(KParamType.BLOODGLU_BEFORE_MEAL)));
        tvBeforeUnit = (TextView) rlBeforeGlu.findViewById(R.id.tv_measure_unit);
        tvBeforeGlu = (TextView) rlBeforeGlu.findViewById(R.id.tv_measure_value);
        ivBeforeDining = (ImageView) rlBeforeGlu.findViewById(R.id.iv_glu_flag);
        tvBeforeGluName.setText(R.string.before_glu);
        tvBeforeUnit.setText(R.string.unit_mmol_l);
        tvBeforeGlu.setText(R.string.invalid_data);
        tvBeforeGluName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(rlBeforeGlu);
            }
        });
        rlBeforeGlu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(rlBeforeGlu);
            }
        });
        ivBeforeDining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(rlBeforeGlu);
            }
        });

        tvAfterGluName = (TextView) rlAfterGlu.findViewById(R.id.tv_measure_name);
        tvAfterGluMax = (TextView) rlAfterGlu.findViewById(R.id.tv_measure_max);
        tvAfterGluMin = (TextView) rlAfterGlu.findViewById(R.id.tv_measure_min);
        tvAfterGluMax.setText(String.valueOf(ReferenceUtils.
                getMaxReference(KParamType.BLOODGLU_AFTER_MEAL)));
        tvAfterGluMin.setText(String.valueOf(ReferenceUtils.
                getMinReference(KParamType.BLOODGLU_AFTER_MEAL)));
        tvAfterUnit = (TextView) rlAfterGlu.findViewById(R.id.tv_measure_unit);
        tvAfterGlu = (TextView) rlAfterGlu.findViewById(R.id.tv_measure_value);
        ivAfterDining = (ImageView) rlAfterGlu.findViewById(R.id.iv_glu_flag);
        tvAfterGluName.setText(R.string.after_glu);
        tvAfterUnit.setText(R.string.unit_mmol_l);

        tvAfterGlu.setText(R.string.invalid_data);
        tvAfterGluName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(rlAfterGlu);
            }
        });
        rlAfterGlu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(rlAfterGlu);
            }
        });
        ivAfterDining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(rlAfterGlu);
            }
        });

        tvUaName = (TextView) rlUa.findViewById(R.id.tv_measure_name);
        TextView tvUaMax = (TextView) rlUa.findViewById(R.id.tv_measure_max);
        TextView tvUaMin = (TextView) rlUa.findViewById(R.id.tv_measure_min);
        String uaMax = String.format(getString(R.string.rule_limit_2_after_point),
                ReferenceUtils.getMaxReference(KParamType.URICACID_TREND));
        tvUaMax.setText(uaMax);
        String uaMin = String.format(getString(R.string.rule_limit_2_after_point),
                ReferenceUtils.getMinReference(KParamType.URICACID_TREND));
        tvUaMin.setText(uaMin);
        TextView tvUaUnit = (TextView) rlUa.findViewById(R.id.tv_measure_unit);
        tvUa = (TextView) rlUa.findViewById(R.id.tv_measure_value);
        tvUaName.setTextColor(UiUtils.getColor(R.color.measure_value_text_color));
        tvUaName.setText(R.string.ua);
        tvUaUnit.setText(R.string.unit_mmol_l);
        tvUa.setTextColor(UiUtils.getColor(R.color.measure_value_text_color));
        tvUa.setText(R.string.invalid_data);

        tvTotalChoName = (TextView) rlTotalCho.findViewById(R.id.tv_measure_name);
        TextView tvTotalChoMax = (TextView) rlTotalCho.findViewById(R.id.tv_measure_max);
        TextView tvTotalChoMin = (TextView) rlTotalCho.findViewById(R.id.tv_measure_min);

        String choMax = String.format(getString(R.string.rule_limit_2_after_point),
                ReferenceUtils.getMaxReference(KParamType.CHOLESTEROL_TREND));
        String choMin = String.format(getString(R.string.rule_limit_2_after_point),
                ReferenceUtils.getMinReference(KParamType.CHOLESTEROL_TREND));
        tvTotalChoMax.setText(choMax);
        tvTotalChoMin.setText(choMin);

        TextView tvTotalChoUnit = (TextView) rlTotalCho.findViewById(R.id.tv_measure_unit);
        tvTotalCho = (TextView) rlTotalCho.findViewById(R.id.tv_measure_value);
        tvTotalChoName.setTextColor(UiUtils.getColor(R.color.measure_value_text_color));
        tvTotalChoName.setText(R.string.total_cho);
        tvTotalChoUnit.setText(R.string.unit_mmol_l);
        tvTotalCho.setTextColor(UiUtils.getColor(R.color.measure_value_text_color));
        tvTotalCho.setText(R.string.invalid_data);
    }

    /**
     * 切换血糖选中的整体布局颜色
     * @param gluLayout 选中的布局
     */
    private void replaceGluStyle(View gluLayout) {
        if (gluLayout == rlAfterGlu) {
            currentGlu = AFTER_GLU;
            tvAfterGluName.setTextColor(UiUtils.getColor(R.color.measure_name_text_color));
            tvAfterGlu.setTextColor(UiUtils.getColor(R.color.measure_name_text_color));
            tvAfterGluMax.setTextColor(UiUtils.getColor(R.color.measureMax));
            tvAfterGluMin.setTextColor(UiUtils.getColor(R.color.measureMax));
            tvAfterUnit.setTextColor(UiUtils.getColor(R.color.measureMax));
            ivAfterDining.setImageDrawable(UiUtils.getDrawable(R.mipmap.radio_sel));

            tvBeforeGluName.setTextColor(UiUtils.getColor(R.color.line));
            tvBeforeGlu.setTextColor(UiUtils.getColor(R.color.line));
            tvBeforeGluMax.setTextColor(UiUtils.getColor(R.color.line));
            tvBeforeGluMin.setTextColor(UiUtils.getColor(R.color.line));
            tvBeforeUnit.setTextColor(UiUtils.getColor(R.color.line));
            ivBeforeDining.setImageDrawable(UiUtils.getDrawable(R.mipmap.radio_nor));
            UiUtils.setMeasureResult(KParamType.BLOODGLU_AFTER_MEAL, measureData, tvAfterGluName,
                    tvAfterGlu);
        } else {
            currentGlu = BEFORE_GLU;
            tvBeforeGluName.setTextColor(UiUtils.getColor(R.color.measure_name_text_color));
            tvBeforeGlu.setTextColor(UiUtils.getColor(R.color.measure_name_text_color));
            tvBeforeGluMax.setTextColor(UiUtils.getColor(R.color.measureMax));
            tvBeforeGluMin.setTextColor(UiUtils.getColor(R.color.measureMax));
            tvBeforeUnit.setTextColor(UiUtils.getColor(R.color.measureMax));
            ivBeforeDining.setImageDrawable(UiUtils.getDrawable(R.mipmap.radio_sel));

            tvAfterGluName.setTextColor(UiUtils.getColor(R.color.line));
            tvAfterGlu.setTextColor(UiUtils.getColor(R.color.line));
            tvAfterGluMax.setTextColor(UiUtils.getColor(R.color.line));
            tvAfterGluMin.setTextColor(UiUtils.getColor(R.color.line));
            tvAfterUnit.setTextColor(UiUtils.getColor(R.color.line));
            ivAfterDining.setImageDrawable(UiUtils.getDrawable(R.mipmap.radio_nor));
            UiUtils.setMeasureResult(KParamType.BLOODGLU_BEFORE_MEAL, measureData, tvBeforeGluName,
                    tvBeforeGlu);
        }
    }
}
