package com.konsung.presenter.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.data.ProviderReader;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.UrtPresenter;
import com.konsung.service.AIDLServer;
import com.konsung.ui.base.ViewHolder;
import com.konsung.ui.fragment.MeasureUrtFragment;
import com.konsung.utils.KParamType;
import com.konsung.utils.UiUtils;
import com.konsung.utils.constant.GlobalConstant;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 尿常规逻辑实现
 **/

public class UrtPresenterImpl extends BasePresenter<UrtPresenter.View>
        implements UrtPresenter.Presenter {

    /**
     * 11项检查
     */
    public static final int FLAG_URT_ELEVEN = 11;
    /**
     * 13项检查
     */
    public static final int FLAG_URT_THIRTEEN = 13;
    /**
     * 14项检查
     */
    public static final int FLAG_URT_FOURTEEN = 14;

    //是否再测量中
    private int tempValue = GlobalConstant.INVALID_DATA;

    AIDLServer aidlServer;
    UrtPresenter.View view;

    /**
     * 构造函数
     * @param view 布局操作
     */
    public UrtPresenterImpl(UrtPresenter.View view) {
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
    public ArrayList<Integer> initUrtData() {

        //默认是14项，还有11项,需要读取配置
        int value = ProviderReader.getUrtConfig(((MeasureUrtFragment) view).getActivity());
//                SpUtils.getSpInt(GlobalConstant.SYS_CONFIG, GlobalConstant.DEVICE_CONFIG_TAG,
//                GlobalConstant.DEVICE_CONFIG);
        //EmpUi(恩普尿常规) URIT-31（优利特尿常规）Mission U120；（尿液分析仪）
//        if ((value & (0x01 << 1)) != 0 || (value & (0x01 << 9)) != 0
//                || (value & (0x01 << 10)) != 0) {
//        }

        ArrayList<Integer> loadItemKeys = new ArrayList<>();
        //先加载11项相同的
        //加载键
        loadItemKeys.add(KParamType.URINERT_LEU);
        loadItemKeys.add(KParamType.URINERT_NIT);
        loadItemKeys.add(KParamType.URINERT_UBG);
        loadItemKeys.add(KParamType.URINERT_PRO);
        loadItemKeys.add(KParamType.URINERT_PH);
        loadItemKeys.add(KParamType.URINERT_SG);
        loadItemKeys.add(KParamType.URINERT_BLD);
        loadItemKeys.add(KParamType.URINERT_KET);
        loadItemKeys.add(KParamType.URINERT_BIL);
        loadItemKeys.add(KParamType.URINERT_GLU);
        loadItemKeys.add(KParamType.URINERT_ASC);
        if ((value & (0x01 << 1)) != 0) {
            loadItemKeys.add(KParamType.URINERT_ALB);
            loadItemKeys.add(KParamType.URINERT_CRE);
            loadItemKeys.add(KParamType.URINE_AC);
        } else if ((value & (0x01 << 2)) != 0) {
            loadItemKeys.add(KParamType.URINERT_ALB);
            loadItemKeys.add(KParamType.URINERT_CRE);
            loadItemKeys.add(KParamType.URINERT_CA);
        }
        return loadItemKeys;
    }

    @Override
    public HashMap<Integer, String> initUrtCodes() {
        HashMap<Integer, String> loadItemCodes = new HashMap<Integer, String>();
        loadItemCodes.put(KParamType.URINERT_LEU, UiUtils.getString(R.string.urt_code_leu));
        loadItemCodes.put(KParamType.URINERT_NIT, UiUtils.getString(R.string.urt_code_nit));
        loadItemCodes.put(KParamType.URINERT_UBG, UiUtils.getString(R.string.urt_code_ubg));
        loadItemCodes.put(KParamType.URINERT_PRO, UiUtils.getString(R.string.urt_code_pro));
        loadItemCodes.put(KParamType.URINERT_PH, UiUtils.getString(R.string.urt_code_ph));
        loadItemCodes.put(KParamType.URINERT_SG, UiUtils.getString(R.string.urt_code_sg));
        loadItemCodes.put(KParamType.URINERT_BLD, UiUtils.getString(R.string.urt_code_bld));
        loadItemCodes.put(KParamType.URINERT_KET, UiUtils.getString(R.string.urt_code_ket));
        loadItemCodes.put(KParamType.URINERT_BIL, UiUtils.getString(R.string.urt_code_bil));
        loadItemCodes.put(KParamType.URINERT_GLU, UiUtils.getString(R.string.urt_code_glu));
        loadItemCodes.put(KParamType.URINERT_ASC, UiUtils.getString(R.string.urt_code_vc));
        loadItemCodes.put(KParamType.URINERT_ALB, UiUtils.getString(R.string.urt_code_ma));
        loadItemCodes.put(KParamType.URINERT_CRE, UiUtils.getString(R.string.urt_code_cr));
        loadItemCodes.put(KParamType.URINERT_CA, UiUtils.getString(R.string.urt_code_ca));
        loadItemCodes.put(KParamType.URINE_AC, "");
        return loadItemCodes;
    }

    @Override
    public HashMap<Integer, String> initUrtNames() {
        HashMap<Integer, String> loadItemNames = new HashMap<>();
        int config = ProviderReader.getDeviceConfig(view.getContent());
        loadItemNames.put(KParamType.URINERT_LEU, UiUtils.getString(R.string.urt_name_leu));
        loadItemNames.put(KParamType.URINERT_NIT, UiUtils.getString(R.string.urt_name_nit));
        loadItemNames.put(KParamType.URINERT_UBG, UiUtils.getString(R.string.urt_name_ubg));
        loadItemNames.put(KParamType.URINERT_PRO, UiUtils.getString(R.string.urt_name_pro));
        loadItemNames.put(KParamType.URINERT_PH, UiUtils.getString(R.string.urt_name_ph));
        loadItemNames.put(KParamType.URINERT_SG, UiUtils.getString(R.string.urt_name_sg));
        loadItemNames.put(KParamType.URINERT_KET, UiUtils.getString(R.string.urt_name_ket));
        loadItemNames.put(KParamType.URINERT_BIL, UiUtils.getString(R.string.urt_name_bil));
        loadItemNames.put(KParamType.URINERT_GLU, UiUtils.getString(R.string.urt_name_glu));
        if ((config & (0x01 << 10)) != 0) {
            loadItemNames.put(KParamType.URINERT_ASC, UiUtils.getString(R.string.urt_name_vc_u120));
            loadItemNames.put(KParamType.URINERT_BLD, UiUtils.getString(
                    R.string.urt_name_bld_u120));
        } else {
            loadItemNames.put(KParamType.URINERT_ASC, UiUtils.getString(R.string.urt_name_vc));
            loadItemNames.put(KParamType.URINERT_BLD, UiUtils.getString(R.string.urt_name_bld));
        }
        loadItemNames.put(KParamType.URINERT_ALB, UiUtils.getString(R.string.urt_name_ma));
        loadItemNames.put(KParamType.URINERT_CRE, UiUtils.getString(R.string.urt_name_cr));
        loadItemNames.put(KParamType.URINERT_CA, UiUtils.getString(R.string.urt_name_ca));
        loadItemNames.put(KParamType.URINE_AC, UiUtils.getString(R.string.urt_name_ac));
        return loadItemNames;
    }

    @Override
    public UrtHolder getParamLayout(Context context, ViewGroup root) {
        return new UrtHolder(context, R.layout.item_urt_check, root);
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
                    switch (param) {
                        case KParamType.URINERT_PH:
                        case KParamType.URINERT_UBG:
                        case KParamType.URINERT_BLD:
                        case KParamType.URINERT_PRO:
                        case KParamType.URINERT_KET:
                        case KParamType.URINERT_NIT:
                        case KParamType.URINERT_GLU:
                        case KParamType.URINERT_BIL:
                        case KParamType.URINERT_SG:
                        case KParamType.URINERT_LEU:
                        case KParamType.URINERT_ASC:
                        case KParamType.URINERT_ALB:
                        case KParamType.URINERT_CRE:
                        case KParamType.URINERT_CA:
                        case KParamType.URINE_AC:
                            view.setValue(param, value);
                            aidlServer.saveTrend(param, value);
                            aidlServer.saveToDb2();
                            break;
                        //兼容恩普尿机的VC
                        case KParamType.URINERT_VC:
                            view.setValue(KParamType.URINERT_ASC, value);
                            aidlServer.saveTrend(KParamType.URINERT_ASC, value);
                            aidlServer.saveToDb2();
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

    /**
     * 尿常规布局持有者
     */
    public class UrtHolder extends ViewHolder {

        public View view;
        @InjectView(R.id.num_tx)
        public TextView txNum;
        @InjectView(R.id.code_tx)
        public TextView txCode;
        @InjectView(R.id.tv_name)
        public TextView txName;
        @InjectView(R.id.result_tx)
        public TextView txResult;
        @InjectView(R.id.flag_tx)
        public TextView imgFlag;
        @InjectView(R.id.reference_tx)
        public TextView txReference;

        /**
         * 构造器
         * @param context 上下文
         * @param id 布局资源文件id
         * @param root 父布局
         */
        public UrtHolder(Context context, @LayoutRes int id, ViewGroup root) {
            super(context, id, root);
            view = LayoutInflater.from(context).inflate(id, root, false);
            view.setPadding(0, 8, 0, 0);
            ButterKnife.inject(this, view);
        }
    }
}
