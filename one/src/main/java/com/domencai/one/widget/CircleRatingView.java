package com.domencai.one.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
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

public class CircleRatingView extends View {
    private static final int STROKE_WIDTH = DrawUtils.dp2px(1.5f);
    private static final int TITLE_TEXT_SIZE = DrawUtils.dp2px(14);  //标题字体大小
    private static final int RATING_TEXT_SIZE = DrawUtils.dp2px(17); //分数字体大小
    private static final int CENTER_TEXT_SIZE = DrawUtils.dp2px(36); //中间文字字体大小

    private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSolidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDashPaint = new Paint();
    private float mRingHeight;
    private List<RateInfo> mRateList;
    private Path mPath = new Path();
    private String mAverageRate;
    private float mCenterX, mCenterY;

    public CircleRatingView(Context context) {
        this(context, null);
    }

    public CircleRatingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(STROKE_WIDTH);

        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mDashPaint.setStyle(Paint.Style.STROKE);
        mDashPaint.setStrokeWidth(STROKE_WIDTH);
        mDashPaint.setColor(0x4cffffff);
        mDashPaint.setPathEffect(new DashPathEffect(new float[]{8, 8}, 0));
        setData();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        mRingHeight = size / 16f;
        mCenterX = mCenterY = size / 2;
        calculatePoint();
    }

    private void calculatePoint() {
        if (mRateList == null) {
            return;
        }
        mPath.reset();

        float[] src = new float[2];
        src[0] = mCenterX;
        float[] des = new float[2];
        Matrix matrix = new Matrix();
        for (int i = 0, len = mRateList.size(); i < len; i++) {
            if (i > 0) {
                matrix.postRotate(360f / len, mCenterX, mCenterY);
            }
            src[1] = mCenterY - mRingHeight * (mRateList.get(i).mRating - 0.5f);
            matrix.mapPoints(des, src);
            if (i == 0) {
                mPath.moveTo(des[0], des[1]);
            } else {
                mPath.lineTo(des[0], des[1]);
            }
        }
        mPath.close();
        mSolidPaint.setShader(new LinearGradient(mCenterX, 0, mCenterX, mCenterY * 2,
                    0xb23710ff, 0xb28d3aff, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRateList == null) {
            return;
        }
        setLayerType(LAYER_TYPE_SOFTWARE, null); //为了画虚线，关闭硬件加速
        mLinePaint.setColor(0x66ffffff);
        for (int i = 1; i < 5; i++) {
            canvas.drawCircle(mCenterX, mCenterY, mRingHeight * (i + 0.5f), mLinePaint);
        }
        canvas.save();
        mTextPaint.setColor(Color.WHITE);
        for (int i = 0, len = mRateList.size(); i < len; i++) {
            canvas.save();
            float y = mCenterY - mRingHeight * (i == 0 ? 5.8f : 6.4f);
            RateInfo rateInfo = mRateList.get(i);
            canvas.rotate(-360 / len * i, mCenterX, y);
            mTextPaint.setTextSize(TITLE_TEXT_SIZE);
            canvas.drawText(rateInfo.mName, mCenterX, y - DrawUtils.dp2px(8), mTextPaint);
            mTextPaint.setTextSize(RATING_TEXT_SIZE);
            canvas.drawText(rateInfo.getRating(), mCenterX, y + TITLE_TEXT_SIZE, mTextPaint);
            canvas.restore();
            canvas.drawLine(mCenterX, mCenterY - mRingHeight * 1.5f, mCenterX, mCenterY - mRingHeight * 4.5f, mDashPaint);
            canvas.rotate(360 / len, mCenterX, mCenterY);
        }
        canvas.restore();
        canvas.drawPath(mPath, mSolidPaint); //画填充色
        mLinePaint.setColor(0xff602dfc);
        canvas.drawPath(mPath, mLinePaint); //画填充块边线
        mTextPaint.setColor(0xffffcc00);
        mTextPaint.setTextSize(CENTER_TEXT_SIZE);
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        int baseline = (getHeight() - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(mAverageRate, mCenterX, baseline, mTextPaint); //画中央平均分数
    }

    private void setData() {
        mAverageRate = "4.5";
        if (mRateList == null) {
            mRateList = new ArrayList<>();
            mRateList.add(new RateInfo("Heart Line", 3.5f));
            mRateList.add(new RateInfo("Head Line", 4.5f));
            mRateList.add(new RateInfo("Life Line", 2.5f));
        }
    }

    private static class RateInfo {
        String mName;
        float mRating;

        RateInfo(String name, float rating) {
            mName = name;
            mRating = rating;
        }

        String getRating() {
            return String.valueOf(mRating);
        }
    }
}
