package com.domencai.one;


import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Domen、on 2017/10/10.
 */

public interface OneApi {
    @GET
    Observable<OneBean> getOneHome(@Url String url);
}
