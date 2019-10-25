package com.meteor.download;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.meteor.downloadlib.bean.DownloadInfo;
import com.meteor.downloadlib.manager.DownloadManager;
import com.meteor.downloadlib.utils.DownloadListenerUtils;
import com.meteor.downloadlib.utils.DownloadObserver;

public class MainActivity extends AppCompatActivity implements DownloadObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadListenerUtils.getInstance().registerObserver(this);
    }

    @Override
    public void onDownloadStateChanged(DownloadInfo info) {

    }

    @Override
    public void onDownloadProgressed(DownloadInfo info) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadListenerUtils.getInstance().unRegisterObserver(this);
    }
}
