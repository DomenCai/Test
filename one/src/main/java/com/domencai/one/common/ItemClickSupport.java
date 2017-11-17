package com.domencai.one.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * BaseRecycleViewAdapter 点击长按事件处理
 */
public class ItemClickSupport {

    public interface OnItemClickListener {
        void onItemClick(RecyclerView recyclerView, View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(RecyclerView recyclerView, View view, int position);
    }

    private RecyclerView mRecyclerView;
    private BaseAdapter mAdapter;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public ItemClickSupport(RecyclerView recyclerView, BaseAdapter adapter) {
        this.mRecyclerView = recyclerView;
        this.mAdapter = adapter;
        this.mAdapter.setItemClickSupport(this);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (null != onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        if (null != onItemLongClickListener) {
            this.mOnItemLongClickListener = onItemLongClickListener;
        }
    }

    void onItemClick(View v) {
        if (null != mRecyclerView && null != mAdapter && null != mOnItemClickListener) {
            final int position = mRecyclerView.getChildLayoutPosition(v);
            mOnItemClickListener.onItemClick(mRecyclerView, v, position);
        }
    }

    boolean onItemLongClick(View v) {
        if (null != mRecyclerView && null != mAdapter && null != mOnItemLongClickListener) {
            final int position = mRecyclerView.getChildLayoutPosition(v);
            return mOnItemLongClickListener.onItemLongClick(mRecyclerView, v, position);
        }
        return false;
    }
}
