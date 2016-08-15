package controller;

import ivisQuery.IvisQuery;

/**
 * Abstract class declaring an application template identifier
 * 
 * @author Christian Danowski
 *
 */
public class AbstractMessage {

	/*
	 * identifier for the application template
	 */
	private String applicationTemplateIdentifier;
	
	/*
	 * query against the global schema
	 */
	private IvisQuery query;
	
	private String responseInfoMessage;

	public String getApplicationTemplateIdentifier() {
		return applicationTemplateIdentifier;
	}

	public void setApplicationTemplateIdentifier(String applicationTemplateIdentifier) {
		this.applicationTemplateIdentifier = applicationTemplateIdentifier;
	}

	public IvisQuery getQuery() {
		return query;
	}

	public void setQuery(IvisQuery query) {
		this.query = query;
	}

	public String getResponseInfoMessage() {
		return responseInfoMessage;
	}

	public void setResponseInfoMessage(String responseInfoMessage) {
		this.responseInfoMessage = responseInfoMessage;
	}

}
