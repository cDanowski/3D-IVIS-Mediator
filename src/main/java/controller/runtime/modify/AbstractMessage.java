package controller.runtime.modify;

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

	public String getApplicationTemplateIdentifier() {
		return applicationTemplateIdentifier;
	}

	public void setApplicationTemplateIdentifier(String applicationTemplateIdentifier) {
		this.applicationTemplateIdentifier = applicationTemplateIdentifier;
	}

}
