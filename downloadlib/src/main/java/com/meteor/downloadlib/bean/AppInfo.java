package com.meteor.downloadlib.bean;

/**
 * @Author Gongll
 * @Date 2019/4/29 15:52
 * @Description
 */
public class AppInfo {
    private Long id;
    private int appSize;//app大小
    private String url;
    private String name;

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

    public int getAppSize() {
        return appSize;
    }

    public void setAppSize(int appSize) {
        this.appSize = appSize;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
