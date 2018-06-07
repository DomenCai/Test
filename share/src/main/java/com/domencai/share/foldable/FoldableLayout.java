package com.domencai.share.foldable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.domencai.share.R;
import com.domencai.share.interpolator.EaseCubicInterpolator;

/**
 * Created by Domen、on 2017/8/23.
 */

public class FoldableLayout extends FrameLayout {

    private static final int ANIMATION_DURATION = 800;
    private static final int CAMERA_DISTANCE = 3000;

    protected RelativeLayout mContentLayout;
    protected ImageView mImageViewBelow;
    protected ImageView mImageViewAbove;
    protected View mCoverView;
    protected View mDetailView;
    protected View mRootView;
    protected int mCoverHeight;
    private boolean mIsFolded = true;
    private boolean mIsAnimating = false;
    private Matrix mMatrix;

    private final TimeInterpolator mTimeInterpolator = new EaseCubicInterpolator();

    public FoldableLayout(@NonNull Context context) {
        this(context, null);
    }

    public FoldableLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupView(getContext());
    }

    private void setupView(Context context) {
        setClipChildren(false);
        mRootView = LayoutInflater.from(context).inflate(R.layout.foldable_layout, this, true);
        mContentLayout = (RelativeLayout) findViewById(R.id.foldable_content_view);
        mImageViewBelow = (ImageView) findViewById(R.id.foldable_layout_below_bitmap);
        mImageViewAbove = (ImageView) findViewById(R.id.foldable_layout_above_bitmap);
        mMatrix = new Matrix();
        mMatrix.postScale(1, -1);
    }

    public void setupViews(@LayoutRes int coverLayoutId, @LayoutRes int detailLayoutId, int coverHeight) {
        mCoverView = LayoutInflater.from(getContext()).inflate(coverLayoutId, mContentLayout, false);
        mDetailView = LayoutInflater.from(getContext()).inflate(detailLayoutId, mContentLayout, false);
        mContentLayout.addView(mCoverView);
        mContentLayout.addView(mDetailView);
        mDetailView.setVisibility(GONE);
        float density = getResources().getDisplayMetrics().density;
        mCoverHeight = (int) (coverHeight * density);
        mContentLayout.setCameraDistance(CAMERA_DISTANCE * density);
    }

    public void toggle() {
        if (mIsAnimating) {
            return;
        }
        ensureBitmaps();
        mContentLayout.setPivotY(mCoverHeight);
        mContentLayout.setPivotX(mCoverView.getWidth() / 2);

        if (mIsFolded) {
            unfoldWithAnimation();
        } else {
            foldWithAnimation();
        }
    }

    public boolean isFolded() {
        return mIsFolded;
    }

    public void setFolded(boolean folded) {
        mIsFolded = folded;
        if (mIsFolded) {
            foldWithoutAnimation();
        } else {
            unfoldWithoutAnimation();
        }
    }

    /**
     * 直接折叠布局，在列表中用到
     */
    private void foldWithoutAnimation() {
        mIsFolded = true;
        mCoverView.setVisibility(VISIBLE);
        mDetailView.setVisibility(GONE);
        if (getLayoutParams() != null) {
            getLayoutParams().height = mCoverHeight;
        }
        requestLayout();
    }

    /**
     * 直接展开布局，在列表中用到
     */
    private void unfoldWithoutAnimation() {
        mIsFolded = false;
        mCoverView.setVisibility(GONE);
        mDetailView.setVisibility(VISIBLE);
        if (getLayoutParams() != null) {
            getLayoutParams().height = mCoverHeight * 2;
        }
        requestLayout();
    }

    /**
     * 展开动画
     */
    private void unfoldWithAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, -180);

        animator.setInterpolator(mTimeInterpolator);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private boolean mReplaceDone = false;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                if (fraction >= 0.5 && !mReplaceDone) {
                    mReplaceDone = true;
                    mCoverView.setVisibility(GONE);
                }
                mContentLayout.setRotationX((Float) animation.getAnimatedValue());

                getLayoutParams().height = (int) (mCoverHeight * (1 + fraction));
                requestLayout();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(true);
                mDetailView.setVisibility(VISIBLE);
                mContentLayout.setRotationX(0);
                mIsFolded = false;
                mIsAnimating = false;
            }
        });
        animator.start();
    }

    /**
     * 折叠动画
     */
    private void foldWithAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(-180, 0);

        animator.setInterpolator(mTimeInterpolator);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private boolean mReplaceDone = false;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                if (fraction >= 0.5 && !mReplaceDone) {
                    mReplaceDone = true;
                    mCoverView.setVisibility(VISIBLE);
                }
                mContentLayout.setRotationX((Float) animation.getAnimatedValue());
                getLayoutParams().height = (int) (mCoverHeight * (2 - fraction));
                requestLayout();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimating = true;
                mContentLayout.setRotationX(180);
                mDetailView.setVisibility(GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(true);
                mIsFolded = true;
                mIsAnimating = false;
            }
        });
        animator.start();
    }

    private void ensureBitmaps() {
        Bitmap detailBitmap = getBitmap(mDetailView);
        int width = detailBitmap.getWidth();
        int height = detailBitmap.getHeight() / 2;
        Bitmap detailT = Bitmap.createBitmap(detailBitmap, 0, 0, width, height);
        Bitmap detailB = Bitmap.createBitmap(detailBitmap, 0, height, width, height, mMatrix, true);

        mImageViewAbove.setBackground(new BitmapDrawable(getResources(), detailB));
        mImageViewBelow.setBackground(new BitmapDrawable(getResources(), detailT));
        setVisibility(false);
    }

    private Bitmap getBitmap(View view) {
        int specW = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY);
        int specH = MeasureSpec.makeMeasureSpec(mCoverHeight * 2, MeasureSpec.EXACTLY);
        view.measure(specW, specH);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        view.draw(c);
        return b;
    }

    private void setVisibility(boolean gone) {
        mImageViewAbove.setVisibility(gone ? GONE : VISIBLE);
        mImageViewBelow.setVisibility(gone ? GONE : VISIBLE);
    }
}
