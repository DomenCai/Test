package com.domencai.one.parallax;

import android.app.Activity;
import android.app.Application;
import android.graphics.Canvas;
import android.os.Bundle;

import com.domencai.one.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Domen、on 2018/2/12.
 */

public class ParallaxHelper implements Application.ActivityLifecycleCallbacks {
    private List<Activity> mActivityStack;

    public static ParallaxHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private ParallaxHelper() {
        mActivityStack = new LinkedList<>();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivityStack.add(activity);
        ParallaxBack annotation = checkAnnotation(activity.getClass());
        if (annotation != null) {
            createBackLayout(activity);
        }
    }

    private ParallaxBack checkAnnotation(Class<? extends Activity> c) {
        Class mc = c;
        ParallaxBack parallaxBack;
        while (Activity.class.isAssignableFrom(mc)) {
            parallaxBack = (ParallaxBack) mc.getAnnotation(ParallaxBack.class);
            if (parallaxBack != null)
                return parallaxBack;
            mc = mc.getSuperclass();
        }
        return null;
    }

    private void createBackLayout(Activity activity) {
        ParallaxBackLayout backLayout = new ParallaxBackLayout(activity);
        backLayout.setId(R.id.parallax_layout);
        backLayout.attachToActivity(activity);
        backLayout.setBackgroundView(new GoBackView(activity));
    }

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivityStack.remove(activity);
    }

    public static class GoBackView implements ParallaxBackLayout.IBackgroundView {

        private Activity mActivityBack;

        private GoBackView(Activity activity) {
            List<Activity> activities = ParallaxHelper.getInstance().mActivityStack;
            int index = activities.indexOf(activity);
            if (index > 0) {
                mActivityBack = activities.get(index - 1);
            }
        }

        @Override
        public void draw(Canvas canvas) {
            if (mActivityBack != null) {
                mActivityBack.getWindow().getDecorView().draw(canvas);
            }
        }

        @Override
        public boolean canGoBack() {
            return mActivityBack != null;
        }
    }

    private static class SingletonHolder {
        private static final ParallaxHelper INSTANCE = new ParallaxHelper();
    }
}
