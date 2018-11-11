package com.brijesh.webster.news;

import android.content.Context;
import androidx.multidex.MultiDex;


/*
** Used for getting the application instance
**/
public class Application extends android.app.Application {
    private static Application applicationInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;


    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public static Application getMyTimesApplicationInstance(){
        return applicationInstance;
    }
}
