package com.example.yuanmengzeng.hexagonblock;

import android.app.Application;

/**
 * MyBaseApp Created by yuanmengzeng on 2016/6/29.
 */
public class MyBaseApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        ZYMLog.info("ZYM mybaseApplication oncreate");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        ZYMLog.info("ZYM mybaseApplication oncreate");
    }
}
