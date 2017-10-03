
package com.neo4j.perf;

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
