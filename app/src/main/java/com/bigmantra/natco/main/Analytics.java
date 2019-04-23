package com.bigmantra.natco.main;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.bigmantra.natco.BuildConfig;
import com.instabug.library.Feature;
import com.instabug.library.IBGInvocationEvent;
import com.instabug.library.Instabug;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Girish Lakshmanan on 9/6/19.
 */

public class Analytics {
    private static final String TAG = Analytics.class.getSimpleName();

    public static void init(Application application) {

        Fabric.with(application, new Crashlytics());

        // Instabug
        new Instabug.Builder(application, BuildConfig.INSTABUG_KEY)
                .setInvocationEvent(IBGInvocationEvent.IBGInvocationEventShake)
                .setTrackingUserStepsState(!BuildConfig.DEBUG ? Feature.State.ENABLED : Feature.State.DISABLED)
                .setCrashReportingState(!BuildConfig.DEBUG ? Feature.State.ENABLED : Feature.State.DISABLED)
                .setDebugEnabled(true)
                .build();

//        if (BuildConfig.DEBUG) {
//            LeakCanary.install(application);
//        }
    }

    public static void track(String eventName) {
        MixpanelAPI mixpanelAPI = EApplication.getInstance().getMixpanelAPI();
        mixpanelAPI.track(eventName);
    }
}
