package com.domencai.one.video;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Domen、on 2018/2/12.
 */

public interface VideoApi {
    @GET("weibo_list.php?apiver=20100&category=weibo_videos&page=0&page_size=30&max_timestamp=-1&latest_viewed_ts=-1&platform=aphone&sysver=7.0&appver=1.7.3&buildver=1.7.3&app_ver=10703&udid=a_96dc4fa20c720385&channel=yingyongbao")
    Observable<VideoResponed> getVideo();
}
