package com.meteor.downloadlib;

import com.meteor.downloadlib.bean.AppInfo;
import com.meteor.downloadlib.utils.DownloadObserver;

/**
 * @Author Gongll
 * @Date 2019/10/25 15:46
 * @Description
 */
public class Downloader {
    private AppInfo info;//构建要下载的对象
    private String path;//保存路径
    private DownloadObserver listener;
    private boolean isParticle;

    public Downloader(AppInfo info, String path, DownloadObserver listener, boolean isParticle) {
        this.info = info;
        this.path = path;
        this.listener = listener;
        this.isParticle = isParticle;
    }

    public static DownloaderBuilder builder() {
        return new DownloaderBuilder();
    }
}
