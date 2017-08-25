package com.domencai.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.domencai.share.foldable.FoldableActivity;
import com.domencai.share.paging.PagingActivity;
import com.domencai.share.rotate.Rotate3DActivity;
import com.domencai.share.vector.VectorActivity;

/**
 * Created by Domen、on 2017/8/23.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toPagingView(View view) {
        startActivity(new Intent(this, PagingActivity.class));
    }

    public void toRotate3DLayout(View view) {
        startActivity(new Intent(this, Rotate3DActivity.class));
    }

    public void toFoldableLayout(View view) {
        startActivity(new Intent(this, FoldableActivity.class));
    }

    public void toVector(View view) {
        startActivity(new Intent(this, VectorActivity.class));
    }
}
