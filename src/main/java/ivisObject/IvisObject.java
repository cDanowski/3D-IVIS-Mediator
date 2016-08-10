package ivisObject;

import java.util.List;

/**
 * Java representation of generic IVIS objects retrieved by wrappers.
 * 
 * @author Christian Danowski
 *
 */
public class IvisObject {

	List<AttributeValuePair> attributeValuePairs;
	private String elementName;

	public IvisObject(String elementName, List<AttributeValuePair> attributeValuePairs) {
		super();
		this.elementName = elementName;
		this.attributeValuePairs = attributeValuePairs;
	}

	public List<AttributeValuePair> getAttributeValuePairs() {
		return attributeValuePairs;
	}

	public String getElementName() {
		return elementName;
	}

	public Object getValueForAttribute(String attributeName) {
		
		for (AttributeValuePair attributeValuePair : attributeValuePairs) {
			if (attributeValuePair.getName().equalsIgnoreCase(attributeName))
				return attributeValuePair.getValue();
		}
		
		/*
		 * if nothing has been found return null
		 */
		return null;
	}

	@Override
	public String toString() {
		return "IvisObject [attributeValuePairs=" + attributeValuePairs + ", elementName=" + elementName + "]";
	}

}
