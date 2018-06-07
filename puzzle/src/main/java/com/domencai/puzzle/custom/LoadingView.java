package com.domencai.puzzle.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.domencai.puzzle.R;

/**
 * Created by Domen、on 2017/9/21.
 */

public class LoadingView extends View {
    private static final int SPEED = 10;
    private Bitmap mBitmap;
    private int mRotate;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_loading);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mBitmap.getWidth() * 2, mBitmap.getHeight() * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int rotate = mRotate % 270;
        if (rotate < 180) {
            canvas.rotate(rotate, getWidth() / 2, getHeight() / 2);
        }
        mRotate += SPEED;
        canvas.drawBitmap(mBitmap, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2, null);
        invalidate();
    }
}
