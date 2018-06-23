package com.springcloud.cmserver.mgmt.cm.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskUtility {
	
	private static TaskUtility instance;

	private ThreadPoolExecutor threadPool = null;

	public TaskUtility() {
		

		threadPool = new ThreadPoolExecutor(5, 20, 10,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10),
				new ThreadPoolExecutor.CallerRunsPolicy());

	}
	
	public TaskUtility(int corePoolSize, int maximumPoolSize, int workQueue){
		threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 10,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(workQueue),
				new ThreadPoolExecutor.CallerRunsPolicy());
	}
	
	public static void createInstance(int corePoolSize, int maximumPoolSize, int workQueue){
		instance = new TaskUtility(corePoolSize, maximumPoolSize, workQueue);
	}

	public static TaskUtility getInstance() {
		if (instance == null) {
			instance = new TaskUtility();
		}
		return instance;
	}

	public void execute(Thread thread) {
		threadPool.execute(thread);
	}
	
	public void getThing(){
		System.out.println("active:" + threadPool.getActiveCount() + " max:" + threadPool.getMaximumPoolSize() + " min:" + threadPool.getCorePoolSize());
	}
}
