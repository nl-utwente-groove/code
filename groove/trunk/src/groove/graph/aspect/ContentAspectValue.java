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
 * $Id: ContentAspectValue.java,v 1.1 2007-04-12 16:14:51 rensink Exp $
 */
package groove.graph.aspect;

import static groove.graph.aspect.Aspect.CONTENT_ASSIGN;
import groove.util.FormatException;

/**
 * Specialisation of aspect values that have additional content.
 * The class acts as a factory for its own values (through {@link #newValue(String)}). 
 * The content is converted to and from a string value by a parser passed
 * in at construction time.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class ContentAspectValue<C> extends AspectValue {
    /**
     * Creates a new aspect value factory, for a given aspect and with a given name.
     * Instances of the aspect value can be obtained by calling the factory method
     * {@link #newValue(String)}.
     * Throws an exception if an aspect value with the same name exists already.
     * @param aspect the aspect for which this is a value
     * @param name the name of the aspect value.
     * @throws groove.util.FormatException if the value name is already used
     */
    public ContentAspectValue(Aspect aspect, String name, ContentParser<C> parser) throws FormatException {
    	super(aspect, name);
    	this.parser = parser;
    	this.content = null;
    }

    /**
     * Constructs a specialisation of a given aspect value with a given 
     * content. 
     * @param original the aspect value being copied
     * @param content the content of the specialised value
     */
    ContentAspectValue(AspectValue original, ContentParser<C> parser, C content) {
    	super(original.getAspect(), original.getName(), original.getIncompatibles());
    	this.content = content;
    	this.parser = parser;
    }

	/**
	 * Returns the content of this aspect value.
	 * @return the content, or <code>null</code> if this instance is
	 * to be used as a factory.
	 */
	public final C getContent() {
		return this.content;
	}

    /**
	 * Returns the parser for content values.
	 * @return the parser passed in at construction time, if this instance
	 * acts as a factory; <code>null</code> otherwise. 
	 */
	public final ContentParser<C> getParser() {
		return this.parser;
	}

	/**
     * Creates a new, specialised instance of this value with content
     * parsed from a given string value.
     * @throws FormatException if <code>value</code> is not correctly formatted.
     * @throws UnsupportedOperationException if this instance is not a factory.
     */
    abstract public ContentAspectValue newValue(String value) throws FormatException;
    
    /**
     * Returns the name and optional content of the aspect.
     * @see #getName()
     */
    @Override
    public String toString() {
        return getName() + CONTENT_ASSIGN + getParser().toString(getContent());
    }
    
    /** Flag indicating that content is optional for actual values. */
    private final ContentParser<C> parser;
    /** The (further) content of this value. */
    private final C content;
}
