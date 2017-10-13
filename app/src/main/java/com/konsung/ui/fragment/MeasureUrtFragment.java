package com.konsung.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.data.ProviderReader;
import com.konsung.exception.ReferenceValueException;
import com.konsung.presenter.UrtPresenter;
import com.konsung.presenter.impl.UrtPresenterImpl;
import com.konsung.ui.base.BaseFragment;
import com.konsung.utils.KParamType;
import com.konsung.utils.ReferenceUtils;
import com.konsung.utils.UiUtils;
import com.konsung.utils.VideoUtil;
import com.konsung.utils.constant.GlobalConstant;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 尿常规测量页面
 **/
public class MeasureUrtFragment extends BaseFragment<UrtPresenterImpl>
        implements UrtPresenter.View, View.OnClickListener {

    private static final int TIME_REFRESH = 1000;

    @InjectView(R.id.title_tx)
    TextView tvTitle;
    @InjectView(R.id.urt_layout)
    LinearLayout containLayout;
    @InjectView(R.id.tv_look)
    TextView tvLook;

    private View view;

    //参数布局 Integer 见KParamType
    private HashMap<Integer, UrtPresenterImpl.UrtHolder> layouts;
    //需要添加的布局Key
    private ArrayList<Integer> loadItemKeys;
    //需要加载的项目代码
    private HashMap<Integer, String> loadItemCodes;
    //需要加载的项目名称
    private HashMap<Integer, String> loadItemNames;
    private MeasureDataBean measureDataBean;
    private long lastClickTimestamp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_urt, container, false);
        ButterKnife.inject(this, view);
        presenter.bindAidlService();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        loadItemKeys = presenter.initUrtData();
        int urtConfig = ProviderReader.getUrtConfig(getActivity());
        String text = "";
        if ((urtConfig & (0x01 << 1)) != 0) {
            //11+2
            text = getString(R.string.urt_check, "11+2");
        } else if ((urtConfig & (0x01 << 2)) != 0) {
            //14
            text = getString(R.string.urt_check, "14");
        } else {
            text = getString(R.string.urt_check, "11");
        }
        tvTitle.setText(text);
        //初始化项目代码
        loadItemCodes = presenter.initUrtCodes();
        //初始化项目名称
        loadItemNames = presenter.initUrtNames();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        //需要获取当前尿常规模式(11项，14项)
        layouts = new HashMap<>();
        if (loadItemKeys != null) {
            for (Integer key : loadItemKeys) {
                layouts.put(key, presenter.getParamLayout(getActivity(), containLayout));
            }
        }
        layouts = loadUrtValueLayout();
        tvLook.setOnClickListener(VideoUtil.getVideoListener(getActivity(),
                KParamType.URINERT_ALB));
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

            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public UrtPresenterImpl initPresenter() {
        return new UrtPresenterImpl(this);
    }

    @Override
    public HashMap<Integer, UrtPresenterImpl.UrtHolder> loadUrtValueLayout() {
        HashMap<Integer, UrtPresenterImpl.UrtHolder> map = new HashMap<>();
        int position = 0;
        View childAt = containLayout.getChildAt(0);
        containLayout.removeAllViews();
        containLayout.addView(childAt);
        for (Integer key : loadItemKeys) {
            UrtPresenterImpl.UrtHolder holder = presenter.getParamLayout(getActivity(),
                    containLayout);
            holder.txNum.setText(String.valueOf(position + 1));
            holder.txCode.setText(loadItemCodes.get(key));
            holder.txName.setText(loadItemNames.get(key));
            holder.txResult.setText(R.string.invalid_data);
            holder.txReference.setText(ReferenceUtils.getReferenceRange(key));
            position++;
            holder.txResult.setText(R.string.invalid_data);
            map.put(key, holder);
            containLayout.addView(holder.view);
        }

        return map;
    }

    @Override
    public void setValue(int param, int value) {
        if (layouts != null) {
            if (value == GlobalConstant.INVALID_DATA) {
                layouts.get(param).txResult.setText(R.string.invalid_data);
            } else {
                layouts.get(param).txResult.setText(UiUtils.measureValueShowUi(param,
                        value));
                try {
                    int result = ReferenceUtils.compareWithReference(param, value);
                    switch (result) {
                        case ReferenceUtils.FLAG_VALUE_ABOVE:
                            layouts.get(param).txResult.setTextColor(UiUtils.getColor(
                                    R.color.error_value_color));
                            layouts.get(param).imgFlag.setText(R.string.flag_value_high);
                            layouts.get(param).imgFlag.setTextColor(UiUtils.getColor(
                                    R.color.error_value_color));
                            break;
                        case ReferenceUtils.FLAG_VALUE_BELOW:
                            layouts.get(param).imgFlag.setText(R.string.flag_value_low);
                            layouts.get(param).txResult.setTextColor(UiUtils.getColor(
                                    R.color.error_value_color));
                            layouts.get(param).imgFlag.setTextColor(UiUtils.getColor(
                                    R.color.error_value_color));
                            break;
                        case ReferenceUtils.FLAG_VALUE_NORMAL:
                            layouts.get(param).imgFlag.setText("");
                            layouts.get(param).txResult.setTextColor(UiUtils.getColor(
                                    R.color.value_text));
                            break;
                        default:
                            break;
                    }
                } catch (ReferenceValueException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setDataBean(MeasureDataBean bean) {
        this.measureDataBean = bean;
        if (bean != null) {
            for (Integer key : loadItemKeys) {
                setValue(key, bean.getTrendValue(key));
            }
        }
    }

    @Override
    public Context getContent() {
        return getActivity();
    }
}
