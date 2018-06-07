package com.domencai.puzzle;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.domencai.puzzle.layoutmanager.OverLayCardLayoutManager;
import com.domencai.puzzle.layoutmanager.RenRenCallback;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private LinearLayoutManager mLayoutManager;
//    private OrientationHelper mOrientationHelper;
    private RecyclerView mRecycleView;
    private LinearSnapHelper mSnapHelper;
//    private ImageView mCenterImage;
//    private ImageView mLeftImage;
//    private ImageView mRightImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mCenterImage = (ImageView) findViewById(R.id.iv_center);
//        mLeftImage = (ImageView) findViewById(R.id.iv_left);
//        mRightImage = (ImageView) findViewById(R.id.iv_right);
//        mCenterImage.setCameraDistance(4000 * getResources().getDisplayMetrics().density);
        mRecycleView = (RecyclerView) findViewById(R.id.recycle);
        MyAdapter adapter = new MyAdapter(getScreenWidth());
        mRecycleView.setAdapter(adapter);
//        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mRecycleView.setLayoutManager(new OverLayCardLayoutManager());
        OverLayCardLayoutManager.CardConfig.initConfig(this);
        ItemTouchHelper.Callback callback = new RenRenCallback(mRecycleView, adapter, Arrays.asList(mHoroscopes));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecycleView);
//        mRecycleView.scrollToPosition(Integer.MAX_VALUE / 2);
//        mSnapHelper = new MySnapHelper();
//        mSnapHelper.attachToRecyclerView(mRecycleView);
//        mOrientationHelper = OrientationHelper.createHorizontalHelper(mLayoutManager);
    }

    private OnScrollListener mScrollListener = new OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            int center = mOrientationHelper.getEnd() / 2;
//            for (int i = 0; i < mLayoutManager.getChildCount(); i++) {
//                final View child = mLayoutManager.getChildAt(i);
//                View bg = child.findViewById(R.id.iv_bg);
//                float fraction = (getViewCenter(child) - center) / center;
//                float absFraction = Math.abs(fraction);
//                float scale = 2.5f - absFraction * 1.25f;
//                float tranY = center * absFraction * absFraction;
//                float tranX = fraction * center * 0.75f;
//                bg.setScaleX(scale);
//                bg.setScaleY(scale);
//                bg.setTranslationY(tranY);
//                bg.setTranslationX(tranX);
//            }

        }

//        private float getViewCenter(View view) {
//            return mOrientationHelper.getDecoratedStart(view) +
//                    mOrientationHelper.getDecoratedMeasurement(view) / 2;
//        }
    };

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecycleView.addOnScrollListener(mScrollListener);
    }

    @Override
    protected void onPause() {
        mRecycleView.removeOnScrollListener(mScrollListener);
        super.onPause();
    }

    private static class MyAdapter extends RecyclerView.Adapter<Helper> {
        private int mScreenWidth;

        MyAdapter(int screenWidth) {
            mScreenWidth = screenWidth;
        }

        @Override
        public Helper onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_recycle, parent, false);
            view.getLayoutParams().width = mScreenWidth / 3  * 2;
            return new Helper(view);
        }

        @Override
        public void onBindViewHolder(Helper holder, int position) {
            Horoscope horoscope = mHoroscopes[position % mHoroscopes.length];
            holder.mContent.setText(horoscope.name);
            holder.mIcon.setImageResource(horoscope.icon);
            holder.mBg.setImageResource(horoscope.bg);
        }

        @Override
        public int getItemCount() {
            return 12;
        }

    }

    private static final class Helper extends RecyclerView.ViewHolder {
        TextView mContent;
        ImageView mIcon;
        ImageView mBg;

        Helper(View itemView) {
            super(itemView);
            mContent = (TextView) itemView.findViewById(R.id.tv_content);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            mBg = (ImageView) itemView.findViewById(R.id.iv_bg);
        }
    }

    private static class Horoscope {
        String name;
        int icon;
        int bg;

        Horoscope(String name, int icon, int bg) {
            this.name = name;
            this.icon = icon;
            this.bg = bg;
        }
    }

    private static Horoscope[] mHoroscopes = {
            new Horoscope("白羊座", R.drawable.btn_aries, R.drawable.ic_aries),
            new Horoscope("金牛座", R.drawable.btn_taurus, R.drawable.ic_taurus),
            new Horoscope("双子座", R.drawable.btn_gemini, R.drawable.ic_gemini),
            new Horoscope("巨蟹座", R.drawable.btn_cancer, R.drawable.ic_cancer),
            new Horoscope("狮子座", R.drawable.btn_leo, R.drawable.ic_leo),
            new Horoscope("处女座", R.drawable.btn_virgo, R.drawable.ic_virgo),
            new Horoscope("天秤座", R.drawable.btn_libra, R.drawable.ic_libra),
            new Horoscope("天蝎座", R.drawable.btn_scorpio, R.drawable.ic_scorpio),
            new Horoscope("射手座", R.drawable.btn_sagittarius, R.drawable.ic_sagittarius),
            new Horoscope("摩羯座", R.drawable.btn_capricorn, R.drawable.ic_capricorn),
            new Horoscope("水瓶座", R.drawable.btn_aquarius, R.drawable.ic_aquarius),
            new Horoscope("双鱼座", R.drawable.btn_pisces, R.drawable.ic_pisces)
    };
}
