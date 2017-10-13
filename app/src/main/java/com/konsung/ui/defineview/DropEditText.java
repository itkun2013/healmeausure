package com.konsung.ui.defineview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konsung.R;

/**
 * 自定义编辑框
 */
public class DropEditText extends FrameLayout implements View.OnClickListener,
        OnItemClickListener {
    private TextView mEditText;  // 输入框
    private ImageView mDropImage; // 右边的图片按钮
    private PopupWindow mPopup; // 点击图片弹出popupwindow
    private WrapListView mPopView; // popupwindow的布局

    private int mDrawableLeft;
    private int mDropMode; // flow_parent or wrap_content
    private String mHit;
    private int width = 382;
    private OnItemSelectedCallback callback;

    /**
     * 构造
     * @param context 上下文
     * @param attrs 参数
     */
    public DropEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * @param context 上下文
     * @param attrs 参数
     * @param defStyle 资源标识符
     */
    public DropEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.defineview_edit_layout, this);
        mPopView = (WrapListView) LayoutInflater.from(context)
                .inflate(R.layout.pop_view, null);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable
                .dropEditText, defStyle, 0);
        mDrawableLeft = ta.getResourceId(R.styleable
                .dropEditText_drawableRight, R.mipmap.ic_launcher);
        mDropMode = ta.getInt(R.styleable.dropEditText_dropMode, 0);
        mHit = ta.getString(R.styleable.dropEditText_hint);
        ta.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mEditText = (TextView) findViewById(R.id.dropview_edit);
        mDropImage = (ImageView) findViewById(R.id.dropview_image);

        //mEditText.setSelectAllOnFocus(true);
        mDropImage.setImageResource(mDrawableLeft);

        if (!TextUtils.isEmpty(mHit)) {
            mEditText.setHint(mHit);
        }
        mEditText.setClickable(true);
        mEditText.setOnClickListener(this);
        mDropImage.setOnClickListener(this);
        mPopView.setOnItemClickListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 如果布局发生改
        // 并且dropMode是flower_parent
        // 则设置ListView的宽度
        width = getMeasuredWidth();
        if (changed && 0 == mDropMode) {
            mPopView.setListWidth(getMeasuredWidth());
        }
    }

    /**
     * 设置Adapter
     * @param adapter ListView的Adapter
     */
    public void setAdapter(BaseAdapter adapter) {
        mPopView.setAdapter(adapter);

        mPopup = new PopupWindow(mPopView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopup.setFocusable(true); // 让popwin获取焦点
    }

    /**
     * 设置选中回调
     * @param callback 回调对象
     */
    public void setOnItemSelectedCallback(OnItemSelectedCallback callback) {
        this.callback = callback;
    }

    /**
     * 获取输入框内的内容
     * @return String content
     */
    public String getText() {
        return mEditText.getText().toString();
    }

    /**
     * 设置当前索引对应的内容
     * @param position 索引
     */
    public void setItemSelected(int position) {
        Object user = mPopView.getAdapter().getItem(position);
        mEditText.setText(user.toString());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dropview_image || v.getId() == R.id.dropview_edit) {
            if (mPopup.isShowing()) {
                mPopup.dismiss();
                return;
            }

            mPopup.showAsDropDown(this, 0, 0);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Object user = mPopView.getAdapter().getItem(position);
        mEditText.setText(user.toString());
        if (callback != null) {
            callback.onItemSelected(position);
        }
        mPopup.dismiss();
    }

    /**
     * 回调对象
     */
    public static abstract class OnItemSelectedCallback {
        /**
         * 回调事件
         * @param position 索引
         */
        public abstract void onItemSelected(int position);
    }
}
