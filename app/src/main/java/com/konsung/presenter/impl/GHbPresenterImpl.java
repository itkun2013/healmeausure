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
import com.konsung.bean.StatisticalPicBean;
import com.konsung.data.ProviderReader;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.GHbPresenter;
import com.konsung.service.AIDLServer;
import com.konsung.utils.KParamType;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 糖化血红蛋白-逻辑
 **/
public class GHbPresenterImpl extends BasePresenter<GHbPresenter.View>
        implements GHbPresenter.Presenter {

    private GHbPresenter.View view;
    AIDLServer aidlServer;

    /**
     * 糖化血红蛋白Presenter
     * @param view 布局操作
     */
    public GHbPresenterImpl(GHbPresenter.View view) {
        this.view = view;
    }

    /**
     * 绑定服务
     */
    @Override
    public void bindAidlService() {
        Intent intent = new Intent(UiUtils.getContent(), AIDLServer.class);
        UiUtils.getContent().startService(intent);
        UiUtils.getContent().bindService(intent, serviceConnection, Context
                .BIND_AUTO_CREATE);
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
                inflate(R.layout.layout_ghb_tutorial, root, false);
        TextView tvIntroduction = (TextView) view.findViewById(R.id.tv_introduction);
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
            //绑定服务成功时，数据回显
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

                    switch (param) {
                        case KParamType.GHB_HBA1C_NGSP:
                            if (value != GlobalConstant.INVALID_DATA) {
                                aidlServer.saveTrend(param, value);
                                view.measureSuccess(value,
                                        GlobalConstant.INVALID_DATA, GlobalConstant.INVALID_DATA);
                            }
                            break;
                        case KParamType.GHB_HBA1C_IFCC:
                            if (value != GlobalConstant.INVALID_DATA) {
                                aidlServer.saveTrend(param, value);
                                view.measureSuccess(GlobalConstant.INVALID_DATA,
                                        value, GlobalConstant.INVALID_DATA);
                            }
                            break;
                        case KParamType.GHB_EAG:
                            if (value != GlobalConstant.INVALID_DATA) {
                                aidlServer.saveTrend(param, value);
                                aidlServer.saveToDb2();
                                view.measureSuccess(GlobalConstant.INVALID_DATA,
                                        GlobalConstant.INVALID_DATA, value);
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

    @Override
    public List<Integer> getStatisticalTableItem() {
        List<Integer> paramList = new ArrayList<>();
        paramList.add(KParamType.GHB_HBA1C_NGSP); //趋势表需要显示多少个参数，吧相对应的参数传进来即可
        paramList.add(KParamType.GHB_HBA1C_IFCC);
        paramList.add(KParamType.GHB_EAG);
        return paramList;
    }

    @Override
    public StatisticalPicBean[] getStatisticalPic(Context context) {
        String currentIdCard = ProviderReader.readCurrentPatient(context).getIdCard();
        StatisticalPicBean picBean = new StatisticalPicBean();
        picBean.setIdCard(currentIdCard);
        picBean.setDataSize(10); //数据长度10个
        picBean.setStartCount(0); //数据库查询起始位置0
        picBean.setMaxValue(150); //y刻度值最大300
        picBean.setMinValue(0); //y刻度值最小0
        picBean.setySize(10); //y轴10个刻度
        picBean.setParameter(KParamType.GHB_HBA1C_IFCC);
        picBean.setUnit(UiUtils.getString(R.string.unit_mmol_mol));
        StatisticalPicBean picBean2 = new StatisticalPicBean();
        picBean2.setIdCard(currentIdCard);
        picBean2.setDataSize(10); //数据长度10个
        picBean2.setStartCount(0); //数据库查询起始位置0
        picBean2.setMaxValue(400); //y刻度值最大300
        picBean2.setMinValue(0); //y刻度值最小0
        picBean2.setySize(10); //y轴10个刻度
        picBean2.setParameter(KParamType.GHB_EAG);
        picBean2.setUnit(UiUtils.getString(R.string.unit_mg_dl));
        StatisticalPicBean[] beans = {picBean, picBean2};
        return beans;
    }
}
