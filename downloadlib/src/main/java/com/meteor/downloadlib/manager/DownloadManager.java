package com.meteor.downloadlib.manager;

import android.util.Log;

import com.meteor.downloadlib.bean.AppInfo;
import com.meteor.downloadlib.bean.DownloadInfo;
import com.meteor.downloadlib.utils.DownloadListenerUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Gongll
 * @Date 2019/4/29 15:51
 * @Description
 */
public class DownloadManager {
    public static final int STATE_NONE = 0;
    /**
     * 等待中
     */
    public static final int STATE_WAITING = 1;
    /**
     * 下载中
     */
    public static final int STATE_DOWNLOADING = 2;
    /**
     * 暂停
     */
    public static final int STATE_PAUSED = 3;
    /**
     * 下载完毕
     */
    public static final int STATE_DOWNLOADED = 4;
    /**
     * 下载失败
     */
    public static final int STATE_ERROR = 5;

    private static DownloadManager instance;

    private DownloadManager() {
    }

    /**
     * 用于记录下载信息，如果是正式项目，需要持久化保存
     */
    private Map<Long, DownloadInfo> mDownloadMap = new ConcurrentHashMap<Long, DownloadInfo>();
    /**
     * 用于记录所有下载的任务，方便在取消下载时，通过id能找到该任务进行删除
     */
    private Map<Long, DownloadTask> mTaskMap = new ConcurrentHashMap<Long, DownloadTask>();

    public static synchronized DownloadManager getInstance() {
        if (instance == null) {
            instance = new DownloadManager();
        }
        return instance;
    }

    /**
     * 下载，需要传入一个appInfo对象
     */
    public synchronized void download(final AppInfo appInfo) {
        PrepareTask prepareTask = new PrepareTask(appInfo);
        ThreadManager.getShortPool().execute(prepareTask);
    }

    /**
     * 准备线程，获取文件大小
     */
    private class PrepareTask implements Runnable {
        private AppInfo appInfo;

        public PrepareTask(AppInfo appInfo) {
            this.appInfo = appInfo;
        }

        @Override
        public void run() {
            // 先判断是否有这个app的下载信息
            DownloadInfo info = mDownloadMap.get(appInfo.getId());
            if (info == null) {// 如果没有，则根据appInfo创建一个新的下载信息
                /**
                 * 判断是否为颗粒下载
                 */
                if (appInfo.getUrl() != null) {
                    info = DownloadInfo.clone(false, appInfo);
                    info.setTotal(getDownloadFileSize(info.getUrl()));
                } else if (appInfo.getUrls().size() > 0) {
                    info = DownloadInfo.clone(true, appInfo);
                    info.setTotal(info.getUrls().size());
                }
                mDownloadMap.put(appInfo.getId(), info);
            }
            // 判断状态是否为STATE_NONE、STATE_PAUSED、STATE_ERROR。只有这3种状态才能进行下载，其他状态不予处理
            if (info.getDownloadState() == STATE_NONE
                    || info.getDownloadState() == STATE_PAUSED
                    || info.getDownloadState() == STATE_ERROR) {
                // 下载之前，把状态设置为STATE_WAITING，因为此时并没有产开始下载，只是把任务放入了线程池中，当任务真正开始执行时，才会改为STATE_DOWNLOADING
                info.setDownloadState(STATE_WAITING);
                DownloadListenerUtils.getInstance().notifyDownloadStateChanged(info);// 每次状态发生改变，都需要回调该方法通知所有观察者

                DownloadTask task = null;// 创建一个下载任务，放入线程池
                if (appInfo.getUrl() != null) {
                    task = new DownloadTask(false, info);
                } else if (appInfo.getUrls().size() > 0) {
                    task = new DownloadTask(true, info);
                }
                mTaskMap.put(info.getId(), task);
                ThreadManager.getDownloadPool().execute(task);
            }
        }
    }

