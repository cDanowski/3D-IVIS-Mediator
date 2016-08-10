package mediator_wrapper.wrapper.impl.csv;

import java.util.List;

/**
 * Helper class to represent all records of a CSV file including headers
 * 
 * @author Christian Danowski
 *
 */
public class CsvRecords {

	private String[] headers;
	private List<String[]> rows;

	public CsvRecords(String[] headers, List<String[]> rows) {
		this.headers = headers;

		this.rows = rows;
	}

	public String[] getHeaders() {
		return headers;
	}

	public List<String[]> getRows() {
		return rows;
	}

}
