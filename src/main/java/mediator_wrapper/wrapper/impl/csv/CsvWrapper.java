package mediator_wrapper.wrapper.impl.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.DocumentException;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.common.processor.RowProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

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

	private Map<String, Integer> csvHeaderIndicesMap;
	private String[] headers;

	public CsvWrapper(String pathToSourcefile, String pathToShadowCopyFile, String localSchemaMappingLocation)
			throws DocumentException {
		super(pathToSourcefile, pathToShadowCopyFile, localSchemaMappingLocation);
	}

	@Override
	public List<IvisObject> queryData(IvisQuery queryAgainstGlobalSchema, List<String> subquerySelectors_globalSchema)
			throws Exception {
		Map<String, String> subqueries_global_and_local_schema = transformIntoGlobalAndLocalSubqueries(
				queryAgainstGlobalSchema, subquerySelectors_globalSchema);

		IvisQuery localQuery = (IvisQuery) this.transformToLocalQuery(queryAgainstGlobalSchema);

		return this.executeLocalQuery(localQuery, subqueries_global_and_local_schema, queryAgainstGlobalSchema);
	}

	@Override
	public boolean modifyDataInstance(RuntimeModificationMessage modificationMessage) throws IOException {

		boolean hasModified = false;

		IvisQuery globalQuery = modificationMessage.getQuery();

		IvisQuery localQuery = (IvisQuery) this.transformToLocalQuery(globalQuery);

		hasModified = executeDataInstanceModification(modificationMessage, localQuery);

		return hasModified;
	}

	@Override
	public List<IvisObject> onSourceFileChanged(IvisQuery query_globalSchema,
			List<String> subquerySelectors_globalSchema) throws IOException {
		IvisQuery query_localSchema = (IvisQuery) this.transformToLocalQuery(query_globalSchema);

		Map<String, String> subqueries_global_and_local_schema = this
				.transformIntoGlobalAndLocalSubqueries(query_globalSchema, subquerySelectors_globalSchema);

		String elementName = this.getNameFromXPathExpression(query_globalSchema.getSelector());

		List<String[]> records = getRecordsForFilter(query_localSchema, this.getSourceFile());

		/*
		 * shadow copy contains all elements BEFORE the modification happened!
		 * 
		 * hence, we can compare allRecords against the shadowCopyRecords to
		 * identify modifications
		 */
		List<String[]> records_shadowCopy = getRecordsForFilter(query_localSchema, this.getShadowCopyFile());

		List<IvisObject> modifiedInstances = identifyModifiedOrNewInstances(records, records_shadowCopy,
				subqueries_global_and_local_schema, elementName);

		return modifiedInstances;
	}

	private List<String[]> getRecordsForFilter(IvisQuery query_localSchema, File file)
			throws UnsupportedEncodingException, FileNotFoundException {
		/*
		 * parse document
		 */
		CsvRecords csvRecords = this.parseAllCsvRecords(file);

		List<String[]> records = csvRecords.getRows();

		/*
		 * for each row, check filter statements
		 * 
		 * if record doe not pass the filters, then remove it
		 */
		ListIterator<String[]> listIterator = records.listIterator();
		while (listIterator.hasNext()) {
			String[] record = listIterator.next();

			/*
			 * check filters
			 */
			if (!this.passesFilters(record, query_localSchema, this.csvHeaderIndicesMap))
				listIterator.remove();
		}
		return records;
	}

	private List<IvisObject> identifyModifiedOrNewInstances(List<String[]> records, List<String[]> records_shadowCopy,
			Map<String, String> subqueries_global_and_local_schema, String elementName) throws IOException {
		List<IvisObject> modifiedObjects = new ArrayList<IvisObject>();

		int idHeaderIndex = 0;

		Map<String, String[]> idForCsvRecordMap = createIdForCsvRecordMap(records_shadowCopy, idHeaderIndex);

		/*
		 * for each record, find the corresponding shadowCopy
		 * 
		 * if none is found --> new object!
		 * 
		 * if existing is found --> compare each property for equality
		 */

		for (String[] csvRecord : records) {
			String recordId = csvRecord[idHeaderIndex];

			if (!idForCsvRecordMap.containsKey(recordId)) {
				/*
				 * new object
				 */
				modifiedObjects.add(createIvisObject(csvRecord, subqueries_global_and_local_schema, elementName,
						csvHeaderIndicesMap));
			}

			else {
				// compare to shadow copy

				String[] shadowCopy = idForCsvRecordMap.get(recordId);

				if (hasModifiedProperties(csvRecord, shadowCopy)) {
					/*
					 * modified object
					 */
					modifiedObjects.add(createIvisObject(csvRecord, subqueries_global_and_local_schema, elementName,
							csvHeaderIndicesMap));
				}
			}
		}

		/*
		 * now create new shadow copy file from current main file!
		 */
		this.replaceShadowCopy();

		return modifiedObjects;

	}

	private boolean hasModifiedProperties(String[] csvRecord, String[] shadowCopy) {
		for (int i = 0; i < csvRecord.length; i++) {
			if (!csvRecord[i].equals(shadowCopy[i]))
				return true;
		}
		return false;
	}

	private Map<String, String[]> createIdForCsvRecordMap(List<String[]> records_shadowCopy, int idHeaderIndex) {
		Map<String, String[]> idForCsvRecordMap = new HashMap<>();

		for (String[] csvRecord : records_shadowCopy) {
			String idValue = csvRecord[idHeaderIndex];

			idForCsvRecordMap.put(idValue, csvRecord);
		}

		return idForCsvRecordMap;
	}

	private boolean executeDataInstanceModification(RuntimeModificationMessage modificationMessage,
			IvisQuery localQuery) throws UnsupportedEncodingException, FileNotFoundException, IOException {

		String propertySelector_localSchema = this.getSchemaMapping()
				.get(modificationMessage.getPropertySelector_globalSchema());

		String elementName = this.getNameFromXPathExpression(modificationMessage.getQuery().getSelector());

		boolean hasModified = false;

		try {
			/*
			 * use univocity parser to parse CSV file
			 */
			CsvRecords csvRecords = this.parseAllCsvRecords(this.getSourceFile());

			List<String[]> allRecords = csvRecords.getRows();

			allRecords = findAndModifyInstance(modificationMessage, localQuery, propertySelector_localSchema,
					elementName, allRecords);

			/*
			 * write back
			 */
			persistRecords(allRecords);

			hasModified = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hasModified;
	}

	private List<String[]> findAndModifyInstance(RuntimeModificationMessage modificationMessage, IvisQuery localQuery,
			String propertySelector_localSchema, String elementName, List<String[]> allRecords) {
		for (String[] currentRecord : allRecords) {
			if (this.passesFilters(currentRecord, localQuery, this.csvHeaderIndicesMap)) {
				/*
				 * modify row
				 * 
				 * +
				 * 
				 * create IvisObject from Row
				 */

				currentRecord = modifyRecord(currentRecord, propertySelector_localSchema,
						modificationMessage.getNewPropertyValue());

				break;
			}
		}

		return allRecords;
	}

	private void persistRecords(List<String[]> allRecords) throws IOException {

		CsvWriterSettings writerSettings = new CsvWriterSettings();

		CsvWriter writer = new CsvWriter(new FileWriter(this.getSourceFile()), writerSettings);

		// Write the record headers of this file
		writer.writeHeaders(this.headers);

		// Here we just tell the writer to write everything and close the given
		// output Writer instance.
		writer.writeStringRowsAndClose(allRecords);

	}

	private String[] modifyRecord(String[] currentRecord, String selector_local, Object newPropertyValue) {
		int recordIndex = csvHeaderIndicesMap.get(selector_local);

		currentRecord[recordIndex] = String.valueOf(newPropertyValue);

		return currentRecord;
	}

	private CsvParserSettings configureParserSettings(RowProcessor processor) {
		CsvParserSettings parserSettings = new CsvParserSettings();

		// configure the parser to automatically detect what line
		// separator sequence is in the input
		parserSettings.setLineSeparatorDetectionEnabled(true);

		parserSettings.setRowProcessor(processor);

		// Let's consider the first parsed row as the headers of each column in
		// the file.
		parserSettings.setHeaderExtractionEnabled(true);

		return parserSettings;
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
	protected Map<String, String> transformIntoGlobalAndLocalSubqueries(IvisQuery globalQuery,
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
	protected List<IvisObject> executeLocalQuery(Object localQuery,
			Map<String, String> subqueries_global_and_local_schema, IvisQuery globalQuery) throws Exception {
		// we know that in this class the localQuery is of type String!
		return this.retrieveDataFromCsv((IvisQuery) localQuery, subqueries_global_and_local_schema, globalQuery);
	}

	private List<IvisObject> retrieveDataFromCsv(IvisQuery localQuery,
			Map<String, String> subqueries_global_and_local_schema, IvisQuery globalQuery)
			throws UnsupportedEncodingException, FileNotFoundException {

		List<IvisObject> ivisObjects = new ArrayList<IvisObject>();

		String elementName = this.getNameFromXPathExpression(globalQuery.getSelector());

		/*
		 * parse document
		 */
		CsvRecords csvRecords = this.parseAllCsvRecords(this.getSourceFile());

		List<String[]> allRecords = csvRecords.getRows();

		/*
		 * for each row, check filter statements
		 * 
		 * if record passes the filters, then create an IvisObject from it
		 */
		for (String[] record : allRecords) {
			/*
			 * check filters
			 */
			if (this.passesFilters(record, localQuery, this.csvHeaderIndicesMap))
				ivisObjects.add(this.createIvisObject(record, subqueries_global_and_local_schema, elementName,
						this.csvHeaderIndicesMap));

		}

		return ivisObjects;
	}

	private IvisObject createIvisObject(String[] record, Map<String, String> subqueries_global_and_local_schema,
			String elementName, Map<String, Integer> csvHeaderIndicesMap) {
		List<AttributeValuePair> attributeValuePairs = new ArrayList<AttributeValuePair>(
				subqueries_global_and_local_schema.size());

		Iterator<Entry<String, String>> subqueryIterator = subqueries_global_and_local_schema.entrySet().iterator();

		while (subqueryIterator.hasNext()) {
			try {
				Entry<String, String> subqueryEntry = subqueryIterator.next();

				String name = getNameFromXPathExpression(subqueryEntry.getKey());

				/*
				 * in case of CSV, the value represents the header of the
				 * corresponding column
				 */
				Integer recordIndex = csvHeaderIndicesMap.get(subqueryEntry.getValue());
				Object value = record[recordIndex];

				attributeValuePairs.add(new AttributeValuePair(name, value));
			} catch (Exception e) {
				continue;
			}
		}

		addWrapperReference(attributeValuePairs);

		IvisObject newIvisObject = new IvisObject(elementName, attributeValuePairs);

		return newIvisObject;
	}

	private boolean passesFilters(String[] record, IvisQuery localQuery, Map<String, Integer> csvHeaderIndicesMap) {

		/*
		 * if there are no filters specified just return true!
		 */
		if (localQuery.getFilters() == null || localQuery.getFilters().size() == 0)
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
			if (objectValue.equalsIgnoreCase(String.valueOf(filterValue)))
				passesFilter = true;
			break;
		case GREATER_THAN:
			try {
				double numericObjectValue = Double.parseDouble(objectValue);
				double numericFilterValue = Double.parseDouble(String.valueOf(filterValue));
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
				double numericFilterValue = Double.parseDouble(String.valueOf(filterValue));
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
				double numericFilterValue = Double.parseDouble(String.valueOf(filterValue));
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
				double numericFilterValue = Double.parseDouble(String.valueOf(filterValue));
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
			if (!objectValue.equalsIgnoreCase(String.valueOf(filterValue)))
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

	private CsvRecords parseAllCsvRecords(File file) throws UnsupportedEncodingException, FileNotFoundException {
		// A RowListProcessor stores each parsed row in a List.
		RowListProcessor rowProcessor = new RowListProcessor();

		CsvParserSettings parserSettings = configureParserSettings(rowProcessor);

		// creates a parser instance with the given settings
		CsvParser parser = new CsvParser(parserSettings);

		// the 'parse' method will parse the file and delegate each parsed row
		// to the RowProcessor
		parser.parse(getReader(file));

		// get the parsed records from the RowListProcessor here.
		List<String[]> rows = rowProcessor.getRows();

		this.headers = rowProcessor.getHeaders();

		/*
		 * create maps to map each filter column name and subquery selector
		 * (column) name to the array index
		 */
		this.csvHeaderIndicesMap = makeCsvHeaderIndicesMap(headers);

		return new CsvRecords(this.headers, rows);
	}

	private Reader getReader(File resource) throws UnsupportedEncodingException, FileNotFoundException {

		return new InputStreamReader(new FileInputStream(resource), "UTF-8");

	}

}
