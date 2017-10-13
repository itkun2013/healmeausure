package com.konsung.presenter.impl;

import android.app.Fragment;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.MeasureItemBean;
import com.konsung.data.ProviderReader;
import com.konsung.network.EchoServerEncoder;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.MeasurePresenter;
import com.konsung.service.AIDLServer;
import com.konsung.ui.activity.MeasureActivity;
import com.konsung.ui.fragment.MeasureBeneTrinityFragment;
import com.konsung.ui.fragment.MeasureBloodFatFragment;
import com.konsung.ui.fragment.MeasureEcgFragment;
import com.konsung.ui.fragment.MeasureGHbFragment;
import com.konsung.ui.fragment.MeasureGluFragment;
import com.konsung.ui.fragment.MeasureHeightFragment;
import com.konsung.ui.fragment.MeasureHemoglobinFragment;
import com.konsung.ui.fragment.MeasureNibpFragment;
import com.konsung.ui.fragment.MeasureQuicklyFragment;
import com.konsung.ui.fragment.MeasureSpo2Fragment;
import com.konsung.ui.fragment.MeasureTempFragment;
import com.konsung.ui.fragment.MeasureUrtFragment;
import com.konsung.ui.fragment.MeasureWbcFragment;
import com.konsung.ui.fragment.MeasureWeightFragment;
import com.konsung.utils.KParamType;
import com.konsung.utils.ProtocolDefine;
import com.konsung.utils.SpUtils;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 健康测量主界面的逻辑处理
 */

