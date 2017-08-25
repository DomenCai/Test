package com.domencai.share.rotate;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.domencai.share.utils.DrawUtils;

/**
 * Created by Domen、on 2017/8/24.
 */

public class Rotate3DLayout extends FrameLayout {

    private static final int LEFT_TOP = 0;
    private static final int RIGHT_TOP = 1;
    private static final int LEFT_BOTTOM = 2;
    private static final int RIGHT_BOTTOM = 3;

    private static final int TO_LEFT = 1;
    private static final int TO_RIGHT = 2;

    private Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mShaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Camera mCamera = new Camera();
    private RectF mRect;
    private Path mStrokePath = new Path();
    private Matrix mMatrix = new Matrix();
    private Shader mShader;
    private GestureDetectorCompat mGestureDetectorCompat;
    ObjectAnimator mAnimator;

    private float[] mNormalPoints;
    private float[] mFrontPoints;
    private float[] mBehindPoints;
    private float mScale;
    private float mRotateY;
    private int mPaintAlpha;
    private int mDepth;
    private int mMargin;
    private int mIndex;
    private int mDirection;
    private boolean isRepeat;
    private boolean hadChanged;

    public Rotate3DLayout(@NonNull Context context) {
        this(context, null);
    }

    public Rotate3DLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mStrokePaint.setColor(Color.BLACK);
        mStrokePaint.setStyle(Paint.Style.FILL);
        mShaderPaint.setStyle(Paint.Style.FILL);

        mCamera.setLocation(0, 0, DrawUtils.dp2px(getContext(), -12));
        mMargin = DrawUtils.dp2px(getContext(), 2);
        mDepth = DrawUtils.dp2px(getContext(), 28);

        int size = DrawUtils.getScreenWidth(getContext());
        mShader = new LinearGradient(0, size, size, 0, ShaderInfo.COLOS, ShaderInfo.POS, Shader.TileMode.REPEAT);

        mGestureDetectorCompat = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float
                    distanceY) {
                setRotateY(mRotateY - distanceX * 180 / getWidth());
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float
                    velocityY) {
                float tranX = e2.getX() - e1.getX();
                if (tranX * velocityX / getWidth() > 1000) {
                    mDirection = tranX > 0 ? TO_RIGHT : TO_LEFT;
                }
                return true;
            }
        });

        mAnimator = new ObjectAnimator();
        mAnimator.setTarget(this);
        mAnimator.setPropertyName("rotateY");
        mAnimator.setInterpolator(new DecelerateInterpolator());
