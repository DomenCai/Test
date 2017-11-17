package com.domencai.one.api;

import com.domencai.one.RxUtils;
import com.domencai.one.bean.BookBean;
import com.domencai.one.bean.BookDetail;
import com.domencai.one.bean.BookMixAToc;
import com.domencai.one.bean.BookRecommend;
import com.domencai.one.bean.ChapterRead;
import com.domencai.one.bean.Recommend;
import com.domencai.one.bean.RecommendBookList;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func3;

/**
 * Created by Domen、on 2017/11/13.
 */

public class BookServer {

    private static final String API_BASE_URL = "http://api.zhuishushenqi.com";
    public static final String IMG_URL = "http://statics.zhuishushenqi.com";

    private static BookServer instance;

    private BookApi service;

    private BookServer() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .addNetworkInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 添加Rx适配器
                .addConverterFactory(GsonConverterFactory.create()) // 添加Gson转换器
                .client(okHttpClient)
                .build();
        service = retrofit.create(BookApi.class);
    }

    public static BookServer getInstance() {
        if (instance == null)
            instance = new BookServer();
        return instance;
    }

    public Observable<List<Recommend.RecommendBooks>> getRecommend(String gender) {
        return service.getRecommend(gender).map(new Func1<Recommend, List<Recommend.RecommendBooks>>() {
            @Override
            public List<Recommend.RecommendBooks> call(Recommend recommend) {
                return recommend.books;
            }
        }).compose(RxUtils.<List<Recommend.RecommendBooks>>getSchedulerTransformer());
    }

    public Observable<BookMixAToc> getBookMixAToc(String bookId) {
        return service.getBookMixAToc(bookId);
    }

    public Observable<ChapterRead> getChapterRead(String url) {
        return service.getChapterRead(url);
    }

    public Observable<BookBean> getBook(String bookId) {
        return Observable.zip(service.getBookDetail(bookId), service.getRecommendBook(bookId), service.getRecommendBookList(bookId),
                new Func3<BookDetail, BookRecommend, RecommendBookList, BookBean>() {
                    @Override
                    public BookBean call(BookDetail bookDetail, BookRecommend bookRecommend, RecommendBookList recommendBookList) {
                        BookBean bookBean = new BookBean();
                        bookBean.mBookDetail = bookDetail;
                        bookBean.mBooksList = bookRecommend.books;
                        bookBean.mRecommendBooks = recommendBookList.booklists;
                        return bookBean;
                    }
                }).compose(RxUtils.<BookBean>getSchedulerTransformer());
    }
}
