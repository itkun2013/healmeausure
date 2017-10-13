package com.konsung.ui.defineview;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.konsung.R;
import com.konsung.utils.VideoUtil;
import com.konsung.utils.constant.GlobalConstant;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 操作视频窗口
 */

public class HelpVideoActivity extends Activity {

    @InjectView(R.id.video_view)
    CustomVideoView videoView;
    @InjectView(R.id.btn_exit)
    Button btnExit;

    Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_video);
        ButterKnife.inject(this);
        int param = getIntent().getIntExtra(VideoUtil.VIDEO_PARAM, GlobalConstant.INVALID_DATA);
        videoUri = VideoUtil.getVideoUri(param, this);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoUtil.play(this, videoView, videoUri);
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0, R.anim.anim_video_close);
    }
}
