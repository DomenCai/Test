package com.domencai.one.parallax;

import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

/**
 * ParallaxBackLayout
 *
 * @author An Zewei (anzewei88[at]gmail[dot]com)
 * @since ${VERSION}
 */

class ShadowDrawable extends GradientDrawable {

    private static final int[] COLORS = {0x66000000, 0x11000000, 0x00000000};

    ShadowDrawable() {
        super(Orientation.RIGHT_LEFT, COLORS);
        setGradientRadius(90);
        setSize(50, 50);
    }

    void drawShadow(Canvas canvas, View child, int width) {
        setBounds(child.getLeft() - getIntrinsicWidth(), child.getTop(), child.getLeft(), child.getBottom());
        setAlpha((width - child.getLeft()) * 255 / width);
        draw(canvas);
    }
}
