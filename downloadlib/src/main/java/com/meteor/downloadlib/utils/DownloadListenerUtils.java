package com.meteor.downloadlib.utils;

import com.meteor.downloadlib.bean.DownloadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Gongll
 * @Date 2019/10/24 20:29
 * @Description
 */
public class DownloadListenerUtils {
    /**
     * 用于记录观察者，当信息发送了改变，需要通知他们
     */
    private List<DownloadObserver> mObservers = new ArrayList<>();


    private void DownloadListenerUtils() {

    }

    private static final class DownloderHolder {
        private static final DownloadListenerUtils DOWNLAODER_UNIT = new DownloadListenerUtils();
    }

    public static DownloadListenerUtils getInstance() {
        return DownloderHolder.DOWNLAODER_UNIT;
    }

    /**
     * 注册观察者
     */
    public void registerObserver(DownloadObserver observer) {
        synchronized (mObservers) {
            if (!mObservers.contains(observer)) {
                mObservers.add(observer);
            }
        }
    }

    /**
     * 反注册观察者
     */
    public void unRegisterObserver(DownloadObserver observer) {
        synchronized (mObservers) {
            if (mObservers.contains(observer)) {
                mObservers.remove(observer);
            }
        }
    }

    /**
     * 当下载状态发送改变的时候回调
     */
    public void notifyDownloadStateChanged(DownloadInfo info) {
        synchronized (mObservers) {
            for (DownloadObserver observer : mObservers) {
                observer.onDownloadStateChanged(info);
            }
        }
    }

    /**
     * 当下载进度发送改变的时候回调
     */
    public void notifyDownloadProgressed(DownloadInfo info) {
        synchronized (mObservers) {
            for (DownloadObserver observer : mObservers) {
                observer.onDownloadProgressed(info);
            }
        }
    }
}
