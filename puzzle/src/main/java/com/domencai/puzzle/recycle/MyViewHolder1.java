package com.domencai.puzzle.recycle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.domencai.puzzle.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Domen、on 2018/3/30.
 */

public class MyViewHolder1 extends BaseViewHolder<String> {

    private TextView mTextView;
    private RecyclerView mRecyclerView;

    private BaseAdapter<String> mAdapter = new BaseAdapter<String>() {
        @Override
        public BaseViewHolder<String> onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new MyViewHolder2(viewGroup);
        }
    };

    public MyViewHolder1(ViewGroup parentGroup) {
        super(parentGroup, R.layout.view_holder_1);
    }

    @Override
    public void initViews(View itemView) {
        mTextView = (TextView) itemView.findViewById(R.id.text_1);
        mRecyclerView = (RecyclerView) itemView.findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
//        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void updateViews(String data, int position) {
        mTextView.setText(data);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("Sub Item:" + i);
        }
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setData(list);
    }
}
