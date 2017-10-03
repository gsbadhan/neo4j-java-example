/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.exception;

import org.digi.lg.neo4j.core.ErrorCodes;

/**
 * This is common exception for all operation. with error code constructor, we
 * can differentiate various type of errors.
 * 
 * @author gurpreet.singh
 *
 */
@SuppressWarnings("serial")
public class SecurityException extends RuntimeException {
	private final ErrorCodes errorCode;

	public SecurityException() {
		super();
		this.errorCode = ErrorCodes.NONE;
	}

	public SecurityException(ErrorCodes errorCode) {
		super("");
		this.errorCode = errorCode;
	}

	public SecurityException(String message, ErrorCodes errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public SecurityException(String message, Throwable cause, ErrorCodes errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public SecurityException(Throwable cause, ErrorCodes errorCode) {
		super(cause);
		this.errorCode = errorCode;
	}

	public ErrorCodes getErrorCode() {
		return this.errorCode;
	}

}
