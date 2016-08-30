package mediator_wrapper.mediation.impl.dataSourceMonitor;

import java.util.List;

public class DataSourceChangeMessage {

	private String dataSourceIdentifier;

	/*
	 * if set stores the value of the modified data instance
	 * 
	 * might not be set for all data sources!
	 */
	private List<String> recordIds;

	public String getDataSourceIdentifier() {
		return dataSourceIdentifier;
	}

	public void setDataSourceIdentifier(String dataSourceIdentifier) {

		this.dataSourceIdentifier = dataSourceIdentifier;
	}

	/**
	 * If set, stores the value of the modified data instance.
	 * 
	 * But might not be set for all data sources!
	 * 
	 * @return
	 */
	public List<String> getRecordIds() {
		return recordIds;
	}

	public void setRecordIds(List<String> recordIds) {
		this.recordIds = recordIds;
	}

	@Override
	public String toString() {
		return "DataSourceChangeMessage [dataSourceIdentifier=" + dataSourceIdentifier + ", recordIds=" + recordIds
				+ "]";
	}

}
