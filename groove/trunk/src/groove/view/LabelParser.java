package groove.view;

import groove.graph.Label;

/** Interface for parsers from strings to labels. */
public interface LabelParser {
	/** 
	 * Method turning a string into a label.
	 * @param text the string to be parsed into a label
	 * @return the label constructed from <code>text</code>
	 * @throws FormatException if <code>text</code> is not correctly formatted
	 * according to this parser. 
	 */
	Label parse(String text) throws FormatException;
    
    /** 
     * Method turning a label into a string from which the label can be resonstructed by this parser,
     * or <code>null</code> if no such string can be found.
     */
    String unparse(Label label);
}