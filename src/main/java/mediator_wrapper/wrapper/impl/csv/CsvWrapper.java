package mediator_wrapper.wrapper.impl.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.DocumentException;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import controller.runtime.modify.RuntimeModificationMessage;
import ivisObject.AttributeValuePair;
import ivisObject.IvisObject;
import ivisQuery.FilterStrategy;
import ivisQuery.FilterType;
import ivisQuery.IvisFilterForQuery;
import ivisQuery.IvisQuery;
import mediator_wrapper.wrapper.IvisWrapperInterface;
import mediator_wrapper.wrapper.abstract_types.AbstractIvisFileWrapper;

/**
 * Wrapper to manage access to CSV files.
 * 
 * @author Christian Danowski
 *
 */
public class CsvWrapper extends AbstractIvisFileWrapper implements IvisWrapperInterface {

	

	public CsvWrapper(String pathToSourcefile, String pathToSchemaMappingFile) throws DocumentException {
		super(pathToSourcefile, pathToSchemaMappingFile);
	}

	@Override
	public List<IvisObject> queryData(IvisQuery queryAgainstGlobalSchema, List<String> subquerySelectors_globalSchema)
			throws Exception {
		Map<String, String> subqueries_global_and_local_schema = transformIntoLocalSubqueries(queryAgainstGlobalSchema,
				subquerySelectors_globalSchema);

		IvisQuery localQuery = (IvisQuery) this.transformToLocalQuery(queryAgainstGlobalSchema);

		return this.executeLocalQuery(localQuery, subqueries_global_and_local_schema, queryAgainstGlobalSchema);
	}

