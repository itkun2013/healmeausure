package com.konsung.bean;

import android.app.Fragment;

/**
 * 测量项的bean
 */

public class MeasureItemBean {
    private boolean isClick; //记录是否点击
    private int itemHigPic; //点击高亮图片的地址
    private int itemNorPic; //没有点击图片的地址
    private String itemName; //记录测量项的名字
    private int position; //记录测量项的索引
    private int param0; //对应的参数
    private int param1; //对应的参数
    private int param2; //对应的参数
    private Fragment fragment; //存储对应的Fragment
    private boolean isMeasureFinish; //是否测量完成
    private boolean isMeasureFinish1; //适配百捷三合一尿酸
    private boolean isMeasureFinish2; //适配百捷三合一总胆固醇

    public int getParam0() {
        return param0;
    }

    public void setParam0(int param0) {
        this.param0 = param0;
    }

    public int getParam1() {
        return param1;
    }

    public void setParam1(int param1) {
        this.param1 = param1;
    }

    public int getParam2() {
        return param2;
    }

    public void setParam2(int param2) {
        this.param2 = param2;
    }

    public boolean isMeasureFinish() {
        return isMeasureFinish;
    }

    public void setMeasureFinish(boolean measureFinish) {
        isMeasureFinish = measureFinish;
    }

    public boolean isMeasureFinish1() {
        return isMeasureFinish1;
    }

    public void setMeasureFinish1(boolean measureFinish1) {
        isMeasureFinish1 = measureFinish1;
    }

    public boolean isMeasureFinish2() {
        return isMeasureFinish2;
    }

    public void setMeasureFinish2(boolean measureFinish2) {
        isMeasureFinish2 = measureFinish2;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public int getItemHigPic() {
        return itemHigPic;
    }

    public void setItemHigPic(int itemHigPic) {
        this.itemHigPic = itemHigPic;
    }

    public int getItemNorPic() {
        return itemNorPic;
    }

    public void setItemNorPic(int itemNorPic) {
        this.itemNorPic = itemNorPic;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
