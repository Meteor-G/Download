package com.meteor.download;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.meteor.downloadlib.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Gongll
 * @Date 2019/10/25 17:10
 * @Description
 */
public class DownloadActivity extends Activity {
    private int type;
    private RecyclerView rvDownload;
    private FileDownloadAdapter downloadAdapter;
    private List<AppInfo> appInfos = new ArrayList<>();
    private static final String URL = "http://download.haozip.com/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        type = getIntent().getIntExtra("type", 1);
        initView();
    }

    private void initView() {
        rvDownload = findViewById(R.id.rv_download);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvDownload.setLayoutManager(manager);
        if (type == MainActivity.TYPE_NORMAL) {
            appInfos = simulateNormalData();
        } else if (type == MainActivity.TYPE_PARTICLE) {
            appInfos = simulateParticleData();
        }
        downloadAdapter = new FileDownloadAdapter(this, type == MainActivity.TYPE_PARTICLE);
        downloadAdapter.startObserver();
        downloadAdapter.initData(appInfos);
        rvDownload.setAdapter(downloadAdapter);
    }

    private List<AppInfo> simulateNormalData() {
        List<AppInfo> appInfos = new ArrayList<>();
        appInfos.add(new AppInfo(1L, URL + "haozip_v3.1.exe", "haozip_v3.1.exe"));
        appInfos.add(new AppInfo(2L, URL + "haozip_v3.1_hj.exe", "haozip_v3.1_hj.exe"));
        appInfos.add(new AppInfo(3L, URL + "haozip_v2.8_x64_tiny.exe", "haozip_v2.8_x64_tiny.exe"));
        appInfos.add(new AppInfo(4L, URL + "haozip_v2.8_tiny.exe", "haozip_v2.8_tiny.exe"));
        return appInfos;
    }

    private List<AppInfo> simulateParticleData() {
        List<AppInfo> appInfos = new ArrayList<>();
        List<String> listUrl = new ArrayList<>();
        listUrl.add("http://b-ssl.duitang.com/uploads/item/201208/30/20120830173930_PBfJE.jpeg");
        listUrl.add("http://b-ssl.duitang.com/uploads/blog/201312/04/20131204184148_hhXUT.jpeg");
        listUrl.add("http://img.redocn.com/sheying/20150309/lantianbaiyunshulin_3983653.jpg");
        listUrl.add("http://img3.cache.netease.com/photo/0001/2010-09-28/6HMTLQ3R00AQ0001.jpg");

        appInfos.add(new AppInfo(1L, "111111", listUrl));
        appInfos.add(new AppInfo(2L, "222222", listUrl));
        appInfos.add(new AppInfo(3L, "333333", listUrl));
        appInfos.add(new AppInfo(4L, "444444", listUrl));
        return appInfos;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
