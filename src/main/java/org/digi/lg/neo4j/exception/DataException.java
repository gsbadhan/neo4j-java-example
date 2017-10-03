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
import org.slf4j.helpers.MessageFormatter;

/**
 * This is common exception for all operation. with error code constructor, we
 * can differentiate various type of errors.
 * 
 * @author gurpreet.singh
 *
 */
@SuppressWarnings("serial")
public class DataException extends RuntimeException {
	private final ErrorCodes errorCode;

	public DataException() {
		super();
		this.errorCode = ErrorCodes.NONE;
	}

	public DataException(String message, Object... args) {
		super(MessageFormatter.format(message, args).getMessage());
		this.errorCode = ErrorCodes.NONE;
	}

	public DataException(String message, ErrorCodes errorCode, Object... args) {
		super(MessageFormatter.format(message, args).getMessage());
		this.errorCode = errorCode;
	}

	public DataException(ErrorCodes errorCode) {
		super("");
		this.errorCode = errorCode;
	}

	public DataException(String message, ErrorCodes errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public DataException(String message, Throwable cause, ErrorCodes errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public DataException(Throwable cause, ErrorCodes errorCode) {
		super(cause);
		this.errorCode = errorCode;
	}

	public ErrorCodes getErrorCode() {
		return this.errorCode;
	}

}
