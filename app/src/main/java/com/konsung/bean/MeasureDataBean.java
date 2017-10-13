package com.konsung.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.konsung.utils.KParamType;
import com.konsung.utils.UUIDGenerator;
import com.konsung.utils.constant.GlobalConstant;

import java.util.ArrayList;
import java.util.Date;

/**
 * 测量数据实体
 * 用来封装测量时的数据
 * 主要MeasureDataByPatientListAdapter传递数据
 */
@DatabaseTable(tableName = "t_measuredata")
public class MeasureDataBean {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String uuid; //记录数据的唯一标示uuid
    @DatabaseField
    private String gluStyle = ""; //测量方式：0:为空腹血糖，1为随机血糖，2为餐后血糖
    @DatabaseField
    private boolean uploadFlag; //记录是否上传的标准
    @DatabaseField
    private String idcard = ""; //用身份证作为绑定用户的依据
    @DatabaseField
    private int ecgHr = GlobalConstant.INVALID_DATA; // 心电图心率
    @DatabaseField
    private int ecgBr = GlobalConstant.INVALID_DATA; // 心电图呼吸
    @DatabaseField
    private int tAxis = GlobalConstant.INVALID_DATA; // 心电t波心电轴 单位°
    @DatabaseField
    private int pAxis = GlobalConstant.INVALID_DATA; // 心电P波心电轴 单位°
    @DatabaseField
    private int qrsAxis = GlobalConstant.INVALID_DATA; // 心电qrs波心电轴 单位°
    @DatabaseField
    private int qrs = GlobalConstant.INVALID_DATA; // 心电qrs间期
    @DatabaseField
    private int pr = GlobalConstant.INVALID_DATA; // 心电pr间期
    @DatabaseField
    private int qt = GlobalConstant.INVALID_DATA; // 心电qt间期
    @DatabaseField
    private int qtc = GlobalConstant.INVALID_DATA; // 心电qtc间期
    @DatabaseField
    private int sv1 = GlobalConstant.INVALID_DATA; // 心电sv1 单位mV
    @DatabaseField
    private int rv5 = GlobalConstant.INVALID_DATA; // 心电rv5 单位mV
    @DatabaseField
    private String ecgDiagnoseResult = "";
    @DatabaseField
    private int spo2Tred = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int spo2Pr = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int nibpSys = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int nibpDia = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int nibpPr = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int temp = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int irtemp = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int urinertLeu = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int urinertNit = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int urinertUbg = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int urinertPro = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int urinertPh = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int urinertBld = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int urinertSg = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int urinertBil = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int urinertKet = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int urinertGlu = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int urinertAsc = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int urinertAlb = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int urinertCre = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int urinertCa = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int bloodgluBeforeMeal = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int bloodgluAfterMeal = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int bloodWbc = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int bloodHgb = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int bloodHct = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int uricacidTrend = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int cholesterolTrend = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int height = GlobalConstant.INVALID_DATA; //身高
    @DatabaseField
    private int weight = GlobalConstant.INVALID_DATA; // 体重
    @DatabaseField
    private int bloodFatCho = GlobalConstant.INVALID_DATA; // 血脂总胆固醇
    @DatabaseField
    private int bloodFatTrig = GlobalConstant.INVALID_DATA; // 血脂甘油三脂
    @DatabaseField
    private int bloodFatLdl = GlobalConstant.INVALID_DATA; // 血脂低密度脂蛋白
    @DatabaseField
    private int bloodFatHdl = GlobalConstant.INVALID_DATA; // 血脂高密度脂蛋白
    @DatabaseField
    private int urinertAc = GlobalConstant.INVALID_DATA; //A:C值（u60 11+2项特有）

    @DatabaseField
    private boolean isHaveData; //是否有测量数据。true有数据

    @DatabaseField
    private String ecgWaveI = "";

    @DatabaseField
    private String ecgWaveIi = "";

    @DatabaseField
    private String ecgWaveIii = "";

    @DatabaseField
    private String ecgWaveAvr = "";

    @DatabaseField
    private String ecgWaveAvl = "";

