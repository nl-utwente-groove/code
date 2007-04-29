/* $Id: ContentParser.java,v 1.1 2007-04-29 09:22:24 rensink Exp $ */
package groove.view.aspect;

import groove.view.FormatException;

/**
 * Interface for a parser that can translate a string to an aspect value
 * content of a given (generic) type and back.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface ContentParser<C> {
	/**
	 * Parses a given string value and returns the encoded content.
	 * @param value the string value to be converted
	 * @throws FormatException if <code>value</code> is not correctly formatted
	 * according to the rules of this parser. 
	 */
	C toContent(String value) throws FormatException;
	
	/** 
	 * Gives a string description of a given content.
	 * This is the inverse operation of {@link #toContent(String)}.
	 * @param content the content to be converted to a string.
	 * @return a string description of <code>content</code>
	 */
	String toString(C content);
}
