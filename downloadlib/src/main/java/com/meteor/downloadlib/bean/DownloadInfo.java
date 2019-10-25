package com.meteor.downloadlib.bean;

import com.meteor.downloadlib.DownloaderAppliaction;
import com.meteor.downloadlib.manager.DownloadManager;
import com.meteor.downloadlib.utils.FileUtil;

import java.util.List;

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
    private int total;//app大小
    private String url;
    private List<String> urls;

    public static DownloadInfo clone(boolean isParticle, AppInfo appInfo) {
        DownloadInfo info = new DownloadInfo();
        info.setId(appInfo.getId());
        info.setDownloadState(DownloadManager.STATE_NONE);
        info.setCurrentSize(0);
        info.setPath(FileUtil.getDownloadDir(DownloaderAppliaction.getContent()) + "/" + appInfo.getName());
        if (isParticle) {
            info.setUrls(appInfo.getUrls());
        } else {
            info.setUrl(appInfo.getUrl());
        }
        return info;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
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
