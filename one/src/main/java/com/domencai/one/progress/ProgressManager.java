package com.domencai.one.progress;


import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.GlideException;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Domen、on 2018/2/9.
 */

public class ProgressManager {
    private final Map<String, List<OnProgressListener>> mListeners;
    private OkHttpClient okHttpClient;

    private ProgressManager() {
        mListeners = new HashMap<>();
        okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request();
                        Response response = chain.proceed(request);
                        return response.newBuilder()
                                .body(new ProgressResponseBody(request.url().toString(), response.body(), LISTENER))
                                .build();
                    }
                }).build();
    }

    public static ProgressManager getInstance() {
        return SingleHolder.INSTANCE;
    }

    OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    private final OnProgressListener LISTENER = new OnProgressListener() {
        @Override
        public void onProgress(String imageUrl, long bytesRead, long totalBytes, boolean isDone, GlideException exception) {
            List<OnProgressListener> listeners = mListeners.get(imageUrl);
            if (listeners != null) {
                for (OnProgressListener listener : listeners) {
                    listener.onProgress(imageUrl, bytesRead, totalBytes, isDone, exception);
                }
                if (isDone) {
                    mListeners.remove(imageUrl);
                }
            }
        }
    };

    public synchronized void addListener(@NonNull String url, @NonNull OnProgressListener listener) {
        List<OnProgressListener> progressListeners = mListeners.get(url);
        if (progressListeners == null) {
            progressListeners = new LinkedList<>();
            mListeners.put(url, progressListeners);
        }
        progressListeners.add(listener);
    }

    public void removeListener(@NonNull String url, @NonNull OnProgressListener listener) {
        List<OnProgressListener> listeners = mListeners.get(url);
        if (listeners != null && !listeners.isEmpty()) {
            listeners.remove(listener);
        }
    }

    private static class SingleHolder {
        private static final ProgressManager INSTANCE = new ProgressManager();
    }
}