	@Override
	public Object applyModification(RuntimeModificationMessage modificationMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object transformToLocalQuery(IvisQuery globalQuery) {
		String selector_globalSchema = globalQuery.getSelector();

		String selector_localSchema = this.getSchemaMapping().get(selector_globalSchema);

		/*
		 * transform the selectors of filters to local schema
		 */
		List<IvisFilterForQuery> globalFilters = globalQuery.getFilters();

		List<IvisFilterForQuery> localFilters = new ArrayList<IvisFilterForQuery>();
		
		if (globalFilters != null && globalFilters.size() > 0) {

			for (IvisFilterForQuery globalFilter : globalFilters) {
				String filterSelector_globalSchema = globalFilter.getSelector();
				String filterSelector_localSchema = this.getSchemaMapping().get(filterSelector_globalSchema);

				IvisFilterForQuery localFilter = new IvisFilterForQuery();
				localFilter.setSelector(filterSelector_localSchema);
				localFilter.setFilterType(globalFilter.getFilterType());
				localFilter.setFilterValue(globalFilter.getFilterValue());

				localFilters.add(localFilter);
			}
		}

		IvisQuery localQuery = new IvisQuery();
		localQuery.setFilters(localFilters);
		localQuery.setFilterStrategy(globalQuery.getFilterStrategy());
		localQuery.setSelector(selector_localSchema);

		return localQuery;
	}

	@Override
	protected Map<String, String> transformIntoLocalSubqueries(IvisQuery globalQuery,
			List<String> subquerySelectors_globalSchema) {

		Map<String, String> subqueries_global_and_local_schema = new HashMap<String, String>();

		for (String subquery_globalSchema : subquerySelectors_globalSchema) {

			/*
			 * for a CSV file the subquery_localSchema represents the header of
			 * the CSV column
			 * 
			 * hence, we do not have to do anything else here
			 */
			String subquery_localSchema = this.getSchemaMapping().get(subquery_globalSchema);

			subqueries_global_and_local_schema.put(subquery_globalSchema, subquery_localSchema);
		}

		return subqueries_global_and_local_schema;
	}

	@Override
	protected List<IvisObject> executeLocalQuery(Object localQuery, Map<String, String> subqueries_global_and_local_schema,
			IvisQuery globalQuery) throws Exception {
		// we know that in this class the localQuery is of type String!
		return this.retrieveDataFromCsv((IvisQuery) localQuery, subqueries_global_and_local_schema, globalQuery);
	}

	private List<IvisObject> retrieveDataFromCsv(IvisQuery localQuery, Map<String, String> subqueries_global_and_local_schema,
			IvisQuery globalQuery) throws UnsupportedEncodingException, FileNotFoundException {

		List<IvisObject> ivisObjects = new ArrayList<IvisObject>();

		String elementName = this.getNameFromXPathExpression(globalQuery.getSelector());

		/*
		 * parse document
		 */
		CsvRecords csvRecords = this.parseAllCsvRecords();

		String[] headers = csvRecords.getHeaders();

		List<String[]> allRecords = csvRecords.getRows();

		/*
		 * create maps to map each filter column name and subquery selector
		 * (column) name to the array index
		 */
		Map<String, Integer> csvHeaderIndicesMap = makeCsvHeaderIndicesMap(headers);

		/*
		 * for each row, check filter statements
		 * 
		 * if record passes the filters, then create an IvisObject from it
		 */
		for (String[] record : allRecords) {
			/*
			 * check filters
			 */
			if (this.passesFilters(record, localQuery, csvHeaderIndicesMap))
				ivisObjects.add(
						this.createIvisObject(record, subqueries_global_and_local_schema, elementName, csvHeaderIndicesMap));

		}

		return ivisObjects;
	}

	private IvisObject createIvisObject(String[] record, Map<String, String> subqueries_global_and_local_schema, String elementName,
			Map<String, Integer> csvHeaderIndicesMap) {
		List<AttributeValuePair> attributeValuePairs = new ArrayList<AttributeValuePair>(
				subqueries_global_and_local_schema.size());

		Iterator<Entry<String, String>> subqueryIterator = subqueries_global_and_local_schema.entrySet().iterator();
		
		while(subqueryIterator.hasNext()){
			Entry<String, String> subqueryEntry = subqueryIterator.next();
			
			String name = getNameFromXPathExpression(subqueryEntry.getKey());

			/*
			 * in case of CSV, the value represents the header of the corresponding column
			 */
			Integer recordIndex = csvHeaderIndicesMap.get(subqueryEntry.getValue());
			Object value = record[recordIndex];

			attributeValuePairs.add(new AttributeValuePair(name, value));
		}
		
		addWrapperReference(attributeValuePairs);

		IvisObject newIvisObject = new IvisObject(elementName, attributeValuePairs);

		return newIvisObject;
	}

	private boolean passesFilters(String[] record, IvisQuery localQuery, Map<String, Integer> csvHeaderIndicesMap) {
		
		/*
		 * if there are no filters specified just return true!
		 */
		if(localQuery.getFilters() == null || localQuery.getFilters().size() == 0)
			return true;
		
		/*
		 * else check filters!
		 */
		
		boolean passesFilters = false;

		List<IvisFilterForQuery> localFilters = localQuery.getFilters();
		FilterStrategy filterStrategy = localQuery.getFilterStrategy();

		for (IvisFilterForQuery localFilter : localFilters) {
			/*
			 * filter selector is equal to one of the headers of the CSV record!
			 */
			String filterSelector_local = localFilter.getSelector();
			FilterType filterType = localFilter.getFilterType();
			Object filterValue = localFilter.getFilterValue();

			int recordIndex = csvHeaderIndicesMap.get(filterSelector_local);

			String objectValue = record[recordIndex];

			if (this.passesFilter(objectValue, filterValue, filterType)) {
				passesFilters = true;
				/*
				 * if filter strategy is set to OR, then just one filter must be
				 * passed.
				 * 
				 * Hence, we can skip other filters and return true!
				 */
				if (filterStrategy.equals(FilterStrategy.OR))
					break;

			} else {
				passesFilters = false;

				/*
				 * if filter strategy is set to AND, then if one filter fails,
				 * we can return false, since every filter must be passed, which
				 * is not the case
				 * 
				 * Hence, we can skip other filters and return false!
				 */
				if (filterStrategy.equals(FilterStrategy.AND))
					break;
			}
		}

		return passesFilters;
	}

	private boolean passesFilter(String objectValue, Object filterValue, FilterType filterType) {
		boolean passesFilter = false;

		switch (filterType) {
		case EQUAL:
			if (objectValue.equalsIgnoreCase(String.valueOf( filterValue)))
				passesFilter = true;
			break;
		case GREATER_THAN:
			try {
				double numericObjectValue = Double.parseDouble(objectValue);
				double numericFilterValue = Double.parseDouble( String.valueOf( filterValue));
				if (numericObjectValue > numericFilterValue)
					passesFilter = true;
			} catch (Exception e) {
				e.printStackTrace();
				/*
				 * do nothing, passesFilter remains false
				 */
			}
			break;
		case GREATER_THAN_OR_EQUAL_TO:
			try {
				double numericObjectValue = Double.parseDouble(objectValue);
				double numericFilterValue = Double.parseDouble( String.valueOf( filterValue));
				if (numericObjectValue >= numericFilterValue)
					passesFilter = true;
			} catch (Exception e) {
				e.printStackTrace();
				/*
				 * do nothing, passesFilter remains false
				 */
			}
			break;
		case LESS_THAN:
			try {
				double numericObjectValue = Double.parseDouble(objectValue);
				double numericFilterValue = Double.parseDouble( String.valueOf( filterValue));
				if (numericObjectValue < numericFilterValue)
					passesFilter = true;
			} catch (Exception e) {
				e.printStackTrace();
				/*
				 * do nothing, passesFilter remains false
				 */
			}
			break;
		case LESS_THAN_OR_EQUAL_TO:
			try {
				double numericObjectValue = Double.parseDouble(objectValue);
				double numericFilterValue = Double.parseDouble( String.valueOf( filterValue));
				if (numericObjectValue <= numericFilterValue)
					passesFilter = true;
			} catch (Exception e) {
				e.printStackTrace();
				/*
				 * do nothing, passesFilter remains false
				 */
			}
			break;
		case NOT_EQUAL:
			if (!objectValue.equalsIgnoreCase(String.valueOf( filterValue)))
				passesFilter = true;
			break;

		default:
			break;
		}

		return passesFilter;
	}

	private Map<String, Integer> makeCsvHeaderIndicesMap(String[] headers) {
		Map<String, Integer> csvHeaderIndicesMap = new HashMap<String, Integer>();

		for (int i = 0; i < headers.length; i++) {
			String header = headers[i];

			csvHeaderIndicesMap.put(header, i);
		}

		return csvHeaderIndicesMap;
	}

	private CsvRecords parseAllCsvRecords() throws UnsupportedEncodingException, FileNotFoundException {
		/*
		 * use univocity parser to parse CSV file
		 */
		CsvParserSettings parserSettings = new CsvParserSettings();

		// configure the parser to automatically detect what line
		// separator sequence is in the input
		parserSettings.setLineSeparatorDetectionEnabled(true);

		// A RowListProcessor stores each parsed row in a List.
		RowListProcessor rowProcessor = new RowListProcessor();

		parserSettings.setRowProcessor(rowProcessor);

		// Let's consider the first parsed row as the headers of each column in
		// the file.
		parserSettings.setHeaderExtractionEnabled(true);

		// creates a parser instance with the given settings
		CsvParser parser = new CsvParser(parserSettings);

		// the 'parse' method will parse the file and delegate each parsed row
		// to the RowProcessor
		parser.parse(getReader(this.getSourceFile()));

		// get the parsed records from the RowListProcessor here.
		String[] headers = rowProcessor.getHeaders();
		List<String[]> rows = rowProcessor.getRows();

		return new CsvRecords(headers, rows);
	}

	private Reader getReader(File resource) throws UnsupportedEncodingException, FileNotFoundException {

		return new InputStreamReader(new FileInputStream(resource), "UTF-8");

	}

}
