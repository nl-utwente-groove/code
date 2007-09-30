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
 * $Id: NestingAspectValue.java,v 1.2 2007-09-30 21:50:15 rensink Exp $
 */
package groove.view.aspect;

import groove.view.FormatException;

/**
 * Aspect value encoding the nesting level within a nested rule or condition. 
 * The nesting level is a string interpreted as an identifier of the level.
 * @author kramor
 * @version $Revision $
 */
public class NestingAspectValue extends ContentAspectValue<String> {
	/**
	 * Constructs a new nesting level-containing aspect value.
	 * @param name the aspect value name
	 * @throws FormatException if <code>name</code> is an already existing aspect value
	 */
	public NestingAspectValue(String name) throws FormatException {
		super(NestingAspect.getInstance(), name, parser);
	}
	
	/** Creates an instance of a given nesting aspect value, with a given level. */
	NestingAspectValue(NestingAspectValue original, String level) {
		super(original, parser, level);
	}

	@Override
	public ContentAspectValue<String> newValue(String value) throws FormatException {
		return new NestingAspectValue(this, parser.toContent(value));
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

	/** Level of nesting within the rule. Determined on runtime, not stored */
	private String nestingLevel;
	
	/** ContentParser used for this AspectValue */
	static private final ContentParser<String> parser = new NestingContentParser();
	
	/** Content parser which acts as the identity function on strings. */
	static private class NestingContentParser implements ContentParser<String> {
		public String toContent(String value) throws FormatException {
			return value;
		}
		
		public String toString(String content) {
			return content;
		}
	}
}
