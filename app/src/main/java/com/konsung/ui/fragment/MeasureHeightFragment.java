package com.konsung.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.presenter.HandInputPresenter;
import com.konsung.presenter.impl.HandInputPresenterImpl;
import com.konsung.ui.base.BaseFragment;
import com.konsung.utils.KParamType;
import com.konsung.utils.ReferenceUtils;
import com.konsung.utils.TextWatcherGenerator;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 身高测量页面
 **/
public class MeasureHeightFragment extends BaseFragment<HandInputPresenterImpl>
        implements HandInputPresenter.View, View.OnClickListener {

    @InjectView(R.id.tv_name)
    TextView txName;
    @InjectView(R.id.et_value)
    EditText edValue;
    @InjectView(R.id.tv_unit)
    TextView txUnit;
    @InjectView(R.id.btn_save)
    Button btnSave;
    @InjectView(R.id.tv_tips)
    TextView txTips;

    /**
     * 允许输入的最大长度
     */
    static final int INPUT_MAX_LENGTH = 5;

    /**
     * 输入框的长度，dp
     */
    static final int INPUT_VIEW_WIDTH = 220;

    /**
     * 限制小数点后位数长度
     */
    static final int LIMIT_AFTER_DOT_LENGTH = 1;

    private View view;

    private MeasureDataBean bean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_input_with_hand, container, false);
        ButterKnife.inject(this, view);
        initView();
        presenter.bindAidlService();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bean != null) {
            int height = bean.getTrendValue(KParamType.HEIGHT);
            if (height != GlobalConstant.INVALID_DATA) {
                edValue.setText(String.valueOf(height / GlobalConstant.FACTOR));
            } else {
                edValue.setText("");
            }
        }
    }

    /**
     * 初始化布局
     */
    private void initView() {
        txName.setText(R.string.height);
        btnSave.setEnabled(false);
        edValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(INPUT_MAX_LENGTH)});
        edValue.getLayoutParams().width = UiUtils.dpToPx(INPUT_VIEW_WIDTH, getResources());
        //获取焦点时，全选内容
        edValue.setText("");
        edValue.setSelectAllOnFocus(true);
        edValue.addTextChangedListener(presenter.getDotLimitTextWatcher(LIMIT_AFTER_DOT_LENGTH));
        edValue.addTextChangedListener(TextWatcherGenerator.getTextWatcher(KParamType.HEIGHT,
                (int) ReferenceUtils.getMaxReference(KParamType.HEIGHT), btnSave));
        txUnit.setText(R.string.unit_cm);
        txTips.setText(R.string.tips_input_height);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //保存
            case R.id.btn_save:
                String valueStr = edValue.getText().toString();
                if (valueStr.length() > 0 && !valueStr.equals("0")) {
                    Float valueF = Float.valueOf(valueStr);
                    int height = (int) (valueF * GlobalConstant.TREND_FACTOR);
                    boolean saved = presenter.save(KParamType.HEIGHT, height);
                    if (saved) {
                        //清除焦点
                        edValue.clearFocus();
                        hideKeyboard(edValue);
                        Toast.makeText(UiUtils.getContent(), UiUtils.getString(
                                R.string.save_success), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UiUtils.getContent(), R.string.service_err,
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public HandInputPresenterImpl initPresenter() {
        return new HandInputPresenterImpl(this);
    }

    @Override
    public void setDataBean(MeasureDataBean bean) {
        this.bean = bean;
        if (bean != null) {
            int height = bean.getTrendValue(KParamType.HEIGHT);
            if (height != GlobalConstant.INVALID_DATA) {
                edValue.setText(String.valueOf(height / GlobalConstant.FACTOR));
            } else {
                edValue.setText("");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard(edValue);
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
}
