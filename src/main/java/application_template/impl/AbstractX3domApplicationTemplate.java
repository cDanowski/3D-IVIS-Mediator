package application_template.impl;

/**
 * Abstract application template class which provides basic mappers based on
 * external triturus library.
 * 
 * @author Christian Danowski
 *
 */
public abstract class AbstractX3domApplicationTemplate {

	private Object colorMapper;

	private Object symbolMapper;

	private Object positionMapper;
	
	/**
	 * TODO rotationMapper, scaleMapper?
	 */

	/**
	 * 
	 * @return the colorMapper, which may be used to color an object based on a
	 *         certain attribute
	 */
	public Object getColorMapper() {
		return colorMapper;
	}

	/**
	 * Set a new colorMapper. Color mapper is used from the external triturus
	 * library.
	 * 
	 * @param colorMapper
	 *            the colorMapper, which may be used to color an object based on
	 *            a certain attribute
	 */
	public void setColorMapper(Object colorMapper) {
		this.colorMapper = colorMapper;
	}

	/**
	 * 
	 * @return the symbolMapper, which may be used to transform an object into a
	 *         visual symbol
	 */
	public Object getSymbolMapper() {
		return symbolMapper;
	}

	/**
	 * Set a new symbolMapper. Symbol mapper is used from the external triturus
	 * library.
	 * 
	 * @param symbolMapper
	 *            the symbolMapper, which may be used to transform an object
	 *            into a visual symbol
	 */
	public void setSymbolMapper(Object symbolMapper) {
		this.symbolMapper = symbolMapper;
	}

	/**
	 * 
	 * @return the positionMapper, which may be used to compute scene
	 *         coordinates for an abstract object based on (up to) three data
	 *         properties of the object.
	 */
	public Object getPositionMapper() {
		return positionMapper;
	}

	/**
	 * A position mapper defines a 3D attribute space based on (up to) three
	 * properties of a data object. The min and max values of the data
	 * properties of the data object are used to specify the mapping to scene
	 * coordinates.
	 * 
	 * @param positionMapper
	 *            the positionMapper, which may be used to compute scene
	 *            coordinates for an abstract object based on (up to) three data
	 *            properties of the object.
	 */
	public void setPositionMapper(Object positionMapper) {
		this.positionMapper = positionMapper;
	}

	/**
	 * Transforms an abstract data object into a visual symbol. The
	 * transformation mapping (e.g. color and size of the resulting shape) is
	 * based on the value of the specified property.
	 * 
	 * @param abstractData
	 *            the data object, which shall be transformed
	 * @param propertyValue
	 *            the value of the data property, which is used to compute
	 *            visual properties of the produced shape (like color, scale,
	 *            position)
	 * @return
	 */
	public Object transformToVisualSymbol(Object abstractData, double propertyValue) {

		/*
		 * TODO implement
		 */

		return null;

	};

	/**
	 * Computes and sets the color of the given visual shape using the specified
	 * propertyValue.
	 * 
	 * @param visualShape
	 *            the shape, which shall be assigned with the new color
	 * @param propertyValue
	 *            the value, which is used to compute the color using the
	 *            class's colorMapper
	 * @return the visual shape, which has been asigned with a new color
	 */
	public Object colorizeObject(Object visualShape, double propertyValue){
		/*
		 * TODO implement
		 */
		
		return null;
	};

}
