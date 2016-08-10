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
	public String applicationTemplateIdentifier;
	
	/*
	 * query against the global schema
	 */
	public IvisQuery query;

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

}
