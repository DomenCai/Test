package com.domencai.one.common;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by Domen、on 2017/11/16.
 */

public class BaseViewHolder  extends RecyclerView.ViewHolder {
    private SparseArray<View> mViewSparseArray;

    BaseViewHolder(ViewGroup parent, int layoutId) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        mViewSparseArray = new SparseArray<>();
    }

    @SuppressWarnings("unchecked")
    public  <V extends View> V findView(int id) {
        View v = mViewSparseArray.get(id);
        if (v == null) {
            v = itemView.findViewById(id);
            mViewSparseArray.put(id, v);
        }
        return (V) v;
    }

    public BaseViewHolder setText(int id, String text) {
        TextView textView = findView(id);
        textView.setText(text);
        return this;
    }

    public BaseViewHolder setTextColor(int id, int color) {
        TextView textView = findView(id);
        textView.setTextColor(color);
        return this;
    }

    public BaseViewHolder setImage(int id, String imgUrl) {
        ImageView imageView = findView(id);
        Glide.with(itemView.getContext()).load(imgUrl).into(imageView);
        return this;
    }
}
