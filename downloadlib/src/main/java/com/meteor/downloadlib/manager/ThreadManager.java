package com.meteor.downloadlib.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author Gongll
 * @Date 2019/4/29 15:55
 * @Description
 */
public class ThreadManager {
    public static final String DEFAULT_SINGLE_POOL_NAME = "DEFAULT_SINGLE_POOL_NAME";
    private static ThreadPoolProxy mLongPool = null;
    private static Object mLongLock = new Object();
    private static ThreadPoolProxy mShortPool = null;
    private static Object mShortLock = new Object();
    private static ThreadPoolProxy mDownloadPool = null;
    private static Object mDownloadLock = new Object();
    private static Map<String, ThreadPoolProxy> mMap = new HashMap<String, ThreadPoolProxy>();
    private static Object mSingleLock = new Object();

    /**
     * 获取下载线程
     */
    public static ThreadPoolProxy getDownloadPool() {
        synchronized (mDownloadLock) {
            if (mDownloadPool == null) {
                mDownloadPool = new ThreadPoolProxy(3, 3, 5L);
            }
            return mDownloadPool;
        }
    }

    /**
     * 获取一个用于执行长耗时任务的线程池，避免和短耗时任务处在同一个队列而阻塞了重要的短耗时任务，通常用来联网操作
     */
    public static ThreadPoolProxy getLongPool() {
        synchronized (mLongLock) {
            if (mLongPool == null) {
                mLongPool = new ThreadPoolProxy(5, 5, 5L);
            }
            return mLongPool;
        }
    }

    /**
     * 获取一个用于执行短耗时任务的线程池，避免因为和耗时长的任务处在同一个队列而长时间得不到执行，通常用来执行本地的IO/SQL
     */
    public static ThreadPoolProxy getShortPool() {
        synchronized (mShortLock) {
            if (mShortPool == null) {
                mShortPool = new ThreadPoolProxy(2, 2, 5L);
            }
            return mShortPool;
        }
    }

    /**
     * 获取一个单线程池，所有任务将会被按照加入的顺序执行，免除了同步开销的问题
     */
    public static ThreadPoolProxy getSinglePool() {
        return getSinglePool(DEFAULT_SINGLE_POOL_NAME);
    }

    /**
     * 获取一个单线程池，所有任务将会被按照加入的顺序执行，免除了同步开销的问题
     */
    public static ThreadPoolProxy getSinglePool(String name) {
        synchronized (mSingleLock) {
            ThreadPoolProxy singlePool = mMap.get(name);
            if (singlePool == null) {
                singlePool = new ThreadPoolProxy(1, 1, 5L);
                mMap.put(name, singlePool);
            }
            return singlePool;
        }
    }

    public static class ThreadPoolProxy {
        private ThreadPoolExecutor mPool;
        private int mCorePoolSize;
        private int mMaximumPoolSize;
        private long mKeepAliveTime;

        private ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            mCorePoolSize = corePoolSize;
            mMaximumPoolSize = maximumPoolSize;
            mKeepAliveTime = keepAliveTime;
        }

        /**
         * 执行任务，当线程池处于关闭，将会重新创建新的线程池
         */
        public synchronized void execute(Runnable run) {
            if (run == null) {
                return;
            }
            if (mPool == null || mPool.isShutdown()) {
                mPool = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, mKeepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
            }
            mPool.execute(run);
        }

        /**
         * 取消线程池中某个还未执行的任务
         */
        public synchronized void cancel(Runnable run) {
            if (mPool != null && (!mPool.isShutdown() || mPool.isTerminating())) {
                mPool.getQueue().remove(run);
            }
        }

        /**
         * 取消线程池中某个还未执行的任务
         */
        public synchronized boolean contains(Runnable run) {
            if (mPool != null && (!mPool.isShutdown() || mPool.isTerminating())) {
                return mPool.getQueue().contains(run);
            } else {
                return false;
            }
        }

        /**
         * 立刻关闭线程池，并且正在执行的任务也将会被中断
         * <p>
         * 会尝试interrupt线程池中正在执行的线程
         * 等待执行的线程也会被取消
         * 但是并不能保证一定能成功的interrupt线程池中的线程。
         * 会返回并未终止的线程列表List<Runnable>
         * shutdownNow()方法比shutdown()强硬了很多，不仅取消了排队的线程而且确实尝试终止当前正在执行的线程。
         */
        public void stop() {
            if (mPool != null && (!mPool.isShutdown() || mPool.isTerminating())) {
                mPool.shutdownNow();
            }
        }

        /**
         * 平缓关闭单任务线程池，但是会确保所有已经加入的任务都将会被执行完毕才关闭
         * * <p>
         * 不能接受新的submit
         * 并没有任何的interrupt操作，会等待线程池中所有线程（执行中的以及排队的）执行完毕
         * 可以理解为是个标识性质的方法，标识这程序有意愿在此刻终止线程池的后续操作。
         */
        public synchronized void shutdown() {
            if (mPool != null && (!mPool.isShutdown() || mPool.isTerminating())) {
                mPool.shutdown();
            }
        }
    }
}
