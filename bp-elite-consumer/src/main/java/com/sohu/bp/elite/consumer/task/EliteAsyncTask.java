package com.sohu.bp.elite.consumer.task;

import java.util.concurrent.Callable;

/**
 * 
 * @author nicholastang
 * 2016-10-14 19:34:49
 * TODO elite异步线程基类
 */
public class EliteAsyncTask implements Runnable, Callable<Boolean>{

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

    @Override
    public Boolean call() throws Exception {
        return false;
    }

}