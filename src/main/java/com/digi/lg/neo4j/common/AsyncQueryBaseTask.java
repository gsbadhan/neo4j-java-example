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

public abstract class AsyncQueryBaseTask extends Thread {
	private AsyncQueryTaskManager processor;
	private boolean done = false;
	private boolean free = false;

	public AsyncQueryBaseTask(AsyncQueryTaskManager processor, String taskName) {
		super();
		this.processor = processor;
		setName(taskName);
		start();
	}

	@Override
	public void run() {
		collect();
		setDone(true);
		waitForOut();
		postMerge();
	}

	public abstract void collect();

	protected void waitForOut() {
		while (true) {
			processor.poll();
			if (isFree()) {
				break;
			}
		}
	}

	public abstract void postMerge();

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

}
