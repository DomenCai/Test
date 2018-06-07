package com.domencai.one.video;

import com.domencai.one.utils.RxUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Domen、on 2017/11/13.
 */

public class VideoServer {

    private static final String API_BASE_URL = "http://fuli1024.com/weibofun/";

    private static VideoServer instance;

    private VideoApi service;

    private VideoServer() {
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
        service = retrofit.create(VideoApi.class);
    }

    public static VideoServer getInstance() {
        if (instance == null)
            instance = new VideoServer();
        return instance;
    }

    public Observable<List<VideoBean>> getVideo() {
        return service.getVideo().map(new Func1<VideoResponed, List<VideoBean>>() {
            @Override
            public List<VideoBean> call(VideoResponed videoResponed) {
                return videoResponed.mVideoBeans;
            }
        }).compose(RxUtils.<List<VideoBean>>getSchedulerTransformer());
    }
}
