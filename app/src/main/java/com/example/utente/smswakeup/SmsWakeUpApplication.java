package com.example.utente.smswakeup;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by utente on 16/06/2016.
 */
public class SmsWakeUpApplication extends Application {
    public static final String TAG="SmsWakeUpApplication";

    public static Context getMyContext() {
        return myContext;
    }

    private static Context myContext;

    public SmsWakeUpApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

       // Timber.plant(new Timber.DebugTree());
        Timber.plant(new CrashReportingTree());
        Timber.tag(TAG);
        Timber.e("\n-------------------------------------------------------------");
        Timber.tag("LEGENDA");
        Timber.e("\nTIMESTAMP - PRIORITA' - TAG - CLASSE - METODO");
        // Timber.e("Application Created");
        Timber.tag(TAG);
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        myContext = getApplicationContext();
    }

    @Override
    public void onTerminate() {
        //Timber.e("Application onTerminate");
        Timber.tag(TAG);
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Timber.tag(TAG);
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        Timber.tag(TAG);
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Timber.tag(TAG);
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onTrimMemory(level);
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        Timber.tag(TAG);
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.registerComponentCallbacks(callback);
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        Timber.tag(TAG);
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.unregisterComponentCallbacks(callback);
    }

    @Override
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        Timber.tag(TAG);
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        Timber.tag(TAG);
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.unregisterActivityLifecycleCallbacks(callback);
    }

    @Override
    public void registerOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        Timber.tag(TAG);
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.registerOnProvideAssistDataListener(callback);
    }

    @Override
    public void unregisterOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        Timber.tag(TAG);
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.unregisterOnProvideAssistDataListener(callback);
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.Tree {

        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            LogToFile.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    LogToFile.logError(t);
                } else if (priority == Log.WARN) {
                    LogToFile.logWarning(t);
                }
            }
        }
    }
}
