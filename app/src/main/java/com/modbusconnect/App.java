package com.modbusconnect;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    public static boolean IS_DEBUG = true;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

}
