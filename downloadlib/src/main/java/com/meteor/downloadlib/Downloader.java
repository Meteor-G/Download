package com.meteor.downloadlib;

import android.content.Context;

/**
 * @Author Gongll
 * @Date 2019/10/24 19:48
 * @Description
 */
public class Downloader {
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static Context getContent() {
        if (mContext == null) {
            new RuntimeException("Downloader don't init");
        }
        return mContext;
    }
}