    @DatabaseField
    private String ecgWaveAvf = "";

    @DatabaseField
    private String ecgWaveV1 = "";

    @DatabaseField
    private String ecgWaveV2 = "";

    @DatabaseField
    private String ecgWaveV3 = "";

    @DatabaseField
    private String ecgWaveV4 = "";

    @DatabaseField
    private String ecgWaveV5 = "";

    @DatabaseField
    private String ecgWaveV6 = "";

    @DatabaseField
    private int waveNum = 0;

    @DatabaseField
    private int nibpMap = GlobalConstant.INVALID_DATA;

    //健康测量时间
    @DatabaseField
    private Date measureTime = new Date();

    //体检建表时间
    @DatabaseField
    private Date checkDay = new Date(); //检测时间
    @DatabaseField
    private String doctor = ""; //责任医师
    @DatabaseField
    private String waistline = ""; //腰围
    @DatabaseField
    private int respPa = GlobalConstant.INVALID_DATA; //呼吸频率
    @DatabaseField
    private String mammarygland = ""; //乳腺
    @DatabaseField
    private String mammaryglanderr = "";
    @DatabaseField
    private String vulvaerr = "";
    @DatabaseField
    private String vaginaerr = "";
    @DatabaseField
    private String cervicalerr = "";
    @DatabaseField
    private String palaceerr = "";
    @DatabaseField
    private String attach = "";
    @DatabaseField
    private String attacherr = "";
    @DatabaseField
    private int ghbNGSP = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int ghbIFCC = GlobalConstant.INVALID_DATA;
    @DatabaseField
    private int ghbEGA = GlobalConstant.INVALID_DATA;
    private int ecgSize = 10;
    //字節緩存
    private ArrayList<String> wave1;
    private ArrayList<String> wave2;
    private ArrayList<String> wave3;
    private ArrayList<String> wave4;
    private ArrayList<String> wave5;
    private ArrayList<String> wave6;
    private ArrayList<String> wave7;
    private ArrayList<String> wave8;
    private ArrayList<String> wave9;
    private ArrayList<String> wave10;
    private ArrayList<String> wave11;
    private ArrayList<String> wave12;

    // 构造器
    public MeasureDataBean() {
        wave1 = new ArrayList<>();
        wave2 = new ArrayList<>();
        wave3 = new ArrayList<>();
        wave4 = new ArrayList<>();
        wave5 = new ArrayList<>();
        wave6 = new ArrayList<>();
        wave7 = new ArrayList<>();
        wave8 = new ArrayList<>();
        wave9 = new ArrayList<>();
        wave10 = new ArrayList<>();
        wave11 = new ArrayList<>();
        wave12 = new ArrayList<>();
        //为uuid赋值
        uuid = UUIDGenerator.getUUID();
        doctor = "林家栋"; //主干代码未有服务器获取测量医生因此默认为林家栋
    }

    public String getEcgWave(int param) {
        String str = "";
        switch (param) {
            case 1:
                str = ecgWaveI;

                break;
            case 2:
                str = ecgWaveIi;
                break;
            case 3:
                str = ecgWaveIii;
                break;
            case 4:
                str = ecgWaveAvr;
                break;
            case 5:
                str = ecgWaveAvl;
                break;
            case 6:
                str = ecgWaveAvf;
                break;
            case 7:
                str = ecgWaveV1;
                break;
            case 8:
                str = ecgWaveV2;
                break;
            case 9:
                str = ecgWaveV3;
                break;
            case 10:
                str = ecgWaveV4;
                break;
            case 11:
                str = ecgWaveV5;
                break;
            case 12:
                str = ecgWaveV6;
                break;
        }
        return str;
    }

    /**
     * 清空心电数据
     */
    public void initEcgWave() {
        wave1.clear();
        wave2.clear();
        wave3.clear();
        wave4.clear();
        wave5.clear();
        wave6.clear();
        wave7.clear();
        wave8.clear();
        wave9.clear();
        wave10.clear();
        wave11.clear();
        wave12.clear();
    }

