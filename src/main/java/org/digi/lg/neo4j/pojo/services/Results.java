/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.pojo.services;

import org.digi.lg.neo4j.core.ErrorCodes;

public class Results<RET> {
	private final RET data;
	private final int pageOffset;
	private final ErrorCodes errorCode;

	public Results(final RET data) {
		super();
		this.data = data;
		this.pageOffset = -1;
		this.errorCode = ErrorCodes.NONE;
	}

	public Results(final ErrorCodes errorCode) {
		super();
		this.data = null;
		this.pageOffset = -1;
		this.errorCode = errorCode;
	}

	public Results(final RET data, final ErrorCodes errorCode) {
		super();
		this.data = data;
		this.pageOffset = -1;
		this.errorCode = errorCode;
	}

	public Results(final RET data, final int pageOffset) {
		super();
		this.data = data;
		this.pageOffset = pageOffset <= 0 ? -1 : pageOffset;
		this.errorCode = ErrorCodes.NONE;
	}

	public RET getData() {
		return data;
	}

	public int getPageOffset() {
		return pageOffset;
	}

	public ErrorCodes getErrorCode() {
		return errorCode;
	}

}
