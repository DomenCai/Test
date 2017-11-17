package com.domencai.one;

import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Domen、on 2017/9/12.
 */

public class RxUtils {

    private static Observable.Transformer sSchedulerTransformer = new  Observable.Transformer() {
        @Override public Object call(Object observable) {
            return ((Observable)  observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> getSchedulerTransformer() {
        return (Observable.Transformer<T, T>) sSchedulerTransformer;
    }

    public static abstract class SimpleSubscriber<T> extends Subscriber<T> {
        private static final String TAG = "SimpleSubscriber";
        private String mTag = TAG;

        @SuppressWarnings("unused")
        protected SimpleSubscriber() {}

        @SuppressWarnings("unused")
        protected SimpleSubscriber(String tag) {
            mTag = tag;
        }

        @Override
        public void onCompleted() {}

        @Override
        public void onError(Throwable e) {
            Log.w(mTag, "onError: " + e.getMessage());
        }
    }
}
