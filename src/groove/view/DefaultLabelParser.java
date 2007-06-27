package groove.view;

import groove.graph.DefaultLabel;
import groove.graph.Label;

/** 
 * Parser that turns a string into a default label,
 * after testing the string for correct formatting using a 
 * callback method that can be overridden by subclasses. 
 */
public class DefaultLabelParser implements LabelParser {
	public Label parse(String text) throws FormatException {
		testFormat(text);
		return DefaultLabel.createLabel(text);
	}

	/** 
	 * Callback method to test if a given text adheres to the formatting
	 * standards of this class.
	 * To be overridden by subclasses; this implementation is empty.
	 * @param text the string to be tested
	 * @throws FormatException if <code>text</code> is not correctly
	 * formatted. The message of the exception should make clear what the
	 * mismatch is.
	 */
	protected void testFormat(String text) throws FormatException {
		// empty
	}

    /** This implementation just takes the label text. */
    public String unparse(Label label) {
        return label.text();
    }
}
