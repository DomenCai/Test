package com.domencai.one.progress;

import com.bumptech.glide.load.engine.GlideException;

/**
 * Created by Domen、on 2018/2/9.
 */

public interface OnProgressListener {
    void onProgress(String imageUrl, long bytesRead, long totalBytes, boolean isDone,
            GlideException exception);
}
