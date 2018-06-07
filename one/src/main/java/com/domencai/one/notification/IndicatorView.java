package com.domencai.one.notification;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.domencai.one.R;
import com.domencai.one.utils.DrawUtils;

/**
 * Created by Domen、on 2018/4/2.
 */

public class IndicatorView extends AppCompatTextView {

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mPath = new Path();
    private RectF mRect = new RectF();
    private int mBgColor;
    private float mOffset;
    private boolean mTowardTop;

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        mPaint.setColor(mBgColor);
        if (mTowardTop) {
            setPadding(DrawUtils.dp2px(16), DrawUtils.dp2px(12), DrawUtils.dp2px(16), DrawUtils.dp2px(6));
        } else {
            setPadding(DrawUtils.dp2px(16), DrawUtils.dp2px(6), DrawUtils.dp2px(16), DrawUtils.dp2px(12));
        }
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.IndicatorView);
        mTowardTop = attribute.getBoolean(R.styleable.IndicatorView_toward_top, true);
        mBgColor = attribute.getColor(R.styleable.IndicatorView_backgroundColor, 0xff18e1ff);
        mOffset = attribute.getFloat(R.styleable.IndicatorView_offset, 0.5f);
        attribute.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int padding = DrawUtils.dp2px(6);
        if (mTowardTop) {
            mPath.moveTo(0, 0);
            mPath.lineTo(padding, padding);
            mPath.lineTo(-padding, padding);
            mPath.close();
            mRect.set(0, padding, w, h);
        } else {
            mPath.moveTo(0, h);
            mPath.lineTo(padding, h - padding);
            mPath.lineTo(-padding, h - padding);
            mPath.close();
            mRect.set(0, 0, w, h - padding);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(-getLeft() + DrawUtils.getScreenWidth(getContext()) * mOffset, 0);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        canvas.drawRoundRect(mRect, DrawUtils.dp2px(6), DrawUtils.dp2px(6), mPaint);
        super.onDraw(canvas);
    }
}
