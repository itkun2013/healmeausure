package com.konsung.ui.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.konsung.R;
import com.konsung.adapter.HealthMeasureAdapter;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.MeasureItemBean;
import com.konsung.data.ProviderReader;
import com.konsung.presenter.MeasurePresenter;
import com.konsung.presenter.impl.MeasurePresenterImpl;
import com.konsung.sqlite.DBManager;
import com.konsung.ui.base.BaseActivity;
import com.konsung.utils.BroadcastUtils;
import com.konsung.utils.KParamType;
import com.konsung.utils.ReferenceUtils;
import com.konsung.utils.ToastUtils;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 健康测量主界面
 */
public class MeasureActivity extends BaseActivity<MeasurePresenterImpl> implements
        MeasurePresenter.View {

    /**
     * 血氧探头状态
     * 血氧探头状态在AIDLService对接AppDevice时发送一次,和血氧探头状态发生改变时发送
     * 因为要获取到初始状态，所以需要缓存
     */
    public static int probeSpo2Status = GlobalConstant.INVALID_DATA;

    /**
     * 心电探头状态
     * 心电探头状态状态每隔一秒才发一次，ECG页面可能会出现页面闪动情况
     * 因为要获取到初始状态，所以需要缓存
     */
    public static int probeEcgStatus = GlobalConstant.INVALID_DATA;

    public static boolean isCheckingEcg = false; //在心电测量中禁止其他操作
    public static int ecgCheckTimes = 0; //心电测量剩余时间
    @InjectView(R.id.lv_measure)
    ListView lvMeasure;

    private int showFragment; //当前显示的fragment
    private Map<Integer, Fragment> allFragment; //所有测量项的碎片
    private HealthMeasureAdapter healthMeasureAdapter;
    private MeasureDataBean measureDataBean;
    List<MeasureItemBean> measureItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        bindEvent();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        displayMetrics.scaledDensity = displayMetrics.density;
    }

    @Override
    protected void onStart() {
        presenter.bindService();
        overridePendingTransition(0, 0);
        super.onStart();
        ReferenceUtils.sendCitizenDetailToAppDevice(this);
        //通知启动管理，健康测量界面已经显示
        BroadcastUtils.sentHealthMeasureStatusToStartManager(this);
        init();
//        //TODO-跳出其它界面以后，返回本界面，需要情况测量数据
//        measureDataBean = presenter.refreshMeasureDataBean(this);

        overridePendingTransition(0, 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConstant.ACTION_PATIENT_CHANGE);
        registerReceiver(patientChangeReceiver, filter);
    }

    /**
     * 初始化
     */
    private void init() {
        //初始化界面有变化时才进行重新加载，否则不刷新界面
        List<MeasureItemBean> measureItems = presenter.getMeasureItems();
        if (null != measureItems) {
            this.measureItems = measureItems;
            allFragment = presenter.getAllFragment(measureItems);
            switchToFragment(presenter.defaultFragment());
            healthMeasureAdapter = new HealthMeasureAdapter(this, this.measureItems);
            lvMeasure.setVerticalScrollBarEnabled(false);
            lvMeasure.setAdapter(healthMeasureAdapter);
        }
    }

    /**
     * 绑定点击事件
     */
    private void bindEvent() {
        lvMeasure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isCheckingEcg) {
                    ToastUtils.showShortToast(getString(R.string.ecg_checking) +
                            ecgCheckTimes + "s");
                } else {
                    if (healthMeasureAdapter != null) {
                        healthMeasureAdapter.setSelectItem(position);
                    }
                    switchToFragment(position);
                }
            }
        });
    }

    @Override
    public void switchToFragment(int position) {
        // TODO 当前的Fragment没显示就切换,还要更换背景效果
        showFragment = position;
        Fragment fragment = allFragment.get(position);
        if (fragment != null && !fragment.isVisible()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fl_measure, fragment, fragment.getClass().getName());
            ft.commit();
        }
    }

    @Override
    public void setMeasureData(MeasureDataBean bean) {
        this.measureDataBean = bean;
    }

    @Override
    public void updateMeasureData(int param) {
        if (healthMeasureAdapter != null) {
            for (MeasureItemBean bean : measureItems) {
                if (bean.getParam0() == param) {
                    bean.setMeasureFinish(true);
                } else if (bean.getParam1() == param) { //兼容百捷三合一
                    bean.setMeasureFinish1(true);
                } else if (bean.getParam2() == param) { //兼容百捷三合一
                    bean.setMeasureFinish2(true);
                    //兼容餐前后血糖
                }
                //兼容餐后单血糖
                if (bean.getParam1() == param && param == KParamType.BLOODGLU_BEFORE_MEAL) {
                    bean.setMeasureFinish(true);
                }
            }
            healthMeasureAdapter.updateMeasureData(measureItems);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isCheckingEcg) {
            ToastUtils.showShortToast(R.string.ecg_checking);
        } else {
            clickKeyCode(keyCode);
        }
        return true;
    }

    /**
     * 点击物理按键的方法
     * @param keyCode 按键的code
     */
    private void clickKeyCode(int keyCode) {
        switch (keyCode) {
            //血氧
            case 266: //物理按键传过来的keycode
                switchToFragment(2); // adapter对应碎片的索引
                if (healthMeasureAdapter != null) {
                    healthMeasureAdapter.setSelectItem(2);
                }
                break;
            //心电
            case 267:
                switchToFragment(1);
                if (healthMeasureAdapter != null) {
                    healthMeasureAdapter.setSelectItem(1);
                }
                break;
            //血压
            case 268:
                switchToFragment(3);
                if (healthMeasureAdapter != null) {
                    healthMeasureAdapter.setSelectItem(3);
                }
                break;
            //血糖
            case 269:
                switchToFragment(5);
                if (healthMeasureAdapter != null) {
                    healthMeasureAdapter.setSelectItem(5);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.bindService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unbindService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //提醒健康档案上传数据
        unregisterReceiver(patientChangeReceiver);
        BroadcastUtils.sentUploadFlagToHealthFile(this);
    }

    @Override
    public void onDestroy() {
        DBManager.getDBHelper(this).close();
        DBManager.releaseHelper(this, presenter.getAidlService());
        super.onDestroy();
    }

    BroadcastReceiver patientChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case GlobalConstant.ACTION_PATIENT_CHANGE:
                    //TODO 下发居民信息，体重身高无法获取
                    ReferenceUtils.sendCitizenDetailToAppDevice(MeasureActivity.this);
                    //重新设置测量信息
                    String idcard = ProviderReader.readCurrentPatient(UiUtils.getContent())
                            .getIdCard();
                    MeasureDataBean measureDataBean = ProviderReader.readLatestMeasureData(
                            context, idcard);
                    presenter.initMeasureData(measureDataBean);
                    //初始化界面
                    init();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public MeasurePresenterImpl initPresenter() {
        return new MeasurePresenterImpl(this);
    }
}
