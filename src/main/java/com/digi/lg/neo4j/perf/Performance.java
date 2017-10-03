/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package com.digi.lg.neo4j.perf;

import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;

public class Performance {

	private Performance() {

	}

	public static Log4JStopWatch startWatch(PerfConstants perfConstants) {
		return new Log4JStopWatch(perfConstants.name());
	}

	public static void stopWatch(StopWatch watch) {
		watch.stop();
	}

}
