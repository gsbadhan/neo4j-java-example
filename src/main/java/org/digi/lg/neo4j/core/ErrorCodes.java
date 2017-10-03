/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.core;

/**
 * Generic error codes. we can use for application level or database level.
 * 
 * Error code formation standards:<br>
 * 1xx: Informational responses<br>
 * 2xx: Success responses<br>
 * 3xx Client errors responses<br>
 * 
 * @author GURPREET.SINGH
 */

public enum ErrorCodes {
	SUCCESS(200), FAILED(300), NOT_AUTHORISED(301), NOT_FOUND(302), INVALID_OPERATION(303), MISSING_PARAMETER(
			304), INVALID_PARAMETER(305), INVALID_TOKEN(306), ALREADY_EXIST(100), NONE(-1);

	private int code;

	private ErrorCodes(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}
}
