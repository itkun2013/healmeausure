package com.konsung.ui.holder;

import android.content.Context;
import android.view.WindowManager;

import com.konsung.R;
import com.konsung.ui.base.ViewHolder;
import com.konsung.ui.defineview.EcgViewFor12;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 正在测量中的心电持有者
 */
public class EcgMeasureHolder extends ViewHolder {

    @InjectView(R.id.view_ecg)
    public EcgViewFor12 ecgView;

    private boolean checking = false;

    /**
     * 持有者构造器，固定布局
     * @param context 上下文
     */
    public EcgMeasureHolder(Context context) {
        super(context, R.layout.layout_measure_ecg, null);
        ButterKnife.inject(this, view);
        ecgView.setLayoutZoom(0.6f);
    }

    /**
     * 从Window中移除
     * @param windowManager 窗口管理器
     */
    public void remove(WindowManager windowManager) {
        if (checking) {
            windowManager.removeViewImmediate(view);
            checking = false;
        }
    }

    /**
     * 添加进悬浮窗
     * @param windowManager 窗口管理器
     */
    public void addToWindow(WindowManager windowManager) {
        if (!checking) {
            final WindowManager.LayoutParams params = new WindowManager.
                    LayoutParams(WindowManager.LayoutParams.TYPE_PHONE);
            params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM; //可以获取back事件
            windowManager.addView(view, params);
            checking = true;
        }
    }

    /**
     * 写入心电数据
     * @param param 参数
     * @param data 心电数据
     */
    public void addEcgData(int param, int data) {
        ecgView.addEcgData(param, data);
    }
}
