package com.domencai.one.video;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Domen、on 2018/2/12.
 */

public class VideoResponed {
    @SerializedName("items")
    public List<VideoBean> mVideoBeans;
}
