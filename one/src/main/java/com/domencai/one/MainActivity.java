package com.domencai.one;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        final MyAdapter myAdapter = new MyAdapter(getScreenWidth());
        recyclerView.setAdapter(myAdapter);
        final LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        HttpManager.getInstance().getOneList()
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<OneBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(OneBean oneBean) {
                        List<OneBean.DataBean> dataList = oneBean.data;
                        myAdapter.setList(dataList);
                        for (OneBean.DataBean dataBean : dataList) {
                            Log.w("MainActivity", "onNext: " + dataBean.toString());
                            Log.w("MainActivity", "onNext: ..........................");
                        }
                    }
                });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                    int position = recyclerView.getChildAdapterPosition(view);
                    OneBean.DataBean dataBean = myAdapter.getDataBean(position);
                    Glide.with(MainActivity.this)
                            .load(dataBean.hp_img_url)
                            .bitmapTransform(new BlurTransformation(MainActivity.this, 25, 4))
                            .into(new SimpleTarget<GlideDrawable>() {
                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                    recyclerView.setBackground(resource);
                                }
                            });
                }
            }
        });
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public void query(View view) {
        HttpManager.getInstance().getOneList()
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<OneBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(OneBean oneBean) {
                        List<OneBean.DataBean> dataList = oneBean.data;
                        for (OneBean.DataBean dataBean : dataList) {
                            Log.w("MainActivity", "onNext: " + dataBean.toString());
                            Log.w("MainActivity", "onNext: ..........................");
                        }
                    }
                });
    }

    private static class MyAdapter extends RecyclerView.Adapter<Helper> {
        private int mScreenWidth;
        private List<OneBean.DataBean> mList;

        MyAdapter(int screenWidth) {
            mScreenWidth = screenWidth;
            mList = new ArrayList<>();
        }

        @Override
        public Helper onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_layout, parent, false);
            view.getLayoutParams().width = mScreenWidth / 5  * 4;
            return new Helper(view);
        }

        @Override
        public void onBindViewHolder(final Helper holder, int position) {
            OneBean.DataBean dataBean = mList.get(position);
            holder.mTitle.setText(dataBean.hp_title);
            String author = dataBean.hp_author;
            author = author.substring(0, author.indexOf(" ")).replace("＆", " | ");
            holder.mAuthor.setText(author);
            holder.mContent.setText(dataBean.hp_content);
            Glide.with(holder.mCover.getContext())
                    .load(dataBean.hp_img_url)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<?
                                                        super GlideDrawable> glideAnimation) {
                            ImageView cover = holder.mCover;
                            float radio = resource.getIntrinsicHeight() * 1f / resource.getIntrinsicWidth();
                            cover.getLayoutParams().height = (int) (cover.getWidth() * radio);
                            cover.setImageDrawable(resource);
                        }
                    });

//            Horoscope horoscope = mHoroscopes[position % mHoroscopes.length];
//            holder.mContent.setText(horoscope.name);
//            holder.mIcon.setImageResource(horoscope.icon);
//            holder.mBg.setImageResource(horoscope.bg);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        void setList(List<OneBean.DataBean> list) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }

        OneBean.DataBean getDataBean(int position) {
            return mList.get(position);
        }
    }

    private static final class Helper extends RecyclerView.ViewHolder {
        ImageView mCover;
        TextView mContent;
        TextView mTitle;
        TextView mAuthor;

        Helper(View itemView) {
            super(itemView);
            mCover = itemView.findViewById(R.id.cover_image);
            mContent = itemView.findViewById(R.id.content_text);
            mTitle = itemView.findViewById(R.id.title_text);
            mAuthor = itemView.findViewById(R.id.author_text);
        }
    }
}
