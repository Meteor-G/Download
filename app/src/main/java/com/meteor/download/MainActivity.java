package com.meteor.download;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.meteor.downloadlib.bean.AppInfo;
import com.meteor.downloadlib.bean.DownloadInfo;
import com.meteor.downloadlib.manager.DownloadManager;
import com.meteor.downloadlib.utils.DownloadListenerUtils;
import com.meteor.downloadlib.utils.DownloadObserver;

public class MainActivity extends AppCompatActivity {
    // 固定下载的资源路径，这里可以设置网络上的地址

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_PARTICLE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_file_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                intent.putExtra("type", TYPE_NORMAL);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_particle_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                intent.putExtra("type", TYPE_PARTICLE);
                startActivity(intent);
            }
        });
    }
}
