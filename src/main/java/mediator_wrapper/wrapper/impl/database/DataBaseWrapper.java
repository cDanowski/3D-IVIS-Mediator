package mediator_wrapper.wrapper.impl.database;

import java.util.List;
import java.util.Map;

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
	public boolean modifyDataInstance(RuntimeModificationMessage modificationMessage,
			List<String> subquerySelectors_globalSchema) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Object transformToLocalQuery(IvisQuery globalQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, String> transformIntoGlobalAndLocalSubqueries(IvisQuery globalQuery,
			List<String> subquerySelectors_globalSchema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<IvisObject> executeLocalQuery(Object localQuery,
			Map<String, String> subquerySelectors_global_and_local_schema, IvisQuery globalQuery) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
