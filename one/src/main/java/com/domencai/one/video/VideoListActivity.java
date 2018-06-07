package com.domencai.one.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.domencai.one.R;
import com.domencai.one.common.BaseAdapter;
import com.domencai.one.common.BaseViewHolder;
import com.domencai.one.utils.RxUtils;

import java.util.List;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * Created by Domen、on 2018/2/11.
 */

public class VideoListActivity extends AppCompatActivity {

    private BaseAdapter<VideoBean> mAdapter = new BaseAdapter<VideoBean>(R.layout.item_video) {
        @Override
        public void bind(BaseViewHolder viewHolder, VideoBean data, int position) {
            JZVideoPlayerStandard jzvd = viewHolder.findView(R.id.video_player);
            jzvd.setUp(data.mVplayUrl, JZVideoPlayer.SCREEN_WINDOW_LIST, data.mWbody);
            Glide.with(VideoListActivity.this)
                    .load(data.mVpicSmall)
                    .into(jzvd.thumbImageView);

//            viewHolder.setText(R.id.video_player, data.title)
//                    .setText(R.id.tv_author, data.author)
//                    .setText(R.id.tv_summary, data.shortIntro)
//                    .setText(R.id.tv_sentiment, data.latelyFollower + "人气 | " + data.retentionRatio + "%读者留存")
//                    .setImage(R.id.iv_cover, BookServer.IMG_URL + data.cover);
        }
    };
//
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomend);
        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                JZVideoPlayer jzvd = view.findViewById(R.id.video_player);
                if (jzvd != null && JZUtils.dataSourceObjectsContainsUri(jzvd.dataSourceObjects, JZMediaManager.getCurrentDataSource())) {
                    JZVideoPlayer.releaseAllVideos();
                }
            }
        });
        VideoServer.getInstance().getVideo().subscribe(new RxUtils.SimpleSubscriber<List<VideoBean>>() {
            @Override
            public void onNext(List<VideoBean> videoBeans) {
                mAdapter.setData(videoBeans);
                for (VideoBean bean : videoBeans) {
                    Log.w("VideoListActivity", "onNext: " + bean.mWbody);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

}
