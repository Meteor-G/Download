package com.meteor.downloadlib.bean;

import java.util.List;

/**
 * @Author Gongll
 * @Date 2019/4/29 15:52
 * @Description
 */
public class AppInfo {
    private Long id;
    private String url;
    private String name;
    private List<String> urls;

    public AppInfo(Long id, String url, String name) {
        this.id = id;
        this.url = url;
        this.name = name;
    }

    public AppInfo(Long id, String name, List<String> urls) {
        this.id = id;
        this.name = name;
        this.urls = urls;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
