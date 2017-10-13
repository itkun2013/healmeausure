package com.konsung.presenter.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.BeneTrinityPresenter;
import com.konsung.service.AIDLServer;
import com.konsung.utils.KParamType;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 百捷三合一逻辑实现
 **/

public class BeneTrinityPresenterImpl extends BasePresenter<BeneTrinityPresenter.View>
        implements BeneTrinityPresenter.Presenter {

    AIDLServer aidlServer;
    BeneTrinityPresenter.View view;

    /**
     * 构造函数
     * @param view 布局操作
     */
    public BeneTrinityPresenterImpl(BeneTrinityPresenter.View view) {
        this.view = view;
    }

    @Override
    public void bindAidlService() {
        Intent intent = new Intent(UiUtils.getContent(), AIDLServer.class);
        UiUtils.getContent().startService(intent);
        UiUtils.getContent().bindService(intent, serviceConnection, Context
                .BIND_AUTO_CREATE);
    }

    @Override
    public List<Integer> getStatisticalTableItem() {
        List<Integer> paramList = new ArrayList<>();
        paramList.add(KParamType.BLOODGLU_BEFORE_MEAL); //趋势表需要显示多少个参数，吧相对应的参数传进来即可
        paramList.add(KParamType.URICACID_TREND);
        paramList.add(KParamType.CHOLESTEROL_TREND);
        return paramList;
    }

    @Override
    public void saveGlu(int param, int value) {
        if (aidlServer != null) {
            aidlServer.saveTrend(param, value);
            aidlServer.saveToDb2();
        }
    }

    /**
     * 获取安装引导界面
     * @param context 上下文
     * @param root 父布局
     * @param onClickListener 观看操作视频文本点击事件
     * @return 引导界面的View
     */
    public View getInstallGuideView(Context context, ViewGroup root, View.OnClickListener
            onClickListener) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_glu_tutorial, root, false);
        TextView tvLookVideo = (TextView) view.findViewById(R.id.tv_look);
        tvLookVideo.setOnClickListener(onClickListener);
        return view;
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlServer = ((AIDLServer.MsgBinder) service).getService();
            view.setMeasureData(aidlServer.getMeasureDataBean());
            aidlServer.setSendMSGToFragment(new AIDLServer.SendMSGToFragment() {

                @Override
                public void sendParaStatus(String name, String version) {
                }

                @Override
                public void sendWave(int param, byte[] bytes) {

                }

                @Override
                public void sendTrend(int param, int value) {
                    int invalidData = GlobalConstant.INVALID_DATA;
                    switch (param) {
                        case KParamType.URICACID_TREND:
                            if (value != GlobalConstant.INVALID_DATA) {
                                view.measureSuccess(invalidData, value, invalidData);
                                aidlServer.saveTrend(param, value);
                                aidlServer.saveToDb2();
                            }
                            break;
                        case KParamType.CHOLESTEROL_TREND:
                            if (value != GlobalConstant.INVALID_DATA) {
                                view.measureSuccess(invalidData, invalidData, value);
                                aidlServer.saveTrend(param, value);
                                aidlServer.saveToDb2();
                            }
                            break;
                        case KParamType.BLOODGLU_AFTER_MEAL:
                            if (value != GlobalConstant.INVALID_DATA) {
                                view.measureSuccess(value, invalidData, invalidData);
                                aidlServer.saveTrend(param, value);
                                aidlServer.saveToDb2();
                            }
                            break;
                        case KParamType.BLOODGLU_BEFORE_MEAL:
                            if (value != GlobalConstant.INVALID_DATA) {
                                view.measureSuccess(value, invalidData, invalidData);
                                aidlServer.saveTrend(param, value);
                                aidlServer.saveToDb2();
                            }
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void sendConfig(int param, int value) {
                }

                @Override
                public void sendPersonalDetail(String name, String idcard, int sex, int type) {

                }

                @Override
                public void send12LeadDiaResult(byte[] bytes) {

                }
            });
        }
    };
}
