package com.konsung.utils.constant;

/**
 * 成年男人的测量参考值范围
 */

public interface ReferenceValue {
    int ECG_MAX = 120; //心率
    int ECG_MIN = 60; //
    int SPO2_MAX = 100; //血氧
    int SPO2_MIN = 90; //
    int SPO2_PR_MAX = 120; //血氧脉率
    int SPO2_PR_MIN = 50; //
    float TEMP_MAX = 36.7f; //体温
    float TEMP_MIN = 35.9f; //
    int BP_SBP_MAX = 160; //收缩压
    int BP_SBP_MIN = 90; //
    int BP_DBP_MAX = 90; //舒张压
    int BP_DBP_MIN = 50; //
}
