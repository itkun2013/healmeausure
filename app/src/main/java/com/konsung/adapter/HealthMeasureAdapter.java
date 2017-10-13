package com.konsung.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.MeasureItemBean;
import com.konsung.utils.SpUtils;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 健康测量选项卡适配器
 */
public class HealthMeasureAdapter extends BaseAdapter {
    private List<MeasureItemBean> list;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private int selectItem = 0; // 选中的bean item值
    private int currentPosition = 0; // 选中的position
    private int beneCheckLayout = 0; // 百捷三合一界面
    private MeasureDataBean measureDataBean;

    /**
     * 构造
     * @param mContext 上下文
     * @param list 数据集
     */
    public HealthMeasureAdapter(Context mContext, List<MeasureItemBean> list) {
        super();
        this.mContext = mContext;
        this.list = list;
        layoutInflater = LayoutInflater.from(mContext);
        beneCheckLayout = SpUtils.getSpInt(GlobalConstant.APP_CONFIG,
                GlobalConstant.BENECHECK_LAYOUT, 5); //默认第五个位置为百捷三合一
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_measure_list_three, null);
            ButterKnife.inject(mViewHolder, convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        MeasureItemBean bean = list.get(position);
        mViewHolder.llMeasureItem2.setVisibility(View.GONE);
        mViewHolder.llMeasureItem3.setVisibility(View.GONE);
        if (bean.getPosition() == beneCheckLayout && bean.getPosition() == selectItem) {
            mViewHolder.llMeasureItem2.setVisibility(View.VISIBLE);
            mViewHolder.llMeasureItem3.setVisibility(View.VISIBLE);
            mViewHolder.llMeasureItemAll.setBackgroundResource(R.drawable.sidemenu_sel2);
            mViewHolder.tvMeasure1.setTextColor(UiUtils.getColor(R.color.menuText));
            mViewHolder.tvMeasure2.setTextColor(UiUtils.getColor(R.color.menuText));
            mViewHolder.tvMeasure3.setTextColor(UiUtils.getColor(R.color.menuText));
            mViewHolder.tvMeasure1.setText(bean.getItemName());
            mViewHolder.tvMeasure2.setText(UiUtils.getString(R.string.ua));
            mViewHolder.tvMeasure3.setText(UiUtils.getString(R.string.total_cho));
            mViewHolder.ivMeasureItem2.setImageResource(bean.getItemHigPic());
        } else if (bean.getPosition() == beneCheckLayout) {
            mViewHolder.llMeasureItem2.setVisibility(View.VISIBLE);
            mViewHolder.llMeasureItem3.setVisibility(View.VISIBLE);
            mViewHolder.llMeasureItemAll.setBackgroundResource(R.drawable.sidemenu_nor2);
            mViewHolder.tvMeasure1.setTextColor(UiUtils.getColor(R.color.menuText_nor));
            mViewHolder.tvMeasure2.setTextColor(UiUtils.getColor(R.color.menuText_nor));
            mViewHolder.tvMeasure3.setTextColor(UiUtils.getColor(R.color.menuText_nor));
            mViewHolder.tvMeasure1.setText(bean.getItemName());
            mViewHolder.tvMeasure2.setText(UiUtils.getString(R.string.ua));
            mViewHolder.tvMeasure3.setText(UiUtils.getString(R.string.total_cho));
            mViewHolder.ivMeasureItem2.setImageResource(bean.getItemNorPic());
        } else if (bean.getPosition() == selectItem) { //根据选中的item设置高亮
            mViewHolder.llMeasureItem1.setBackgroundResource(R.drawable.sidemenu_sel2);
            mViewHolder.tvMeasure1.setTextColor(UiUtils.getColor(R.color.menuText));
            mViewHolder.tvMeasure1.setText(bean.getItemName());
            mViewHolder.ivMeasureItem1.setImageResource(bean.getItemHigPic());
        } else {
            mViewHolder.llMeasureItem1.setBackgroundResource(R.drawable.sidemenu_nor2);
            mViewHolder.tvMeasure1.setTextColor(UiUtils.getColor(R.color.menuText_nor));
            mViewHolder.tvMeasure1.setText(bean.getItemName());
            mViewHolder.ivMeasureItem1.setImageResource(bean.getItemNorPic());
        }
        if (position == currentPosition - 1) { //隐藏高亮item上的分割线
            mViewHolder.llMeasureItem1.setBackgroundColor(Color.TRANSPARENT);
            mViewHolder.llMeasureItemAll.setBackgroundColor(Color.TRANSPARENT);
        }
        if (bean.isMeasureFinish()) {
            mViewHolder.ivMeasureDone1.setImageDrawable(UiUtils.getDrawable(R.mipmap.ic_done));
        } else {
            mViewHolder.ivMeasureDone1.setImageDrawable(null);
        }
        if (bean.isMeasureFinish1()) {
            mViewHolder.ivMeasureDone2.setImageDrawable(UiUtils.getDrawable(R.mipmap.ic_done));
        } else {
            mViewHolder.ivMeasureDone2.setImageDrawable(null);
        }
        if (bean.isMeasureFinish2()) {
            mViewHolder.ivMeasureDone3.setImageDrawable(UiUtils.getDrawable(R.mipmap.ic_done));
        } else {
            mViewHolder.ivMeasureDone3.setImageDrawable(null);
        }
        return convertView;
    }

    /**
     * 设置当前点击的item索引
     * @param position item索引
     */
    public void setSelectItem(int position) {
        selectItem = list.get(position).getPosition();
        currentPosition = position;
        notifyDataSetChanged();
    }

    /**
     * 更新测量数据
     * @param list 测量数据
     */
    public void updateMeasureData(List<MeasureItemBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    /**
     * 控件复用的容器
     */
    public class ViewHolder {
        @InjectView(R.id.ll_measure_item_all)
        LinearLayout llMeasureItemAll;
        @InjectView(R.id.ll_measure_item1)
        LinearLayout llMeasureItem1;
        @InjectView(R.id.tv_measure1)
        TextView tvMeasure1;
        @InjectView(R.id.iv_measure_item1)
        ImageView ivMeasureItem1;
        @InjectView(R.id.iv_measure_done1)
        ImageView ivMeasureDone1;
        @InjectView(R.id.ll_measure_item2)
        LinearLayout llMeasureItem2;
        @InjectView(R.id.tv_measure2)
        TextView tvMeasure2;
        @InjectView(R.id.iv_measure_item2)
        ImageView ivMeasureItem2;
        @InjectView(R.id.iv_measure_done2)
        ImageView ivMeasureDone2;
        @InjectView(R.id.ll_measure_item3)
        LinearLayout llMeasureItem3;
        @InjectView(R.id.tv_measure3)
        TextView tvMeasure3;
        @InjectView(R.id.iv_measure_item3)
        ImageView ivMeasureItem3;
        @InjectView(R.id.iv_measure_done3)
        ImageView ivMeasureDone3;
    }
}
