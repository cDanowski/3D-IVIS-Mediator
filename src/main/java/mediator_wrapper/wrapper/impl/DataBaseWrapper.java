package mediator_wrapper.wrapper.impl;

import java.util.List;

import controller.runtime.modify.RuntimeModificationMessage;
import ivisObject.IvisObject;
import ivisQuery.IvisQuery;
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
	public List<IvisObject> queryData(IvisQuery queryAgainstGlobalSchema, List<String> subquerySelectors) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object applyModification(RuntimeModificationMessage modificationMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object transformToLocalQuery(IvisQuery globalQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<String> transformIntoLocalSubqueries(IvisQuery globalQuery,
			List<String> subquerySelectors_globalSchema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<IvisObject> executeLocalQuery(Object localQuery, List<String> subquerySelectors_localSchema,
			IvisQuery globalQuery) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}
