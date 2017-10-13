package com.konsung.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.presenter.TempPresenter;
import com.konsung.presenter.impl.TempPresenterImpl;
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
 * 体温测量页面
 **/
public class MeasureTempFragment extends BaseFragment<TempPresenterImpl>
        implements TempPresenter.View, View.OnClickListener {
    public static final int GUIDE_LAYOUT = 0; //引导界面
    public static final int PROBE_INSERT_LAYOUT = 1; //安装探头
    public static final int OVER_LAYOUT = 4; //测量结束界面

    public static final int GUIDE_BEING = 4; //提示引导
    public static final int GUIDE_FINISH = 5; //完成引导
    public static final int GUIDE_STEP1 = 6; //引导步骤

    public static final int GUIDE_STEP2 = 7; //引导步骤

    //体温测量值布局
    @InjectView(R.id.relative_layout1)
    RelativeLayout tempValueLayout;
    //测量值布局2，这里没用到的，隐藏
    @InjectView(R.id.relative_layout2)
    RelativeLayout valueLayout2;
    //测量刷新布局
    @InjectView(R.id.layout_contain)
    RelativeLayout containLayout;
    //探头安装指示
    @InjectView(R.id.tv_measure_template1)
    TextView tvInstall;
    //手指插入指示
    @InjectView(R.id.tv_measure_template2)
    TextView tvInsert;
    @InjectView(R.id.btn_measure_template1)
    Button btnMeasure;
    @InjectView(R.id.btn_measure_template2)
    Button btnTrend;

    //血氧值
    private TextView tvTemp;
    private TextView tvTempName;

//    //脉率值
//    private TextView tvPr;
//    private TextView tvPrName;

    //引导界面
    private View guideView = null;
    //测量成功界面
    private View checkSuccessView = null;

    private View currentView = null;
    private MeasureDataBean measureDataBean = null;

    private View view;

    //趋势图
    private StatisticalDialogController statisticalController;
    private long lastClickTimestamp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_spo2, container, false);
        ButterKnife.inject(this, view);
        initView();
        presenter.bindAidlService();
        return view;
    }

    /**
     * 初始化布局
     */
    private void initView() {
        refresh(GUIDE_LAYOUT);
        valueLayout2.setVisibility(View.GONE);
        refreshGuide(tvInstall, GUIDE_STEP1);
        refreshGuide(tvInsert, GUIDE_STEP2);
        tvInstall.setText(getString(R.string.temp_pls_install_probe));
        tvInsert.setText(getString(R.string.temp_pls_move_to_head));
        btnMeasure.setVisibility(View.GONE);
        btnTrend.setOnClickListener(this);
    }

    /**
     * 初始化血氧
     */
    private void initTemp() {
        tvTempName = (TextView) tempValueLayout.findViewById(R.id.tv_measure_name);
        tvTempName.setTextColor(UiUtils.getColor(R.color.measure_name_text_color));
        tvTempName.setText(R.string.temp);
        TextView tvTempMax = (TextView) tempValueLayout.findViewById(R.id.tv_measure_max);
        //参数值未定，还需要重做，暂时写死，
        tvTempMax.setText(String.valueOf(ReferenceUtils.getMaxReference(KParamType.IRTEMP_TREND)));
        TextView tvTempMin = (TextView) tempValueLayout.findViewById(R.id.tv_measure_min);
        tvTempMin.setText(String.valueOf(ReferenceUtils.getMinReference(KParamType.IRTEMP_TREND)));
        tvTemp = (TextView) tempValueLayout.findViewById(R.id.tv_measure_value);
        tvTemp.setTextColor(UiUtils.getColor(R.color.measure_value_text_color));

        tvTemp.setText(R.string.invalid_data);
        TextView tvTempUnit = (TextView) tempValueLayout.findViewById(R.id.tv_measure_unit);
        tvTempUnit.setText(R.string.unit_temp);
        setDataBean(measureDataBean);
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
        initTemp();
    }

    @Override
    public void refresh(int state) {
        switch (state) {
            //引导页面
            case GUIDE_LAYOUT:
                if (guideView == null) {
                    guideView = presenter.getInstallGuideView(getActivity(), containLayout,
                            VideoUtil.getVideoListener(getActivity(), KParamType.IRTEMP_TREND));
                }
                if (currentView != guideView) {
                    changeView(guideView);
                }

                break;
            case PROBE_INSERT_LAYOUT:
                if (guideView == null) {
                    guideView = presenter.getInstallGuideView(getActivity(), containLayout,
                            VideoUtil.getVideoListener(getActivity(), KParamType.IRTEMP_TREND));
                }
                if (currentView != guideView) {
                    changeView(guideView);
                }
                break;
            //测量完成
            case OVER_LAYOUT:
                if (checkSuccessView == null) {
                    checkSuccessView = presenter.getSuccessLayout(getActivity(), containLayout);
                }
                if (currentView != checkSuccessView) {

                    changeView(checkSuccessView);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 改变显示布局
     * @param view 布局
     */
    private void changeView(View view) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (currentView != null) {
            containLayout.removeView(currentView);
        }
        currentView = view;
        containLayout.addView(currentView, params);
    }

    @Override
    public void measureSuccess(int temperature) {
        if (temperature != GlobalConstant.INVALID_DATA) {
            UiUtils.setMeasureResult(KParamType.IRTEMP_TREND, temperature, tvTempName, tvTemp);
        }
    }

    @Override
    public void refreshGuide(TextView tv, int state) {
        switch (state) {
            case GUIDE_BEING: //正在引导
                tv.setTextSize(30);
                tv.setTextColor(getResources().getColor(R.color.measure_name_text_color));
                Drawable dra = getResources().getDrawable(R.mipmap.ic_doing);
                dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
                tv.setCompoundDrawables(dra, null, null, null);
                break;
            case GUIDE_FINISH: //完成引导
                tv.setTextSize(UiUtils.getDimens(R.dimen.px20));
                Drawable dra2 = getResources().getDrawable(R.mipmap.ic_finished);
                dra2.setBounds(0, 0, dra2.getMinimumWidth(), dra2.getMinimumHeight());
                tv.setCompoundDrawables(dra2, null, null, null);
                break;
            case GUIDE_STEP2: //引导步骤
                tv.setTextSize(UiUtils.getDimens(R.dimen.px20));
                Drawable dra3 = getResources().getDrawable(R.mipmap.ic_step2);
                dra3.setBounds(0, 0, dra3.getMinimumWidth(), dra3.getMinimumHeight());
                tv.setCompoundDrawables(dra3, null, null, null);
                break;
            case GUIDE_STEP1: //引导步骤
                tv.setTextSize(UiUtils.getDimens(R.dimen.px20));
                Drawable dra4 = getResources().getDrawable(R.mipmap.ic_step1);
                dra4.setBounds(0, 0, dra4.getMinimumWidth(), dra4.getMinimumHeight());
                tv.setCompoundDrawables(dra4, null, null, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void setDataBean(MeasureDataBean bean) {
        measureDataBean = bean;
        if (bean != null) {
            int irTemp = bean.getTrendValue(KParamType.IRTEMP_TREND);
            UiUtils.setMeasureResult(KParamType.IRTEMP_TREND, irTemp, tvTempName, tvTemp);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        guideView = null;
        currentView = null;
    }

    @Override
    public TempPresenterImpl initPresenter() {
        return new TempPresenterImpl(this);
    }
}
