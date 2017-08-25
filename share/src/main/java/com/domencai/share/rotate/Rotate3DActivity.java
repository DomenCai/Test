package com.domencai.share.rotate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.domencai.share.R;
import com.domencai.share.paging.PagingView;

/**
 * Created by Domen、on 2017/8/24.
 */

public class Rotate3DActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_3d);
    }

    public void paging(View view) {
        if (view instanceof PagingView) {
            ((PagingView) view).startAnimator();
        }
    }
}
