package com.domencai.puzzle.custom;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import com.domencai.puzzle.R;

/**
 * Created by Domen、on 2017/9/18.
 */

public class StarAnimatorView extends View {
    private Bitmap mMainStar;
    private Bitmap mSmallStar;
    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private float[] mCurrentPosition = new float[2];
    private float len;

    private Path mPath = new Path();

    public StarAnimatorView(Context context) {
        this(context, null);
    }

    public StarAnimatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mMainStar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_star_main);
        mSmallStar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_star_small);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.BLACK);



        Path path = new Path();
        RectF rect = new RectF(mSmallStar.getWidth() / 2, mMainStar.getHeight() * 0.2f,
                mMainStar.getWidth() + mSmallStar.getWidth() / 2, mMainStar.getHeight() * 0.76f);
        Matrix matrix = new Matrix();
        matrix.postRotate(18, (mMainStar.getWidth() + mSmallStar.getWidth()) / 2, mMainStar.getHeight() / 2);
//        matrix.postTranslate(mSmallStar.getWidth(), 0);
        path.addOval(rect, Path.Direction.CW);
        path.transform(matrix);
        mPath.addPath(path);

        mPathMeasure = new PathMeasure(path, false);
        len = mPathMeasure.getLength() ;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mMainStar.getWidth() + mSmallStar.getWidth(), mMainStar.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(50, 0, 0, 0);
        mPathMeasure.getPosTan(len, mCurrentPosition, null);
        if (len < mPathMeasure.getLength() / 2) {
            drawMainStar(canvas);
            drawSmallStar(canvas);
        } else {
            drawSmallStar(canvas);
            drawMainStar(canvas);
        }
        canvas.drawPath(mPath, mPaint);
    }

    private void drawSmallStar(Canvas canvas) {
        canvas.save();
        float fraction = len / mPathMeasure.getLength();
        float scale = (float) (Math.sin(fraction * 2 * Math.PI) * 0.2 + 0.8);
        canvas.scale(scale, scale, mCurrentPosition[0], mCurrentPosition[1]);
        canvas.drawBitmap(mSmallStar, mCurrentPosition[0] - mSmallStar.getWidth() / 2,
                mCurrentPosition[1] - mSmallStar.getHeight() / 2, mPaint);
        canvas.restore();
    }

    private void drawMainStar(Canvas canvas) {
        canvas.drawBitmap(mMainStar, (getWidth() - mMainStar.getWidth()) / 2,
                (getHeight() - mMainStar.getHeight()) / 2, null);
    }

    public void startAnimator(Animator.AnimatorListener listener) {

        ValueAnimator animator = ValueAnimator.ofFloat(mPathMeasure.getLength(), 0).setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                len = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.setInterpolator(new EaseCubicInterpolator(0.25f, 0.5f, 0.8f, 0.8f));
        animator.start();
    }

    public void startAnimator() {
        startAnimator(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        len = mPathMeasure.getLength();
//        invalidate();
        startAnimator();
        return super.onTouchEvent(event);
    }

    /**
     * 缓动三次方曲线插值器.(基于三次方贝塞尔曲线)
     */
    private static class EaseCubicInterpolator implements Interpolator {
        private final static int ACCURACY = 4096;
        private int mLastI = 0;
        private final PointF mControlPoint1 = new PointF();
        private final PointF mControlPoint2 = new PointF();

        /**
         * 设置中间两个控制点.
         * 在线工具: http://cubic-bezier.com/
         */
        EaseCubicInterpolator(float x1, float y1, float x2, float y2) {
            mControlPoint1.x = x1;
            mControlPoint1.y = y1;
            mControlPoint2.x = x2;
            mControlPoint2.y = y2;
        }

        @Override
        public float getInterpolation(float input) {
            float t = input;
            // 近似求解t的值[0,1]
            for (int i = mLastI; i < ACCURACY; i++) {
                t = 1.0f * i / ACCURACY;
                double x = cubicCurves(t, 0, mControlPoint1.x, mControlPoint2.x, 1);
                if (x >= input) {
                    mLastI = i;
                    break;
                }
            }
            double value = cubicCurves(t, 0, mControlPoint1.y, mControlPoint2.y, 1);
            if (value > 0.999d) {
                value = 1;
                mLastI = 0;
            }
            return (float) value;
        }

        /**
         * 求三次贝塞尔曲线(四个控制点)一个点某个维度的值.<br>
         * 参考资料: <em> http://devmag.org.za/2011/04/05/bzier-curves-a-tutorial/ </em>
         *
         * @param t   取值[0, 1]
         */
        double cubicCurves(double t, double value0, double value1, double value2, double value3) {
            double value;
            double u = 1 - t;
            double tt = t * t;
            double uu = u * u;
            double uuu = uu * u;
            double ttt = tt * t;

            value = uuu * value0;
            value += 3 * uu * t * value1;
            value += 3 * u * tt * value2;
            value += ttt * value3;
            return value;
        }
    }
}
