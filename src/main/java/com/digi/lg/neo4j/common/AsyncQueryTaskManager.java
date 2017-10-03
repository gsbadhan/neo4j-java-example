/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package com.digi.lg.neo4j.common;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

public class AsyncQueryTaskManager {
	private static final Logger LOGGER = Logger.getLogger(AsyncQueryTaskManager.class);
	private ReentrantLock pollLock = null;
	private Condition waitCondition = null;
	private Queue<AsyncQueryBaseTask> queue = null;

	public AsyncQueryTaskManager(Queue<AsyncQueryBaseTask> queue) {
		super();
		this.queue = queue;
		this.pollLock = new ReentrantLock(true);
		this.waitCondition = pollLock.newCondition();
	}

	public void poll() {
		try {
			pollLock.lock();
			AsyncQueryBaseTask task = queue.peek();
			if (task == null)
				return;

			if (task.isDone()) {
				queue.poll();
				task.setFree(true);
				waitCondition.signal();
			} else {
				waitCondition.await();
			}
		} catch (Exception e) {
			LOGGER.error("error occured in poll()", e);
		} finally {
			pollLock.unlock();
		}
	}

}
