package com.meteor.downloadlib.utils;

import com.meteor.downloadlib.bean.DownloadInfo;

/**
 * @Author Gongll
 * @Date 2019/10/24 20:30
 * @Description
 */
public interface DownloadObserver {
    public void onDownloadStateChanged(DownloadInfo info);

    public void onDownloadProgressed(DownloadInfo info);
}
