package controller.runtime.additional_data;

import ivisQuery.IvisQuery;

/**
 * Java representation of a runtime request data message, which is sent by
 * clients to request additional data from within a running scene.
 * 
 * @author Christian Danowski
 *
 */
public class RuntimeRequestDataMessage {
	
	/*
	 * query against the global schema
	 */
	public IvisQuery query;
	
	/*
	 * the resulting scene content as X3DOM subtree
	 */
	public String responseSceneContent;

}
