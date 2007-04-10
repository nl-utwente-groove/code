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
 * $Id: JUserObject.java,v 1.2 2007-03-27 14:18:29 rensink Exp $
 */
package groove.gui.jgraph;

import groove.util.ExprParser;
import groove.util.Groove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * User object underlying a {@link JVertex}or {@link JEdge}. The object behaves as a set that can
 * be loaded from a {@link String}.
 * 
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public class JUserObject<T> extends TreeSet<T> {
    /** The default label separator. */
    public static final String NEWLINE = "\n";

    /** Space character. */
    public static final char SPACE = ' ';

    /** Quote character for {@link #getPrintLabel(String)}. */
    public static final char QUOTE = '\'';

    /** Whitespace recognizer in a regular expression. */
    public static final String WHITESPACE = "\\s*";

    /**
     * Removes hard spaces (as in {@link #SPACE}) from the start and end of a given value, and
     * returns the resulting string.
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
     * Constructs an object whose string description uses a given string as a separator between
     * labels (in {@link #toString()}.
     * The behaviour on loading from an empty set or string can also be set.
     * @param jCell the cell for which this is the user object
     * @param printSeparator the intended label print separator
     * @param allowEmptyLabelSet set to <code>true</code> if the label set should not be empty.
     * @ensure <tt>getPrintSeparator().equals(printSeparator) && getEditSeparator().equals(editSeparator)</tt>
     */
    public JUserObject(JCell jCell, String printSeparator, boolean allowEmptyLabelSet) {
        this.jCell = jCell;
    	this.printSeparator = printSeparator;
    	this.trimmedPrintSeparator = trim(printSeparator);
        this.allowEmptyLabelSet = allowEmptyLabelSet;
    }

    /**
     * Returns a collection of strings describing the objects contained in this user object.
     * This method delegates to {@link JCell#getLabelSet()} of the underlying cell.
     * @return the string descriptions of the objects contained in this collection
     * @ensure all elements of <tt>result</tt> are instances of <tt>String</tt>.
     */
    final public Collection<String> getLabelSet() {
    	return jCell.getLabelSet();
    }

    /**
     * This implementation returns the string description of the label set view on this user object,
     * as returned by {@link #getLabelSet()}. This consists of the individual labels, separated by
     * this user object's print separator (set at construction time).
     * @see #getLabelSet()
     */
    @Override
    public String toString() {
    	List<String> printLabels = new ArrayList<String>();
    	int labelIndex = 0;
    	for (String label: getLabelSet()) {
    		printLabels.add(getPrintLabel(label));
    		labelIndex++;
    	}
    	return Groove.toString(printLabels.toArray(), "", "", printSeparator);
    }

    /**
     * Returns a user object collection with the same separators and elements as this one.
     */
    @Override
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
     * Returns a string description that is based on <tt>getLabel(object)</tt> but
     * is quoted (with {@link #QUOTE}) if the print separator occurs in the label. 
     * @param object the object for which the description is required
     * @return the label, quoted if necessary
     * @see #getLabel(Object)
     */
    protected String getPrintLabel(String label) {
        if (label.indexOf(trimmedPrintSeparator) >= 0) {
            return ExprParser.toQuoted(label, QUOTE);
        } else {
            return label;
        }        
    }

    /** The call of which this is the user object. */
    private final JCell jCell;
    /**
     * The separator, used in the string description of the entire user object, between the
     * descriptions of the individual objects in the collection.
     */
    private final String printSeparator;
    /**
     * Version of {@link #printSeparator} used to detect whether
     * quoting of labels is necessary for inambiguity.
     */
    private final String trimmedPrintSeparator;

    /**
     * Indicates if an empty label set is allows.
     * If so, the empty string will be interpreted as an empty set.
     */
    private boolean allowEmptyLabelSet = true;
}
