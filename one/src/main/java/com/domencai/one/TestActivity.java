package com.domencai.one;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.domencai.one.progress.OnProgressListener;
import com.domencai.one.progress.ProgressManager;

/**
 * Created by Domen、on 2017/11/21.
 */

public class TestActivity extends AppCompatActivity {
    private final static String LAGER = "https://wx1.sinaimg.cn/large/ab53832cly1fo73o5men3g208w0ebx6u.gif";
    private final static String THUMB = "http://ww3.sinaimg.cn/thumbnail/ab53832cly1fo73o5men3g208w0ebx6u.gif";

    private GifDrawable mGifDrawable;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        final StarAnimatorView starAnimator = findViewById(R.id.star_animator);
        starAnimator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starAnimator.startAnimator();
            }
        });
//        mProgressBar = findViewById(R.id.glide_progress);
//        ImageView imageView = findViewById(R.id.image_view);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mGifDrawable != null) {
//                    if (mGifDrawable.isRunning()) {
//                        mGifDrawable.stop();
//                    } else {
//                        mGifDrawable.start();
//                    }
//                }
//            }
//        });
//        addListener();
//        loadGif(imageView);
    }

    private void addListener() {
        ProgressManager.getInstance().addListener(LAGER, new OnProgressListener() {
            @Override
            public void onProgress(String imageUrl, long bytesRead, long totalBytes, boolean isDone, GlideException exception) {
                mProgressBar.setProgress((int) (bytesRead * 100f / totalBytes));
            }
        });
    }


    public void loadGif(final ImageView imageView){
        DrawableImageViewTarget target1 = new DrawableImageViewTarget(imageView){
            @Override
            public void onResourceReady(Drawable resource, @Nullable Transition<? super Drawable> transition) {
                super.onResourceReady(resource, transition);
                Log.w("TestActivity", "onResourceReady: ");
                if (resource instanceof GifDrawable) {
                    mGifDrawable = (GifDrawable) resource;
                }
            }
        };
        RequestBuilder<Drawable> builder = Glide.with(this).load(THUMB);
        Glide.with(this)
                .load(LAGER)
                .thumbnail(builder)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(target1);
    }
}
