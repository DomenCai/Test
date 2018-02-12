package com.domencai.one;

import android.app.Application;
import android.content.Context;

import com.domencai.one.parallax.ParallaxHelper;

/**
 * Created by Domen、on 2017/11/27.
 */

public class App extends Application {
    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = getApplicationContext();
        registerActivityLifecycleCallbacks(ParallaxHelper.getInstance());
    }

    public static Context getAppContext() {
        return sAppContext;
    }
}
