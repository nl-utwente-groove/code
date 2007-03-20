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
 * $Id: JUserObject.java,v 1.1.1.2 2007-03-20 10:42:47 kastenberg Exp $
 */
package groove.gui.jgraph;

import groove.util.ExprParser;
import groove.util.Groove;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * User object underlying a {@link JVertex}or {@link JEdge}. The object behaves as a set that can
 * be loaded from a {@link String}.
 * 
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class JUserObject<T> extends TreeSet<T> {
    /** The default label separator. */
    public static final String NEWLINE = "\n";

    /** The HTML label separator. */
    public static final String HTML_NEWLINE = "<br>";

    /** Space character. */
    public static final char SPACE = ' ';

    /** Quote character for {@link #getPrintLabel(Object)}. */
    public static final char QUOTE = '\'';

    /** Whitespace recognizer in a regular expression. */
    public static final String WHITESPACE = "\\s*";

    /**
     * Removes hard spaces (as in {@link #SPACE}) from the start and end of a given value, and
     * returns the resulting strong.
     * @param value the value to be trimmed
     * @return the result of trimming
     * @see String#trim()
     */
    public static String trim(String value) {
        StringBuffer result = new StringBuffer(value);
        while (result.charAt(0) == SPACE) {
            result.deleteCharAt(0);
        }
        while (result.charAt(result.length() - 1) == SPACE) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    /**
     * Constructs an object with a new line as print and edit label separator.
     * @see #NEWLINE
     * @ensure <tt>getPrintSeparator().equals(NEWLINE) && getEditSeparator().equals(NEWLINE)</tt>
     */
    public JUserObject() {
        this(NEWLINE);
    }

    /**
     * Constructs an object whose string description uses a given string as a print and edit
     * separator.
     * @param separator the intended label separator
     * @see #getPrintSeparator()
     * @ensure <tt>getPrintSeparator().equals(separator) && 
     * getEditSeparator.equals(separator)</tt>
     */
    public JUserObject(String separator) {
        this(separator, false);
    }

    /**
     * Constructs an object whose string description uses a given string as a separator between
     * labels (in {@link #toString()}.
     * The behaviour on loading from an empty set or string can also be set.
     * @param printSeparator the intended label print separator
     * @param allowEmptyLabelSet set to <code>true</code> if the label set should not be empty.
     * @see #getPrintSeparator()
     * @ensure <tt>getPrintSeparator().equals(printSeparator) && getEditSeparator().equals(editSeparator)</tt>
     */
    public JUserObject(String printSeparator, boolean allowEmptyLabelSet) {
        this.printSeparator = printSeparator;
        this.allowEmptyLabelSet = allowEmptyLabelSet;
    }

    /**
     * Returns the print separator of this user object. The print separator is used to separate
     * individual labels in the string description of the entire user object, as returned by
     * {@link #toString()}. It is set at construction time.
     * @return the print separator
     * @see #toString()
     */
    public String getPrintSeparator() {
        return printSeparator;
    }

    /**
     * Returns a collection of strings describing the objects contained in this user object.
     * @return the string descriptions of the objects contained in this collection
     * @ensure all elements of <tt>result</tt> are instances of <tt>String</tt>.
     */
    public Collection<String> getLabelSet() {
        Set<String> result = new LinkedHashSet<String>();
        for (T label: this) {
        	result.add(getLabel(label));
        }
        return result;
    }

    /**
     * This implementation returns the string description of the label set view on this user object,
     * as returned by {@link #getLabelSet()}. This consists of the individual labels, separated by
     * {@link #getPrintSeparator()}
     * @see #getLabelSet()
     * @see #getPrintSeparator()
     */
    public String toString() {
    	String[] printLabels = new String[size()];
    	int labelIndex = 0;
    	for (T label: this) {
    		printLabels[labelIndex] = getPrintLabel(label);
    		labelIndex++;
    	}
    	return Groove.toString(printLabels, "", "", getPrintSeparator());
    }

    /**
     * Returns a user object collection with the same separators and elements as this one.
     */
    public JUserObject<T> clone() {
        JUserObject<T> result = (JUserObject<T>) super.clone();
        result.addAll(this);
        result.setAllowEmptyLabelSet(isAllowEmptyLabelSet());
        return result;
    }

    /**
     * Indicates if this user object may have an empty label set.
     * If so, then loading it with an empty string results in the empty label
     * set; if not, then the initial value is set to the empty string.
     * The property is set to <tt>true</tt> by default.
     * @return <tt>true</tt> if the user object may have an empty label set
     */
    public boolean isAllowEmptyLabelSet() {
        return allowEmptyLabelSet;
    }
    
    /**
     * Sets the <tt>allowEmptyLabelSet</tt> property to a given value.
     * This does not affect the current set, even if the new value is <tt>false</tt>
     * and the label set is currently empty; only the loading behaviour 
     * (see (@link #load(String)}) is affected.
     * @param allowEmptyLabelSet the value for {@link #allowEmptyLabelSet}
     * @see #isAllowEmptyLabelSet()
     */
    public void setAllowEmptyLabelSet(boolean allowEmptyLabelSet) {
        this.allowEmptyLabelSet = allowEmptyLabelSet;
    }

	/**
	 * Returns the string description for a given object of the type contained in this user object
	 * collection. It is
	 * used un {@link #toString()} to create the string description of the entire user object
	 * collection. This implementation returns <tt>object.toString()</tt>.
	 * @param object the object for which the description is required
	 * @return the corresponding description
	 */
	protected String getLabel(T object) {
	    return object.toString();
	}

    /**
     * Returns a string description that is based on <tt>getLabel(object)</tt> but
     * is quoted (with {@link #QUOTE}) if the print separator occurs in the label. 
     * @param object the object for which the description is required
     * @return the label, quoted if necessary
     * @see #getLabel(Object)
     */
    protected String getPrintLabel(T object) {
        String label = getLabel(object);
        if (label.indexOf(trim(getPrintSeparator())) >= 0) {
            return ExprParser.toQuoted(label, QUOTE);
        } else {
            return label;
        }        
    }

    /**
     * The separator, used in the string description of the entire user object, between the
     * descriptions of the individual objects in the collection.
     */
    private final String printSeparator;

    /**
     * Indicates if an empty label set is allows.
     * If so, the empty string will be interpreted as an empty set.
     */
    private boolean allowEmptyLabelSet = true;
}
