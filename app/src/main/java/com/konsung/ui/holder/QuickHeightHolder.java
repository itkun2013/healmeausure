package com.konsung.ui.holder;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.konsung.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 快检身高体重持有者
 */

public class QuickHeightHolder {
    private View view;
    @InjectView(R.id.tv_name)
    public TextView tvName;
    @InjectView(R.id.tv_unit)
    public TextView tvUnit;
    @InjectView(R.id.btn_save)
    public Button btnSave;
    public EditText etValue;

    /**
     * 快检持有者
     * @param view 布局View
     */
    public QuickHeightHolder(View view) {
        this.view = view;
        ButterKnife.inject(this, view);
        etValue = (EditText) view.findViewById(R.id.et_value);
    }
}
