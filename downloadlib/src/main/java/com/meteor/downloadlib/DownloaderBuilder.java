package com.meteor.downloadlib;

import android.text.TextUtils;

import com.meteor.downloadlib.bean.AppInfo;
import com.meteor.downloadlib.utils.DownloadObserver;
import com.meteor.downloadlib.utils.FileUtil;

/**
 * @Author Gongll
 * @Date 2019/10/25 16:37
 * @Description
 */
public class DownloaderBuilder {
    private AppInfo info;//构建要下载的对象
    private String path;//保存路径
    private DownloadObserver listener;


    DownloaderBuilder() {

    }

    public final DownloaderBuilder setInfo(AppInfo info) {
        this.info = info;
        return this;
    }

    public final DownloaderBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public final DownloaderBuilder setListener(DownloadObserver listener) {
        this.listener = listener;
        return this;
    }

    public final Downloader build(boolean isParticle) {
        if (info == null) {
            throw new RuntimeException("info  don't is null");
        }
        if (TextUtils.isEmpty(path)) {
            path = FileUtil.getDownloadDir(DownloaderAppliaction.getContent()) + "/" + info.getName();
        }
        return new Downloader(info, path, listener, isParticle);
    }

    public final Downloader build() {
        return new Downloader(info, path, listener, false);
    }
}
