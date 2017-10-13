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
import com.konsung.presenter.GluPresenter;
import com.konsung.presenter.impl.GluPresenterImpl;
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
 * 血糖测量页面
 **/
public class MeasureGluFragment extends BaseFragment<GluPresenterImpl>
        implements GluPresenter.View, View.OnClickListener {
    private static final int BEFORE_GLU = 0;
    private static final int AFTER_GLU = 1;

    //空腹血糖
    @InjectView(R.id.relative_before_glu)
    View beforeGlu;
    //餐后血糖
    @InjectView(R.id.relative_after_glu)
    View afterGlu;
    @InjectView(R.id.layout_contain)
    RelativeLayout containLayout;
    @InjectView(R.id.tv_measure_template1)
    TextView tvLink; //步骤1
    @InjectView(R.id.tv_measure_template2)
    TextView tvSetout; // 步骤2
    @InjectView(R.id.tv_measure_template3)
    TextView tvDetection; // 步骤3
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
    private View view;
    private int currentGlu;
    private MeasureDataBean measureDataBean;
    private StatisticalDialogController statisticalController;
    private long lastClickTimestamp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_glu, container, false);
        ButterKnife.inject(this, view);
        initView();
        presenter.bindAidlService();
        return view;
    }

    /**
     * 初始化布局
     */
    private void initView() {
        btnTrend.setOnClickListener(this);
    }

    /**
     * 初始化导入的控件
     */
    private void initViewData() {
        initMeasureValueView();
        initGuideView();
        initMeasureData();
    }

    /**
     * 初始化测量数据
     */
    private void initMeasureData() {
        if (measureDataBean == null) {
            return;
        }
        int trendValue = measureDataBean.getTrendValue(KParamType.BLOODGLU_AFTER_MEAL);
        if (trendValue == GlobalConstant.INVALID_DATA) {
            replaceGluStyle(beforeGlu);
            UiUtils.setMeasureResult(KParamType.BLOODGLU_BEFORE_MEAL, measureDataBean,
                    tvBeforeGluName, tvBeforeGlu);
        } else {
            replaceGluStyle(afterGlu);
            UiUtils.setMeasureResult(KParamType.BLOODGLU_AFTER_MEAL, measureDataBean,
                    tvAfterGluName, tvAfterGlu);
        }
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
            //趋势统计
            case R.id.btn_measure_template2:
                StatisticalDialogController statisticalController = new
                        StatisticalDialogController(getActivity(), presenter
                        .getStatisticalTableItem(), presenter.getStatisticalPic(getActivity()),
                        false);
                statisticalController.showDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initViewData();
    }

    @Override
    public void measureSuccess(int glu) {
        if (glu != GlobalConstant.INVALID_DATA) {
            if (currentGlu == AFTER_GLU) {
                UiUtils.setMeasureResult(KParamType.BLOODGLU_AFTER_MEAL, glu,
                        tvAfterGluName, tvAfterGlu);
                presenter.saveGlu(KParamType.BLOODGLU_AFTER_MEAL, glu);
                presenter.saveGlu(KParamType.BLOODGLU_BEFORE_MEAL, GlobalConstant.INVALID_DATA);
                UiUtils.setMeasureResult(KParamType.BLOODGLU_BEFORE_MEAL, GlobalConstant
                        .INVALID_DATA, tvBeforeGluName, tvBeforeGlu);
                measureDataBean.setTrendValue(KParamType.BLOODGLU_AFTER_MEAL, glu);
            } else {
                UiUtils.setMeasureResult(KParamType.BLOODGLU_BEFORE_MEAL, glu,
                        tvBeforeGluName, tvBeforeGlu);
                presenter.saveGlu(KParamType.BLOODGLU_BEFORE_MEAL, glu);
                presenter.saveGlu(KParamType.BLOODGLU_AFTER_MEAL, GlobalConstant.INVALID_DATA);
                UiUtils.setMeasureResult(KParamType.BLOODGLU_AFTER_MEAL, GlobalConstant
                        .INVALID_DATA, tvAfterGluName, tvAfterGlu);
                measureDataBean.setTrendValue(KParamType.BLOODGLU_BEFORE_MEAL, glu);
            }
        }
    }

    @Override
    public void setMeasureData(MeasureDataBean measureDataBean) {
        this.measureDataBean = measureDataBean;
        initMeasureData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public GluPresenterImpl initPresenter() {

        return new GluPresenterImpl(this);
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
        tvBeforeGluName = (TextView) beforeGlu.findViewById(R.id.tv_measure_name);
        tvBeforeGluMax = (TextView) beforeGlu.findViewById(R.id.tv_measure_max);
        tvBeforeGluMin = (TextView) beforeGlu.findViewById(R.id.tv_measure_min);
        tvBeforeUnit = (TextView) beforeGlu.findViewById(R.id.tv_measure_unit);
        tvBeforeGlu = (TextView) beforeGlu.findViewById(R.id.tv_measure_value);
        ivBeforeDining = (ImageView) beforeGlu.findViewById(R.id.iv_glu_flag);
        tvBeforeGluName.setText(R.string.before_glu);
        tvBeforeUnit.setText(R.string.unit_mmol_l);
        tvBeforeGlu.setText(R.string.invalid_data);
        tvBeforeGluName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(beforeGlu);
            }
        });
        beforeGlu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(beforeGlu);
            }
        });
        ivBeforeDining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(beforeGlu);
            }
        });
        tvBeforeGluMax.setText(String.valueOf(ReferenceUtils.
                getMaxReference(KParamType.BLOODGLU_BEFORE_MEAL)));
        tvBeforeGluMin.setText(String.valueOf(ReferenceUtils.
                getMinReference(KParamType.BLOODGLU_BEFORE_MEAL)));

        tvAfterGluName = (TextView) afterGlu.findViewById(R.id.tv_measure_name);
        tvAfterGluMax = (TextView) afterGlu.findViewById(R.id.tv_measure_max);
        tvAfterGluMin = (TextView) afterGlu.findViewById(R.id.tv_measure_min);
        tvAfterUnit = (TextView) afterGlu.findViewById(R.id.tv_measure_unit);
        tvAfterGlu = (TextView) afterGlu.findViewById(R.id.tv_measure_value);
        ivAfterDining = (ImageView) afterGlu.findViewById(R.id.iv_glu_flag);
        tvAfterGluName.setText(R.string.after_glu);
        tvAfterUnit.setText(R.string.unit_mmol_l);

        tvAfterGlu.setText(R.string.invalid_data);
        tvAfterGluName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(afterGlu);
            }
        });
        afterGlu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(afterGlu);
            }
        });
        ivAfterDining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(afterGlu);
            }
        });
        tvAfterGluMax.setText(String.valueOf(ReferenceUtils.
                getMaxReference(KParamType.BLOODGLU_AFTER_MEAL)));
        tvAfterGluMin.setText(String.valueOf(ReferenceUtils.
                getMinReference(KParamType.BLOODGLU_AFTER_MEAL)));
    }

    /**
     * 切换血糖选中的整体布局颜色
     * @param gluLayout 选中的布局
     */
    private void replaceGluStyle(View gluLayout) {
        if (gluLayout == afterGlu) {
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
            UiUtils.setMeasureResult(KParamType.BLOODGLU_AFTER_MEAL, measureDataBean,
                    tvAfterGluName, tvAfterGlu);
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
            UiUtils.setMeasureResult(KParamType.BLOODGLU_BEFORE_MEAL, measureDataBean,
                    tvBeforeGluName, tvBeforeGlu);
        }
    }
}
