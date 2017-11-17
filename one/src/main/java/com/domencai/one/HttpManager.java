package com.domencai.one;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by Domen、on 2017/10/10.
 */

public class HttpManager {
    private OneApi mOneApi;

    private HttpManager() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .addNetworkInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://v3.wufazhuce.com:8000/api/hp/more/")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mOneApi = retrofit.create(OneApi.class);
    }

    public static HttpManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * singleton
     */
    private static class SingletonHolder {
        private static final HttpManager INSTANCE = new HttpManager();
    }

    public Observable<OneBean> getOneList() {
        return mOneApi.getOneHome("http://v3.wufazhuce.com:8000/api/hp/more/0?version=v3.5.3");
    }
}