public class MeasurePresenterImpl extends BasePresenter<MeasurePresenter.View> implements
        MeasurePresenter.Presenter {
    MeasureActivity view;
    AIDLServer aidlServer;
    private int deviceConfig; //设备配置参数
    private int measureConfig; //测量项配置参数

    /**
     * 构造器
     * @param view 测量界面
     */
    public MeasurePresenterImpl(MeasureActivity view) {
        this.view = view;
    }

    @Override
    public List<MeasureItemBean> getMeasureItems() {
        //当配置项数据没有发生变化时，提示界面不需要刷新
        int tempDeviceConfig = ProviderReader.getDeviceConfig(view);
        int tempMeasureConfig = ProviderReader.getFragmentDisplayConfig(view);
        if (this.deviceConfig == tempDeviceConfig && this.measureConfig == tempMeasureConfig) {
            return null;
        }

        //获取配置项
        this.deviceConfig = tempDeviceConfig;
        this.measureConfig = tempMeasureConfig;
        EchoServerEncoder.setDeviceConfig(ProtocolDefine.NET_DEVICE_CONFIG, deviceConfig);
        //KSM5(心电、血氧 、血压、我们自己的参数版)

        int position = 0;

        List<MeasureItemBean> list = new ArrayList<>();
        MeasureItemBean app1 = new MeasureItemBean();
        app1.setItemName(UiUtils.getString(R.string.quickly));
        app1.setItemNorPic(R.mipmap.ic_rapidtest);
        app1.setItemHigPic(R.mipmap.ic_rapidtest_sel);
        app1.setPosition(position);
        app1.setFragment(new MeasureQuicklyFragment());
        list.add(app1);
        if ((this.deviceConfig & (0x01 << 0)) != 0) {
            MeasureItemBean app2 = new MeasureItemBean();
            app2.setItemName(UiUtils.getString(R.string.ecg));
            app2.setItemNorPic(R.mipmap.ic_ecg);
            app2.setItemHigPic(R.mipmap.ic_ecg_sel);
            app2.setPosition(++position);
            app2.setFragment(new MeasureEcgFragment());
            app2.setParam0(KParamType.ECG_HR);
            list.add(app2);
            MeasureItemBean app3 = new MeasureItemBean();
            app3.setItemName(UiUtils.getString(R.string.spo2));
            app3.setItemNorPic(R.mipmap.ic_spo2);
            app3.setItemHigPic(R.mipmap.ic_spo2_sel);
            app3.setFragment(new MeasureSpo2Fragment());
            app3.setPosition(++position);
            app3.setParam0(KParamType.SPO2_PR);
            list.add(app3);
            MeasureItemBean app4 = new MeasureItemBean();
            app4.setItemName(UiUtils.getString(R.string.bp));
            app4.setItemNorPic(R.mipmap.ic_bp);
            app4.setItemHigPic(R.mipmap.ic_bp_sel);
            app4.setFragment(new MeasureNibpFragment());
            app4.setPosition(++position);
            app4.setParam0(KParamType.NIBP_DIA);
            list.add(app4);
        }
        //HTD8819（振海红外线读卡器8808  8819）TH809（热映体温计）
        if ((this.deviceConfig & (0x01 << 4)) != 0 || (this.deviceConfig & (0x01 << 5)) != 0) {
            MeasureItemBean app5 = new MeasureItemBean();
            app5.setItemName(UiUtils.getString(R.string.temp));
            app5.setItemNorPic(R.mipmap.ic_temp);
            app5.setItemHigPic(R.mipmap.ic_temp_sel);
            app5.setFragment(new MeasureTempFragment());
            app5.setPosition(++position);
            app5.setParam0(KParamType.IRTEMP_TREND);
            list.add(app5);
        }

        //GA7(三诺血糖) BeneCheckGlu（百捷单血糖） OGM111（艾康血糖）倍稳血糖
        if ((this.deviceConfig & (0x01 << 2)) != 0
                || (this.deviceConfig & (0x01 << 6)) != 0
                || (this.deviceConfig & (0x01 << 8)) != 0
                || (this.deviceConfig & (0x01 << 16)) != 0) {
            MeasureItemBean app6 = new MeasureItemBean();
            app6.setItemName(UiUtils.getString(R.string.glu));
            app6.setItemNorPic(R.mipmap.ic_glu);
            app6.setItemHigPic(R.mipmap.ic_glu_sel);
            app6.setFragment(new MeasureGluFragment());
            app6.setPosition(++position);
            app6.setParam0(KParamType.BLOODGLU_AFTER_MEAL);
            app6.setParam1(KParamType.BLOODGLU_BEFORE_MEAL);
            list.add(app6);
            SpUtils.saveToSp(GlobalConstant.APP_CONFIG, GlobalConstant.BENECHECK_LAYOUT, -1);
        }

        //BeneCheck（百捷三合一包括血糖、尿酸、总胆固醇）
        if ((this.deviceConfig & (0x01 << 7)) != 0) {
            MeasureItemBean app11 = new MeasureItemBean();
            app11.setItemName(UiUtils.getString(R.string.glu));
            app11.setItemNorPic(R.mipmap.ic_glu);
            app11.setItemHigPic(R.mipmap.ic_glu_sel);
            app11.setFragment(new MeasureBeneTrinityFragment());
            app11.setPosition(++position);
            app11.setParam0(KParamType.BLOODGLU_BEFORE_MEAL);
            app11.setParam1(KParamType.URICACID_TREND);
            app11.setParam2(KParamType.CHOLESTEROL_TREND);
            list.add(app11);
            SpUtils.saveToSp(GlobalConstant.APP_CONFIG, GlobalConstant.BENECHECK_LAYOUT, position);
        } else {
            SpUtils.saveToSp(GlobalConstant.APP_CONFIG, GlobalConstant.BENECHECK_LAYOUT, -1);
        }

        //EmpUi(恩普尿常规); URIT-31（优利特尿常规）; Mission U120（尿液分析仪）
        if ((this.deviceConfig & (0x01 << 1)) != 0 || (this.deviceConfig & (0x01 << 9)) != 0
                || (this.deviceConfig & (0x01 << 10)) != 0) {
            MeasureItemBean app7 = new MeasureItemBean();
            app7.setItemName(UiUtils.getString(R.string.urine));
            app7.setItemNorPic(R.mipmap.ic_urt);
            app7.setItemHigPic(R.mipmap.ic_urt_sel);
            app7.setFragment(new MeasureUrtFragment());
            app7.setPosition(++position);
            app7.setParam0(KParamType.URINERT_LEU);
            list.add(app7);
        }
        //HemoCue WBC（白细胞分析仪）
        if ((this.deviceConfig & (0x01 << 11)) != 0) {
            MeasureItemBean app8 = new MeasureItemBean();
            app8.setItemName(UiUtils.getString(R.string.wbc));
            app8.setItemNorPic(R.mipmap.ic_wbc);
            app8.setItemHigPic(R.mipmap.ic_wbc_sel);
            app8.setFragment(new MeasureWbcFragment());
            app8.setPosition(++position);
            app8.setParam0(KParamType.BLOOD_WBC);
            list.add(app8);
        }
        //HemoCue HD（血红蛋白分析仪） URIT-12（优利特血红蛋白分析仪） Mission HD（血红蛋白分析仪）
        if ((this.deviceConfig & (0x01 << 12)) != 0 || (this.deviceConfig & (0x01 << 13)) != 0
                || (this.deviceConfig & (0x01 << 14)) != 0) {
            MeasureItemBean app9 = new MeasureItemBean();
            app9.setItemName(UiUtils.getString(R.string.hb));
            app9.setItemNorPic(R.mipmap.ic_hgb);
            app9.setItemHigPic(R.mipmap.ic_hgb_sel);
            app9.setFragment(new MeasureHemoglobinFragment());
            app9.setPosition(++position);
            app9.setParam0(KParamType.BLOOD_HCT);
            list.add(app9);
        }
        //糖化血红蛋白
        if ((this.deviceConfig & (0x01 << 17)) != 0) {
            MeasureItemBean app9 = new MeasureItemBean();
            app9.setItemName(UiUtils.getString(R.string.hba1c));
            app9.setItemNorPic(R.mipmap.ic_hba1c);
            app9.setItemHigPic(R.mipmap.ic_hba1c_sel);
            app9.setFragment(new MeasureGHbFragment());
            app9.setPosition(++position);
            app9.setParam0(KParamType.GHB_EAG);
            list.add(app9);
        }
        //血脂4项
        if ((this.deviceConfig & (0x01 << 15)) != 0) {
            MeasureItemBean app12 = new MeasureItemBean();
            app12.setItemName(UiUtils.getString(R.string.blood_fat));
            app12.setItemNorPic(R.mipmap.ic_ua);
            app12.setItemHigPic(R.mipmap.ic_ua_sel);
            app12.setFragment(new MeasureBloodFatFragment());
            app12.setPosition(++position);
            app12.setParam0(KParamType.BLOOD_FAT_LDL);
            list.add(app12);
        }

        //身高
        if ((measureConfig & (0x01 << 0)) != 0) {
            MeasureItemBean app13 = new MeasureItemBean();
            app13.setItemName(UiUtils.getString(R.string.height));
            app13.setItemNorPic(R.mipmap.ic_height);
            app13.setItemHigPic(R.mipmap.ic_height_sel);
            app13.setFragment(new MeasureHeightFragment());
            app13.setPosition(++position);
            app13.setParam0(KParamType.HEIGHT);
            list.add(app13);
        }
        //体重measureConfig
        if ((measureConfig & (0x01 << 1)) != 0) {
            MeasureItemBean app12 = new MeasureItemBean();
            app12.setItemName(UiUtils.getString(R.string.weight));
            app12.setItemNorPic(R.mipmap.ic_weight);
            app12.setItemHigPic(R.mipmap.ic_weight_sel);
            app12.setFragment(new MeasureWeightFragment());
            app12.setPosition(++position);
            app12.setParam0(KParamType.WEIGHT);
            list.add(app12);
        }
        return list;
    }

    @Override
    public int defaultFragment() {
        return 0;
    }

    @Override
    public Map<Integer, Fragment> getAllFragment(List<MeasureItemBean> measureItems) {
        Map<Integer, Fragment> map = new HashMap();
        if (measureItems != null) {
            for (MeasureItemBean bean : measureItems) {
                map.put(bean.getPosition(), bean.getFragment());
            }
        }
        return map;
    }

    @Override
    public void bindService() {
        // intent的action为康尚aidl服务器
        Intent intent = new Intent(UiUtils.getContent(), AIDLServer.class);
        view.startService(intent);
        view.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void unbindService(Context context) {
        if (aidlServer != null) {
            aidlServer.initMeasureBean(null);
            context.unbindService(serviceConnection);
        }
    }

    @Override
    public Service getAidlService() {
        return aidlServer;
    }

    @Override
    public void getPatient() {
        //暂时内置一个

    }

    @Override
    public void stopService() {
        view.stopService(new Intent(view, AIDLServer.class));
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlServer = ((AIDLServer.MsgBinder) service).getService();
            if (aidlServer.getMeasureDataBean() == null) {
                //向健康档案查询当天，如果有未上传记录就覆盖，如果没有就自动创建
                String idcard = ProviderReader.readCurrentPatient(aidlServer).getIdCard();
                MeasureDataBean measureDataBean = ProviderReader.readLatestMeasureData(
                        view, idcard);
                if (measureDataBean == null) {
                    measureDataBean = new MeasureDataBean();
                    measureDataBean.setIdcard(idcard);
                }
                aidlServer.initMeasureBean(measureDataBean);
            }
            checkMeasureDataBean(aidlServer.getMeasureDataBean());
            view.setMeasureData(aidlServer.getMeasureDataBean());
            aidlServer.setMeasureStatus(new AIDLServer.RefreshMeasureList() {
                @Override
                public void onRefresh(int param) {
                    view.updateMeasureData(param);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 检查测量数据中是否有已经进行过的测量项，如果有，则打勾
     * @param bean 测量数据
     */
    private void checkMeasureDataBean(MeasureDataBean bean) {
        if (bean == null) {
            return;
        }
        Class kParamCls = (Class) KParamType.class;
        Field[] fls = kParamCls.getFields();
        KParamType kParamType = new KParamType();
        for (Field field : fls) {
            String type = field.getType().toString();
            if (type.endsWith("int")) {
                try {
                    int param = field.getInt(kParamType);
                    if (bean.getTrendValue(param) != GlobalConstant.INVALID_DATA) {
                        view.updateMeasureData(param);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化测量数据
     * @param bean 测量数据
     */
    public void initMeasureData(MeasureDataBean bean) {
        if (aidlServer != null) {
            aidlServer.initMeasureBean(bean);
        }
    }
}
