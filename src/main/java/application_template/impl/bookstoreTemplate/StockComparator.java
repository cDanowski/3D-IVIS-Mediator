package application_template.impl.bookstoreTemplate;

import java.util.Comparator;

import ivisObject.IvisObject;

/**
 * Expects objects to have an Integer attribute called "stock" to be compared to
 * each other.
 * 
 * @author Christian Danowski
 *
 */
public class StockComparator implements Comparator<IvisObject> {

	/**
	 * {@inheritDoc} <br/>
	 * <br/>
	 * 
	 * ----------------------------------- <br/>
	 * <br/>
	 * <b>Expects objects to have an Integer attribute called "stock" to be
	 * compared to each other.</b>
	 */
	@Override
	public int compare(IvisObject o1, IvisObject o2) {
		// TODO Auto-generated method stub

		Integer stock1 = Integer.parseInt(String.valueOf(o1.getValueForAttribute(BookstoreApplicationConstants.BOOK_STOCK)));
		Integer stock2 = Integer.parseInt(String.valueOf(o2.getValueForAttribute(BookstoreApplicationConstants.BOOK_STOCK)));
		int compareResult = stock1.compareTo(stock2);
		return compareResult;
	}

}
