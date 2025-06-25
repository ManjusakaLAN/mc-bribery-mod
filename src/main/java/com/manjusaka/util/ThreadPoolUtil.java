package com.manjusaka.util;

import java.util.concurrent.*;

public class ThreadPoolUtil {
    // 核心线程数
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    // 最大线程数
    private static final int MAX_POOL_SIZE = 100;
    // 空闲线程超时时间（秒）
    private static final long KEEP_ALIVE_TIME = 60L;
    // 阻塞队列容量
    private static final int BLOCKING_QUEUE_CAPACITY = 200;

    // 线程池实例
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(BLOCKING_QUEUE_CAPACITY),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 提交任务到线程池
     *
     * @param task 要执行的任务
     */
    public static void submitTask(Runnable task) {
        THREAD_POOL_EXECUTOR.execute(task);
    }

    /**
     * 关闭线程池
     */
    public static void shutdownThreadPool() {
        THREAD_POOL_EXECUTOR.shutdown();
    }

    /**
     * 获取当前线程池是否已关闭
     *
     * @return 是否关闭
     */
    public static boolean isShutdown() {
        return THREAD_POOL_EXECUTOR.isShutdown();
    }
}
