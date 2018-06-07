package com.domencai.puzzle.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.domencai.puzzle.util.DrawUtils;
import com.domencai.puzzle.R;

/**
 * Created by Domen、on 2017/8/31.
 */

public class PuzzleView extends FrameLayout {
    private int mInterval;
    private int mColumn;
    private int mRow;
    private int mPieceSize;

    public PuzzleView(Context context) {
        this(context, null);
    }

    public PuzzleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PuzzleView);
        mInterval = typedArray.getDimensionPixelSize(R.styleable.PuzzleView_interval, DrawUtils.dp2px(context, 4));
        mColumn = typedArray.getInt(R.styleable.PuzzleView_column, 3);
        mRow = typedArray.getInt(R.styleable.PuzzleView_row, 3);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        mPieceSize = (width - (mColumn + 3) * mInterval) / mColumn;
        int height = mPieceSize * mRow + (mRow + 3) * mInterval;
        setMeasuredDimension(width, height);
        Log.w("PuzzleView", "onMeasure: ");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && getChildCount() == 0) {
            Log.w("PuzzleView", "onSizeChanged: ");
            for (int i = 0; i < mColumn * mRow; i++) {
                TextView parent = (TextView) View.inflate(getContext(), R.layout.item_text, null);
                parent.setGravity(Gravity.BOTTOM);
                addView(parent, mPieceSize, mPieceSize);
                parent.setText(String.valueOf(i));
//                TextView textView = new TextView(getContext());
//                LayoutParams params = new LayoutParams(mPieceSize, mPieceSize);
//                textView.setLayoutParams(params);
//
//                textView.setText(String.valueOf(i));
////                textView.setGravity(Gravity.CENTER | Gravity.BOTTOM);
//                textView.setGravity(Gravity.CENTER_HORIZONTAL);
//                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//                textView.setBackgroundColor(Color.GRAY);
//                textView.setTextColor(Color.WHITE);
//                addView(textView);
            }
            TextView textView = new TextView(getContext());
            LayoutParams params = new LayoutParams(mPieceSize * 2, mPieceSize);
            textView.setLayoutParams(params);
            textView.setText(String.valueOf(11));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
            textView.setBackgroundColor(Color.GRAY);
            textView.setTextColor(Color.WHITE);
            addView(textView);
            requestLayout();
        }
        Log.w("PuzzleView", "onSizeChanged: " + getChildCount());
    }

    @Override
    protected void onLayout(boolean b, int left, int top, int right, int bottom) {
        int childLeft = mInterval * 2;
        int childTop = mInterval * 2;
        View child;
        Log.w("PuzzleView", "onLayout: " + getChildCount());
        for (int i = 0; i < mColumn * mRow; i++) {
            child = getChildAt(i);
            child.layout(childLeft, childTop, childLeft + mPieceSize, childTop + mPieceSize);
            childLeft += mPieceSize + mInterval;
            if (childLeft >= getMeasuredWidth() - mInterval) {
                childLeft = mInterval * 2;
                childTop += mPieceSize + mInterval;
            }
        }
        child = getChildAt(9);
        child.layout(0, 0, mPieceSize * 2, mPieceSize);
        Log.w("PuzzleView", "onLayout: " + child.getMeasuredWidth() + ", " + child.getMeasuredHeight());
    }
}
