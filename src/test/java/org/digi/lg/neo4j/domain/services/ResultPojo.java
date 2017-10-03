/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.domain.services;

import com.google.gson.Gson;

public class ResultPojo {

	private String status;
	private Object result;
	private Object error;

	public ResultPojo setResult(Object output) {
		status = "success";
		result = output;
		return this;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public Object getResult() {
		return result;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getError() {
		return error;
	}

	public void setError(Object error) {
		this.error = error;
	}

}
