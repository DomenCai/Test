package com.domencai.one.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

/**
 * Created by Domen、on 2017/11/16.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    private int mLayoutId;
    private List<T> mData;
    private ItemClickSupport mItemClickSupport;

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.w("BaseAdapter", "onClick: ");
            if (mItemClickSupport != null) {
                Log.w("BaseAdapter", "onClick: mItemClickSupport != null");
                mItemClickSupport.onItemClick(view);
            }
        }
    };

    private View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            return mItemClickSupport != null && mItemClickSupport.onItemLongClick(view);
        }
    };

    protected BaseAdapter(int layoutId) {
        mLayoutId = layoutId;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = new BaseViewHolder(parent, mLayoutId);
        Log.w("BaseAdapter", "onCreateViewHolder: ");
        if (mItemClickSupport != null) {
            Log.w("BaseAdapter", "onCreateViewHolder: mItemClickSupport != null");
            viewHolder.itemView.setOnClickListener(mClickListener);
            viewHolder.itemView.setOnLongClickListener(mLongClickListener);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        bind(holder, getItemData(position), position);
    }

    public abstract void bind(BaseViewHolder viewHolder, T data, int position);

    @Override
    public int getItemCount() {
        return null != mData ? mData.size() : 0;
    }

    public void setData(@NonNull List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public List<T> getData() {
        if (mData == null) {
            return Collections.emptyList();
        }
        return mData;
    }

    public T getItemData(int position) {
        if (position < 0 || position >= getItemCount()) {
            return null;
        }
        return mData.get(position);
    }

    void setItemClickSupport(ItemClickSupport itemClickSupport) {
        Log.w("BaseAdapter", "setItemClickSupport: ");
        this.mItemClickSupport = itemClickSupport;
    }
}
