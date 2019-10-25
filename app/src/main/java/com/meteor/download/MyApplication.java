package com.meteor.download;

import android.app.Application;

import com.meteor.downloadlib.DownloaderAppliaction;

/**
 * @Author Gongll
 * @Date 2019/10/24 19:53
 * @Description
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DownloaderAppliaction.init(this);
    }
}
