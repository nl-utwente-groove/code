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
 * $Id$
 */
package groove.graph.aspect;

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.util.FormatException;

import static groove.graph.aspect.Aspect.SEPARATOR;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that is responsible for recognising aspects from edge labels.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectParser {
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
     * Normalises a would-be label, by parsing it as if it were label text,
     * and returning a string description of the parsed result.
     * @param plainText the string to be normalised
     * @return the parsed <code>plainText</code>, turned back into a string
     * @throws FormatException if <code>plainText</code> is not formatted
     * correctly according to the rules of the parser.
     * @see #getParseData(String)
     */
    public static String normalize(String plainText) throws FormatException {
    	return getInstance().getParseData(plainText).toString();
    }

    /**
	 * Turns an aspect value into a string that can be read
	 * by {@link #getParseData(String)}.
	 */
	static public String toString(AspectValue value) {
		return value.getName()+SEPARATOR;
	}

	/**
	 * Converts a collection of aspect values plus an actual
	 * label text into a string that can be parsed back.
	 */
	static public String toString(Collection<AspectValue> values, String labelText) {
		StringBuffer result = new StringBuffer();
		for (AspectValue value: values) {
			result.append(AspectParser.toString(value));
		}
		if (values.size() > 0 && (labelText.length() == 0 || labelText.contains(SEPARATOR))) {
			result.append(SEPARATOR);
		}
		result.append(labelText);
		return result.toString();
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
     * @throws FormatException if <code>prefixedText</code> contains an
     * apparent aspect value that is not recognised by {@link AspectValue#getValue(String)}.
     */
    public AspectParseData getParseData(String plainText) throws FormatException {
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
				} catch (FormatException exc) {
					throw new FormatException("%s in label '%s'", exc.getMessage(), plainText);
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
	 * return value <code>true</code>, otherwise it results in a {@link FormatException}.
	 * @param parsedValues the already existing map
	 * @param valueText string description of a new {@link AspectValue}
	 * @return <code>true</code> if <code>valueText</code> is a duplicate value 
	 * for an existing aspect, and {@link #isLenient()} is set.
	 * @throws FormatException if <code>valueText</code> is not a 
	 * valid {@link AspectValue}, or duplicates another and the parser is
	 * not lenient.
	 */
	private boolean addParsedValue(AspectMap parsedValues, String valueText) throws FormatException {
		boolean stopParsing = false;
		AspectValue value = AspectValue.getValue(valueText);
		if (value == null) {
			throw new FormatException(String.format("Unknown aspect value '%s'", valueText));
		} else {
			AspectValue oldValue = parsedValues.put(value.getAspect(), value);
			if (oldValue != null) {
				if (isLenient()) {
					stopParsing = true;
				} else {
					throw new FormatException(
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
    protected Label createLabel(String text) throws FormatException {
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
		} catch (FormatException exc) {
			if (isLenient()) {
				// if the text cannot be parsed as a regular expression, 
				// just turns it into a default label
				return DefaultLabel.createLabel(text);
			} else {
				throw new FormatException(exc);
			}
		}
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
