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
 * $Id: AspectParser.java,v 1.1.1.2 2007-03-20 10:42:43 kastenberg Exp $
 */
package groove.graph.aspects;

import groove.graph.DefaultLabel;
import groove.graph.GraphFormatException;
import groove.graph.Label;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.util.ExprFormatException;
import groove.util.Groove;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that is responsible for recognising aspects from edge labels.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class AspectParser {
	/** 
	 * String used to separate the textual representation of aspect values
	 * in a label. 
	 * When the separator occurs twice in direct succession, this denotes the
	 * end of the aspect prefix.
	 */
    public static final String SEPARATOR = Groove.getXMLProperty("label.aspect.separator");

    /** The singleton lenient parser instance. */
    private static AspectParser lenientParser = new AspectParser(true);
    /** The singleton strict parser instance. */
    private static AspectParser strictParser = new AspectParser(false);
    
    /**
     * Returns a strict or lenient parser instance.
     * @see #isLenient()
     */
    public static AspectParser getInstance(boolean lenient) {
        if (lenient) {
        	return lenientParser;
        } else {
        	return strictParser;
        }
    }

    /**
     * Returns a lenient parser instance.
     * @see #getInstance(boolean)
     */
    public static AspectParser getInstance() {
        return getInstance(true);
    }

    /** 
     * Creates a lenient parser. 
     * #see {@link #AspectParser(boolean)} 	
     */
    private AspectParser() {
    	this(true);
    }

    /** 
     * Constructs a parser with given leniency.
     * If the parser is lenient, then certain errors in the labels
     * are disregarded. 
     * @see #isLenient()
     */
    private AspectParser(boolean lenient) {
    	for (Aspect aspect: Aspect.allAspects) {
    		registerAspect(aspect);
    	}
    	this.lenient = lenient;
    }
    
    /**
     * Registers a new aspect with this parser.
     * This means the aspect will be recognised from now on.
     * @param aspect the aspect to be added
     */
    public void registerAspect(Aspect aspect) {
        aspects.add(aspect);
    }
    
    /**
	 * Returns an unmodifiable view on the set of all registered aspects.
	 */
	public Set<Aspect> getAspects() {
		return Collections.unmodifiableSet(aspects);
	}

	/**
     * Extracts the aspect information from a plain label text.
     * @param plainText the text to start from
     * @return an object containing information about the aspect value,
     * the possible end marker, and the possible actual label text present
     * in <code>plainText</code>
     * @throws GraphFormatException if <code>prefixedText</code> contains an
     * apparent aspect value that is not recognised by {@link AspectValue#getValue(String)}.
     */
    public AspectParseData getParseData(String plainText) throws GraphFormatException {
    	AspectMap parsedValues = new AspectMap();
		boolean stopParsing = false;
		boolean endFound = false;
		int prevIndex = 0;
		int nextIndex = plainText.indexOf(SEPARATOR, prevIndex);
		while (!stopParsing && nextIndex >= prevIndex) {
			// look for the next aspect value between prevIndex and nextIndex
			String valueText = plainText.substring(prevIndex, nextIndex);
			prevIndex = nextIndex + SEPARATOR.length();
			stopParsing = endFound = valueText.length() == 0;
			if (! endFound) {
	            try {
					stopParsing = addParsedValue(parsedValues, valueText);
				} catch (GraphFormatException exc) {
					throw new GraphFormatException("%s in label '%s'", exc.getMessage(), plainText);
				}
			}
			nextIndex = plainText.indexOf(SEPARATOR, prevIndex);
		}
		String text = plainText.substring(prevIndex);
		return createParseData(parsedValues, endFound, createLabel(text));
    }

	/**
	 * Indicates if parsing is lenient.
	 * If the parser is lenient, certain errors are disregarded;
	 * in particular, labels that cannot be parsed as
	 * regular expressions are interpreted as default labels, and
	 * duplicate aspect values are interpreted as the end of the 
	 * aspect value prefix.
	 */
	public boolean isLenient() {
		return lenient;
	}

	/**
	 * Adds a parsed aspect value to an already exiosting aspect map,
	 * while testing for duplicates.
	 * If {@link #isLenient()} is set, a duplicate results in a
	 * return value <code>true</code>, otherwise it results in a {@link GraphFormatException}.
	 * @param parsedValues the already existing map
	 * @param valueText string description of a new {@link AspectValue}
	 * @return <code>true</code> if <code>valueText</code> is a duplicate value 
	 * for an existing aspect, and {@link #isLenient()} is set.
	 * @throws GraphFormatException if <code>valueText</code> is not a 
	 * valid {@link AspectValue}, or duplicates another and the parser is
	 * not lenient.
	 */
	private boolean addParsedValue(AspectMap parsedValues, String valueText) throws GraphFormatException {
		boolean stopParsing = false;
		AspectValue value = AspectValue.getValue(valueText);
		if (value == null) {
			throw new GraphFormatException(String.format("Unknown aspect value '%s'", valueText));
		} else {
			AspectValue oldValue = parsedValues.put(value.getAspect(), value);
			if (oldValue != null) {
				if (isLenient()) {
					stopParsing = true;
				} else {
					throw new GraphFormatException(
							String.format("Aspect %s has values '%s' and '%s'",
									value.getAspect(),
									oldValue,
									value));
				}
			}
		}
		return stopParsing;
	}
    
    /** Callback factory method for {@link AspectParseData}s. */
    protected AspectParseData createParseData(AspectMap values, boolean hasEnd, Label label) {
    	return new AspectParseData(values, hasEnd, label);
    }
    
    /**
     * Factory method to create a label from a string.
     * This implementation parses the string as a regular expression;
     * if it yields an atom, a {@link DefaultLabel} is returned, 
     * otherwise a {@link RegExprLabel} is returned.
     * Also makes a {@link DefaultLabel} if the text cannot be parsed as a regular expression.
     */
    protected Label createLabel(String text) throws GraphFormatException {
    	if (text == null || text.length() == 0) {
    		return null;
    	} else try {
			RegExpr textAsRegExpr = RegExpr.parse(text);
			if (textAsRegExpr instanceof RegExpr.Atom) {
				// to maintain existing quotes, just take the original text
				return DefaultLabel.createLabel(text);
			} else {
				return new RegExprLabel(textAsRegExpr);
			}
		} catch (ExprFormatException exc) {
			if (isLenient()) {
				// if the text cannot be parsed as a regular expression, 
				// just turns it into a default label
				return DefaultLabel.createLabel(text);
			} else {
				throw new GraphFormatException(exc);
			}
		}
	}

    /**
     * Turns an aspect value into a string that can be read
     * by {@link #getParseData(String)}.
     */
    public String toText(AspectValue value) {
    	return value.getName()+SEPARATOR;
    }

    /**
     * The set of registered aspects.
     */
    private final Set<Aspect> aspects = new HashSet<Aspect>();
    /** 
     * Indicates that parsing should be lenient,
     * i.e., some errors are glossed over.
     */
    private final boolean lenient;
}
