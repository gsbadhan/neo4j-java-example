package org.digi.lg.neo4j.core;

@SuppressWarnings("serial")
public class StopException extends RuntimeException {
	public StopException() {
		super();
	}

	public StopException(String error) {
		super(error);
	}

}
