package com.konsung.ui.holder;

import android.view.View;
import android.widget.Button;

import com.konsung.R;
import com.konsung.ui.base.ViewHolder;
import com.konsung.ui.defineview.CustomVideoView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 播放界面持有者
 */
public class VideoHolder extends ViewHolder {
    @InjectView(R.id.btn_exit)
    public Button btnExit;
    @InjectView(R.id.video_view)
    public CustomVideoView videoView;

    /**
     * 播放界面持有者构造器
     * @param view 布局控件
     */
    public VideoHolder(View view) {
        super(view);
        ButterKnife.inject(this, view);
    }
}
