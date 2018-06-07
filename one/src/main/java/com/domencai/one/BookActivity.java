package com.domencai.one;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.domencai.one.api.BookServer;
import com.domencai.one.bean.BookBean;
import com.domencai.one.bean.BookDetail;
import com.domencai.one.bean.Recommend;
import com.domencai.one.bean.RecommendBookList.RecommendBook;
import com.domencai.one.common.BaseAdapter;
import com.domencai.one.common.BaseViewHolder;
import com.domencai.one.common.ItemClickSupport;
import com.domencai.one.parallax.ParallaxBack;
import com.domencai.one.utils.BookManager;
import com.domencai.one.utils.FormatUtil;
import com.domencai.one.utils.RxUtils;
import com.google.gson.Gson;

import java.util.Locale;

import static com.domencai.one.bean.BookRecommend.BooksBean;

/**
 * Created by Domen、on 2017/11/13.
 */
@ParallaxBack
public class BookActivity extends AppCompatActivity implements View.OnClickListener, ItemClickSupport.OnItemClickListener {
    private static final String KEY_BOOK_ID = "BOOK_ID";

    private ImageView mIvCover;
    private TextView mTvTitle;
    private TextView mTvAuthor;
    private TextView mTvTagWord;
    private RatingBar mRatingBar;
    private TextView mTvRatingBarDes;
    private TextView mTvUpdate;
    private TextView mTvFollowCount;
    private TextView mTvRetention;
    private TextView mTvDailyWord;
    private TextView mChaptersCount;
    private TextView mTvIntroduction;
    private BookDetail mBookDetail;

    private BaseAdapter<BooksBean> mBooksAdapter = new BaseAdapter<BooksBean>(R.layout.item_recommend_book) {
        @Override
        public void bind(BaseViewHolder viewHolder, BooksBean data, int position) {
            viewHolder.setText(R.id.tv_title, data.title)
                    .setImage(R.id.iv_cover, BookServer.IMG_URL + data.cover);
        }
    };

    private BaseAdapter<RecommendBook> mBookListAdapter = new BaseAdapter<RecommendBook>(R.layout.item_recommend_book_list) {
        @Override
        public void bind(BaseViewHolder viewHolder, RecommendBook data, int position) {
            viewHolder.setText(R.id.tv_title, data.title)
                    .setText(R.id.tv_author, data.author)
                    .setText(R.id.tv_summary, data.desc)
                    .setText(R.id.tv_des, "共" + data.bookCount + "本书 | " + data.collectorCount + "次收藏")
                    .setImage(R.id.iv_cover, BookServer.IMG_URL + data.cover);
        }
    };

    public static void start(Context context, String bookId) {
        context.startActivity(new Intent(context, BookActivity.class)
                .putExtra(KEY_BOOK_ID, bookId));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        RecyclerView rvRecommendBook = findViewById(R.id.rv_recommend_book);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvRecommendBook.setLayoutManager(layoutManager);
        rvRecommendBook.setAdapter(mBooksAdapter);
        new ItemClickSupport(rvRecommendBook, mBooksAdapter).setOnItemClickListener(this);
        RecyclerView rvRecommendBookList = findViewById(R.id.rv_recommend_book_list);
        rvRecommendBookList.setNestedScrollingEnabled(false);
        rvRecommendBookList.setAdapter(mBookListAdapter);
//        new ItemClickSupport(rvRecommendBookList, mBookListAdapter).setOnItemClickListener(this);
        initView();
        initData();
    }

    private void initView() {
        mIvCover = findViewById(R.id.iv_cover);
        mTvTitle = findViewById(R.id.tv_title);
        mTvAuthor = findViewById(R.id.tv_author);
        mTvTagWord = findViewById(R.id.tv_des);
        mRatingBar = findViewById(R.id.rating_bar);
        mTvRatingBarDes = findViewById(R.id.tv_rating_msg);
        mTvUpdate = findViewById(R.id.tv_update);
        mTvFollowCount = findViewById(R.id.tv_follower);
        mTvRetention = findViewById(R.id.tv_retention);
        mTvDailyWord = findViewById(R.id.tv_daily_word);
        mChaptersCount = findViewById(R.id.tv_chapters);
        mTvIntroduction = findViewById(R.id.tv_introduction);
        findViewById(R.id.tv_follow).setOnClickListener(this);
        findViewById(R.id.tv_read).setOnClickListener(this);
    }

    private void initData() {
        BookServer.getInstance().getBook(getIntent().getStringExtra(KEY_BOOK_ID))
                .subscribe(new RxUtils.SimpleSubscriber<BookBean>() {
                    @Override
                    public void onNext(BookBean bookBean) {
                        mBooksAdapter.setData(bookBean.mBooksList);
                        mBookListAdapter.setData(bookBean.mRecommendBooks);
                        mBookDetail = bookBean.mBookDetail;
                        BookDetail.RatingBean ratingBean = mBookDetail.rating;
                        String tagWord = " | " + mBookDetail.cat + " | " + FormatUtil.formatWordCount(mBookDetail.wordCount);
                        String ratingDes = String .format(Locale.getDefault(), "%.2f分  %d人评", ratingBean.score, ratingBean.count);
                        String retention = mBookDetail.retentionRatio + "%";
                        mTvTitle.setText(mBookDetail.title);
                        mTvAuthor.setText(mBookDetail.author);
                        mTvTagWord.setText(tagWord);
                        mTvRatingBarDes.setText(ratingDes);
                        mTvUpdate.setText(FormatUtil.getDescriptionTimeFromDateString(mBookDetail.updated));
                        mTvFollowCount.setText(String.valueOf(mBookDetail.latelyFollower));
                        mTvRetention.setText(retention);
                        mTvDailyWord.setText(String.valueOf(mBookDetail.serializeWordCount));
                        mChaptersCount.setText(String.valueOf(mBookDetail.serializeWordCount));
                        mTvIntroduction.setText(mBookDetail.longIntro);
                        mRatingBar.setRating((float) (ratingBean.score / 2));
                        Glide.with(BookActivity.this).load(BookServer.IMG_URL + mBookDetail.cover).into(mIvCover);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_follow:
                Recommend.RecommendBooks books = new Recommend.RecommendBooks();
                books._id = mBookDetail._id;
                books.title = mBookDetail.title;
                books.author = mBookDetail.author;
                books.shortIntro = mBookDetail.longIntro;
                books.latelyFollower = mBookDetail.latelyFollower;
                books.retentionRatio = Double.parseDouble(mBookDetail.retentionRatio);
                books.cover = mBookDetail.cover;
                BookManager.getInstance().saveRecommend(books._id, new Gson().toJson(books));
                Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_read:
                String bookId = getIntent().getStringExtra(KEY_BOOK_ID);
                BookManager.getInstance().saveBookId(bookId);
                ReadActivity.start(BookActivity.this, bookId);
                break;
        }
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View view, int position) {
        switch (recyclerView.getId()) {
            case R.id.rv_recommend_book:
                BookActivity.start(this, mBooksAdapter.getItemData(position)._id);
                break;
            case R.id.rv_recommend_book_list:
                BookActivity.start(this, mBookListAdapter.getItemData(position).id);
                break;
        }
    }
}
