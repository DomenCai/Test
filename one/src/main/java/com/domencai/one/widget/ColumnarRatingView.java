package com.domencai.one.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.domencai.one.utils.DrawUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Domen、on 2018/1/22.
 */

public class ColumnarRatingView extends View {
    private static final int[] COLORS = {
            0xff43a4ff, 0xff66d6ff,
            0xffff9c32, 0xffffd163,
            0xff6a48ff, 0xffa48bff,
            0xff03d981, 0xff50ffdc,
            0xffff6072, 0xffff9283,
            0xff8e33ec, 0xffe382ff
    };

    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSolidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<RateInfo> mRateList;
    private List<Shader> mShaderList;
    private float mStart, mFullWidth;
    private RectF mRect = new RectF();

    public ColumnarRatingView(Context context) {
        this(context, null);
    }

    public ColumnarRatingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTextPaint.setTextSize(DrawUtils.sp2px(14));
        mTextPaint.setColor(Color.WHITE);
        setData();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = DrawUtils.dp2px(32) * (mRateList == null ? 0 : mRateList.size());
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mRateList == null) {
            return;
        }
        mStart = getMaxLength() + DrawUtils.dp2px(30);
        mFullWidth = w - mStart - mTextPaint.measureText("100") - DrawUtils.dp2px(30);
        mShaderList = new ArrayList<>();
        for (int i = 0; i < mRateList.size(); i++) {
            float end = mRateList.get(i).mRating / 100f * mFullWidth + mStart;
            mShaderList.add(new LinearGradient(mStart, 0, end, 0, COLORS[i * 2], COLORS[i * 2 + 1], Shader.TileMode.CLAMP));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRateList == null) {
            return;
        }
        canvas.save();
        int top = DrawUtils.dp2px(11), bottom = DrawUtils.dp2px(21);
        for (int i = 0; i < mRateList.size(); i++) {
            RateInfo info = mRateList.get(i);
            canvas.drawText(info.mName, DrawUtils.dp2px(16), bottom, mTextPaint);
            mRect.set(mStart, DrawUtils.dp2px(13), mStart + mFullWidth, DrawUtils.dp2px(19));
            mSolidPaint.setShader(null);
            mSolidPaint.setColor(0x19ffffff);
            canvas.drawRoundRect(mRect, top, top, mSolidPaint);
            mRect.set(mStart, top, mStart + info.mRating / 100f * mFullWidth, bottom);
            mSolidPaint.setShader(mShaderList.get(i));
            mSolidPaint.setColor(Color.WHITE);
            canvas.drawRoundRect(mRect, top, top, mSolidPaint);
            canvas.drawText(info.getRating(),  mStart + mFullWidth + DrawUtils.dp2px(16), bottom, mTextPaint);
            canvas.translate(0, DrawUtils.dp2px(32));
        }
        canvas.restore();
    }

    private void setData() {
        if (mRateList == null) {
            mRateList = new ArrayList<>();
            mRateList.add(new RateInfo("Passion", 89));
            mRateList.add(new RateInfo("Reliability", 73));
            mRateList.add(new RateInfo("Goal", 56));
            mRateList.add(new RateInfo("Cooperation", 91));
            mRateList.add(new RateInfo("Self-Confident", 62));
//            mRateList.add(new RateInfo("Creative", 75));
        }
    }

    private float getMaxLength() {
        float max = 0;
        for (RateInfo info : mRateList) {
            max = Math.max(max, mTextPaint.measureText(info.mName));
        }
        return max;
    }

    private static class RateInfo {
        String mName;
        int mRating;

        RateInfo(String name, int rating) {
            mName = name;
            mRating = rating;
        }

        String getRating() {
            return String.valueOf(mRating);
        }
    }
}
