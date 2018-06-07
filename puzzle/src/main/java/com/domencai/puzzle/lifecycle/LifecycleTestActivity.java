package com.domencai.puzzle.lifecycle;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.SeekBar;

import com.domencai.puzzle.R;

/**
 * Created by Domen、on 2017/9/4.
 */

public class LifecycleTestActivity extends LifecycleActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setMax(100);
        final ViewModelTest viewModelTest = ViewModelProviders.of(this).get(ViewModelTest.class);
        viewModelTest.getMediatorLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.w("LifecycleTestActivity", "onChanged: " + s);
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewModelTest.setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
