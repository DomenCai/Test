package com.domencai.puzzle.recycle;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by Domen、on 2018/3/30.
 */

@SuppressWarnings("WeakerAccess")
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> {

    protected List<T> mData;

    @Override
    public void onBindViewHolder(BaseViewHolder<T> tBaseViewHolder, int i) {
        tBaseViewHolder.updateViews(mData.get(i), i);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }
}
