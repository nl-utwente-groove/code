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
 * $Id: NamedAspectValue.java,v 1.1 2007-10-14 11:17:37 rensink Exp $
 */
package groove.view.aspect;

import groove.view.FormatException;

/**
 * Aspect value encoding the nesting level within a nested rule or condition. 
 * The nesting level is a string interpreted as an identifier of the level.
 * @author kramor
 * @version $Revision $
 */
public class NamedAspectValue extends ContentAspectValue<String> {
	/**
	 * Constructs a new nesting level-containing aspect value.
	 * @param name the aspect value name
	 * @throws FormatException if <code>name</code> is an already existing aspect value
	 */
	public NamedAspectValue(Aspect aspect, String name) throws FormatException {
		super(aspect, name);
	}
	
	/** Creates an instance of a given nesting aspect value, with a given level. */
	NamedAspectValue(NamedAspectValue original, String level) {
		super(original, level);
	}

	@Override
	public ContentAspectValue<String> newValue(String value) throws FormatException {
		return new NamedAspectValue(this, parser.toContent(value));
	}
	
	/**
	 * Set the level of the node, which is determined on runtime by the nested rule view.
	 */
	public void setLevel(String level) { 
		nestingLevel = level;
	}
	
	/**
	 * Returns the level of nesting of the node. May be null if it has not
	 * yet been assigned a nesting level
	 * @return the level of nesting as a String, or null if not yet assigned
	 */
	public String getLevel() {
		return nestingLevel;
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
	ContentParser<String> createParser() {
		return new NameParser();
	}

	/** ContentParser used for this AspectValue */
	private final ContentParser<String> parser = new NameParser();
	/** Level of nesting within the rule. Determined on runtime, not stored */
	private String nestingLevel;

	/** Content parser which acts as the identity function on strings. */
	private class NameParser implements ContentParser<String> {
		/** Empty constructor with the correct visibility. */
		NameParser() {
			// empty
		}

		public String toContent(String value) throws FormatException {
			if (value.length() == 0) {
				return value;
			}
			if (!isValidFirstChar(value.charAt(0))) {
				throw new FormatException("Invalid start character '%c' in name '%s'", value.charAt(0), value);
			}
			for (int i = 1; i < value.length(); i++) {
				char c = value.charAt(i);
				if (!isValidNextChar(c)) {
					throw new FormatException("Invalid character '%c' in name '%s'", c, value);
				}
			}
			return value;
		}
		
		public String toString(String content) {
			return content;
		}
	}
}
