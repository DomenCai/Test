package com.domencai.puzzle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by Domen、on 2017/9/18.
 */

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        boolean isToday = DateUtils.isToday(System.currentTimeMillis() - 7 * 60 * 60 * 1000);
        Log.w("StartActivity", "onCreate: " + isToday);
        final ImageView imageView = (ImageView) findViewById(R.id.image_view);
        ImageView imageViewBlur = (ImageView) findViewById(R.id.image_view_blur);
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1434873740857-1bc5653afda8")
                .bitmapTransform(new BlurTransformation(this, 25, 4))
//                .bitmapTransform(new SketchFilterTransformation(this))
                .into(imageViewBlur);
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1443827423664-eac70d49dd0d")
                .into(imageView);
//        final BlurredView blurredView = (BlurredView) findViewById(R.id.blurred_view);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                blurredView.setBlurredLevel(progress);
                imageView.setAlpha(1 - progress * 0.01f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
