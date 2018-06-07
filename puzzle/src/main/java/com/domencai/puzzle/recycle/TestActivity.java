package com.domencai.puzzle.recycle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Domen、on 2018/3/30.
 */

public class TestActivity extends AppCompatActivity {

    private BaseAdapter<String> mAdapter = new BaseAdapter<String>() {
        @Override
        public BaseViewHolder<String> onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new MyViewHolder1(viewGroup);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView recyclerView = new RecyclerView(this);
        setContentView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("item:" + i);
        }
        mAdapter.setData(list);
    }
}
