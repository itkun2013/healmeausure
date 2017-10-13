package com.konsung.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.MediaController;

import com.konsung.R;
import com.konsung.ui.defineview.CustomVideoView;
import com.konsung.ui.listener.VideoClickListener;

import java.io.File;

/**
 * 视频工具类
 */
public class VideoUtil {

    /**
     * 視頻播放頁面傳值字段
     * {@link com.konsung.ui.defineview.HelpVideoActivity}
     * {@link KParamType}可能值
     */
    public static final String VIDEO_PARAM = "param";

    /**
     * 获取视频状态
     * @param view Fragment布局
     * @return 是否正在播放视频
     */
    public boolean getVideoStatus(View view) {
        if (view.findViewById(R.id.video_view) == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 視頻路徑獲取方法
     * @param param 參數
     * {@link KParamType}可能值
     * @param context 上下文
     * @return 路徑
     */
    public static Uri getVideoUri(int param, Context context) {
        String uri = "";

        switch (param) {
            case KParamType.ECG_HR:
                uri = Environment.getExternalStorageDirectory() + File.separator + "video.mp4";
                break;
            case KParamType.SPO2_TREND:
            case KParamType.SPO2_PR:
                uri = Environment.getExternalStorageDirectory() + File.separator + "video.mp4";
                break;
            case KParamType.NIBP_SYS:
            case KParamType.NIBP_DIA:
                uri = Environment.getExternalStorageDirectory() + File.separator + "video.mp4";
                break;
            case KParamType.IRTEMP_TREND:
                uri = Environment.getExternalStorageDirectory() + File.separator + "video.mp4";
                break;
            case KParamType.BLOODGLU_BEFORE_MEAL:
            case KParamType.BLOODGLU_AFTER_MEAL:
            case KParamType.URICACID_TREND:
            case KParamType.CHOLESTEROL_TREND:
                uri = Environment.getExternalStorageDirectory() + File.separator + "video.mp4";
                break;
            case KParamType.URINERT_SG:
            case KParamType.URINERT_LEU:
            case KParamType.URINERT_NIT:
            case KParamType.URINERT_UBG:
            case KParamType.URINERT_PRO:
            case KParamType.URINERT_BLD:
            case KParamType.URINERT_KET:
            case KParamType.URINERT_BIL:
            case KParamType.URINERT_GLU:
            case KParamType.URINERT_VC:
            case KParamType.URINERT_ASC:
            case KParamType.URINERT_ALB:
            case KParamType.URINERT_CA:
            case KParamType.URINERT_CRE:
            case KParamType.URINERT_PH:
                uri = Environment.getExternalStorageDirectory() + File.separator + "video.mp4";
                break;
            case KParamType.BLOOD_WBC:
                uri = Environment.getExternalStorageDirectory() + File.separator + "video.mp4";
                break;
            case KParamType.BLOOD_HGB:
            case KParamType.BLOOD_HCT:
                uri = Environment.getExternalStorageDirectory() + File.separator + "video.mp4";
                break;
            case KParamType.GHB_HBA1C_NGSP:
            case KParamType.GHB_HBA1C_IFCC:
            case KParamType.GHB_EAG:
                uri = Environment.getExternalStorageDirectory() + File.separator + "video.mp4";
                break;
            case KParamType.BLOOD_FAT_CHO:
            case KParamType.BLOOD_FAT_TRIG:
            case KParamType.BLOOD_FAT_HDL:
            case KParamType.BLOOD_FAT_LDL:
                uri = Environment.getExternalStorageDirectory() + File.separator + "video.mp4";
                break;
            default:

                break;
        }
        return Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.video);
    }

    /**
     * 播放视频方法
     * @param context 上下文
     * @param uri 视频路径
     * @param videoView 播放视频的videoView
     */
    public static void play(Context context, CustomVideoView videoView, Uri uri) {
        videoView.setMediaController(new MediaController(context));
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        videoView.setBackgroundColor(0);
    }

    /**
     * 获取视频观看点击事件监听器
     * @param context 上下文
     * @param param 参数
     * @return 视频观看点击事件监听器
     */
    public static VideoClickListener getVideoListener(Context context, int param) {
        return new VideoClickListener(context, param);
    }
}
