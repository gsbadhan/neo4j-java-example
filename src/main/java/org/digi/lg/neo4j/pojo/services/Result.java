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

import com.google.gson.Gson;

public class Result {
	private String status;
	private Object result;
	private Object error;
	private String pageOffset = "-1";

	public Result setResult(Object output) {
		status = "success";
		result = output;
		return this;
	}

	public Result setResult(Object output, String pageOffset) {
		status = "success";
		result = output;
		this.pageOffset = pageOffset;
		return this;
	}

	public Result setResult(Object output, int pageOffset) {
		status = "success";
		result = output;
		this.pageOffset = String.valueOf(pageOffset);
		return this;
	}

	public Result setError(ErrorPojo output) {
		status = "error";
		error = output;
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

	public String getPageOffset() {
		return pageOffset;
	}

	public void setPageOffset(String pageOffset) {
		this.pageOffset = pageOffset;
	}

}