    /**
     * ecg数据会一直发送，保存10秒钟的数据
     * @param param
     * @param wave wave
     */
    public void setEcgWave(int param, String wave) {

        switch (param) {
            case 1:
                wave1.add(wave);
                if (wave1.size() > ecgSize) {
                    wave1.remove(0);
                }
                ecgWaveI = "";
                for (int i = 0; i < wave1.size(); i++) {
                    ecgWaveI += wave1.get(i);
                }
                break;
            case 2:
                wave2.add(wave);
                if (wave2.size() > ecgSize) {

                    wave2.remove(0);
                }
                ecgWaveIi = "";
                for (int i = 0; i < wave2.size(); i++) {
                    ecgWaveIi += wave2.get(i);
                }
                break;
            case 3:
                wave3.add(wave);
                if (wave3.size() > ecgSize) {
                    wave3.remove(0);
                }
                ecgWaveIii = "";
                for (int i = 0; i < wave3.size(); i++) {
                    ecgWaveIii += wave3.get(i);
                }
                break;
            case 4:
                wave4.add(wave);
                if (wave4.size() > ecgSize) {
                    wave4.remove(0);
                }
                ecgWaveAvr = "";
                for (int i = 0; i < wave4.size(); i++) {
                    ecgWaveAvr += wave4.get(i);
                }
                break;
            case 5:
                wave5.add(wave);
                if (wave5.size() > ecgSize) {
                    wave5.remove(0);
                }
                ecgWaveAvl = "";
                for (int i = 0; i < wave5.size(); i++) {
                    ecgWaveAvl += wave5.get(i);
                }
                break;
            case 6:
                wave6.add(wave);
                if (wave6.size() > ecgSize) {
                    wave6.remove(0);
                }
                ecgWaveAvf = "";
                for (int i = 0; i < wave6.size(); i++) {
                    ecgWaveAvf += wave6.get(i);
                }
                break;
            case 7:
                wave7.add(wave);
                if (wave7.size() > ecgSize) {
                    wave7.remove(0);
                }
                ecgWaveV1 = "";
                for (int i = 0; i < wave7.size(); i++) {
                    ecgWaveV1 += wave7.get(i);
                }
                break;
            case 8:
                wave8.add(wave);
                if (wave8.size() > ecgSize) {
                    wave8.remove(0);
                }
                ecgWaveV2 = "";
                for (int i = 0; i < wave8.size(); i++) {
                    ecgWaveV2 += wave8.get(i);
                }
                break;
            case 9:
                wave9.add(wave);
                if (wave9.size() > ecgSize) {
                    wave9.remove(0);
                }
                ecgWaveV3 = "";
                for (int i = 0; i < wave9.size(); i++) {
                    ecgWaveV3 += wave9.get(i);
                }
                break;
            case 10:
                wave10.add(wave);
                if (wave10.size() > ecgSize) {
                    wave10.remove(0);
                }
                ecgWaveV4 = "";
                for (int i = 0; i < wave10.size(); i++) {
                    ecgWaveV4 += wave10.get(i);
                }
                break;
            case 11:
                wave11.add(wave);
                if (wave11.size() > ecgSize) {
                    wave11.remove(0);
                }
                ecgWaveV5 = "";
                for (int i = 0; i < wave11.size(); i++) {
                    ecgWaveV5 += wave11.get(i);
                }
                break;
            case 12:
                wave12.add(wave);
                if (wave12.size() > ecgSize) {
                    wave12.remove(0);
                }
                ecgWaveV6 = "";
                for (int i = 0; i < wave12.size(); i++) {
                    ecgWaveV6 += wave12.get(i);
                }
                break;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTrendValue(int param, int value) {
        switch (param) {
            case KParamType.ECG_HR:
                ecgHr = value;
                break;
            case KParamType.SPO2_TREND:
                spo2Tred = value;
                break;
            case KParamType.SPO2_PR:
                spo2Pr = value;
                break;
            case KParamType.NIBP_SYS:
                nibpSys = value;
                break;
            case KParamType.NIBP_DIA:
                nibpDia = value;
                break;
            case KParamType.NIBP_PR:
                nibpPr = value;
                break;
            case KParamType.TEMP_T1:
                temp = value;
                break;
            case KParamType.IRTEMP_TREND:
                irtemp = value;
                break;
            case KParamType.NIBP_MAP:
                nibpMap = value;
                break;
            case KParamType.BLOODGLU_AFTER_MEAL:
                bloodgluAfterMeal = value;
                break;
            case KParamType.BLOODGLU_BEFORE_MEAL:
                bloodgluBeforeMeal = value;
                break;
            case KParamType.URINERT_BIL:
                urinertBil = value;
                break;
            case KParamType.URINERT_BLD:
                urinertBld = value;
                break;
            case KParamType.URINERT_GLU:
                urinertGlu = value;
                break;
            case KParamType.URINERT_KET:
                urinertKet = value;
                break;
            case KParamType.URINERT_LEU:
                urinertLeu = value;
                break;
            case KParamType.URINERT_NIT:
                urinertNit = value;
                break;
            case KParamType.URINERT_PH:
                urinertPh = value;
                break;
            case KParamType.URINERT_PRO:
                urinertPro = value;
                break;
            case KParamType.URINERT_SG:
                urinertSg = value;
                break;
            case KParamType.URINERT_UBG:
                urinertUbg = value;
                break;
            case KParamType.URINERT_ASC:
                urinertAsc = value;
                break;
            case KParamType.URINERT_ALB:
                urinertAlb = value;
                break;
            case KParamType.URINERT_CRE:
                urinertCre = value;
                break;
            case KParamType.URINERT_CA:
                urinertCa = value;
                break;
            case KParamType.BLOOD_WBC:
                bloodWbc = value;
                break;
            case KParamType.BLOOD_HGB:
                bloodHgb = value;
                break;
            case KParamType.BLOOD_HCT:
                bloodHct = value;
                break;
            case KParamType.URICACID_TREND:
                uricacidTrend = value;
                break;
            case KParamType.CHOLESTEROL_TREND:
                cholesterolTrend = value;
                break;
            case KParamType.RESP_RR:
                ecgBr = value;
                break;
            case KParamType.BLOOD_FAT_CHO:
                bloodFatCho = value;
                break;
            case KParamType.BLOOD_FAT_HDL:
                bloodFatHdl = value;
                break;
            case KParamType.BLOOD_FAT_LDL:
                bloodFatLdl = value;
                break;
            case KParamType.BLOOD_FAT_TRIG:
                bloodFatTrig = value;
                break;
            case KParamType.WEIGHT:
                weight = value;
                break;
            case KParamType.HEIGHT:
                height = value;
                break;
            case KParamType.GHB_EAG:
                ghbEGA = value;
                break;
            case KParamType.GHB_HBA1C_IFCC:
                ghbIFCC = value;
                break;
            case KParamType.GHB_HBA1C_NGSP:
                ghbNGSP = value;
                break;
            case KParamType.URINE_AC:
                urinertAc = value;
                break;
            default:
                break;
        }
    }

    /**
     * 根据参数值获取测量值
     * @param param 参数
     * @return 测量值
     */
    public int getTrendValue(int param) {
        switch (param) {
            case KParamType.ECG_HR:
                return ecgHr;
            case KParamType.SPO2_PR:
                return spo2Pr;
            case KParamType.SPO2_TREND:
                return spo2Tred;
            case KParamType.NIBP_SYS:
                return nibpSys;
            case KParamType.NIBP_DIA:
                return nibpDia;
            case KParamType.NIBP_PR:
                return nibpPr;
            case KParamType.TEMP_T1:
                return temp;
            case KParamType.IRTEMP_TREND:
                return irtemp;
            case KParamType.NIBP_MAP:
                return nibpMap;
            case KParamType.BLOODGLU_AFTER_MEAL:
                return bloodgluAfterMeal;
            case KParamType.BLOODGLU_BEFORE_MEAL:
                return bloodgluBeforeMeal;
            case KParamType.URINERT_BIL:
                return urinertBil;
            case KParamType.URINERT_BLD:
                return urinertBld;
            case KParamType.URINERT_GLU:
                return urinertGlu;
            case KParamType.URINERT_KET:
                return urinertKet;
            case KParamType.URINERT_LEU:
                return urinertLeu;
            case KParamType.URINERT_NIT:
                return urinertNit;
            case KParamType.URINERT_PH:
                return urinertPh;
            case KParamType.URINERT_PRO:
                return urinertPro;
            case KParamType.URINERT_SG:
                return urinertSg;
            case KParamType.URINERT_UBG:
                return urinertUbg;
            case KParamType.URINERT_ASC:
                return urinertAsc;
            case KParamType.URINERT_ALB:
                return urinertAlb;
            case KParamType.URINERT_CRE:
                return urinertCre;
            case KParamType.URINERT_CA:
                return urinertCa;
            case KParamType.URINE_AC:
                return urinertAc;
            case KParamType.BLOOD_WBC:
                return bloodWbc;
            case KParamType.BLOOD_HGB:
                return bloodHgb;
            case KParamType.BLOOD_HCT:
                return bloodHct;
            case KParamType.RESP_RR:
                return ecgBr;
            case KParamType.URICACID_TREND:
                return uricacidTrend;
            case KParamType.CHOLESTEROL_TREND:
                return cholesterolTrend;
            case KParamType.BLOOD_FAT_CHO:
                return bloodFatCho;
            case KParamType.BLOOD_FAT_HDL:
                return bloodFatHdl;
            case KParamType.BLOOD_FAT_LDL:
                return bloodFatLdl;
            case KParamType.BLOOD_FAT_TRIG:
                return bloodFatTrig;
            case KParamType.WEIGHT:
                return weight;
            case KParamType.HEIGHT:
                return height;
            case KParamType.GHB_EAG:
                return ghbEGA;
            case KParamType.GHB_HBA1C_IFCC:
                return ghbIFCC;
            case KParamType.GHB_HBA1C_NGSP:
                return ghbNGSP;
            default:
                return -1000;
        }
    }

    public String getEcgDiagnoseResult() {
        return ecgDiagnoseResult;
    }

    public void setEcgDiagnoseResult(String ecgDiagnoseResult) {
        this.ecgDiagnoseResult = ecgDiagnoseResult;
    }

    public boolean isUploadFlag() {
        return uploadFlag;
    }

    public void setUploadFlag(boolean uploadFlag) {
        this.uploadFlag = uploadFlag;
    }

    public boolean isHaveData() {
        return isHaveData;
    }

    public void setHaveData(boolean haveData) {
        isHaveData = haveData;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setGluStyle(String gluStyle) {
        this.gluStyle = gluStyle;
    }

    public String getUuid() {
        return uuid;
    }

    public int gettAxis() {
        return tAxis;
    }

    public void settAxis(int tAxis) {
        this.tAxis = tAxis;
    }

    public int getpAxis() {
        return pAxis;
    }

    public void setpAxis(int pAxis) {
        this.pAxis = pAxis;
    }

    public int getQrsAxis() {
        return qrsAxis;
    }

    public void setQrsAxis(int qrsAxis) {
        this.qrsAxis = qrsAxis;
    }

    public Date getMeasureTime() {
        return measureTime;
    }

    public void setMeasureTime(Date measureTime) {
        this.measureTime = measureTime;
    }

    public int getQrs() {
        return qrs;
    }

    public void setQrs(int qrs) {
        this.qrs = qrs;
    }

    public int getPr() {
        return pr;
    }

    public void setPr(int pr) {
        this.pr = pr;
    }

    public int getQt() {
        return qt;
    }

    public void setQt(int qt) {
        this.qt = qt;
    }

    public int getQtc() {
        return qtc;
    }

    public void setQtc(int qtc) {
        this.qtc = qtc;
    }

    public int getSv1() {
        return sv1;
    }

    public void setSv1(int sv1) {
        this.sv1 = sv1;
    }

    public int getRv5() {
        return rv5;
    }

    public void setRv5(int rv5) {
        this.rv5 = rv5;
    }
}
