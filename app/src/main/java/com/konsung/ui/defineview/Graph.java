package com.konsung.ui.defineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.konsung.R;
import com.konsung.bean.InitChartBean;
import com.konsung.utils.constant.GlobalConstant;

import java.util.ArrayList;

/**
 * 此类为左右刻度曲线图，基于BrokenLineTable，将原来单趋势值绘图，整合到一张图表
 */
public class Graph extends BrokenLineTable {
    private Paint hrPaint;
    private InitChartBean beanLeft;
    private InitChartBean beanRight;
    private Boolean flag; //是否转换为int值

    /**
     * 构造
     * @param context 上下文
     * @param beanLeft 数据源
     * @param beanRight 数据源
     * @param flag 标识
     */
    public Graph(Context context, InitChartBean beanLeft, InitChartBean beanRight, Boolean flag) {
        super(context, beanLeft, flag);
        this.beanLeft = beanLeft;
        this.beanRight = beanRight;
        this.flag = flag;
        hrPaint = new Paint();
        hrPaint.setStrokeWidth(4);
        hrPaint.setColor(getResources().getColor(R.color.text_color6));
        hrPaint.setTextSize(12.0f); //设置文字大小
        hrPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (beanRight != null) {
            drawPulseRateY(canvas);
            //绘制记录线条
            drawLine(canvas, beanRight);
            //绘制记录数字
            drawTextAbortLine(canvas, beanRight);
        }
    }

    /**
     * 绘制线条
     * @param canvas 画布
     * @param bean 数据源
     */
    public void drawLine(Canvas canvas, InitChartBean bean) {
        ArrayList<float[]> dataCache = getData(bean);
        for (int i = 0; i < dataCache.size(); i++) {
            if (bean.getColorsId() != null) {
                hrPaint.setColor(bean.getColorsId()[i]);
            }
            if (dataCache.get(i).length > 2) {
//                canvas.drawLines(dataCache.get(i), paintLine);
                //有数据就花出来，没有数据就空着
                for (int j = 0; j < dataCache.get(i).length - 2; j += 2) {
                    float x1 = dataCache.get(i)[j];
                    float y1 = dataCache.get(i)[j + 1];
                    float x2 = dataCache.get(i)[j + 2];
                    float y2 = dataCache.get(i)[j + 3];
                    if (x1 != GlobalConstant.INVALID_DATA && x2 !=
                            GlobalConstant.INVALID_DATA && y1 !=
                            GlobalConstant.INVALID_DATA && y2 !=
                            GlobalConstant.INVALID_DATA) {
                        canvas.drawLine(x1, y1, x2, y2, hrPaint);
                    }
                }
            }
        }
    }

    /**
     * 绘制线上数据
     * @param canvas 画布
     * @param bean 数据源
     */
    public void drawTextAbortLine(Canvas canvas, InitChartBean bean) {
        ArrayList<float[]> linedataCache = getLineData(bean);
        for (int k = 0; k < linedataCache.size(); k++) {
            for (int i = 0, j = 0; j < linedataCache.get(k).length; i++, j +=
                    2) {
                if (linedataCache.get(k)[j] != GlobalConstant.INVALID_DATA) {
                    String text;
                    if (flag) {
                        text = String.valueOf((int) bean.getValues().get(k)[i]);
                    } else {
                        text = String.valueOf(bean.getValues().get(k)[i]);
                    }
                    float x = linedataCache.get(k)[j];
                    float y = linedataCache.get(k)[j + 1];

                    if (bean.getColorsId() != null) {
                        hrPaint.setColor(bean.getColorsId()[i]);
                    }
                    canvas.drawText(text, x - 5.f, y - 10, hrPaint);
                    drawExCircle(canvas, x, y, 10, getResources().getColor(R
                            .color.background));
                    canvas.drawCircle(x, y, 6, hrPaint);
                }
            }
            //绘制单位
            hrPaint.setTextSize(15.0f);
            canvas.drawText(bean.getUnit(), bean.getPaddingLeft() + 10.0f,
                    bean.getPaddingTop() + 10.0f, hrPaint);
        }
    }
    /**
     * 绘制脉率Y轴
     *
     * @param canvas 画布
     */
    private void drawPulseRateY(Canvas canvas) {
        //设置画笔
        Paint paint = new Paint();
        paint.setStrokeWidth(beanLeft.getXOryDimen());
        paint.setColor(getResources().getColor(R.color.text_color6));
        paint.setTextSize(12.0f); //设置文字大小
        paint.setAntiAlias(true);
        //绘制横坐标
        float x = getWidth() - beanLeft.getPaddingLeft();
        //间距
        float value = beanRight.getMaxValue() / beanLeft.getYSize();
        //间距像素
        float pix = (getHeight() - beanLeft.getPaddingBottom() - beanLeft
                .getPaddingTop()) / beanRight.getMaxValue();
        // 绘制y轴
        canvas.drawLine(x, beanLeft.getPaddingTop(), x, getHeight() - beanLeft
                .getPaddingBottom(), paint);

        //纵坐标刻度个数
        int sizeY = beanLeft.getYSize();
        for (int i = 0; i <= sizeY; i++) {
            //绘制文本的纵坐标
            float y = getHeight() - beanLeft.getPaddingBottom() - pix * (value *
                    i) + 5;
            if (flag) {
                canvas.drawText(String.valueOf((int) value * i), x + 5, y, paint);
            } else {
                canvas.drawText(String.valueOf(value * i), x + 5, y, paint);
            }
            //刻度长度
            int lengthX = 4;
            //绘制y轴刻度
            canvas.drawLine(x - lengthX,  // x1
                    getHeight() - beanLeft.getPaddingBottom() - pix * (value * i), // y1
                    x,  // x2
                    getHeight() - beanLeft.getPaddingBottom() - pix * (value * i), // y2
                    paint);
        }
        //绘制脉率单位
        paint.setTextSize(15.0f);
        canvas.drawText(beanRight.getUnit(), getWidth()
                - beanLeft.getPaddingRight() - 40.0f, beanLeft.getPaddingTop()
                + 10.0f, paint);
    }

