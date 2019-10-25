package com.meteor.download;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.meteor.downloadlib.bean.AppInfo;
import com.meteor.downloadlib.bean.DownloadInfo;
import com.meteor.downloadlib.manager.DownloadManager;
import com.meteor.downloadlib.utils.DownloadListenerUtils;
import com.meteor.downloadlib.utils.DownloadObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Gongll
 * @Date 2019/10/25 17:03
 * @Description
 */
public class FileDownloadAdapter extends RecyclerView.Adapter<FileDownloadAdapter.ViewHolder> implements DownloadObserver {

    private List<AppInfo> appInfos = new ArrayList<>();
    private Activity mContext;
    private boolean isParticle;
    private List<ViewHolder> mDisplayedHolders = new ArrayList<>();


    public FileDownloadAdapter(Activity mContext, boolean isParticle) {
        this.mContext = mContext;
        this.isParticle = isParticle;
    }

    public void initData(List<AppInfo> appInfos) {
        this.appInfos = appInfos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_download_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AppInfo appInfo = appInfos.get(position);
        holder.btnOperation.setText("开始");
        holder.tvTitle.setText(appInfo.getName());
        holder.setData(appInfo);
        mDisplayedHolders.add(holder);
    }

    @Override
    public int getItemCount() {
        return appInfos != null ? appInfos.size() : 0;
    }

    @Override
    public void onDownloadStateChanged(DownloadInfo info) {
        refreshHolder(info);
    }

    @Override
    public void onDownloadProgressed(DownloadInfo info) {
        refreshHolder(info);
    }

    public void startObserver() {
        DownloadListenerUtils.getInstance().registerObserver(this);
    }

    public void stopObserver() {
        DownloadListenerUtils.getInstance().unRegisterObserver(this);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvProgress;
        Button btnOperation;
        private int mState;
        public AppInfo mData;
        private float mProgress;
        private DownloadManager mDownloadManager = DownloadManager.getInstance();

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvProgress = itemView.findViewById(R.id.tv_progress);
            btnOperation = itemView.findViewById(R.id.btn_operation);
            btnOperation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mState == DownloadManager.STATE_NONE
                            || mState == DownloadManager.STATE_PAUSED
                            || mState == DownloadManager.STATE_ERROR) {
                        mDownloadManager.download(mData);
                    } else if (mState == DownloadManager.STATE_WAITING
                            || mState == DownloadManager.STATE_DOWNLOADING) {
                        mDownloadManager.pause(mData);
                    } else if (mState == DownloadManager.STATE_DOWNLOADED) {
                        ToastUtil(mContext, "已经下载完成");
                    }
                }
            });
        }

        public AppInfo getData() {
            return mData;
        }

        public void setData(AppInfo appInfo) {
            DownloadInfo downloadInfo = mDownloadManager.getDownloadInfo(appInfo.getId());
            if (downloadInfo != null) {
                mState = downloadInfo.getDownloadState();
                mProgress = downloadInfo.getCurrentSize() * 1.0f / downloadInfo.getTotal();
            } else {
                mState = DownloadManager.STATE_NONE;
                mProgress = 0;
            }
            this.mData = appInfo;
        }

        public void refreshState(int state, int currentSize, int total) {
            this.mState = state;
            switch (state) {
                case DownloadManager.STATE_NONE:
                    btnOperation.setText("开始下载");
                    break;
                case DownloadManager.STATE_PAUSED:
                    btnOperation.setText("暂停");
                    break;
                case DownloadManager.STATE_ERROR:
                    btnOperation.setText("错误");
                    break;
                case DownloadManager.STATE_WAITING:
                    btnOperation.setText("等待");
                    break;
                case DownloadManager.STATE_DOWNLOADING:
                    btnOperation.setText("正在下载");
                    if (isParticle) {
                        tvProgress.setText("正在下载" + currentSize);
                    } else {
                        tvProgress.setText(currentSize * 100.0 / total + "%");
                    }
                    break;
                case DownloadManager.STATE_DOWNLOADED:
                    btnOperation.setText("已经下载");
                    break;
                default:
                    break;
            }
        }
    }

    private void ToastUtil(Context mContext, String content) {
        Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
    }

    public List<ViewHolder> getDisplayedHolders() {
        synchronized (mDisplayedHolders) {
            return new ArrayList<ViewHolder>(mDisplayedHolders);
        }
    }

    private void refreshHolder(final DownloadInfo info) {
        List<ViewHolder> displayedHolders = getDisplayedHolders();
        for (int i = 0; i < displayedHolders.size(); i++) {
            final ViewHolder holder = displayedHolders.get(i);
            AppInfo appInfo = holder.getData();
            if (appInfo.getId() == info.getId()) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.refreshState(info.getDownloadState(), info.getCurrentSize(), info.getTotal());
                        Log.i("123456456", info.getCurrentSize() + "");
                    }
                });
            }
        }
    }

}
