package com.konsung.ui.defineview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Base64;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.utils.KParamType;
import com.konsung.utils.UiUtils;
import com.konsung.utils.UnitConvertUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 心电报告绘制
 */

public class EcgReportDrawable extends Drawable {
    private float zoom = 0.6f;
    private float pixelPerMm = (float) (1280 / 216.576 * zoom);

    private int height; // 高
    private int width; //宽
    private MeasureDataBean measureBean;

    Handler handler = new Handler();
    Canvas canvas;
    Paint paint;

    RectF rect;

    /**
     * 构造器
     * @param measureBean 数据
     * @param height 画布高度
     * @param width 画布宽度
     */
    public EcgReportDrawable(MeasureDataBean measureBean, int height, int width) {
        this.height = height;
        this.width = width;
        this.measureBean = measureBean;

        paint = new Paint();
        rect = new RectF(0, 0, width, height);
    }

    @Override
    public void draw(final Canvas canvas) {
        this.canvas = canvas;
        new Thread(drawLineRunnable).start();
        handler.post(drawTitleRunnable);
        handler.post(drawWaveRunnable);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    Runnable drawLineRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                drawLine(canvas, width, height);
                drawPoint(canvas, width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable drawTitleRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                drawDottedCurve(canvas, width, height);
                drawRuler(canvas, width, height);
                drawTitle(canvas, width, height);
                drawWaveData(canvas, width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable drawWaveRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                drawWave(canvas, width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 绘制网格线
     * @param canvas 画布
     * @param width 宽
     * @param height 高
     */
    private void drawLine(Canvas canvas, int width, int height) {
        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(0xedb2b2);
        paint.setTextSize(14);
        paint.setAlpha(150);
        paint.setAntiAlias(true);

        // 单位:像素/mm,1280为屏幕横向像素数，216.576为屏幕横向长度（单位mm）
        int widthLen = (int) (width / pixelPerMm); //转换为mm长度
        int heightLen = (int) (height / pixelPerMm); //转换为mm长度

        float startX;
        float startY;
        float stopX;
        float stopY;
        // 画X轴,每隔5mm画一条线
        paint.setStrokeWidth(2.0f);
        startX = 0;
        stopX = width + 20; //规避没有绘制全屏问题
        for (int i = 0; i < heightLen; i++) {
            startY = i * pixelPerMm * 5;
            stopY = i * pixelPerMm * 5;
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }

        // 画Y轴,每隔5mm画一条线
        paint.setStrokeWidth(2.0f);
        startY = 0;
        stopY = height;
        for (int i = 0; i < widthLen; i++) {
            startX = i * pixelPerMm * 5;
            stopX = i * pixelPerMm * 5;
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }

    /**
     * 绘制格子里的点
     * @param canvas 画布
     * @param width 宽
     * @param height 高
     */
    private void drawPoint(Canvas canvas, int width, int height) {
        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(0xedb2b2);
        paint.setTextSize(14);
        paint.setAlpha(150);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        float startX;
        float startY;
        // 单位:像素/mm,1280为屏幕横向像素数，216.576为屏幕横向长度（单位mm）
        int widthLen = (int) (width / pixelPerMm); //转换为mm长度
        int heightLen = (int) (height / pixelPerMm); //转换为mm长度
        float x;
        float y;
        float gap = pixelPerMm;

        for (int a = 0; a < heightLen; a++) { // 网格边长为5
            startY = a * pixelPerMm * 5;
            for (int b = 0; b < widthLen; b++) {
                startX = b * pixelPerMm * 5;
                for (int c = 1; c < 5; c++) { // 平等分5段 四点
                    for (int d = 1; d < 5; d++) {
                        x = c * gap + startX;
                        y = d * gap + startY;
                        canvas.drawCircle(x, y, (float) (0.25 * pixelPerMm), paint); // 0.25半径
                    }
                }
            }
        }
    }

    /**
     * 绘制分割线 虚线
     * @param canvas 画布
     * @param width 宽
     * @param height 高
     */
    private void drawDottedCurve(Canvas canvas, int width, int height) {
        float waveWidgetWidth = (float) width / 2;
        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        path.moveTo(waveWidgetWidth, 80); //80空出II导联的位置。
        path.lineTo(waveWidgetWidth, height);
        PathEffect effects = new DashPathEffect(new float[]{2, 5, 2, 5}, 1); //分割线
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }

    /**
     * 绘制“几”字形标尺
     * @param canvas 画布
     * @param width 宽
     * @param height 高
     */
    private void drawRuler(Canvas canvas, int width, int height) {
        float startX;
        float startY;
        float stopX;
        float stopY;
        float waveWidgetWidth = (float) width / 2; //屏幕一半
        float waveWidgetHeight = (float) height / 10; //由于获取的是全屏的高度，经过调试显示，除以10

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(UiUtils.getColor(R.color.report_text_color));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);

        // i列数，j行数
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 7; j++) {
                startX = (width / 7) - (i * waveWidgetWidth);
                startY = waveWidgetHeight / 2 + waveWidgetHeight * j;
                stopX = (width / 7) - (i * waveWidgetWidth + pixelPerMm);
                stopY = waveWidgetHeight / 2 + waveWidgetHeight * j;
                canvas.drawLine(startX, startY + (float) (pixelPerMm * 2.5), stopX, stopY +
                        (float) (pixelPerMm * 2.5), paint); //画第一横

                startX = (width / 7) - (i * waveWidgetWidth + pixelPerMm);
                startY = waveWidgetHeight / 2 + waveWidgetHeight * j - pixelPerMm * 10;
                stopX = (width / 7) - (i * waveWidgetWidth + pixelPerMm * 3);
                stopY = waveWidgetHeight / 2 + waveWidgetHeight * j - pixelPerMm * 10;
                canvas.drawLine(startX, startY + (float) (pixelPerMm * 2.5), stopX, stopY +
                        (float) (pixelPerMm * 2.5), paint); //画第二横

                startX = (width / 7) - (i * waveWidgetWidth + pixelPerMm * 3);
                startY = waveWidgetHeight / 2 + waveWidgetHeight * j;
                stopX = (width / 7) - (i * waveWidgetWidth + pixelPerMm * 4);
                stopY = waveWidgetHeight / 2 + waveWidgetHeight * j;
                canvas.drawLine(startX, startY + (float) (pixelPerMm * 2.5), stopX, stopY +
                        (float) (pixelPerMm * 2.5), paint); //画第三横

                startX = (width / 7) - (i * waveWidgetWidth + pixelPerMm);
                startY = waveWidgetHeight / 2 + waveWidgetHeight * j;
                stopX = (width / 7) - (i * waveWidgetWidth + pixelPerMm);
                stopY = waveWidgetHeight / 2 + waveWidgetHeight * j - pixelPerMm * 10;
                canvas.drawLine(startX, startY + (float) (pixelPerMm * 2.5), stopX, stopY +
                        (float) (pixelPerMm * 2.5), paint); //画第一竖

                startX = (width / 7) - (i * waveWidgetWidth + pixelPerMm * 3);
                startY = waveWidgetHeight / 2 + waveWidgetHeight * j;
                stopX = (width / 7) - (i * waveWidgetWidth + pixelPerMm * 3);
                stopY = waveWidgetHeight / 2 + waveWidgetHeight * j - pixelPerMm * 10;
                canvas.drawLine(startX, startY + (float) (pixelPerMm * 2.5), stopX, stopY +
                        (float) (pixelPerMm * 2.5), paint); //画第二竖
            }
        }
    }

    /**
     * 绘制波形标题
     * @param canvas 画布
     * @param width 宽startY
     * @param height 高startX
     */
    private void drawTitle(Canvas canvas, int width, int height) {
        float startX;
        float startY;
        float waveWidgetWidth = (float) width / 2;
        float waveWidgetHeight = (float) height / 10; //由于获取的是全屏的高度，经过调试显示，除以10

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(UiUtils.getColor(R.color.report_text_color));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setTextSize(13);
        String[] titleArr1 = new String[]{UiUtils.getString(R.string.ecg_title_II),
                UiUtils.getString(R.string.ecg_title_I),
                UiUtils.getString(R.string.ecg_title_II),
                UiUtils.getString(R.string.ecg_title_III),
                UiUtils.getString(R.string.ecg_title_AVR),
                UiUtils.getString(R.string.ecg_title_AVL),
                UiUtils.getString(R.string.ecg_title_AVF)};
        // j行数
        for (int j = 0; j < 7; j++) {
            startX = pixelPerMm * 5 + (width / 7);
            startY = waveWidgetHeight / 2 + waveWidgetHeight * j - pixelPerMm * 8;
            canvas.drawText(titleArr1[j], startX, startY, paint);
        }

        String[] titleArr2 = new String[]{UiUtils.getString(R.string.ecg_title_V1),
                UiUtils.getString(R.string.ecg_title_V2),
                UiUtils.getString(R.string.ecg_title_V3),
                UiUtils.getString(R.string.ecg_title_V4),
                UiUtils.getString(R.string.ecg_title_V5),
                UiUtils.getString(R.string.ecg_title_V6)};
        // j行数
        for (int j = 1; j < 7; j++) {
            startX = pixelPerMm * 5 + waveWidgetWidth;
            startY = waveWidgetHeight / 2 + waveWidgetHeight * j - pixelPerMm * 8;
            canvas.drawText(titleArr2[j - 1], startX, startY, paint);
        }
    }

    /**
     * 绘制波形数据
     * @param canvas 画布
     * @param width 宽
     * @param height 高
     */
    private void drawWaveData(Canvas canvas, int width, int height) {
        float startX;
        float startY;
        float waveWidgetWidth = (float) width / 2; //宽度一半
        float waveWidgetHeight = (float) height / 10; //由于获取的是全屏的高度，经过调试显示，除以9

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(UiUtils.getColor(R.color.report_text_color));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setTextSize(18);
        startX = pixelPerMm * 4;
        startY = waveWidgetHeight / 2 + waveWidgetHeight * 6 + pixelPerMm * 15;
        canvas.drawText(UiUtils.getString(R.string.ecg_wave_data), startX, startY, paint);
    }

    /**
     * 绘制心电波形
     * @param canvas 画布
     * @param width 宽
     * @param height 高
     * @throws Exception 波形异常
     */
    private void drawWave(Canvas canvas, int width, int height) throws Exception {
        float startX;
        float startY;
        float waveWidgetHeight = (float) height / 10;

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(UiUtils.getColor(R.color.report_text_color));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        startX = pixelPerMm * 4; //几 标尺的宽度
        //先绘制II导联
        startY = pixelPerMm * 0.1f; //为了调节波形与几标尺同水平
        String waveII = Base64.encodeToString(UnitConvertUtil.
                getByteformHexString(measureBean.getEcgWave(KParamType.ECG_II)), Base64.NO_WRAP);
        int[] intII = intValue(waveII);
        drawEcgWave(intII, canvas, paint, width, height, width / 7, startY);
        //再绘制6*2波形
        for (int i = 2; i < 8; i++) { // 1-6:I,II,III,AVR,AVL,AVF波形
            if (i < 4) {
                startY = (waveWidgetHeight * (i - 1)); //为了调节波形与几标尺同水平
            } else {
                startY = (waveWidgetHeight * (i - 1));
            }
            String waveStr = Base64.encodeToString(UnitConvertUtil.
                    getByteformHexString(measureBean.getEcgWave(i - 1)), Base64.NO_WRAP);
            int[] ints = intValue(waveStr);
            if (ints != null) { //由于保存的是10秒的心电数据。这里取前5秒
                ints = Arrays.copyOfRange(ints, 0, ints.length / 2);
            }
            // width / 2， 只绘制宽度的一半
            drawEcgWave(ints, canvas, paint, width / 2, height, width / 7, startY);
        }
        startX = width / 2 + (pixelPerMm * 2); // 屏幕的一半开始绘制
        for (int i = 8; i < 14; i++) { // 7-12 v1-v6波形  + (pixelPerMm * 2.5f)
            if (i < 10) {
                startY = (waveWidgetHeight * (i - 7));
            } else {
                startY = (waveWidgetHeight * (i - 7));
            }
            String waveStr = Base64.encodeToString(UnitConvertUtil.
                    getByteformHexString(measureBean.getEcgWave(i - 1)), Base64.NO_WRAP);
            int[] ints = intValue(waveStr);
            if (ints != null && ints.length > 0) { //由于保存的是10秒的心电数据。这里取后5秒
                ints = Arrays.copyOfRange(ints, ints.length / 2, ints.length);
            }
            drawEcgWave(ints, canvas, paint, width, height, startX, startY);
        }
    }

    /**
     * 转换波形数据
     * @param value 波形数据
     * @return 转换后的数据
     * @throws Exception 转换异常
     */
    private int[] intValue(String value) throws Exception {
        byte[] out = Base64.decode(value.getBytes(), Base64.DEFAULT);
        byte[] f = new byte[4];
        f[3] = out[0];
        f[2] = out[1];
        f[1] = out[2];
        f[0] = out[3];
        List<Integer> vList = new ArrayList<Integer>();
        for (int i = 4; i < out.length; i++) {
            f = new byte[2];
            f[1] = out[i + 0];
            f[0] = out[i + 1];
            vList.add(toInt(f));
            i++;
        }
        int[] b = new int[vList.size()];
        for (int i = 0; i < vList.size(); i++) {
            b[i] = vList.get(i);
        }
        return b;
    }

    /**
     * 字节转int
     * @param bRefArr 字节数据
     * @return int
     */
    private int toInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;
        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }

    /**
     * 根据心电波形数据绘制波形
     * @param value 波形数据
     * @param canvas 画布
     * @param paint 画笔
     * @param width 宽
     * @param height 高
     * @param xStart 初始X坐标
     * @param yStart 初始Y坐标
     */
    private void drawEcgWave(int[] value, Canvas canvas, Paint paint, int width, int height,
            float xStart, float yStart) {
        float[] points1 = new float[20000];
        float[] points2 = new float[20000];
        int j = 0;
        float pointPerPix = (1280 / 216.576f) / (500 / 25); //单位：像素/点
        int pointNum = value.length;
        if (pointNum >= 4) { //超过4个点才绘制
            if (pointNum % 2 != 0) {
                pointNum--;
            }
            for (int i = 0; i < pointNum; i++) {
                if (((i * pointPerPix * zoom) + xStart) >= width) {
                    break;
                }
                points1[j * 2] = (i * pointPerPix * zoom) + xStart;

                // 2048为基线的AD值，1280为屏幕横向分辨率，216.576mm为屏幕宽度，
                // 2150为标志高值，1946为标尺低值，zoom波幅缩放倍数
                points1[j * 2 + 1] = yStart + (160f / 2f - ((float) value[i] -
                        2048) * (1280f / 216.576f) / ((2150f - 1946f) / 10)) * zoom;

                if (j >= 1) {
                    points2[(j - 1) * 2] = points1[j * 2];
                    points2[(j - 1) * 2 + 1] = points1[j * 2 + 1];
                }

                j++;
            }
            points2[(j - 1 - 1) * 2] = 0;
            points2[(j - 1 - 1) * 2 + 1] = 0;
        }
        canvas.drawLines(points1, paint);
        canvas.drawLines(points2, paint);
    }
}
