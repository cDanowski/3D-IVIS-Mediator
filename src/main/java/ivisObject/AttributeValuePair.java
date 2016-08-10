package ivisObject;

/**
 * A generic class to create a pair of attribute name and value
 * @author Christian Danowski
 *
 */
public class AttributeValuePair {
	
	private String name;
	
	private Object value;

	public AttributeValuePair(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "AttributeValuePair [name=" + name + ", value=" + value + "]";
	}

}
