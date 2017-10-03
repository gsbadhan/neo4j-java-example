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

public class ConnectionLinks {
	private String source;
	private String sourceLabel;
	private String destination;
	private String destinationLabel;

	public ConnectionLinks() {
	}

	public ConnectionLinks(String source, String sourceLabel, String destination, String destinationLabel) {
		super();
		this.source = source;
		this.sourceLabel = sourceLabel;
		this.destination = destination;
		this.destinationLabel = destinationLabel;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSourceLabel() {
		return sourceLabel;
	}

	public void setSourceLabel(String sourceLabel) {
		this.sourceLabel = sourceLabel;
	}

	public String getDestinationLabel() {
		return destinationLabel;
	}

	public void setDestinationLabel(String destinationLabel) {
		this.destinationLabel = destinationLabel;
	}

}
