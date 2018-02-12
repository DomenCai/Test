package com.domencai.one.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.domencai.one.R;
import com.domencai.one.api.BookServer;
import com.domencai.one.bean.ChapterBean;
import com.domencai.one.utils.BookManager;
import com.domencai.one.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Domen、on 2017/11/27.
 */

public class BookLayout extends ViewPager {
    private float mTouchX;
    private float mTouchY;
    private boolean mClick;
    private int mTouchSlop;
    private ChapterAdapter mChapterAdapter;
    private OnClickCallback mClickCallback;

    private OnPageChangeListener mOnPageChangeListener = new SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mChapterAdapter.setIndex(position);
        }
    };

    public BookLayout(Context context) {
        this(context, null);
    }

    public BookLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mChapterAdapter = new ChapterAdapter();
        setAdapter(mChapterAdapter);
        addOnPageChangeListener(mOnPageChangeListener);
    }

    public void setChapters(List<ChapterBean> chapters, int chapter) {
        mChapterAdapter.setChapters(chapters, chapter);
        setCurrentItem(chapter, false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mClickCallback == null) {
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = ev.getX();
                mTouchY = ev.getY();
                mClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - mTouchX) > mTouchSlop
                        || Math.abs(ev.getY() - mTouchY) > mTouchSlop) {
                    mClick = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mClick) {
                    if (mTouchY > getHeight() / 4 && mTouchY < getHeight() * 3 / 4) {
                        if (mTouchX > getWidth() * 2 / 3) {
                            mClickCallback.onRightClick();
                        } else if (mTouchX > getWidth() / 3) {
                            mClickCallback.onMiddleClick();
                        } else {
                            mClickCallback.onLeftClick();
                        }
                    } else if (mTouchX > getWidth() / 2) {
                        mClickCallback.onRightClick();
                    } else {
                        mClickCallback.onLeftClick();
                    }
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        removeOnPageChangeListener(mOnPageChangeListener);
        super.onDetachedFromWindow();
    }

    public void saveReadingProgress() {
        int chapter = getCurrentItem();
        ChapterLayout chapterLayout = mChapterAdapter.getView(chapter);
        int page = chapterLayout == null ? 0 : chapterLayout.getCurrentItem();
        BookManager.getInstance().saveBookInfo(chapter, page);
    }

    public void refresh(boolean layoutText) {
        mChapterAdapter.refresh(layoutText);
    }

    public void toLeft() {
        ChapterLayout chapterLayout = mChapterAdapter.getView(getCurrentItem());
        if (!chapterLayout.toLeft()) {
            int goal = Math.max(0, getCurrentItem() - 1);
            setCurrentItem(goal, false);
        }
    }

    public void toRight() {
        ChapterLayout chapterLayout = mChapterAdapter.getView(getCurrentItem());
        if (!chapterLayout.toRight()) {
            int goal = Math.min(mChapterAdapter.getCount() - 1, getCurrentItem() + 1);
            setCurrentItem(goal, false);
        }
    }

    public void setCurrentChapter(int position) {
        setCurrentItem(position, false);
        ChapterLayout chapterLayout = mChapterAdapter.getView(position);
        if (chapterLayout != null) {
            chapterLayout.setCurrentItem(0, false);
        }
    }

    public void setClickCallback(OnClickCallback clickCallback) {
        mClickCallback = clickCallback;
    }

    public interface OnClickCallback {
        void onLeftClick();

        void onMiddleClick();

        void onRightClick();
    }

    private static class ChapterAdapter extends PagerAdapter {
        private int mIndex;
        private int mChapter;
        private List<ChapterBean> mChapterBeans;
        private SparseArray<ChapterLayout> mLayoutSparseArray;

        ChapterAdapter() {
            mChapterBeans = new ArrayList<>();
            mLayoutSparseArray = new SparseArray<>();
        }

        @Override
        public int getCount() {
            return mChapterBeans.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_chapter, container, false);
            TextView tvTitle = view.findViewById(R.id.tv_title);
            tvTitle.setText(mChapterBeans.get(position).mTitle);
            final ChapterLayout chapterLayout = view.findViewById(R.id.chapter_layout);
            BookServer.getInstance().getContentByChapter(position + 1, mChapterBeans.get(position).mLink)
                    .subscribe(new RxUtils.SimpleSubscriber<String>() {
                        @Override
                        public void onNext(String s) {
                            chapterLayout.setText(s, position < mIndex);
                            if (position == mChapter) {
                                chapterLayout.setCurrentItem(BookManager.getInstance().getPage(), false);
                            }
                        }
                    });
            mLayoutSparseArray.put(position, chapterLayout);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            mLayoutSparseArray.remove(position);
        }

        void setChapters(List<ChapterBean> chapterBeans, int chapter) {
            mChapter = chapter;
            mChapterBeans.clear();
            mChapterBeans.addAll(chapterBeans);
            notifyDataSetChanged();
        }

        ChapterLayout getView(int position) {
            return mLayoutSparseArray.get(position);
        }

        void setIndex(int index) {
            mIndex = index;
            for (int i = 0; i < mLayoutSparseArray.size(); i++) {
                ChapterLayout chapterLayout = mLayoutSparseArray.valueAt(i);
                if (chapterLayout == null || chapterLayout.getCount() == 0) {
                    continue;
                }
                int position = mLayoutSparseArray.keyAt(i);
                if (position < mIndex) {
                    chapterLayout.setCurrentItem(chapterLayout.getCount() - 1, false);
                } else if (position > mIndex) {
                    chapterLayout.setCurrentItem(0, false);
                }
            }
        }

        void refresh(boolean layoutText) {
            for (int i = 0; i < mLayoutSparseArray.size(); i++) {
                ChapterLayout chapterLayout = mLayoutSparseArray.valueAt(i);
                if (chapterLayout == null) {
                    continue;
                }
                if (layoutText) {
                    int position = mLayoutSparseArray.keyAt(i);
                    chapterLayout.resetTextSize(position < mIndex, position == mIndex);
                } else {
                    chapterLayout.refreshTextColor();
                }
            }
        }
    }
}
