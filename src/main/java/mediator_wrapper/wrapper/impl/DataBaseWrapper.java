package mediator_wrapper.wrapper.impl;

import controller.runtime.modify.RuntimeModificationMessage;
import mediator_wrapper.wrapper.IvisWrapperInterface;
import mediator_wrapper.wrapper.abstract_types.AbstractIvisDataBaseWrapper;

/**
 * Wrapper that manages database access
 * 
 * @author Christian Danowski
 *
 */
public class DataBaseWrapper extends AbstractIvisDataBaseWrapper implements IvisWrapperInterface {

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
