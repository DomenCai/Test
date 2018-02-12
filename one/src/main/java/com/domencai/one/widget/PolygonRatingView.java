package com.domencai.one.widget;

import android.content.Context;
import android.graphics.Canvas;
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


public class PolygonRatingView extends View {

    private static final int NET_COLOR = 0x4cffffff; //六边形网颜色
    private static final int RATING_STROKE_COLOR = 0xff602dfc; //分数边线
    private static final int RATING_AREA_TOP_COLOR = 0xb23710ff; //分数区域顶部色
    private static final int RATING_AREA_BOTTOM_COLOR = 0xb28d3aff; //分数区域底部色
    private static final int TEXT_COLOR = 0xffffffff; //文字颜色
    private static final int CENTER_TEXT_COLOR = 0xffffcc00; //中间文字颜色
    private static final int WEALTH_INDEX = 2;  //wealth的下标(调整边距)
    private static final int CAREER_INDEX = 4;  //career的下标(调整边距)


    private static final int LINE_WIDTH = 3;
    private static final int RATING_TEXT_SIZE = DrawUtils.dp2px(16); //分数字体大小
    private static final int TITLE_TEXT_SIZE = DrawUtils.dp2px(14);  //标题字体大小
    private static final int CENTER_TEXT_SIZE = DrawUtils.dp2px(40); //中间文字字体大小

    private static final float[] DASH_LINE = {5, 5}; //虚线
    private static final int COUNT = 6;

    private Paint mSolidPaint; //填充块
    private Paint mDashLinePaint; //虚线
    private Paint mLinePaint; //普通线条
    private Paint mTextPaint; //文字
    private Paint mCenterTextPaint;//中间文字
    private Matrix mMatrix;
    private float mLineLength;
    private float mCenterWidth;
    private float mCenterHeight;
    private Path mPath;
    private float mAverageNumber;
    private List<PolygonRatingCardWrapper> mRatingCardWrappers = new ArrayList<>();


    public PolygonRatingView(Context context) {
        super(context);
    }

    public PolygonRatingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(NET_COLOR);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(LINE_WIDTH);

        mDashLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDashLinePaint.setStyle(Paint.Style.STROKE);
        mDashLinePaint.setStrokeWidth(LINE_WIDTH);
        mDashLinePaint.setColor(NET_COLOR);
        mDashLinePaint.setPathEffect(new DashPathEffect(DASH_LINE, 0));

        mSolidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSolidPaint.setStyle(Paint.Style.FILL);
        mSolidPaint.setColor(RATING_STROKE_COLOR);
        mSolidPaint.setStrokeWidth(LINE_WIDTH);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(TEXT_COLOR);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(RATING_TEXT_SIZE);

        mCenterTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterTextPaint.setColor(CENTER_TEXT_COLOR);
        mCenterTextPaint.setTextAlign(Paint.Align.CENTER);
        mCenterTextPaint.setTextSize(CENTER_TEXT_SIZE);

