package com.sohu.bp.elite.task;

import java.util.concurrent.ForkJoinPool;

/**
 * 用于显式处理stream中的parallel的forkJoin线程池
 * @author zhijungou
 * 2017年4月24日
 */
public class EliteParallelPool {
    private static final int FORKJOIN_POOL_SIZE = 8;
    private static ForkJoinPool forkJoinPool;
    
    public void init() {
        forkJoinPool = new ForkJoinPool(FORKJOIN_POOL_SIZE);
    }
    
    public static ForkJoinPool getForkJoinPool() {
        return EliteParallelPool.forkJoinPool;
    }
}
