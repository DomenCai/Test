package com.domencai.one;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Domen、on 2017/11/22.
 */

public class BedView extends View {

    private Paint mPaint = new Paint();
    private float mScale = 1;
    private float mWidthScale;
    private int mColorA = Color.WHITE;
    private int mColorB = Color.BLACK;

    public BedView(Context context) {
        super(context);

    }

    public BedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = MeasureSpec.getSize(widthMeasureSpec) * 9 / 10;
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.scale(mScale, mScale, getWidth() / 2, getHeight() / 2);
        float size = getWidth() / 15f;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                boolean white = (i + j) % 2 == 0;
                mPaint.setColor(white ? mColorA : mColorB);
                canvas.drawRect(size * i, size * j, size * (i + 1), size * (j + 1), mPaint);
                mPaint.setColor(!white ? mColorA : mColorB);
                drawSmallRect(canvas, size * (i + 0.5f), size * (j + 0.5f), size / 3);
            }
        }
    }

    private void drawSmallRect(Canvas canvas, float centerX, float centerY, float maxWidth) {
        float x = Math.abs(centerX - getWidth() / 2) / (maxWidth * 3);
        float y = Math.abs(centerY - getWidth() / 2) / (maxWidth * 3);
//        float halfWidth = maxWidth * mWidthScale / 2;
        float halfWidth = (float) (maxWidth * Math.pow(mWidthScale, (x + y) / 2 + 1.5f) / 2);
        drawRectByCenter(canvas, centerX - maxWidth, centerY - maxWidth, halfWidth);
        drawRectByCenter(canvas, centerX - maxWidth, centerY + maxWidth, halfWidth);
        drawRectByCenter(canvas, centerX, centerY, halfWidth);
        drawRectByCenter(canvas, centerX + maxWidth, centerY - maxWidth, halfWidth);
        drawRectByCenter(canvas, centerX + maxWidth, centerY + maxWidth, halfWidth);
    }

    private void drawRectByCenter(Canvas canvas, float centerX, float centerY, float halfWidth) {
        canvas.drawRect(centerX - halfWidth, centerY - halfWidth, centerX + halfWidth, centerY + halfWidth, mPaint);
    }

    public void setScale(int scale) {
        float value = scale / 100f;
        Log.w("BedView", "setScale: " + value);
        mScale = 3 / (3 - 2 * value);
        invalidate();
    }

    public void setWidth(int width) {
        mWidthScale = width / 100f;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1).setDuration(5000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
//                value = (float) Math.exp(value);
                float scale1 = 1 + value * 2;
                float scale2 = 3 / (3 - 2 * value);
                mScale = (scale1 + scale2) / 2;
//                mScale = scale2;
                mWidthScale = value;
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                int temp = mColorA;
                mColorA = mColorB;
                mColorB = temp;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mColorA = Color.WHITE;
                mColorB = Color.BLACK;
            }
        });
        animator.setRepeatCount(9);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        return super.onTouchEvent(event);
    }
}
