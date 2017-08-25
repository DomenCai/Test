package com.domencai.share.foldable;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.domencai.share.R;

import java.util.HashMap;

/**
 * Created by Domen、on 2017/8/21.
 */

public class FoldableActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foldable_list);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.foldable_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter());
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView
                    .State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen
                        .activity_vertical_margin);
            }
        });
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyHelper> {
        private HashMap<Integer, Boolean> mState = new HashMap<>();

        @Override
        public MyHelper onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyHelper(new FoldableLayout(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(final MyHelper holder, int position) {
            final int index = position;
            final FoldableLayout layout = (FoldableLayout) holder.itemView;
            holder.setImage(ItemContent.PATH[index]);
            holder.setText(index % ItemContent.TITLE.length);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout.toggle();
                    mState.put(index, !layout.isFolded());
                }
            });
            if (mState.containsKey(index)) {
                layout.setFolded(mState.get(index));
            } else {
                layout.setFolded(true);
            }
        }

        @Override
        public int getItemCount() {
            return ItemContent.PATH.length;
        }
    }

    private static class MyHelper extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mContentText;
        private ImageView mCoverImage;
        private ImageView mDetailImage;

        MyHelper(final FoldableLayout itemView) {
            super(itemView);
            itemView.setupViews(R.layout.item_cover, R.layout.item_detail, 100);
            mTitleText = (TextView) itemView.findViewById(R.id.title_text);
            mContentText = (TextView) itemView.findViewById(R.id.content_text);
            mCoverImage = (ImageView) itemView.findViewById(R.id.cover_image);
            mDetailImage = (ImageView) itemView.findViewById(R.id.detail_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.toggle();
                }
            });
        }

        void setImage(String path) {
            Glide.with(itemView.getContext()).load(path).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super
                        GlideDrawable> glideAnimation) {
                    mCoverImage.setImageDrawable(resource);
                    mDetailImage.setImageDrawable(resource);
                }
            });
        }

        void setText(int position) {
            mTitleText.setText(ItemContent.TITLE[position]);
            mContentText.setText(ItemContent.CONTENT[position]);
        }
    }
}
