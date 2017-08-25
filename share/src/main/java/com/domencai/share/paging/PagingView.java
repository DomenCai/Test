package com.domencai.share.paging;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.domencai.share.R;
import com.domencai.share.interpolator.EaseCubicInterpolator;
import com.domencai.share.utils.DrawUtils;

/**
 * Created by Domen、on 2017/8/4.
 */

public class PagingView extends View {

    private Bitmap mBitmap;
    private Camera mCamera = new Camera();

    private int mRotateY;
    private int mRotateZ;
    private boolean isAnimator;

    public PagingView(Context context) {
        this(context, null);
    }

    public PagingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.mipmap.map_icon, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = options.outWidth / DrawUtils.dp2px(getContext(), 72);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.map_icon, options);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPaging(canvas);
    }

    private void drawPaging(Canvas canvas) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int halfSize = mBitmap.getWidth() / 2;

        // 画没有翻转的部分
        canvas.save();
        canvas.rotate(mRotateZ, centerX, centerY);
        canvas.clipRect(0, 0, centerX, getHeight());
        canvas.rotate(-mRotateZ, centerX, centerY);
        canvas.drawBitmap(mBitmap, centerX - halfSize, centerY - halfSize, null);
        canvas.restore();

        // 画右边经过翻转后的图
        canvas.save();
        rotateCamera(canvas);
        canvas.rotate(mRotateZ, centerX, centerY);
        canvas.clipRect(centerX, 0, getWidth(), getHeight());
        canvas.rotate(-mRotateZ, centerX, centerY);
        canvas.drawBitmap(mBitmap, centerX - halfSize, centerY - halfSize, null);
        canvas.restore();
    }

    private void rotateCamera(Canvas canvas) {
        mCamera.save();
        mCamera.rotateY(mRotateY);
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.rotate(mRotateZ);
        mCamera.applyToCanvas(canvas);
        canvas.rotate(-mRotateZ);
        canvas.translate(-getWidth() / 2, -getHeight() / 2);
        mCamera.restore();
    }

    @SuppressWarnings("unused")
    public void setRotateY(int rotateY) {
        this.mRotateY = rotateY;
        invalidate();
    }

    @SuppressWarnings("unused")
    public void setRotateZ(int rotateZ) {
        this.mRotateZ = rotateZ;
        invalidate();
    }

    public void startAnimator() {
        if (isAnimator) {
            return;
        }
        isAnimator = true;
        ObjectAnimator pagingUpAnimator = ObjectAnimator.ofInt(this, "rotateY", 0, -45).setDuration(600);
        pagingUpAnimator.setInterpolator(new EaseCubicInterpolator());
        pagingUpAnimator.start();
        ObjectAnimator rotateAnimator = ObjectAnimator.ofInt(this, "rotateZ", 0, -360).setDuration(1200);
        rotateAnimator.setInterpolator(new EaseCubicInterpolator());
        rotateAnimator.setStartDelay(600);
        rotateAnimator.start();
        ObjectAnimator pagingDownAnimator = ObjectAnimator.ofInt(this, "rotateY", -45, 0).setDuration(400);
        pagingDownAnimator.setInterpolator(new EaseCubicInterpolator());
        pagingDownAnimator.setStartDelay(1800);
        pagingDownAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimator = false;
            }
        });
        pagingDownAnimator.start();
    }
}
