package application_template.impl.bookstoreTemplate;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import application_template.ApplicationTemplateInterface;
import application_template.impl.VisualizationObject;
import ivisObject.IvisObject;

/**
 * Application template, which visualizes book-elements as colored columns.
 * Color depends on attribute value of "stock"-attribute.
 * 
 * @author Christian Danowski
 *
 */
public class BookstoreApplicationTemplate implements ApplicationTemplateInterface {

	private String uniqueApplicationId = "bookstoreApplicationTemplate";

	@Override
	public String getUniqueIdentifier() {
		return uniqueApplicationId;
	}

	/**
	 * {@inheritDoc}<br/>
	 * <br/>
	 * 
	 * ---------------------------------- <br/>
	 * <br/>
	 * 
	 * {@link BookstoreApplicationTemplate} creates an X3DOM scene description
	 * (an html String)
	 */
	@Override
	public String createInitialScene(List<IvisObject> dataToVisualize) {

		List<StockBarWithLayer> visObjects = transformIntoVisualizationObjects(dataToVisualize);

		return createCompleteX3DOMScene(visObjects);

	}

	/**
	 * Transform all objects into instances of {@link StockBarWithLayer}.
	 * 
	 * @param dataToVisualize
	 * @return
	 */
	private List<StockBarWithLayer> transformIntoVisualizationObjects(List<IvisObject> dataToVisualize) {
		List<StockBarWithLayer> visObjects = new ArrayList<StockBarWithLayer>();
		
		// sort elements depending on their current stock value
		Collections.sort(dataToVisualize, new StockComparator());

		// greyish marker color
		Color markerColor = new Color(127, 127, 127);

		for (IvisObject ivisObject : dataToVisualize) {
			StockBarWithLayer visObject = transformToVisualizationObject(markerColor, ivisObject);

			visObjects.add(visObject);
		}

		return visObjects;
	}

	private StockBarWithLayer transformToVisualizationObject(Color markerColor, IvisObject ivisObject) {
		/*
		 * extract all information according to the global schema and create a
		 * new visualization object
		 */
		int stock = Integer.parseInt(String.valueOf(ivisObject.getValueForAttribute(BookstoreApplicationConstants.BOOK_STOCK)));
		int reorderLevel = Integer.parseInt(String.valueOf(ivisObject.getValueForAttribute(BookstoreApplicationConstants.BOOK_REORDER_LEVEL)));
		boolean isReordered = Boolean.parseBoolean(String.valueOf(ivisObject.getValueForAttribute(BookstoreApplicationConstants.BOOK_REORDERED)));
		String title = String.valueOf(ivisObject.getValueForAttribute(BookstoreApplicationConstants.BOOK_TITLE));

		/*
		 * determine color depending on stock and reordered attribute values
		 * 
		 * if (stock > reorderLevel) then green
		 * 
		 * else if (stock <= reorderlevel) and (reordered = false) then red
		 * 
		 * else if (stock <= reorderlevel) and (reordered = true) then blue
		 */
		// instantiate as green
		Color barColor = new Color(0, 255, 0);

		if (stock <= reorderLevel) {
			if (isReordered)
				barColor = new Color(0, 0, 255);
			else
				barColor = new Color(255, 0, 00);
		}

		StockBarWithLayer visObject = new StockBarWithLayer(title, title, stock, barColor, markerColor, reorderLevel);
		return visObject;
	}

	private String createCompleteX3DOMScene(List<StockBarWithLayer> visObjects) {
		StringBuilder stringBuilder = new StringBuilder();

		// start X3DOM scene definition
		stringBuilder.append("<x3d showLog='false'>");
		stringBuilder.append("<scene>");

		// write scene elements

		/*
		 * write all objects TODO translation and category????
		 */

		int numberOfObjects = visObjects.size();

		// column translation
		int translation_x_positive = 7;
		int translation_x_negative = -7;
		// row translation
		int translation_y = 0;
		
		// rotation about 90°
		String rotation_z_left = "0 0 1 1.57";
		String rotation_z_right = "0 0 1 -1.57";

		int translationIncrement = 5;
		
		boolean isEvenIndex = false;

		for (StockBarWithLayer visObject : visObjects) {
			
			if(isEvenIndex){
				// translation to right of the scene
				stringBuilder.append("<transform translation='" + translation_x_positive + " " + translation_y + " 0 '>");
				stringBuilder.append("	<transform rotation='" + rotation_z_right + "' >");
				stringBuilder.append(visObject.writeToX3DOM());
				stringBuilder.append("	</transform>");
				stringBuilder.append("</transform>");
				
				// after each second object increase y-translation
				translation_y = translation_y + translationIncrement;
				isEvenIndex = false;
			}
			else{
				// translation to left of the scene
				stringBuilder.append("<transform translation='" + translation_x_negative + " " + translation_y + " 0 '>");
				stringBuilder.append("	<transform rotation='" + rotation_z_left + "' >");
				stringBuilder.append(visObject.writeToX3DOM());
				stringBuilder.append("	</transform>");
				stringBuilder.append("</transform>");
				
				isEvenIndex = true;
			}

		}

		// end X3DOM scene definition
		stringBuilder.append("</scene>");
		stringBuilder.append("</x3d>");

		return stringBuilder.toString();
	}

	/**
	 * {@inheritDoc}<br/>
	 * <br/>
	 * 
	 * ---------------------------------- <br/>
	 * <br/>
	 * 
	 * {@link BookstoreApplicationTemplate} creates an X3DOM scene node (as
	 * String) that can be integrated into an existing X3DOM scene
	 */
	@Override
	public List<VisualizationObject> visualizeData_runtime(List<IvisObject> dataToVisualize) {

		List<StockBarWithLayer> visObjects = transformIntoVisualizationObjects(dataToVisualize);

		return createX3DOMObjects(visObjects);
	}

	private List<VisualizationObject> createX3DOMObjects(List<StockBarWithLayer> visObjects) {
		List<VisualizationObject> visualizationObjectsForClient= new ArrayList<VisualizationObject>();
		
		for (StockBarWithLayer visObject : visObjects) {

			String x3domString = visObject.writeToX3DOM();
			
			String id = visObject.getId();
			
			/*
			 * create new object mapping the id to the x3dom string reporesenting that object
			 * 
			 * client will know what to do with it!
			 */
			VisualizationObject newObjectForCLient = new VisualizationObject(id, x3domString);
			
			visualizationObjectsForClient.add(newObjectForCLient);
		}

		return visualizationObjectsForClient;
	}

}
