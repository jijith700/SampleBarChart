package com.jijith.barchart.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.jijith.barchart.R;

/*
*
* Created by jijith
*
* */
public class BarChart extends View {


    private RectF rectF = new RectF();
    private RectF barRect = new RectF();

    private String barText = "";
    private float barTextSize;
    private int barTextColor;

    private String yValueText = "";
    private float yValueTextSize;
    private int yValueTextColor;

    private int progress = 0;
    private int max;

    private int barColor;

    private final float default_text_size;
    private final int min_size;

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_PROGRESS = "progress";

    private Paint paint = new Paint();

    public BarChart(Context context) {
        this(context, null);
    }

    public BarChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        default_text_size = sp2px(getResources(), 12);
        min_size = (int) dp2px(getResources(), 100);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BarChart, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }

    public String getYValueText() {
        return yValueText;
    }

    protected void initByAttributes(TypedArray attributes) {

        final int default_text_color = Color.BLACK;
        final int default_max = 100;
        final int default_bar_color = Color.CYAN;

        barColor = attributes.getColor(R.styleable.BarChart_bar_chart_color, default_bar_color);
        barTextColor = attributes.getColor(R.styleable.BarChart_bar_chart_text_color, default_text_color);
        barTextSize = attributes.getDimension(R.styleable.BarChart_bar_chart_text_size, default_text_size);

        yValueTextColor = attributes.getColor(R.styleable.BarChart_bar_chart_ytext_color, default_text_color);
        yValueTextSize = attributes.getDimension(R.styleable.BarChart_bar_chart_ytext_size, default_text_size);

        setMax(attributes.getInt(R.styleable.BarChart_bar_chart_max, default_max));
        setProgress(attributes.getInt(R.styleable.BarChart_bar_chart_progress, 0));

        if (attributes.getString(R.styleable.BarChart_bar_chart_text) != null) {
            setBarText(attributes.getString(R.styleable.BarChart_bar_chart_text));
        }
        if (attributes.getString(R.styleable.BarChart_bar_chart_ytext) != null) {
            setYValueText(attributes.getString(R.styleable.BarChart_bar_chart_ytext));
        }
    }

    protected void initPainters() {

        Paint textPaint;
        textPaint = new TextPaint();
        textPaint.setColor(getBarTextColor());
        textPaint.setTextSize(getBarTextSize());
        textPaint.setAntiAlias(true);

        paint.setAntiAlias(true);
    }

    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (this.progress > getMax()) {
            this.progress %= getMax();
        }
        invalidate();
    }

    public void setYValueText(String yValueText) {
        this.yValueText = yValueText;
        invalidate();
    }

    public float getYValueTextSize() {
        return yValueTextSize;
    }

    public void setYValueTextSize(float yValueTextSize) {
        this.yValueTextSize = yValueTextSize;
        invalidate();
    }

    public int getYValueTextColor() {
        return yValueTextColor;
    }

    public void setYValueTextColor(int yValueTextColor) {
        this.yValueTextColor = yValueTextColor;
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public float getBarTextSize() {
        return barTextSize;
    }

    public void setBarTextSize(float barTextSize) {
        this.barTextSize = barTextSize;
        this.invalidate();
    }

    public int getBarTextColor() {
        return barTextColor;
    }

    public void setBarTextColor(int barTextColor) {
        this.barTextColor = barTextColor;
        this.invalidate();
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return min_size;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return min_size;
    }

    public float getProgressPercentage() {
        return getProgress() / (float) getMax();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int default_width = 150;
        final int default_height = 100;
        int width = 0;
        int height = 0;

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

//        Log.e("width", ""+width);
//        Log.e("height", ""+height);

        if (width < default_width)
            width = default_width;
        if (height < default_height)
            height = default_height;
        rectF.set(0, 0, width, height);

        setMeasuredDimension(width, height);
    }


    public int getBarColor() {
        return barColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
        invalidate();
    }

    public String getBarText() {
        return barText;
    }

    public void setBarText(String barText) {
        this.barText = barText;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint.setStrokeWidth(0);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rectF, paint);
        canvas.save();
        canvas.rotate(180, getWidth()/2, getHeight()/2);

        float height = getProgress() / (float) getMax() * getHeight();
//        Log.e("yHeight", ""+yHeight);

        barRect.set(0, 0, getWidth(), height);

        paint.setStrokeWidth(0);
        paint.setColor(barColor);
        canvas.drawRect(barRect, paint);
        canvas.restore();

        if (!TextUtils.isEmpty(getYValueText())) {
            paint.setColor(getYValueTextColor());
            paint.setTextSize(getYValueTextSize());

            drawCenterTop(canvas, paint, getYValueText());
        }

        if (!TextUtils.isEmpty(getBarText())) {
            paint.setColor(getBarTextColor());
            paint.setTextSize(getBarTextSize());

            drawCenter(canvas, paint, getBarText());
        }
    }

    private Rect r = new Rect();

    private void drawCenter(Canvas canvas, Paint paint, String text) {
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
//        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        float y = cHeight - 20;
        canvas.drawText(text, x, y, paint);
    }

    private void drawCenterTop(Canvas canvas, Paint paint, String text) {
        barRect.round(r);
        canvas.getClipBounds(r);
        int cWidth = r.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = rectF.height() - barRect.height() - 20;
        canvas.drawText(text, x, y, paint);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_TEXT_COLOR, getBarTextColor());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getBarTextSize());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            barTextColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            barTextSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            initPainters();
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }


    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
