/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: NumberedAspectValue.java,v 1.2 2008-01-30 09:31:33 iovka Exp $
 */
package groove.view.aspect;

import groove.view.FormatException;

/**
 * Aspect value encoding wrapping a number value.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NumberedAspectValue extends ContentAspectValue<Integer> {
	/**
	 * Constructs a new nesting level-containing aspect value.
	 * @param name the aspect value name
	 * @throws FormatException if <code>name</code> is an already existing aspect value
	 */
	public NumberedAspectValue(Aspect aspect, String name) throws FormatException {
		super(aspect, name);
	}
	
	/** Creates an instance of a given nesting aspect value, with a given level. */
	NumberedAspectValue(NumberedAspectValue original, Integer number) {
		super(original, number);
	}

	@Override
	public ContentAspectValue<Integer> newValue(String value) throws FormatException {
		return new NumberedAspectValue(this, parser.toContent(value));
	}

	/** 
	 * Indicates if a given character is allowed in level names.
	 * Currently allowed are: letters, digits, currency symbols, 
	 * underscores and periods.
	 * @param c the character to be tested
	 */
	public boolean isValidFirstChar(char c) {
		return Character.isJavaIdentifierStart(c);
	}

	/** 
	 * Indicates if a given character is allowed in level names.
	 * Currently allowed are: letters, digits, currency symbols, 
	 * underscores and periods.
	 * @param c the character to be tested
	 */
	public boolean isValidNextChar(char c) {
		return Character.isJavaIdentifierPart(c);
	}
 
	/** 
	 * This implementation returns a parser which
	 * insists that the content value starts with a character satisfying
	 * {@link #isValidNextChar(char)} and with all next characters satisfying 
	 * {@link #isValidNextChar(char)}.
	 */
	@Override
	ContentParser<Integer> createParser() {
		return new NumberParser();
	}

	/** ContentParser used for this AspectValue */
	private final ContentParser<Integer> parser = new NumberParser();

	/** Content parser which acts as the identity function on strings. */
	private class NumberParser implements ContentParser<Integer> {
		/** Empty constructor with the correct visibility. */
		NumberParser() {
			// empty
		}

		public Integer toContent(String value) throws FormatException {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException exc) {
				throw new FormatException("Value '%s' cannot be parsed as number", value);
			}
		}
		
		public String toString(Integer content) {
			return content.toString();
		}
	}
}
