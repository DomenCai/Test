package com.domencai.one.parallax;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.domencai.one.utils.DrawUtils;

/**
 * Created by Domen、on 2018/2/12.
 */
@SuppressWarnings("unused")
public class ParallaxBackLayout extends FrameLayout {

    private static final float SCROLL_THRESHOLD = 0.5f;
    private static final float FLING_THRESHOLD = DrawUtils.dp2px(3000);

    private Activity mSwipeHelper;
    private View mContentView;
    private IBackgroundView mBackgroundView;
    private ShadowDrawable mShadowLeft;
    private ViewDragHelper mDragHelper;
    private ParallaxSlideCallback mSlideCallback;

    private int mContentLeft;
    private boolean mEnable = true;

    public ParallaxBackLayout(Context context) {
        super(context);
        mDragHelper = ViewDragHelper.create(this, new ViewDragCallback());
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        mShadowLeft = new ShadowDrawable();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mEnable || !mBackgroundView.canGoBack()) {
            return false;
        }
        try {
            return mDragHelper.shouldInterceptTouchEvent(event);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mEnable || !mBackgroundView.canGoBack()) {
            return false;
        }
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean drawShadow = child == mContentView && mContentLeft > 0;
        if (mEnable) {
            drawThumb(canvas, child);
        }
        boolean ret = super.drawChild(canvas, child, drawingTime);
        if (mEnable && drawShadow && mDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
            mShadowLeft.drawShadow(canvas, child, getWidth());
        }
        return ret;
    }

    private void drawThumb(Canvas canvas, View child) {
        if (mContentLeft == 0)
            return;
        int store = canvas.save();
        int left = (child.getLeft() - getWidth()) / 2;
        canvas.translate(left, 0);
        canvas.clipRect(0, 0, left + getWidth(), child.getBottom());
        mBackgroundView.draw(canvas);
        canvas.restoreToCount(store);
    }

    public void attachToActivity(Activity activity) {
        mSwipeHelper = activity;
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decor.removeView(decorChild);
        addView(decorChild, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView = decorChild;
        decor.addView(this);
    }

    public void setEnableGesture(boolean enable) {
        mEnable = enable;
    }

    public void setSlideCallback(ParallaxSlideCallback slideCallback) {
        mSlideCallback = slideCallback;
    }

    public boolean scrollToFinishActivity() {
        if (!mEnable || !mBackgroundView.canGoBack()) {
            return false;
        }
        if (mDragHelper.smoothSlideViewTo(mContentView, getWidth(), 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
            postInvalidate();
            return true;
        }
        return false;
    }

    /**
     * Sets background view.
     */
    public void setBackgroundView(IBackgroundView backgroundView) {
        mBackgroundView = backgroundView;
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {

        private float mPercent;
        private int mLastDx;

        @Override
        public boolean tryCaptureView(View view, int pointerId) {
            boolean edgeTouched = mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_LEFT, pointerId);
            boolean directionCheck = !mDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_VERTICAL, pointerId);
            return edgeTouched & directionCheck;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return child.getWidth();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mLastDx = dx;
            mContentLeft = left;
            mPercent = Math.abs(left * 1f / mContentView.getWidth());
            invalidate();
            if (mSlideCallback != null) {
                mSlideCallback.onPositionChanged(mPercent);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            boolean toRight = xvel >= 0 && mLastDx >= 0;
            boolean moreThanThreshold = xvel * mLastDx > FLING_THRESHOLD || mPercent > SCROLL_THRESHOLD;
            int left = toRight && moreThanThreshold ? releasedChild.getWidth() : 0;
            mDragHelper.settleCapturedViewAt(left, 0);
            invalidate();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (mSlideCallback != null) {
                mSlideCallback.onStateChanged(state);
            }
            if (state == ViewDragHelper.STATE_IDLE && mPercent >= 0.999f) {
                mSwipeHelper.finish();
                mSwipeHelper.overridePendingTransition(0, 0);
            }
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return Math.min(child.getWidth(), Math.max(left, 0));
        }
    }

    /**
     * The interface Background view.
     */
    public interface IBackgroundView {
        void draw(Canvas canvas);

        boolean canGoBack();
    }

    public interface ParallaxSlideCallback {
        void onStateChanged(int state);

        void onPositionChanged(float percent);
    }

}
