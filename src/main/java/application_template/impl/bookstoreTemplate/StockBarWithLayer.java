package application_template.impl.bookstoreTemplate;

import java.awt.Color;
import java.text.DecimalFormat;

import application_template.impl.X3DOM_Constants;

/**
 * Java representation of a visualization object of a colored and extruded bar
 * with an additional thin layer that indicates a certain height.
 * 
 * @author Christian Danowski
 *
 */
public class StockBarWithLayer {

	private String id;

	private String displayName;

	private double height;

	private Color rgbColor_bar;

	private double markerHeight;

	private Color rgbColor_marker;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            unique identifier of the object
	 * @param displayName
	 *            the name that is displayed in generated visualizations
	 * @param height
	 *            the (vertical) height of the bar
	 * @param rgbColor_bar
	 *            the color of the bar
	 * @param rgbColor_marker
	 *            the color of the marker
	 * @param markerHeight
	 *            the (vertical) height of the additional small marker layer
	 */
	public StockBarWithLayer(String id, String displayName, double height, Color rgbColor_bar, Color rgbColor_marker,
			double markerHeight) {
		super();
		// replace any whitespace with an underbar and "'" with nothing
		this.id = id.replaceAll(" ", "_").replaceAll("'", "");
		this.displayName = displayName;
		this.height = height;
		this.rgbColor_bar = rgbColor_bar;
		this.rgbColor_marker = rgbColor_marker;
		this.markerHeight = markerHeight;
	}

	public String getId() {
		return id;
	}

	/**
	 * creates a String representing the specified object in X3DOM
	 * 
	 * @return
	 */
	public String writeToX3DOM() {

		/*
		 * write contents as X3DOM object
		 */
		StringBuilder builder = new StringBuilder();

		/*
		 * write complete object consisting of three subelements (bar, marker,
		 * text)
		 */
		builder.append("<transform id='" + this.id + X3DOM_Constants.ID_SUFFIX_OBJECT + "'>");

		/*
		 * write bar
		 */
		// bar has to be translated about half the height to have all bars on
		// the same ground level.
		builder.append("	<transform translation='0 " + this.height / 2 + " 0'>");
		builder.append("		<shape>");

		builder.append("			<appearance>");
		builder.append("				<material id='" + this.id + X3DOM_Constants.ID_SUFFIX_MATERIAL
				+ "' diffuseColor='" + this.getColorAsStringFrom0To1(this.rgbColor_bar) + "'>");
		builder.append("				</material>");
		builder.append("			</appearance>");

		// create a box element for the bar
		builder.append("			<box size='1 " + this.height + " 1'>");
		builder.append("			</box>");

		builder.append("		</shape>");

		builder.append("	</transform>");

		/*
		 * write marker
		 */
		builder.append("	<transform translation='0 " + this.markerHeight + " 0'>");
		builder.append("		<shape>");

		builder.append("			<appearance>");
		builder.append(
				"				<material diffuseColor='" + this.getColorAsStringFrom0To1(this.rgbColor_marker) + "'>");
		builder.append("				</material>");
		builder.append("			</appearance>");

		// create a box element for the marker
		builder.append("			<box size='1.5 0.01 1.5'>");
		builder.append("			</box>");

		builder.append("		</shape>");

		builder.append("	</transform>");

		/*
		 * write text
		 * 
		 * needs to be moved towards the user
		 */
		builder.append("	<transform translation='0 0 1.1'>");
		builder.append("		<shape>");

		builder.append("			<appearance>");
		// black text
		builder.append("				<material diffuseColor='0 0 0'>");
		builder.append("				</material>");
		builder.append("			</appearance>");

		// create a text element for the marker
		builder.append("			<text string=" + this.createDisplayNameString(this.displayName) + ">");
		builder.append("				<fontStyle size='0.8' justify='MIDDLE'>");
		builder.append("				</fontStyle>");
		builder.append("			</text>");

		builder.append("		</shape>");

		builder.append("	</transform>");

		builder.append("</transform>");

		return builder.toString();
	}

	private String createDisplayNameString(String name) {
		/*
		 * after each second word insert a line feed
		 * 
		 * in x3dom/x3d we have to create a string that separates lines by ""
		 * 
		 * e.g.: '"This is" "an example"' would be a valid String.
		 */

		StringBuilder builder = new StringBuilder();
		builder.append("'");

		String[] allWords = name.split(" ");

		int numberOfWords = allWords.length;

		int index = 0;

		while (index < numberOfWords) {
			builder.append("\"");
			
			builder.append(allWords[index]);
			
			index++;
			
			if(index < numberOfWords){
				builder.append(" " + allWords[index]);
			}

			builder.append("\"");
			
			index++;
		}

		builder.append("'");

		return builder.toString();
	}

	/**
	 * the color is stored as Java AWT {@link Color} with RGB values between
	 * 0-255. This methods transforms the values to a String representing the
	 * RGB values between 0.0 and 1.0, like "1.0 0.0 0.0" for a red color.
	 * 
	 * @param rgbColor
	 *            the color (values between 0-255)
	 * 
	 */
	private String getColorAsStringFrom0To1(Color rgbColor) {
		StringBuilder builder = new StringBuilder();
		DecimalFormat decFormat = new DecimalFormat("#.##");
		int maxValue = 255;

		int red_0_255 = rgbColor.getRed();
		int green_0_255 = rgbColor.getGreen();
		int blue_0_255 = rgbColor.getBlue();

		float red_0_1 = red_0_255 / maxValue;
		float green_0_1 = green_0_255 / maxValue;
		float blue_0_1 = blue_0_255 / maxValue;

		builder.append(red_0_1);
		builder.append(" ");
		builder.append(green_0_1);
		builder.append(" ");
		builder.append(blue_0_1);

		return builder.toString();
	}
}
