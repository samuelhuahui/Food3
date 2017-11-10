package com.huaye.food;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.tencent.bugly.crashreport.CrashReport;

import cn.bmob.v3.Bmob;

/**
 * Created by sunhuahui on 2017/10/10.
 */

public class App extends Application {
    public static Boolean isRunning = false;
    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "e5adc480dc55902232502089279322c9");
        CrashReport.initCrashReport(getApplicationContext(), "0b8ee9b13a", true);
    }
}
