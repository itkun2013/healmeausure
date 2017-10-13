package com.konsung.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.exception.ReferenceValueException;
import com.konsung.utils.KParamType;
import com.konsung.utils.OverCheckUtil;
import com.konsung.utils.ReferenceUtils;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 心电心率历史测量记录适配器
 * Created by DJH on 2016/9/23 0023.
 */
public class StatisticalDataAdapter extends BaseAdapter {
    private Context context;

    // 布局填充器
    private LayoutInflater layoutInflater = null;
    // 数据集合
    private List<MeasureDataBean> measureList = null;
    // 参数集合
    private List<Integer> paramList = null;

    /**
     * 构造器
     * @param context 上下文
     * @param measureDataBeen 内容
     * @param paramList 参数集合
     */
    public StatisticalDataAdapter(Context context, List<MeasureDataBean> measureDataBeen
            , List<Integer> paramList) {
        this.layoutInflater = LayoutInflater.from(context);
        this.measureList = measureDataBeen;
        this.paramList = paramList;
        this.context = context;
    }

    /**
     * 重置数据，刷新界面
     * @param measureList 数据集
     */
    public void setMeasureList(List<MeasureDataBean> measureList) {
        this.measureList = measureList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null != measureList) {
            if (measureList.size() > 10) {
                return 10;
            } else {
                return measureList.size();
            }
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != measureList) {
            return measureList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup
            parent) {
        MeasureDataBean measureBean = measureList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout
                    .item_statistical_table, null);
            ButterKnife.inject(viewHolder, convertView);
            viewHolder.tvArray = new TextView[]{viewHolder.tvValue1, viewHolder.tvValue2,
                    viewHolder.tvValue3, viewHolder.tvValue4};
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        for (int i = 0; i < paramList.size(); i++) {
            int param = paramList.get(i);

            viewHolder.tvSerial.setText(String.valueOf(position + 1));
            viewHolder.tvMeasureTime.setText(UiUtils.getDateFormat(UiUtils.DateState.LONG)
                    .format(measureBean.getMeasureTime()));
            String unit = UiUtils.getValueUnit(param);
            int valueInt = measureBean.getTrendValue(param);
            viewHolder.tvArray[i].setVisibility(View.VISIBLE);

            //计算是否超限，变红
            int textColor = context.getResources().getColor(R.color.value_default);
            try {
                if (ReferenceUtils.FLAG_VALUE_NORMAL != ReferenceUtils.compareWithReference(param,
                        measureList.get(position).getTrendValue(param))) {
                    textColor = ContextCompat.getColor(context, R.color.error_value_color);
                }
            } catch (ReferenceValueException e) {
                e.printStackTrace();
            }
            viewHolder.tvArray[i].setTextColor(textColor);

            //设置值,血糖特殊处理
            if (param == KParamType.BLOODGLU_BEFORE_MEAL) {
                setGluValue(measureBean, viewHolder.tvArray[i]);
                continue;
            }
            //设置值，其它测量项
            if (valueInt == OverCheckUtil.FLAG_BELOW_MIN) {
                viewHolder.tvArray[i].setText(OverCheckUtil.
                        getOverMinString(param, valueInt) + " " + unit);
            } else if (valueInt == OverCheckUtil.FLAG_OVER_MAX) {
                viewHolder.tvArray[i].setText(OverCheckUtil.
                        getOverMaxString(param, valueInt) + " " + unit);
            } else {
                //无效值显示 -?-, 否则缩小100倍再加上单位显示
                String valueStr = valueInt == GlobalConstant.INVALID_DATA ?
                        UiUtils.getString(R.string.invalid_data)
                        : UiUtils.getValueAfterFactor(param, valueInt) + " " + unit;
                viewHolder.tvArray[i].setText(valueStr);
            }
        }
        return convertView;
    }

    /**
     * 设置血糖值(血糖与其他测量项不同，分餐前餐后显示)
     * 血糖保存的时候只保存一项，有餐前就没餐后
     * @param measureDataBean 测量结果
     * @param tvValue 测量结果显示框
     */
    private void setGluValue(MeasureDataBean measureDataBean, TextView tvValue) {
        boolean beforeGlu = true; //默认是餐前血糖
        int param = KParamType.BLOODGLU_BEFORE_MEAL;
        String unit = UiUtils.getValueUnit(param);
        int gluValue = measureDataBean.getTrendValue(param);
        //如果餐前没有值，就查询餐后血糖
        if (gluValue == GlobalConstant.INVALID_DATA) {
            param = KParamType.BLOODGLU_AFTER_MEAL;
            gluValue = measureDataBean.getTrendValue(param);
            beforeGlu = false;
        }
        String valueStr;
        //空腹、餐后血糖值显示，与颜色
        if (beforeGlu) {
            valueStr = gluValue == GlobalConstant.INVALID_DATA ?
                    UiUtils.getString(R.string.invalid_data)
                    : UiUtils.getValueAfterFactor(param, gluValue) + " " + unit
                    + context.getString(R.string.before_meal_with_bracket);
        } else {
            valueStr = gluValue == GlobalConstant.INVALID_DATA ?
                    UiUtils.getString(R.string.invalid_data)
                    : UiUtils.getValueAfterFactor(param, gluValue) + " " + unit
                    + context.getString(R.string.after_meal_with_bracket);
        }
        tvValue.setText(valueStr);
    }

    /**
     * ViewHolder 装载器
     */
    class ViewHolder {
        @InjectView(R.id.tv_serial)
        TextView tvSerial;
        @InjectView(R.id.tv_time)
        TextView tvMeasureTime;
        @InjectView(R.id.tv_value1)
        TextView tvValue1;
        @InjectView(R.id.tv_value2)
        TextView tvValue2;
        @InjectView(R.id.tv_value3)
        TextView tvValue3;
        @InjectView(R.id.tv_value4)
        TextView tvValue4;
        TextView[] tvArray;
    }
}
