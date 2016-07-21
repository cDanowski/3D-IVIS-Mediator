package mediator_wrapper.wrapper.impl;

import java.net.URI;

import controller.runtime.modify.RuntimeModificationMessage;
import mediator_wrapper.wrapper.IvisWrapperInterface;
import mediator_wrapper.wrapper.abstract_types.AbstractIvisFileWrapper;

/**
 * Wrapper to manage access to CSV files.
 * 
 * @author Christian Danowski
 *
 */
public class CsvWrapper extends AbstractIvisFileWrapper implements IvisWrapperInterface{
	
	public CsvWrapper(URI fileLocation, Object localSchema, Object schemaMapping) {
		// TODO instantiate wrapper properly!
		
	}

	@Override
	public Object queryData(String subQueryAgainstGlobalSchema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object applyModification(RuntimeModificationMessage modificationMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object transformToLocalQuery(Object globalQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object executeLocalQuery(Object localQuery) {
		// TODO Auto-generated method stub
		return null;
	}

}
