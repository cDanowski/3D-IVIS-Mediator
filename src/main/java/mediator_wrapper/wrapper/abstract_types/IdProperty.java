package mediator_wrapper.wrapper.abstract_types;

public class IdProperty {
	
	private String selector_globalSchema;
	
	private String selector_localSchema;

	public IdProperty(String selector_globalSchema, String selector_localSchema) {
		super();
		this.selector_globalSchema = selector_globalSchema;
		this.selector_localSchema = selector_localSchema;
	}

	public String getSelector_globalSchema() {
		return selector_globalSchema;
	}

	public String getSelector_localSchema() {
		return selector_localSchema;
	}

	@Override
	public String toString() {
		return "IdProperty [selector_globalSchema=" + selector_globalSchema + ", selector_localSchema="
				+ selector_localSchema + "]";
	}

}