    /**
     * 获取要下载文件的大小
     *
     * @param url1
     * @return
     */
    private int getDownloadFileSize(final String url1) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(url1);
            connection = (HttpURLConnection) url.openConnection();
            return connection.getContentLength();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            connection.disconnect();
        }
    }

    /**
     * 暂停下载
     */
    public synchronized void pause(AppInfo appInfo) {
        stopDownload(appInfo);
        DownloadInfo info = mDownloadMap.get(appInfo.getId());// 找出下载信息
        if (info != null) {// 修改下载状态
            info.setDownloadState(STATE_PAUSED);
            DownloadListenerUtils.getInstance().notifyDownloadStateChanged(info);
        }
    }

    /**
     * 取消下载，逻辑和暂停类似，只是需要删除已下载的文件
     */
    public synchronized void cancel(AppInfo appInfo) {
        stopDownload(appInfo);
        DownloadInfo info = mDownloadMap.get(appInfo.getId());// 找出下载信息
        if (info != null) {// 修改下载状态并删除文件
            info.setDownloadState(STATE_NONE);
            DownloadListenerUtils.getInstance().notifyDownloadStateChanged(info);
            info.setCurrentSize(0);
            File file = new File(info.getPath());
            file.delete();
        }
    }

    /**
     * 如果该下载任务还处于线程池中，且没有执行，先从线程池中移除
     */
    private void stopDownload(AppInfo appInfo) {
        DownloadTask task = mTaskMap.remove(appInfo.getId());// 先从集合中找出下载任务
        if (task != null) {
            ThreadManager.getDownloadPool().cancel(task);// 然后从线程池中移除
        }
    }

    /**
     * 获取下载信息
     */
    public synchronized DownloadInfo getDownloadInfo(long id) {
        return mDownloadMap.get(id);
    }

    public synchronized void setDownloadInfo(long id, DownloadInfo info) {
        mDownloadMap.put(id, info);
    }

    /**
     * 下载任务,
     */
    public class DownloadTask implements Runnable {
        private DownloadInfo info;
        private boolean isParticle;

        public DownloadTask(boolean isParticle, DownloadInfo info) {
            this.isParticle = isParticle;
            this.info = info;
        }

        @Override
        public void run() {
            if (isParticle) {
                ParticleDownloadTask(info);
            } else {
                NormalDownloadTask(info);
            }
        }
    }

    /**
     * 正常大文件断点下载
     *
     * @param info
     */
    private void NormalDownloadTask(DownloadInfo info) {
        info.setDownloadState(STATE_DOWNLOADING);// 先改变下载状态
        DownloadListenerUtils.getInstance().notifyDownloadStateChanged(info);

        HttpURLConnection connection = null;
        RandomAccessFile randomAccessFile = null;
        InputStream is = null;
        int compeleteSize = info.getCurrentSize();

        try {
            URL url = new URL(info.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");

            // 设置范围，格式为Range：bytes x-y;
            connection.setRequestProperty("Range", "bytes=" + compeleteSize + "-" + info.getTotal());
            randomAccessFile = new RandomAccessFile(info.getPath(), "rwd");
            randomAccessFile.seek(compeleteSize);
            is = connection.getInputStream();
            byte[] buffer = new byte[4096];
            int length = -1;
            while (((length = is.read(buffer)) != -1)
                    && info.getDownloadState() == STATE_DOWNLOADING) {
                randomAccessFile.write(buffer, 0, length);
                compeleteSize += length;
                // 每次读取到数据后，都需要判断是否为下载状态，如果不是，下载需要终止，如果是，则刷新进度
                info.setCurrentSize(compeleteSize);
                DownloadListenerUtils.getInstance().notifyDownloadProgressed(info);// 刷新进度
            }

        } catch (Exception e) {
            info.setDownloadState(STATE_ERROR);
            DownloadListenerUtils.getInstance().notifyDownloadStateChanged(info);
            info.setCurrentSize(0);
        } finally {
            connection.disconnect();
            try {
                is.close();
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 判断进度是否和app总长度相等
        if (info.getCurrentSize() == info.getTotal()) {
            info.setDownloadState(STATE_DOWNLOADED);
            DownloadListenerUtils.getInstance().notifyDownloadStateChanged(info);
        } else if (info.getDownloadState() == STATE_PAUSED) {// 判断状态
            DownloadListenerUtils.getInstance().notifyDownloadStateChanged(info);
        } else {
            info.setDownloadState(STATE_ERROR);
            DownloadListenerUtils.getInstance().notifyDownloadStateChanged(info);
            info.setCurrentSize(0);// 错误状态需要删除文件
        }
        mTaskMap.remove(info.getId());
    }

    /**
     * 一个任务是有多个小的文件组成
     *
     * @param info
     */
    private void ParticleDownloadTask(DownloadInfo info) {
        info.setDownloadState(STATE_DOWNLOADING);// 先改变下载状态
        DownloadListenerUtils.getInstance().notifyDownloadStateChanged(info);

        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        int currentSize = info.getCurrentSize();
        File file = new File(info.getPath());
        if (!file.exists()) {
            file.mkdir();
        }
        for (int i = currentSize; i < info.getTotal(); i++) {
            try {
                URL url = new URL(info.getUrls().get(i));
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                is = connection.getInputStream();
                os = new BufferedOutputStream(new FileOutputStream(new File(info.getPath() + "/" + currentSize)));
                byte[] buffer = new byte[1024];
                int length = -1;
                while ((length = is.read(buffer)) != -1) {
                    os.write(buffer, 0, length);
                }
                currentSize += 1;
                info.setCurrentSize(currentSize);
                DownloadListenerUtils.getInstance().notifyDownloadProgressed(info);// 刷新进度
                /**
                 * 在这持久化
                 */
                if (info.getDownloadState() == STATE_PAUSED) {
                    Log.i("STATE_PAUSED", "暂停下载");
                    DownloadListenerUtils.getInstance().notifyDownloadStateChanged(info);
                    break;
                }
                if (currentSize == info.getTotal()) {
                    info.setDownloadState(STATE_DOWNLOADED);
                    Log.i("STATE_DOWNLOADED", "下载结束");
                    DownloadListenerUtils.getInstance().notifyDownloadStateChanged(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
                info.setDownloadState(STATE_ERROR);
                DownloadListenerUtils.getInstance().notifyDownloadStateChanged(info);
                info.setCurrentSize(0);
            } finally {
                try {
                    connection.disconnect();
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mTaskMap.remove(info.getId());
        }
    }
}
