package com.konsung.ui.defineview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.adapter.DialogViewPageAdapter;
import com.konsung.utils.UiUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 趋势表趋势图弹框
 */

public class StatisticalDataDialog extends Dialog implements View.OnClickListener {

    @InjectView(R.id.btn_dialogClose)
    Button btnDialogClose;
    @InjectView(R.id.btn_dialogPic)
    Button btnDialogPic;
    @InjectView(R.id.btn_dialogTable)
    Button btnDialogTable;
    @InjectView(R.id.tv_link)
    TextView tvLink;
    @InjectView(R.id.vp_dialogContent)
    ViewPager vpDialogContent;

    private Context context;
    private List<View> list;

    /**
     * * 构造器
     * @param context 上下文
     * @param views 切换切面的集合
     */
    public StatisticalDataDialog(@NonNull Context context, List<View> views) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        list = views;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_statistical_data);
        ButterKnife.inject(this);
        initButton();
        vpDialogContent.setAdapter(new DialogViewPageAdapter(list));
        btnDialogClose.setOnClickListener(this);
        btnDialogPic.setOnClickListener(this);
        btnDialogTable.setOnClickListener(this);
        vpDialogContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 2 && vpDialogContent.getCurrentItem() == 1) {
                    //2滑动结束，1第2个页面
                    btnDialogPic.setBackground(UiUtils.getDrawable(R.drawable.btn_dialog_menu4));
                    btnDialogPic.setTextColor(UiUtils.getColor(R.color.measure_name_text_color));
                    Drawable drawable = UiUtils.getDrawable(R.mipmap.ic_trend_chart_sel);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    btnDialogPic.setCompoundDrawables(drawable, null, null, null);
                    btnDialogTable.setBackground(UiUtils.getDrawable(R.drawable.btn_dialog_menu1));
                    btnDialogTable.setTextColor(UiUtils.getColor(R.color.gray));
                    Drawable drawable2 = UiUtils.getDrawable(R.mipmap.ic_trend_table_nor);
                    drawable2.setBounds(0, 0, drawable2.getMinimumWidth(),
                            drawable2.getMinimumHeight());
                    btnDialogTable.setCompoundDrawables(drawable2, null, null, null);
                } else if (state == 2 && vpDialogContent.getCurrentItem() == 0) {
                    //2滑动结束，0第1个页面
                    btnDialogTable.setBackground(UiUtils.getDrawable(R.drawable.btn_dialog_menu2));
                    btnDialogTable.setTextColor(UiUtils.getColor(R.color.measure_name_text_color));
                    Drawable drawable2 = UiUtils.getDrawable(R.mipmap.ic_trend_table_sel);
                    drawable2.setBounds(0, 0, drawable2.getMinimumWidth(),
                            drawable2.getMinimumHeight());
                    btnDialogTable.setCompoundDrawables(drawable2, null, null, null);
                    btnDialogPic.setBackground(UiUtils.getDrawable(R.drawable.btn_dialog_menu3));
                    btnDialogPic.setTextColor(UiUtils.getColor(R.color.gray));
                    Drawable drawable = UiUtils.getDrawable(R.mipmap.ic_trend_chart_nor);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    btnDialogPic.setCompoundDrawables(drawable, null, null, null);
                }
            }
        });
    }

    /**
     * 初始化按钮
     */
    private void initButton() {
        if (list != null && list.size() == 1) {
            tvLink.setVisibility(View.GONE);
            btnDialogPic.setVisibility(View.GONE);
            btnDialogTable.setBackgroundResource(R.drawable.btn_dialog_menu);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialogClose:
                dismiss();
                break;
            case R.id.btn_dialogPic:
                vpDialogContent.setCurrentItem(1, true);
                break;
            case R.id.btn_dialogTable:
                vpDialogContent.setCurrentItem(0, true);
                break;
            default:
                break;
        }
    }

    @Override
    public void show() {
        super.show();
        //设置显示dialog时窗体背景变暗
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (metrics.widthPixels * 0.9);
        lp.height = (int) (metrics.heightPixels * 0.9);
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.dimAmount = 0.5f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
