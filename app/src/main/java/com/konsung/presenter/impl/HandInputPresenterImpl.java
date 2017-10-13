package com.konsung.presenter.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;

import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.HandInputPresenter;
import com.konsung.service.AIDLServer;
import com.konsung.utils.UiUtils;

/**
 * 手动输入界面逻辑
 */

public class HandInputPresenterImpl extends BasePresenter<HandInputPresenter.View> implements
        HandInputPresenter.Presenter {
    AIDLServer aidlServer;
    HandInputPresenter.View view;

    /**
     * 构造函数
     * @param view 布局操作
     */
    public HandInputPresenterImpl(HandInputPresenter.View view) {
        this.view = view;
    }

    @Override
    public boolean save(int param, int value) {
        if (aidlServer != null) {
            aidlServer.saveTrend(param, value);
            aidlServer.saveToDb2();
            return true;
        }
        return false;
    }

    @Override
    public TextWatcher getDotLimitTextWatcher(final int limit) {
        return new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) {
                    return;
                }
                if (temp.length() - posDot - 1 > limit) {
                    edt.delete(posDot + limit + 1,
                            posDot + limit + 2);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
        };
    }

    @Override
    public void bindAidlService() {
        Intent intent = new Intent(UiUtils.getContent(), AIDLServer.class);
        UiUtils.getContent().startService(intent);
        UiUtils.getContent().bindService(intent, serviceConnection, Context
                .BIND_AUTO_CREATE);
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlServer = ((AIDLServer.MsgBinder) service).getService();
            view.setDataBean(aidlServer.getMeasureDataBean());
            aidlServer.setSendMSGToFragment(new AIDLServer.SendMSGToFragment() {

                @Override
                public void sendParaStatus(String name, String version) {

                }

                @Override
                public void sendWave(int param, byte[] bytes) {

                }

                @Override
                public void sendTrend(int param, int value) {

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