        mMatrix = new Matrix();
        mPath = new Path();
    }

    private void calculatePoint() {
        mPath.reset();
        mSolidPaint.setShader(new LinearGradient(mCenterWidth, mCenterHeight - mLineLength, mCenterWidth, mCenterHeight + mLineLength,
                RATING_AREA_TOP_COLOR, RATING_AREA_BOTTOM_COLOR, Shader.TileMode.CLAMP));

        float[] src = new float[2];
        src[0] = mCenterWidth;
        float[] des = new float[2];
        mMatrix.reset();
        for (int i = 0; i < COUNT; i++) {
            if (i > 0) {
                mMatrix.postRotate(360f / COUNT, mCenterWidth, mCenterHeight);
            }
            src[1] = mCenterHeight - mLineLength * mRatingCardWrappers.get(i).mRating / 5;
            mMatrix.mapPoints(des, src);
            if (i == 0) {
                mPath.moveTo(des[0], des[1]);
            } else {
                mPath.lineTo(des[0], des[1]);
            }
        }
        mPath.close();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = MeasureSpec.getSize(widthMeasureSpec);
        mLineLength = size * 0.25f;
        mCenterWidth = size / 2;
        mCenterHeight = mLineLength * 1.5f;
        calculatePoint();
        setMeasuredDimension(size, (int) (mLineLength * 3));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(LAYER_TYPE_SOFTWARE, null); //为了画虚线，关闭硬件加速
        mLinePaint.setColor(NET_COLOR);
        canvas.save();
        for (int i = 0; i < COUNT; i++) {
            canvas.rotate(360f / COUNT, mCenterWidth, mCenterHeight);
            for (int j = 2; j <= 5; j++) {
                float l = mLineLength * j / 5;
                float x = (float) (mCenterWidth - l * Math.sin(2 * Math.PI / COUNT));
                float y = (float) (mCenterHeight - l * Math.cos(2 * Math.PI / COUNT));
                canvas.drawLine(x, y, mCenterWidth, mCenterHeight - l, mLinePaint); //画网格
            }
            canvas.save();
            float l = i == 0 || i == 3 ? mLineLength * 1.25f : mLineLength * 1.4f;
            float x = (float) (mCenterWidth - l * Math.sin(2.0 * Math.PI / COUNT));
            float y = (float) (mCenterHeight - l * Math.cos(2.0 * Math.PI / COUNT));
            canvas.rotate(-60 * (i + 1), x, y);
            mTextPaint.setTextSize(TITLE_TEXT_SIZE);
            canvas.drawText(mRatingCardWrappers.get(i).mName, x, y - DrawUtils.dp2px(4), mTextPaint); //画标题
            mTextPaint.setTextSize(RATING_TEXT_SIZE);
            canvas.drawText(mRatingCardWrappers.get(i).mRating + "", x, y + RATING_TEXT_SIZE, mTextPaint); //画分数
            canvas.restore();
            canvas.drawLine(mCenterWidth, mCenterHeight - mLineLength * 0.4f, mCenterWidth, mCenterHeight - mLineLength, mDashLinePaint); //画虚线
        }
        canvas.restore();
        canvas.drawPath(mPath, mSolidPaint); //画填充色
        mLinePaint.setColor(RATING_STROKE_COLOR);
        canvas.drawPath(mPath, mLinePaint); //画填充块边线
        canvas.drawText(mAverageNumber + "", mCenterWidth, mCenterHeight + RATING_TEXT_SIZE / 2 + DrawUtils.dp2px(5), mCenterTextPaint); //画中央平均分数
    }


    /**
     * 设置数据 解析转换后绘制
     */
    public void setData() {
        mAverageNumber = 3.2f;
        if (mRatingCardWrappers.size() == 6) {
            mRatingCardWrappers.get(0).mRating = 3.2f;
            mRatingCardWrappers.get(1).mRating = 3.2f;
            mRatingCardWrappers.get(2).mRating = 3.2f;
            mRatingCardWrappers.get(3).mRating = 3.2f;
            mRatingCardWrappers.get(4).mRating = 3.2f;
            mRatingCardWrappers.get(5).mRating = 3.2f;
        } else {
            mRatingCardWrappers.add(new PolygonRatingView.PolygonRatingCardWrapper("Love", 1f));
            mRatingCardWrappers.add(new PolygonRatingView.PolygonRatingCardWrapper("Health", 3f));
            mRatingCardWrappers.add(new PolygonRatingView.PolygonRatingCardWrapper("Wealth", 4f));
            mRatingCardWrappers.add(new PolygonRatingView.PolygonRatingCardWrapper("Family", 3f));
            mRatingCardWrappers.add(new PolygonRatingView.PolygonRatingCardWrapper("Career", 5f));
            mRatingCardWrappers.add(new PolygonRatingView.PolygonRatingCardWrapper("Marriage", 2f));
        }
        requestLayout();
    }

    /**
     * RatingCard 数据包装
     */
    private class PolygonRatingCardWrapper {
        String mName;
        float mRating;

        PolygonRatingCardWrapper(String mName, float mRating) {
            this.mName = mName;
            this.mRating = mRating;
        }
    }


}
