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
import com.konsung.bean.ValueLayoutBean;
import com.konsung.presenter.GHbPresenter;
import com.konsung.presenter.impl.GHbPresenterImpl;
import com.konsung.ui.base.BaseFragment;
import com.konsung.ui.holder.ValueLayoutHolder;
import com.konsung.utils.KParamType;
import com.konsung.utils.ReferenceUtils;
import com.konsung.utils.StatisticalDialogController;
import com.konsung.utils.UiUtils;
import com.konsung.utils.VideoUtil;
import com.konsung.utils.constant.GlobalConstant;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 糖化血红蛋白(glycosylated hemoglobin 即GHb)测量页面
 **/
public class MeasureGHbFragment extends BaseFragment<GHbPresenterImpl>
        implements GHbPresenter.View, View.OnClickListener {

    /**
     * 参数布局高度(px)
     */
    public static final int VALUE_LAYOUT_HEIGHT = 174;

    @InjectView(R.id.relative_ifcc)
    RelativeLayout relativeIFCC;
    @InjectView(R.id.relative_ngsp)
    RelativeLayout relativeNGSP;
    @InjectView(R.id.relative_ega)
    RelativeLayout relativeEGA;
    //测量刷新布局
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
    Button btnTrend;

    private ValueLayoutHolder holderNGSP;
    private ValueLayoutHolder holderIFCC;
    private ValueLayoutHolder holderEGA;

    private MeasureDataBean measureDataBean = null;

    private View view;
    private StatisticalDialogController statisticalController;
    private long lastClickTimestamp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ghb, container, false);
        ButterKnife.inject(this, view);
        presenter.bindAidlService();
        return view;
    }

    /**
     * 初始化布局
     */
    private void initView() {
        btnMeasure.setVisibility(View.GONE);
        btnTrend.setOnClickListener(this);
        holderNGSP = new ValueLayoutHolder(relativeNGSP);
        holderIFCC = new ValueLayoutHolder(relativeIFCC);
        holderEGA = new ValueLayoutHolder(relativeEGA);
        ValueLayoutBean ngsp = new ValueLayoutBean(getActivity(),
                R.string.ghb_ngsp, String.valueOf(ReferenceUtils.
                getMaxReference(KParamType.GHB_HBA1C_NGSP)), String.valueOf(ReferenceUtils.
                getMinReference(KParamType.GHB_HBA1C_NGSP)), UiUtils.getValueUnit(KParamType
                .GHB_HBA1C_NGSP));
        initLayout(holderNGSP, ngsp);

        ValueLayoutBean ifcc = new ValueLayoutBean(getActivity(),
                R.string.ghb_ifcc, String.valueOf(ReferenceUtils.
                getMaxReference(KParamType.GHB_HBA1C_IFCC)), String.valueOf(ReferenceUtils.
                getMinReference(KParamType.GHB_HBA1C_IFCC)), UiUtils.getValueUnit(KParamType
                .GHB_HBA1C_IFCC));
        initLayout(holderIFCC, ifcc);

        ValueLayoutBean eGA = new ValueLayoutBean(getActivity(),
                R.string.ghb_eag, String.valueOf(ReferenceUtils.
                getMaxReference(KParamType.GHB_EAG)), String.valueOf(ReferenceUtils.
                getMinReference(KParamType.GHB_EAG)), UiUtils.getValueUnit(KParamType.GHB_EAG));
        initLayout(holderEGA, eGA);

        initGuideView();
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
        initView();
    }

    @Override
    public void measureSuccess(int nHbA1cValue, int iHbA1cValue, int eGA) {
        if (nHbA1cValue != GlobalConstant.INVALID_DATA) {
            UiUtils.setMeasureResult(KParamType.GHB_HBA1C_NGSP, nHbA1cValue,
                    holderNGSP.tvName, holderNGSP.tvValue);
        }
        if (iHbA1cValue != GlobalConstant.INVALID_DATA) {
            UiUtils.setMeasureResult(KParamType.GHB_HBA1C_IFCC, iHbA1cValue,
                    holderIFCC.tvName, holderIFCC.tvValue);
        }
        if (eGA != GlobalConstant.INVALID_DATA) {
            UiUtils.setMeasureResult(KParamType.GHB_EAG, eGA,
                    holderEGA.tvName, holderEGA.tvValue);
        }
    }

    @Override
    public void setDataBean(MeasureDataBean bean) {
        measureDataBean = bean;
        if (bean != null) {
            UiUtils.setMeasureResult(KParamType.GHB_HBA1C_NGSP,
                    bean.getTrendValue(KParamType.GHB_HBA1C_NGSP),
                    holderNGSP.tvName, holderNGSP.tvValue);
            UiUtils.setMeasureResult(KParamType.GHB_HBA1C_IFCC,
                    bean.getTrendValue(KParamType.GHB_HBA1C_IFCC),
                    holderIFCC.tvName, holderIFCC.tvValue);
            UiUtils.setMeasureResult(KParamType.GHB_EAG,
                    bean.getTrendValue(KParamType.GHB_EAG),
                    holderEGA.tvName, holderEGA.tvValue);
        }
    }

    @Override
    public GHbPresenterImpl initPresenter() {
        return new GHbPresenterImpl(this);
    }

    /**
     * 初始化导航
     */
    private void initGuideView() {
        containLayout.addView(presenter.getInstallGuideView(getActivity(), containLayout,
                VideoUtil.getVideoListener(getActivity(), KParamType.GHB_HBA1C_NGSP)));
        btnMeasure.setVisibility(View.GONE);
        tvLink.setText(UiUtils.getString(R.string.link_device));
        tvSetout.setText(R.string.ghb_solution_water);
        tvDetection.setText(UiUtils.getString(R.string.begin_measure));
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
     * 初始化测量项布局
     * @param holder 布局持有者
     * @param bean 布局数据集
     */
    private void initLayout(ValueLayoutHolder holder, ValueLayoutBean bean) {
        holder.view.getLayoutParams().height = VALUE_LAYOUT_HEIGHT;
        holder.tvName.setText(bean.getName());
        holder.tvValue.setText(bean.getValue());

        holder.tvMax.setText(bean.getMax());
        holder.tvMin.setText(bean.getMin());
        holder.tvUnit.setText(bean.getUnit());
    }
}
