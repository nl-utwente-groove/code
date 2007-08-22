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
 * $Id: NestingAspectValue.java,v 1.1 2007-08-22 09:19:49 kastenberg Exp $
 */
package groove.nesting;

import groove.nesting.rule.NestedAspectualRuleView;
import groove.view.FormatException;
import groove.view.aspect.Aspect;
import groove.view.aspect.ContentAspectValue;
import groove.view.aspect.ContentParser;

/**
 * 
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:49 $
 */
public class NestingAspectValue extends ContentAspectValue<String> {

	/** ContentParser used for this AspectValue */
	public static final ContentParser<String> parser = new NestingContentParser();
	
	/** Level of nesting within the rule. Determined on runtime, not stored */
	protected String nestingLevel;
	
	/**
	 * Constructs a new NestingAspectValue
	 * @param aspect
	 * @param name
	 * @throws FormatException
	 */
	public NestingAspectValue(Aspect aspect, String name) throws FormatException {
		super(aspect, name, parser);
	}
	
	NestingAspectValue(NestingAspectValue original, String content) {
		super(original, parser, content);
	}

	/* (non-Javadoc)
	 * @see groove.graph.aspect.ContentAspectValue#newValue(java.lang.String)
	 */
	@Override
	public ContentAspectValue newValue(String value) throws FormatException {
		return new NestingAspectValue(this, parser.toContent(value));
	}
	
	/**
	 * Set the level of the node, which is determined on runtime by
	 * {@link NestedAspectualRuleView}
	 * @param level
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

}
