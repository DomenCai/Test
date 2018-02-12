package com.domencai.one.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.domencai.one.utils.BookManager;
import com.domencai.one.utils.DrawUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Domen、on 2017/11/22.
 */

public class ChapterLayout extends ViewPager {

    private static final String DOUBLE_SPACE = "\u3000\u3000";
    private static int sScreenWidth;
    private static int sScreenHeight;

    private String mBody;
    private List<List<String>> mPages = new ArrayList<>();

    public ChapterLayout(Context context) {
        this(context, null);
    }

    public ChapterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        sScreenWidth = DrawUtils.getScreenWidth(context);
        sScreenHeight = DrawUtils.getScreenHeight(context);
    }

    public void setText(String text, boolean toFinal) {
        mBody = DOUBLE_SPACE + text.replaceAll("\n", "\n" + DOUBLE_SPACE);
        layoutText();
        if (toFinal) {
            setCurrentItem(mPages.size() - 1, false);
        }
    }

    public void resetTextSize(boolean toFinal, boolean isCurrent) {
        if (isCurrent) {
            int current = getCurrentItem();
            int size = mPages.size();
            layoutText();
            setCurrentItem(current * mPages.size() / size, false);
        } else {
            layoutText();
            if (toFinal) {
                setCurrentItem(mPages.size() - 1, false);
            }
        }
    }

    private void layoutText() {
        int textSize = BookManager.getInstance().getTextSize();
        int lineSpace = textSize / 2;
        int padding = DrawUtils.dp2px(16);
        int height = sScreenHeight - padding * 3;
        int tempHeight = 0;

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        mPages.clear();
        List<String> pageLines = new ArrayList<>();
        StaticLayout layout = new StaticLayout(mBody, textPaint, sScreenWidth - padding * 2, Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
        for (int i = 0; i < layout.getLineCount(); i++) {
            int start = layout.getLineStart(i);
            int end = layout.getLineEnd(i);
            String line = mBody.substring(start, end);

            if (line.startsWith(DOUBLE_SPACE) && tempHeight > 0) {
                tempHeight += lineSpace;
            }
            tempHeight += textSize;
            if (tempHeight > height) {
                mPages.add(pageLines);
                pageLines = new ArrayList<>();
                i--;
                tempHeight = 0;
            } else {
                pageLines.add(line);
                tempHeight += lineSpace;
            }
        }
        mPages.add(pageLines);
        setAdapter(new BookPageAdapter(mPages));
    }

    public void refreshTextColor() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).invalidate();
        }
    }

    boolean toLeft() {
        if (getCurrentItem() == 0) {
            return false;
        }
        setCurrentItem(getCurrentItem() - 1, false);
        return true;
    }

    boolean toRight() {
        if (getCurrentItem() == mPages.size() - 1) {
            return false;
        }
        setCurrentItem(getCurrentItem() + 1, false);
        return true;
    }

    int getCount() {
        return mPages.size();
    }

    private static class BookPageAdapter extends PagerAdapter {
        private List<List<String>> mPages;

        BookPageAdapter(List<List<String>> list) {
            this.mPages = list;
        }

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PageView pageView = new PageView(container.getContext());
            pageView.setLines(mPages.get(position), position + 1, mPages.size());
            container.addView(pageView);
            return pageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
