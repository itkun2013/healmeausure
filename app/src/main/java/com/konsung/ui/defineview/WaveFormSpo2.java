package com.konsung.ui.defineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by XuJunwei on 2015-04-20.
 */
public class WaveFormSpo2 extends View {

    private static final float DEFAULT_FACTOR = 0.781f; //200 / 256
    private Paint paint;
    private Paint paintBrokenLine;
    private float[] points;
    private int index = 0;
    int x = 0;
    private int sampleRate;
    private Handler handler = new Handler();
    private ArrayList<Byte> wave;
    private ArrayList<byte[]> waveList;
    private float factor = DEFAULT_FACTOR;  // 200 / 256
    private boolean isDrawing = false;

    /**
     * 构造
     * @param context 上下文
     * @param attrs 配置参数
     */
    public WaveFormSpo2(Context context, AttributeSet attrs) {
        super(context, attrs);

        paintBrokenLine = new Paint();
        paintBrokenLine.setStrokeWidth(2);
        paintBrokenLine.setColor(Color.parseColor("#00CED1"));
        paintBrokenLine.setAntiAlias(true);

        paint = new Paint();
        paint.setColor(Color.parseColor("#00CED1"));
        paint.setAntiAlias(true);

        //设置字体大小
        paint.setTextSize(20);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);  //设置画出的线的 粗细程度

        wave = new ArrayList<>();
    }

    /**
     * 构造
     * @param context 上下文
     */
    public WaveFormSpo2(Context context) {
        this(context, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isDrawing) {
            canvas.drawLines(points, paintBrokenLine);
        }
        super.onDraw(canvas);
    }

    /**
     * 停止波形绘制
     */
    public void stop() {
        x = 0;
        index = 0;
        isDrawing = false;
        wave.clear();
        handler.removeCallbacks(update);
        invalidate();
    }

    /**
     * 重置波形数据
     */
    public void reset() {
        stop();
        isDrawing = true;
        points = new float[sampleRate * 40];
        handler.post(update);
    }

    /**
     * 往波形控件中添加数据
     * @param data 数据
     */
    public void setData(byte[] data) {
        if (data == null) {
            return;
        }
        if (isDrawing) {
            for (int i = 0; i < data.length; i++) {
                wave.add(data[i]);
            }
        }
    }

    /**
     * 设置抽样率
     * @param sampleRate 抽样率
     */
    public void setSampleRate(int sampleRate) {
        stop();
        points = new float[sampleRate * 40];
        index = 0;
        this.sampleRate = sampleRate;
        handler.post(update);
    }

    /**
     * 更新界面函数
     */
    private Runnable update = new Runnable() {
        @Override
        public void run() {
            if (sampleRate == 0) {
                handler.postDelayed(this, 40);
                return;
            }

            for (int i = 0; i < 5; i++) {
                if (wave.size() < 4) {
                    handler.postDelayed(this, 40);
                    return;
                }
                //点集X轴起点
                points[index++] = x;
                //点集Y轴起点
                points[index++] = getHeight() - (factor * (wave.get(0) &
                        0xFF));
                points[index++] = ++x;
                points[index++] = getHeight() - (factor * (wave.get(2) &
                        0xFF));
                wave.remove(0);        // SPO2波形占用两个字节，将最前面的两个字节移除
                wave.remove(0);
                if (index >= getWidth() * 4.5f) {
                    index = 0;
                    x = 0;
                    if (wave.size() > 3000) {
                        wave.clear();
                    }
                    break;
                }
            }
            for (int i = 0; i < 32; i += 2) {
                points[index + i] = x + i;
                points[index + i + 1] = -10;
            }

            postInvalidate();
            handler.postDelayed(this, 40);
        }
    };

    /**
     * 设置缩放比例
     * @param ratio 比例
     */
    public void setRatio(float ratio) {
        factor = DEFAULT_FACTOR;
        factor = factor * ratio;
    }
}