    /**
     * 转换为数据转换为x轴需要的float[]
     * @param bean 数据源
     * @return x轴需要的float[]
     */
    private ArrayList<float[]> getData(InitChartBean bean) {
        ArrayList<float[]> dataList = new ArrayList<>();
        for (int j = 0; j < bean.getValues().size(); j++) {
            ArrayList<Float> list = new ArrayList();
            Float[] data = null;
            for (int i = 0; i < bean.getValues().get(j).length; i++) {
                if (bean.getValues().get(j)[i] != GlobalConstant.INVALID_DATA) {
                    //横坐标
                    float x = bean.getPaddingLeft() + bean.getScaleX() * (i + 1);
                    list.add(x);
                    float values = bean.getValues().get(j)[i];
                    //单位像素
                    float pix = (getHeight() - bean.getPaddingBottom() -
                            bean.getPaddingTop()) / (bean.getMaxValue() -
                            bean.getMinValue());
                    //像素位置
                    float y = getHeight() - ((values - bean.getMinValue()) *
                            pix + bean.getPaddingTop() + 40);
                    list.add(y);

                    if (i > 0 && i < bean.getValues().get(j).length - 1) {
                        list.add(x);
                        list.add(y);
                    }
                } else {
                    list.add(-1000f);
                    list.add(-1000f);
                    list.add(-1000f);
                    list.add(-1000f);
                }
            }
            data = list.toArray(new Float[0]);

            float[] result = new float[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = data[i].floatValue();
            }
            dataList.add(result);
        }

        return dataList;
    }

    /**
     * 转换为数据转换为x轴需要的float[]
     * @param bean 数据源
     * @return x轴需要的float[]
     */
    private ArrayList<float[]> getLineData(InitChartBean bean) {

        ArrayList<float[]> dataList = new ArrayList<>();
        for (int j = 0; j < bean.getValues().size(); j++) {
            ArrayList<Float> list = new ArrayList();
            Float[] data = null;
            for (int i = 0; i < bean.getValues().get(j).length; i++) {
                if (bean.getValues().get(j)[i] != GlobalConstant.INVALID_DATA) {
                    //横坐标
                    float x = bean.getPaddingLeft() + bean.getScaleX() * (i + 1);
                    list.add(x);
                    float values = bean.getValues().get(j)[i];
                    //单位像素
                    float pix = (getHeight() - bean.getPaddingBottom() -
                            bean.getPaddingTop()) / (bean.getMaxValue() -
                            bean.getMinValue());
                    //像素位置
                    float y = getHeight() - ((values - bean.getMinValue()) *
                            pix + bean.getPaddingTop() + 40);
                    list.add(y);
                } else {
                    list.add(-1000f);
                    list.add(-1000f);
                }
            }
            data = list.toArray(new Float[0]);

            float[] result = new float[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = data[i].floatValue();
            }
            dataList.add(result);
        }
        return dataList;
    }

    /**
     * 折线图描点画外圈圆
     * @param canvas 画布
     * @param x x轴
     * @param y y轴
     * @param radius 半径
     * @param color 颜色
     */
    private void drawExCircle(Canvas canvas, float x, float y, int radius, int color) {
        Paint paint = new Paint();
        // 去锯齿
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        // 设置paint的外框宽度
        paint.setStrokeWidth(beanLeft.getXOryDimen());
        canvas.drawCircle(x, y, radius, paint);
    }

    /**
     * 构造
     * @param context 上下文
     * @param attrs 配置参数
     */
    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
