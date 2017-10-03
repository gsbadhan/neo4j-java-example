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

public class ErrorPojo {

	private int code;
	private String message;
	private String type;

	public ErrorPojo() {
	}

	public ErrorPojo(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public ErrorPojo setCode(int code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public ErrorPojo setMessage(String message) {
		this.message = message;
		return this;
	}

	public String getType() {
		return type;
	}

	public ErrorPojo setType(String type) {
		this.type = type;
		return this;
	}

	@Override
	public String toString() {

		return new Gson().toJson(this);
	}

}
