package mediator_wrapper.wrapper.abstract_types;

public class DefaultQuery {

	private String defaultQuerySelector_globalSchema;
	private String defaultQuerySelector_localSchema;

	public DefaultQuery(String defaultQuery_selector_globalSchema, String defaultQuery_selector_localSchema) {
		this.defaultQuerySelector_globalSchema = defaultQuery_selector_globalSchema;
		this.defaultQuerySelector_localSchema = defaultQuery_selector_localSchema;
	}

	public String getDefaultQuerySelector_globalSchema() {
		return defaultQuerySelector_globalSchema;
	}

	public String getDefaultQuerySelector_localSchema() {
		return defaultQuerySelector_localSchema;
	}

	@Override
	public String toString() {
		return "DefaultQuery [defaultQuerySelector_globalSchema=" + defaultQuerySelector_globalSchema
				+ ", defaultQuerySelector_localSchema=" + defaultQuerySelector_localSchema + "]";
	}

}
