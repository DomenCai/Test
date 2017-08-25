package com.domencai.share.vector;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.domencai.share.R;

/**
 * Created by Domen、on 2017/8/25.
 */

public class VectorActivity extends AppCompatActivity {

    private boolean mIsDrawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector);
    }

    public void drawer2Allow(View view) {
        ImageView imageView = (ImageView) view;

        AnimatedVectorDrawable allow2drawer = (AnimatedVectorDrawable) getDrawable(R.drawable.allow2drawer_anim);
        AnimatedVectorDrawable drawer2allow = (AnimatedVectorDrawable) getDrawable(R.drawable.drawer2allow_anim);

        AnimatedVectorDrawable animator = mIsDrawer ? drawer2allow : allow2drawer;
        imageView.setImageDrawable(animator);
        if (animator != null) {
            animator.start();
            mIsDrawer = !mIsDrawer;
        }
    }

    public void start(View view) {
        ImageView imageView = (ImageView) view;

        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }
}
