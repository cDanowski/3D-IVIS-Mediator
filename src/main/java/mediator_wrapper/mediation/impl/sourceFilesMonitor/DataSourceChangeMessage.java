package mediator_wrapper.mediation.impl.sourceFilesMonitor;

public class DataSourceChangeMessage {

	private String dataSourceIdentifier;

	/*
	 * if set stores the value of the modified data instance
	 * 
	 * might not be set for all data sources!
	 */
	private String recordId;

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
	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	@Override
	public String toString() {
		return "DataSourceChangeMessage [dataSourceIdentifier=" + dataSourceIdentifier + ", recordId=" + recordId + "]";
	}

}