//        mAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                Log.w("Rotate3DLayout", "onAnimationEnd: ");
//                ObjectAnimator.ofInt(this, "paintAlpha", 255, 0).start();
//            }
//        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mRect == null) {
            mRect = new RectF(mMargin, mMargin, w - mMargin, h - mMargin);
            mNormalPoints = new float[]{mRect.left, mRect.top,
                    mRect.right, mRect.top,
                    mRect.left, mRect.bottom,
                    mRect.right, mRect.bottom};
            mFrontPoints = new float[mNormalPoints.length];
            mBehindPoints = new float[mNormalPoints.length];
        }
        resetChild();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mPaintAlpha == 0) {
            Log.w("Rotate3DLayout", "dispatchDraw: ");
            super.dispatchDraw(canvas);
            return;
        }
        calculateScale();
        drawBg(canvas, false, mDepth / 2);
        drawBg(canvas, true, -mDepth / 2);

        drawStroke(canvas, LEFT_TOP, RIGHT_TOP);
        drawStroke(canvas, LEFT_TOP, LEFT_BOTTOM);
        drawStroke(canvas, RIGHT_TOP, RIGHT_BOTTOM);
        drawStroke(canvas, LEFT_BOTTOM, RIGHT_BOTTOM);

        drawShadow(canvas);
    }

    private void calculateScale() {
        mCamera.save();
        mCamera.rotateY(mRotateY);
        mCamera.translate(0, 0, -mDepth / 2);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();
        mMatrix.preTranslate(-getWidth() / 2, -getHeight() / 2);
        mMatrix.postTranslate(getWidth() / 2, getHeight() / 2);
        mMatrix.mapPoints(mBehindPoints, mNormalPoints);
        float length = Math.max(mBehindPoints[5] - mBehindPoints[1], mBehindPoints[7] - mBehindPoints[3]);
        mScale = (mNormalPoints[5] - mNormalPoints[1]) / length;
    }

    private void drawBg(Canvas canvas, boolean isFront, int tranZ) {
        canvas.save();
        mCamera.save();
        mCamera.rotateY(mRotateY);
        mCamera.translate(0, 0, tranZ);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();
        mMatrix.preTranslate(-getWidth() / 2, -getHeight() / 2);
        mMatrix.postScale(mScale, mScale);
        mMatrix.postTranslate(getWidth() / 2, getHeight() / 2);
        mMatrix.mapPoints(isFront ? mFrontPoints : mBehindPoints, mNormalPoints);
        canvas.concat(mMatrix);

        mShaderPaint.setShader(mShader);
        mShaderPaint.setAlpha(isFront ? mPaintAlpha : (int) (mPaintAlpha * 0.25));
        float offsetX = getWidth() - Math.abs(Math.abs(mRotateY) - 90) * getWidth() / 90f;
        if (mRotateY < 0) {
            offsetX = -offsetX;
        }
        mMatrix.setTranslate(offsetX, 0);
        mShaderPaint.getShader().setLocalMatrix(mMatrix);
        canvas.drawRect(mRect, mShaderPaint);

        if (isFront) {
            super.dispatchDraw(canvas);
        }
        canvas.restore();
    }

    private void drawStroke(Canvas canvas, int firstIndex, int secondIndex) {
        mStrokePath.reset();
        mStrokePath.moveTo(mFrontPoints[firstIndex * 2], mFrontPoints[firstIndex * 2 + 1]);
        mStrokePath.lineTo(mFrontPoints[secondIndex * 2], mFrontPoints[secondIndex * 2 + 1]);
        mStrokePath.lineTo(mBehindPoints[secondIndex * 2], mBehindPoints[secondIndex * 2 + 1]);
        mStrokePath.lineTo(mBehindPoints[firstIndex * 2], mBehindPoints[firstIndex * 2 + 1]);

        mStrokePaint.setAlpha((int) (mPaintAlpha * 0.4));
        canvas.drawPath(mStrokePath, mStrokePaint);
    }

    private void drawShadow(Canvas canvas) {
        int firstIndex = mRotateY < 0 ? RIGHT_TOP : LEFT_TOP;
        int secondIndex = mRotateY < 0 ? RIGHT_BOTTOM : LEFT_BOTTOM;

        mStrokePath.reset();
        mStrokePath.moveTo(mFrontPoints[firstIndex * 2], mFrontPoints[firstIndex * 2 + 1]);
        mStrokePath.lineTo(mFrontPoints[secondIndex * 2], mFrontPoints[secondIndex * 2 + 1]);
        mStrokePath.lineTo(mBehindPoints[secondIndex * 2], mBehindPoints[secondIndex * 2 + 1]);
        mStrokePath.lineTo(mBehindPoints[firstIndex * 2], mBehindPoints[firstIndex * 2 + 1]);

        mMatrix.setTranslate(0, getHeight() / 2);
        mShaderPaint.getShader().setLocalMatrix(mMatrix);
        canvas.drawPath(mStrokePath, mShaderPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAnimator.isRunning()) {
            return false;
        }
        mGestureDetectorCompat.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ObjectAnimator.ofInt(this, "paintAlpha", 0, 255).start();
                mDirection = 0;
                isRepeat = false;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int endRotate = getEndRotate();
                mAnimator.setFloatValues(mRotateY, endRotate);
                mAnimator.setDuration((int) (Math.abs(endRotate - mRotateY) * 4));
                mAnimator.start();
                ObjectAnimator alphaAnimator = ObjectAnimator.ofInt(this, "paintAlpha", 255, 0).setDuration(400);
                alphaAnimator.setStartDelay((int) (Math.abs(endRotate - mRotateY) * 4));
                alphaAnimator.start();
                break;
        }

        return true;
    }

    public void setRotateY(float value) {
        if (value >= 90) {
            mRotateY = value - 180;
            if (!isRepeat || !hadChanged) {
                hadChanged = true;
                mIndex++;
                if (mIndex == getChildCount()) {
                    mIndex = 0;
                }
                resetChild();
            }
        } else if (value <= -90) {
            mRotateY = value + 180;
            if (!isRepeat || !hadChanged) {
                hadChanged = true;
                mIndex--;
                if (mIndex < 0) {
                    mIndex = getChildCount() - 1;
                }
                resetChild();
            }
        } else {
            mRotateY = value;
        }
        invalidate();
    }

    private int getEndRotate() {
        if (mDirection == TO_RIGHT && mRotateY > 0) {
            isRepeat = true;
            hadChanged = false;
            return 180;
        } else if (mDirection == TO_LEFT && mRotateY < 0) {
            isRepeat = true;
            hadChanged = false;
            return -180;
        } else {
            return 0;
        }
    }

    private void resetChild() {
        if (getChildCount() == 0) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(i == mIndex ? VISIBLE : GONE);
        }
    }

    @SuppressLint("unused")
    public void setPaintAlpha(int paintAlpha) {
        mPaintAlpha = paintAlpha;
        invalidate();
    }
}
