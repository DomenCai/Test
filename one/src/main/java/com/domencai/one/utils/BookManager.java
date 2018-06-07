package com.domencai.one.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.domencai.one.App;
import com.domencai.one.bean.Recommend.RecommendBooks;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Domen、on 2017/11/29.
 */

public class BookManager {
    private static final String DEFAULT_BOOK = "58f10c8c50c1a53b441be95c";
    private static final String KEY_VOLUME_FLIP = "volume_flip_enable";
    private static final String KEY_BOOK_INFO = "book_info";
    private static final String KEY_BOOK_ID = "book_id";
    private static final String KEY_TEXT_COLOR = "text_color";
    private static final String KEY_TEXT_SIZE = "text_size";
    private static final String KEY_BACKGROUND_COLOR = "background_color";
    private static final String KEY_CHAPTER_SUFFIX = "_chapter";
    private static final String KEY_PAGE_SUFFIX = "_page";

    private String mBookId;
    private boolean mVolumeFlipEnable;
    private int mTextSize;
    private SharedPreferences mSharedPreferences;

    private BookManager() {
        mSharedPreferences = App.getAppContext().getSharedPreferences(KEY_BOOK_INFO, Context.MODE_PRIVATE);
        mBookId = mSharedPreferences.getString(KEY_BOOK_ID, DEFAULT_BOOK);
        mVolumeFlipEnable = mSharedPreferences.getBoolean(KEY_VOLUME_FLIP, false);
        mTextSize = mSharedPreferences.getInt(KEY_TEXT_SIZE, DrawUtils.sp2px(17));
    }

    public void saveBookId(String bookId) {
        mBookId = bookId;
        mSharedPreferences.edit().putString(KEY_BOOK_ID, bookId).apply();
    }

    public String getBookId() {
        return mBookId;
    }

    public void saveVolumeFlipEnable(boolean enable) {
        mVolumeFlipEnable = enable;
        mSharedPreferences.edit().putBoolean(KEY_VOLUME_FLIP, enable).apply();
    }

    public boolean isVolumeFlipEnable() {
        return mVolumeFlipEnable;
    }

    public void saveTextSize(int textSize) {
        mTextSize = DrawUtils.sp2px(textSize);
        mSharedPreferences.edit().putInt(KEY_TEXT_SIZE, mTextSize).apply();
    }

    public int getTextSize() {
        return mTextSize;
    }

    public int getTextSizeOnSeekBar() {
        return (int) (DrawUtils.px2sp(mTextSize) - 13);
    }

    public void saveColor(int bgRes, int textColor) {
        int bgColor = ContextCompat.getColor(App.getAppContext(), bgRes);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(KEY_TEXT_COLOR, textColor);
        editor.putInt(KEY_BACKGROUND_COLOR, bgColor);
        editor.apply();
    }

    public int getTextColor() {
        return mSharedPreferences.getInt(KEY_TEXT_COLOR, 0xff000000);
    }

    public int getBackgroundColor() {
        return mSharedPreferences.getInt(KEY_BACKGROUND_COLOR, 0xffdbdbdd);
    }

    public void saveBookInfo(int chapter, int page) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(mBookId + KEY_CHAPTER_SUFFIX, chapter);
        editor.putInt(mBookId + KEY_PAGE_SUFFIX, page);
        editor.apply();
    }

    public int getChapter() {
        return mSharedPreferences.getInt(mBookId + KEY_CHAPTER_SUFFIX, 0);
    }

    public int getPage() {
        return mSharedPreferences.getInt(mBookId + KEY_PAGE_SUFFIX, 0);
    }

    public void saveRecommend(String id, String content) {
        Set<String> ids = mSharedPreferences.getStringSet("recommend", null);
        if (ids == null) {
            ids = new HashSet<>();
        }
        ids.add(id);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putStringSet("recommend", ids);
        editor.putString(id, content);
        editor.apply();
    }

    public List<RecommendBooks> getRecommends() {
        List<RecommendBooks> books = new ArrayList<>();
        Set<String> ids = mSharedPreferences.getStringSet("recommend", null);
        if (ids != null && !ids.isEmpty()) {
            Gson gson = new Gson();
            for (String id : ids) {
                String detail = mSharedPreferences.getString(id, null);
                if (!TextUtils.isEmpty(detail)) {
                    RecommendBooks book = gson.fromJson(detail, RecommendBooks.class);
                    books.add(book);
                }
            }
        }
        return books;
    }

    public static BookManager getInstance() {
        return SingleHolder.INSTANCE;
    }
    
    private static class SingleHolder {
        private static final BookManager INSTANCE = new BookManager();
    }
}
