package com.meteor.downloadlib.bean;

import com.meteor.downloadlib.manager.DownloadManager;

/**
 * @Author Gongll
 * @Date 2019/4/29 15:52
 * @Description
 */
public class DownloadInfo {
    private Long id;//下载ID
    private int downloadState;//下载状态
    private int currentSize;//已经下载
    private String path;//路径
    private int appSize;//app大小
    private String url;
    private long progress;

    private boolean hasFinished;

    public static DownloadInfo clone(AppInfo appInfo) {
        DownloadInfo info = new DownloadInfo();
        info.setId(appInfo.getId());
        info.setDownloadState(DownloadManager.STATE_NONE);
        info.setCurrentSize(0);
        info.setPath("/mnt/sdcard/123.exe");
        info.setAppSize(appInfo.getAppSize());
        info.setUrl(appInfo.getUrl());
        info.setProgress(0L);
        info.setHasFinished(false);
        return info;
    }

    public boolean isHasFinished() {
        return hasFinished;
    }

    public void setHasFinished(boolean hasFinished) {
        this.hasFinished = hasFinished;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getAppSize() {
        return appSize;
    }

    public void setAppSize(int appSize) {
        this.appSize = appSize;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(int currentSize) {
        this.currentSize = currentSize;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }
}
