package com.konsung.utils.constant;

/**
 * 常量类
 */
public interface GlobalConstant {
    int STATE_ERROR = 2;
    int STATE_EMPTY = 3;
    int STATE_SUCCESS = 4;

    int INVALID_DATA = -1000; //无效趋势值
    //sharedPreferences字段
    String AIDL_SERVICE_NAME = "com.konsung.aidlServer";
    String REFRESH_IP = "refresh_ip";
    String REFRESH_ADRESS = "yun.konsung.net";
    String SP_DEVICE_CONFIG = "device_config"; //首先项的参数
    String SYS_CONFIG = "sys_config"; //系统首先项的参数
    String APP_CONFIG = "app_config"; //app首先项的参数
    String DEVICE_CONFIG_TAG = "device_config"; //记录系统信息的tag
    int DATABASE_VERSION = 1; //数据库版本号
    String IDCARD = "idcard";
    String SERVICE_IP = "ip";
    String CLOUD_IP = "cloudip"; //云平台ip标记
    String CLOUD_IP_PORT = "cloudipport"; //云平台端口标记
    String CIP_PORT = "9028";
    String SERVER_ADDRESS = "ServerAddress"; //服务器地址标记
    String CLOUD_ADDRESS = "CloudAddress"; //云平台地址标记
    String FULL_ADDRESS = "full_address"; //完整上传地点
    String DETAIL_ADDRESS = "detail_address"; //上传地址的详细地址
    String ORGANIZATION_NAME = "organization_name"; //机构名称
    String SERVER_FIX_ADDRESS =
            "/imms-web/services/VillageHealthPort"; //服务器默认地址
    String CLOUD_FIX_ADDRESS =
            "/cloud/services/VillageHealthPort"; //云平台默认地址

    String APP_ISREFRESH = "app_isrefresh"; //记录是否更新的数据
    String REFRESH_IP_PORT = "refresh_ip_port"; //软件更新的存放端口的标记
    String REFRESH_ADRESS_PORT = "9028"; //默认的软件更新端口
    String CRASHLOGPATH = "KonsungLog";
    //服务器地址存放的标记
    String IP_PROT = "ip_port";
    long MAX_DATA = 209715200; //内存储过低的标准 等于200M

    // 设备配置信息
    // 根据协议对应关系如下：bit0-KSM5;bit1-EmpUi;bit2-GA7;bit3-IDA007;
    // bit4-HTD8819;bit5-TH809;bit6-BeneCheckGlu;bit7-BeneCheck;bit8-OGM111;
    // bit9-URIT-31;bit10-Mission U120;bit11-HemoCue WBC;bit12-HemoCue Hb;
    // bit13-URIT-12;bit14-Mission Hb;bit15-艾康血脂
//    public static final int DEVICE_CONFIG = 0x189F;
    int DEVICE_CONFIG = 0xFFFFFFFF;
    int URINE_CONFIG = 0xFF;
    int QUICK_CONFIG = -0x01;

    String CURRENT_LOGED_DOC_NAME = "";
    String CURRENT_LOGED_ORG_ID = "";

    // 网络数据的放大倍数，趋势数据统一为10000
    int TREND_FACTOR = 100;
    // 尿常规网络数据的放大倍数，趋势数据统一为100
    float FACTOR = 100f;
    float SG_FACTOR = 1000f;
    //根据APPDevice协议，上传值是10^6，显示值时10^9,需要先除以1000，再除以比例
    float WBC_FACTOR = 1000 * FACTOR;

    // 网络端口号
    int PORT = 6613;
    // 网络命令字
    byte PARA_STATUS = 0x12;
    byte NET_TREND = 0x51;
    byte NET_WAVE = 0x52;
    byte NET_ECG_CONFIG = 0x21;
    byte NET_RESP_CONFIG = 0x22;
    byte NET_TEMP_CONFIG = 0x23;
    byte NET_SPO2_CONFIG = 0x24;
    byte NET_NIBP_CONFIG = 0x25;

    byte NET_12LEAD_DIAG_RESULT = 0x60; // 12导心电结果包

    byte NET_DEVICE_CONFIG = 0x70;

    byte NET_PATIENT_CONFIG = 0x11;

    // 无效字符串数据,空字符串
    String INVALID_STRING_DATA = "";
    String INVALID_STRING = "-?-";
    String BENECHECK_LAYOUT = "bene_check_layout"; //百捷三合一界面
    String DATABASE_TABLE_NAME = "t_patient";
    String AUTHORITY_DEVICE_CONFIG = "content://com.konsung.factorymaintain/deviceConfig";
    String AUTHORITY_MEASURE_CONFIG = "content://com.konsung.factorymaintain/measureConfig";
    String AUTHORITY_URT_CONFIG = "content://com.konsung.factorymaintain/urtSetting";
    String QUCIK_CHECK_PAGE_URI_CONFIG = "content://com.konsung.factorymaintain/quickConfig";
    String QUICK_CHECK_HEIGHT_WIGHT_CONFIG = "content://com.konsung" +
            ".factorymaintain/measureConfig"; //获取身高体重是否配置
    String QUICK_CHECK_URINE_CONFIG = "content://com.konsung.factorymaintain/urtSetting";
    String QUICK_CURRENT_PATIENT = "content://com.konsung.healthfile/currentPatient";
    String CONFIG = "config"; //数据类型 配置参数

    //自动上传广播
    String AUTOUPLOAD = "com.konsung.healthfile.autouploadreceiver";

    String DATABASE_FILE = "/storage/emulated/0/Konsung/HealthFiles/Database/"; //数据库存放路径

    /**
     * 保存测量数据
     */
    String KEY_SAVE_MEASURE_DATA = "MeasureDataBean";
    //新增测量数据
    String URI_ADD_MEASURE_DATA = "content://com.konsung.healthfile/addMeasure";
    //更新测量数据
    String URI_UPDATE_MEASURE_DATA = "content://com.konsung.healthfile/updateMeasure";
    //查询测量数据
    String URI_QUERY_MEASURE_DATA = "content://com.konsung.healthfile/queryMeasureData";
    //查询最新10条测量数据
    String URI_QUERY_LATEST_TEN_MEASURE_DATA = "content://com.konsung" +
            ".healthfile/queryMeasureData10";

    String ACTION_PATIENT_CHANGE = "com.konsung.healthfile.replacepatient";

    //按钮点击间隔
    long CLICK_TIMES = 1000;
    String APPID = "3de9dc4a26";
}
