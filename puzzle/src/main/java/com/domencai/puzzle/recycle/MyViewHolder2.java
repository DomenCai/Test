package com.domencai.puzzle.recycle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.domencai.puzzle.R;

/**
 * Created by Domen、on 2018/3/30.
 */

public class MyViewHolder2 extends BaseViewHolder<String> {

    private TextView mTextView;

    public MyViewHolder2(ViewGroup parentGroup) {
        super(parentGroup, R.layout.view_holder_2);
    }

    @Override
    public void initViews(View itemView) {
        mTextView = (TextView) itemView.findViewById(R.id.text_1);
    }

    @Override
    public void updateViews(String data, int position) {
        mTextView.setText(data);
    }
}
