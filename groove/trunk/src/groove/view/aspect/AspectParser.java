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
 * $Id: AspectParser.java,v 1.10 2007-09-30 21:29:08 rensink Exp $
 */
package groove.view.aspect;

import static groove.view.aspect.Aspect.CONTENT_ASSIGN;
import static groove.view.aspect.Aspect.VALUE_SEPARATOR;
import groove.view.ComposedLabelParser;
import groove.view.FormatException;
import groove.view.LabelParser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class that is responsible for recognising aspects from edge labels.
 * @author Arend Rensink
 * @version $Revision: 1.10 $
 */
public class AspectParser {
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
     * @throws FormatException if <code>plainText</code> contains an
     * apparent aspect value that is not recognised by {@link AspectValue#getValue(String)}.
     */
    public AspectParseData getParseData(String plainText) throws FormatException {
    	AspectMap parsedValues = new AspectMap();
    	/*if( plainText == null && false ) {
        	// JHK: the calling method (AspectGraph.getNodeValue) can handle empty aspects, but this method cannot
        	// JHK: this if creates the data AspectGraph expects when there is no aspect-material
    		return createParseData(parsedValues, false, null);
    	}*/
		boolean stopParsing = false;
		boolean endFound = false;
		int prevIndex = 0;
		int nextIndex = plainText.indexOf(VALUE_SEPARATOR, prevIndex);
		while (!stopParsing && nextIndex >= prevIndex) {
			// look for the next aspect value between prevIndex and nextIndex
			String valueText = plainText.substring(prevIndex, nextIndex);
			prevIndex = nextIndex + VALUE_SEPARATOR.length();
			stopParsing = endFound = valueText.length() == 0;
			if (! endFound) {
	            try {
	            	String contentText;
	            	int assignIndex = valueText.indexOf(CONTENT_ASSIGN);
	            	if (assignIndex < 0) {
	            		contentText = null;
	            	} else {
	            		contentText = valueText.substring(assignIndex + CONTENT_ASSIGN.length());
	            		valueText = valueText.substring(0, assignIndex);
	            	}
					stopParsing = addParsedValue(parsedValues, valueText, contentText);
				} catch (FormatException exc) {
					throw new FormatException("%s in '%s'", exc.getMessage(), plainText);
				}
			}
			nextIndex = plainText.indexOf(VALUE_SEPARATOR, prevIndex);
		}
		String text = plainText.substring(prevIndex);
		if (text.length() == 0 && ! endFound) {
			text = null;
		}
		return createParseData(parsedValues, endFound, text);
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
	 * @param contentText string description for the new value's content; <code>null</code> if
	 * the aspect value is not a {@link ContentAspectValue}.
	 * @return <code>true</code> if <code>valueText</code> is a duplicate value 
	 * for an existing aspect, and {@link #isLenient()} is set.
	 * @throws FormatException if <code>valueText</code> is not a 
	 * valid {@link AspectValue}, or duplicates another and the parser is
	 * not lenient, or the presence of content is not as it should be.
	 */
	private boolean addParsedValue(AspectMap parsedValues, String valueText, String contentText) throws FormatException {
		boolean stopParsing = false;
		AspectValue value = AspectValue.getValue(valueText);
		if (value == null) {
			throw new FormatException(String.format("Unknown aspect value '%s'", valueText));
		} else if (value instanceof ContentAspectValue) {
			// use the value as a factory to get a correct instance
			value = ((ContentAspectValue<?>) value).newValue(contentText == null ? "" : contentText);
		} else if (contentText != null) {
			throw new FormatException(String.format("Aspect value '%s' cannot have content", valueText));
		}
		AspectValue oldValue = parsedValues.put(value.getAspect(), value);
		if (oldValue != null) {
			if (isLenient()) {
				stopParsing = true;
			} else if (oldValue == value) {
				throw new FormatException("Duplicate aspect value '%s'", value);
			} else {
				throw new FormatException("Conflicting aspect values '%s' and '%s'", oldValue, value);
			}
		}
		return stopParsing;
	}
    
    /** Callback factory method for {@link AspectParseData}s. */
    private AspectParseData createParseData(AspectMap values, boolean hasEnd, String text) {
    	return new AspectParseData(values, hasEnd, text);
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
     * Returns a strict parser instance.
     * @see #getInstance(boolean)
     */
    public static AspectParser getInstance() {
        return getInstance(false);
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
		return value.toString()+VALUE_SEPARATOR;
	}

	/**
	 * Converts a collection of aspect values plus an actual
	 * label text into a string that can be parsed back.
	 */
	static public StringBuilder toString(Collection<AspectValue> values, StringBuilder labelText) {
		StringBuilder result = new StringBuilder();
		for (AspectValue value: values) {
			result.append(AspectParser.toString(value));
		}
		if (values.size() > 0 && (labelText.length() == 0 || labelText.indexOf(VALUE_SEPARATOR) >= 0)) {
			result.append(VALUE_SEPARATOR);
		}
		result.append(labelText);
		return result;
	}
    
    /** Returns the label parsers induced by a set of aspect values. */
    static public LabelParser getLabelParser(Collection<AspectValue> values) {
        Collection<LabelParser> parsers = new LinkedHashSet<LabelParser>();
        for (AspectValue value: values) {
            LabelParser parser = value.getLabelParser();
            if (parser != null) {
                parsers.add(parser);
            }
        }
        // if none was induced, get the default parser
        if (parsers.isEmpty()) {
            parsers.add(AbstractAspect.getFreeLabelParser());
        }
        return new ComposedLabelParser(parsers);
    }

    /** The singleton lenient parser instance. */
    private static AspectParser lenientParser = new AspectParser(true);
    /** The singleton strict parser instance. */
    private static AspectParser strictParser = new AspectParser(false);
}
