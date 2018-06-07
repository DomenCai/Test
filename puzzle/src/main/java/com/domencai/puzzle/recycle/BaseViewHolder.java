package com.domencai.puzzle.recycle;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Domen、on 2018/3/30.
 */

@SuppressWarnings("WeakerAccess")
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public BaseViewHolder(ViewGroup parentGroup, int layoutId) {
        this(parentGroup, LayoutInflater.from(parentGroup.getContext()).inflate(layoutId, parentGroup, false));
    }

    public BaseViewHolder(ViewGroup parentGroup, View itemView) {
        super(itemView);
        initViews(itemView);
    }

    public abstract void initViews(View itemView);

    public abstract void updateViews(T data, int position);

}
