package com.domencai.one;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.domencai.one.api.BookServer;
import com.domencai.one.bean.Recommend.RecommendBooks;
import com.domencai.one.common.BaseAdapter;
import com.domencai.one.common.BaseViewHolder;
import com.domencai.one.common.ItemClickSupport;
import com.domencai.one.utils.BookManager;
import com.domencai.one.utils.RxUtils;

import java.util.List;

/**
 * Created by Domen、on 2017/11/16.
 */

public class RecommendActivity extends AppCompatActivity implements ItemClickSupport.OnItemClickListener {

    private BaseAdapter<RecommendBooks> mAdapter = new BaseAdapter<RecommendBooks>(R.layout.item_recommend) {
        @Override
        public void bind(BaseViewHolder viewHolder, RecommendBooks data, int position) {
            viewHolder.setText(R.id.tv_title, data.title)
                    .setText(R.id.tv_author, data.author)
                    .setText(R.id.tv_summary, data.shortIntro)
                    .setText(R.id.tv_sentiment, data.latelyFollower + "人气 | " + data.retentionRatio + "%读者留存")
                    .setImage(R.id.iv_cover, BookServer.IMG_URL + data.cover);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomend);
        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setAdapter(mAdapter);

        String gender = "male";
        BookServer.getInstance().getRecommend(gender)
                .subscribe(new RxUtils.SimpleSubscriber<List<RecommendBooks>>() {
                    @Override
                    public void onNext(List<RecommendBooks> recommendBooks) {
                        recommendBooks.addAll(BookManager.getInstance().getRecommends());
                        mAdapter.setData(recommendBooks);
                    }
                });
        new ItemClickSupport(recyclerView, mAdapter).setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View view, int position) {
        Log.w("RecommendActivity", "onItemClick: " + mAdapter.getItemData(position).title);
        BookActivity.start(this, mAdapter.getItemData(position)._id);
    }
}
