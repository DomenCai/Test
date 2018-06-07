package com.domencai.one;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.domencai.one.api.BookServer;
import com.domencai.one.bean.ChapterBean;
import com.domencai.one.common.BaseAdapter;
import com.domencai.one.common.BaseViewHolder;
import com.domencai.one.common.ItemClickSupport;
import com.domencai.one.common.ItemClickSupport.OnItemClickListener;
import com.domencai.one.utils.BookManager;
import com.domencai.one.utils.DrawUtils;
import com.domencai.one.utils.RxUtils;
import com.domencai.one.utils.ScreenUtils;
import com.domencai.one.widget.BookLayout;
import com.domencai.one.widget.BookLayout.OnClickCallback;

import java.util.List;

/**
 * Created by Domen、on 2017/11/17.
 */

public class ReadActivity extends AppCompatActivity implements OnClickListener, OnClickCallback,
        OnItemClickListener, OnSeekBarChangeListener, OnDismissListener, OnCheckedChangeListener {
//    private static final String KEY_BOOK_NAME = "BOOK_NAME";
    private static final String KEY_BOOK_ID = "BOOK_ID";

    private boolean mShowTitle;
    private TextView mTvTitle;
    private TextView mTvProgress;
    private BookLayout mBookLayout;
    private LinearLayout mTitleLayout;
    private BottomSheetDialog mChaptersDialog;
    private LinearLayoutManager mChaptersLayoutManager;
    private BottomSheetDialog mMenuDialog;
    private SeekBar mSbProgress;
    private SeekBar mSbFont;
    private SeekBar mSbBrightness;

    private BaseAdapter<ChapterBean> mChaptersAdapter = new BaseAdapter<ChapterBean>(R.layout.item_chapter) {
        @Override
        public void bind(BaseViewHolder viewHolder, ChapterBean data, int position) {
            boolean isCurrent = mBookLayout.getCurrentItem() == viewHolder.getLayoutPosition();
            viewHolder.setText(R.id.tv_chapter, data.mTitle)
                    .setTextColor(R.id.tv_chapter, isCurrent ? 0xff303F9F : 0xff757575);
        }
    };

    public static void start(Context context, String bookId) {
        context.startActivity(new Intent(context, ReadActivity.class).putExtra(KEY_BOOK_ID, bookId));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setBackgroundDrawable(null);
        mTitleLayout = findViewById(R.id.title_layout);
        mTvTitle = findViewById(R.id.tv_title);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_chapter).setOnClickListener(this);
        findViewById(R.id.btn_more).setOnClickListener(this);
        mBookLayout = findViewById(R.id.book_layout);
        mBookLayout.setBackgroundColor(BookManager.getInstance().getBackgroundColor());
        mBookLayout.setClickCallback(this);
        BookServer.getInstance().getBookChapters()
                .subscribe(new RxUtils.SimpleSubscriber<List<ChapterBean>>() {
                    @Override
                    public void onNext(List<ChapterBean> chapterBeans) {
                        int chapter = BookManager.getInstance().getChapter();
                        mTvTitle.setText(chapterBeans.get(chapter).mTitle);
                        mChaptersAdapter.setData(chapterBeans);
                        mBookLayout.setChapters(chapterBeans, chapter);
                    }
                });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !mShowTitle) {
            mTitleLayout.setTranslationY(-DrawUtils.dp2px(72));
            mBookLayout.setSystemUiVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBookLayout.saveReadingProgress();
    }

    @Override
    public void onBackPressed() {
        if (!hideTitle()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                && BookManager.getInstance().isVolumeFlipEnable() && !mShowTitle
                || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (BookManager.getInstance().isVolumeFlipEnable() && !mShowTitle) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                mBookLayout.toRight();
                return true;// 防止翻页有声音
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                mBookLayout.toLeft();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                moveTaskToBack(false);
                break;
            case R.id.btn_chapter:
                if (mChaptersDialog == null) {
                    initChaptersDialog();
                }
                int mCurrentPosition = mBookLayout.getCurrentItem();
                mChaptersLayoutManager.scrollToPositionWithOffset(mCurrentPosition, 0);
                mChaptersAdapter.notifyItemChanged(mCurrentPosition);
                hideTitle();
                mChaptersDialog.show();
                break;
            case R.id.btn_more:
                if (mMenuDialog == null) {
                    initMenuDialog();
                }
                hideTitle();
                mSbProgress.setProgress(mBookLayout.getCurrentItem());
                mMenuDialog.show();
                break;
            case R.id.iv_last_chapter:
                setSeekBarProgress(mSbProgress, -1);
                mBookLayout.setCurrentChapter(mSbProgress.getProgress());
                break;
            case R.id.iv_next_chapter:
                setSeekBarProgress(mSbProgress, 1);
                mBookLayout.setCurrentChapter(mSbProgress.getProgress());
                break;
            case R.id.iv_smaller:
                setSeekBarProgress(mSbFont, -1);
                break;
            case R.id.iv_largen:
                setSeekBarProgress(mSbFont, 1);
                break;
            case R.id.iv_darken:
                setSeekBarProgress(mSbBrightness, -1);
                break;
            case R.id.iv_brighten:
                setSeekBarProgress(mSbBrightness, 1);
                break;
            case R.id.color_white:
                mBookLayout.setBackgroundResource(R.color.background_white);
                BookManager.getInstance().saveColor(R.color.background_white, Color.BLACK);
                mBookLayout.refresh(false);
                break;
            case R.id.color_yellow:
                mBookLayout.setBackgroundResource(R.color.background_yellow);
                BookManager.getInstance().saveColor(R.color.background_yellow, Color.BLACK);
                mBookLayout.refresh(false);
                break;
            case R.id.color_green:
                mBookLayout.setBackgroundResource(R.color.background_green);
                BookManager.getInstance().saveColor(R.color.background_green, Color.BLACK);
                mBookLayout.refresh(false);
                break;
            case R.id.color_black:
                mBookLayout.setBackgroundResource(R.color.background_black);
                BookManager.getInstance().saveColor(R.color.background_black, Color.GRAY);
                mBookLayout.refresh(false);
                break;
        }
    }

    @Override
    public void onLeftClick() {
        if (!hideTitle()) {
            mBookLayout.toLeft();
        }
    }

    @Override
    public void onMiddleClick() {
        if (!hideTitle()) {
            showTitle();
        }
    }

    @Override
    public void onRightClick() {
        if (!hideTitle()) {
            mBookLayout.toRight();
        }
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View view, int position) {
        mBookLayout.setCurrentChapter(position);
        mChaptersDialog.dismiss();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_progress:
                String progressText = (progress + 1) + "/" + mChaptersAdapter.getItemCount();
                mTvProgress.setText(progressText);
                break;
            case R.id.sb_font:
                BookManager.getInstance().saveTextSize(progress + 13);
                mBookLayout.refresh(true);
                break;
            case R.id.sb_brightness:
                ScreenUtils.setBrightness(this, progress / 20f);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.sb_progress) {
            mTvProgress.setVisibility(View.VISIBLE);
            String progressText = (seekBar.getProgress() + 1) + "/" + mChaptersAdapter.getItemCount();
            mTvProgress.setText(progressText);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.sb_progress) {
            mTvProgress.setVisibility(View.GONE);
            mBookLayout.setCurrentChapter(seekBar.getProgress());
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mBookLayout.setSystemUiVisibility(View.INVISIBLE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        BookManager.getInstance().saveVolumeFlipEnable(isChecked);
    }

    private void showTitle() {
        mTvTitle.setText(mChaptersAdapter.getItemData(mBookLayout.getCurrentItem()).mTitle);
        mBookLayout.setSystemUiVisibility(View.VISIBLE);
        mTitleLayout.animate().translationY(0).start();
        mShowTitle = true;
    }

    private boolean hideTitle() {
        if (mShowTitle) {
            mBookLayout.setSystemUiVisibility(View.INVISIBLE);
            mTitleLayout.animate().translationY(-DrawUtils.dp2px(72)).start();
            mShowTitle = false;
            return true;
        }
        return false;
    }

    private void initChaptersDialog() {
        mChaptersDialog = new BottomSheetDialog(this);
        mChaptersDialog.setOnDismissListener(this);
        RecyclerView chaptersList = new RecyclerView(this);
        mChaptersLayoutManager = new LinearLayoutManager(this);
        chaptersList.setLayoutManager(mChaptersLayoutManager);
        chaptersList.setAdapter(mChaptersAdapter);
        new ItemClickSupport(chaptersList, mChaptersAdapter).setOnItemClickListener(this);
        mChaptersDialog.setContentView(chaptersList);
    }

    private void initMenuDialog() {
        mMenuDialog = new BottomSheetDialog(this);
        mMenuDialog.setContentView(R.layout.dialog_menu);
        mMenuDialog.setOnDismissListener(this);
        View bottomMenu = mMenuDialog.findViewById(R.id.menu_layout);
        assert bottomMenu != null;
        View parent = (View) bottomMenu.getParent();
        parent.setBackgroundColor(Color.TRANSPARENT);
        mTvProgress = bottomMenu.findViewById(R.id.tv_progress);
        CheckBox checkBox = bottomMenu.findViewById(R.id.cb_volume_flip);
        checkBox.setChecked(BookManager.getInstance().isVolumeFlipEnable());
        checkBox.setOnCheckedChangeListener(this);
        bottomMenu.findViewById(R.id.iv_last_chapter).setOnClickListener(this);
        bottomMenu.findViewById(R.id.iv_next_chapter).setOnClickListener(this);
        bottomMenu.findViewById(R.id.iv_smaller).setOnClickListener(this);
        bottomMenu.findViewById(R.id.iv_largen).setOnClickListener(this);
        bottomMenu.findViewById(R.id.iv_darken).setOnClickListener(this);
        bottomMenu.findViewById(R.id.iv_brighten).setOnClickListener(this);
        bottomMenu.findViewById(R.id.color_white).setOnClickListener(this);
        bottomMenu.findViewById(R.id.color_yellow).setOnClickListener(this);
        bottomMenu.findViewById(R.id.color_green).setOnClickListener(this);
        bottomMenu.findViewById(R.id.color_black).setOnClickListener(this);
        initSeekBar(bottomMenu);
    }

    private void initSeekBar(View container) {
        mSbProgress = container.findViewById(R.id.sb_progress);
        mSbProgress.setMax(mChaptersAdapter.getItemCount());
        mSbProgress.setProgress(mBookLayout.getCurrentItem());
        mSbProgress.setOnSeekBarChangeListener(this);

        mSbFont = container.findViewById(R.id.sb_font);
        mSbFont.setProgress(BookManager.getInstance().getTextSizeOnSeekBar());
        mSbFont.setOnSeekBarChangeListener(this);

        mSbBrightness = container.findViewById(R.id.sb_brightness);
        mSbBrightness.setProgress(ScreenUtils.getSystemBrightness());
        mSbBrightness.setOnSeekBarChangeListener(this);
    }

    private void setSeekBarProgress(SeekBar seekBar, int changeValue) {
        int max = seekBar.getMax();
        int progress = seekBar.getProgress();
        seekBar.setProgress(Math.max(0, Math.min(max, progress + changeValue)));
    }
}
