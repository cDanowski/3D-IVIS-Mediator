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

}
