package com.domencai.one;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Domen、on 2017/9/18.
 */

public class StarAnimatorView extends View {
    private Bitmap mMainStar;
    private Bitmap mSmallStar;
    private PathMeasure mPathMeasure;
    private float[] mCurrentPosition = new float[2];
    private float mLen;
    private ValueAnimator mValueAnimator;

    public StarAnimatorView(Context context) {
        this(context, null);
    }

    public StarAnimatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mMainStar = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_start_main_star);
        mSmallStar = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_start_small_star);
        initPath();
        initAnimator();
    }

    private void initPath() {
        Path path = new Path();
        RectF rect = new RectF(mSmallStar.getWidth() / 2, mMainStar.getHeight() * 0.2f,
                mMainStar.getWidth() + mSmallStar.getWidth() / 2, mMainStar.getHeight() * 0.76f);

        Matrix matrix = new Matrix();
        matrix.setRotate(18, (mMainStar.getWidth() + mSmallStar.getWidth()) / 2, mMainStar.getHeight() / 2);
        path.addOval(rect, Path.Direction.CW);
        path.transform(matrix);

        mPathMeasure = new PathMeasure(path, false);
        mLen = mPathMeasure.getLength();
    }

    private void initAnimator() {
        mValueAnimator= ValueAnimator.ofFloat(mPathMeasure.getLength(), 0).setDuration(3000);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLen = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
//        mValueAnimator.setInterpolator(new EasingInterpolator(Ease.SINE_IN_OUT));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mMainStar.getWidth() + mSmallStar.getWidth(), mMainStar.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPathMeasure.getPosTan(mLen, mCurrentPosition, null);
        if (mLen < mPathMeasure.getLength() / 2) {
            drawMainStar(canvas);
            drawSmallStar(canvas);
        } else {
            drawSmallStar(canvas);
            drawMainStar(canvas);
        }
    }

    private void drawSmallStar(Canvas canvas) {
        canvas.save();
        float fraction = mLen / mPathMeasure.getLength();
        float scale = (float) (Math.sin(fraction * 2 * Math.PI) * 0.2 + 0.8);
        canvas.scale(scale, scale, mCurrentPosition[0], mCurrentPosition[1]);
        canvas.drawBitmap(mSmallStar, mCurrentPosition[0] - mSmallStar.getWidth() / 2,
                mCurrentPosition[1] - mSmallStar.getHeight() / 2, null);
        canvas.restore();
    }

    private void drawMainStar(Canvas canvas) {
        canvas.drawBitmap(mMainStar, (getWidth() - mMainStar.getWidth()) / 2,
                (getHeight() - mMainStar.getHeight()) / 2, null);
    }

    @SuppressWarnings("unused")
    public void startAnimator() {
        startAnimator(null);
    }

    @SuppressWarnings("unused")
    public void startAnimator(Animator.AnimatorListener listener) {
        if (listener != null) {
            mValueAnimator.addListener(listener);
        }
        mValueAnimator.start();
    }
}
