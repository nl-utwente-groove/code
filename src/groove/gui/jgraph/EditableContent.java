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
 * $Id: EditableContent.java,v 1.2 2007-06-26 15:50:20 rensink Exp $
 */
package groove.gui.jgraph;

import groove.util.Groove;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Content object that is a collection of strings, and can be reloaded 
 * from an object of collection.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EditableContent extends JCellContent<String> {
    /**
     * Constructs an object whose string description uses a given string as a separator between
     * labels (in {@link #toString()}, and which uses another separator when editing the object and
     * loading the object from a string (in {@link #load(String)}.
     * The behaviour on loading from an empty set or string can also be set.
     * @param allowEmptyLabelSet set to <code>true</code> if the label set should not be empty.
     * @see #getEditSeparator()
     * @ensure <tt>getPrintSeparator().equals(printSeparator) && getEditSeparator().equals(editSeparator)</tt>
     */
    public EditableContent(boolean allowEmptyLabelSet) {
        super();
        this.allowEmptyLabelSet = allowEmptyLabelSet;
    }

	@Override
	public Collection<String> getLabelSet() {
		return Collections.unmodifiableCollection(this);
	}

	/**
	 * Returns the edit separator of this user object. The edit separator is used to recognize
	 * individual labels in the string description of the entire user object, when editing the
	 * object or reloading a new collection from a string in {@link #load(String)}. It is set at
	 * construction time.
	 * @see #load(String)
	 */
	public String getEditSeparator() {
	    return editSeparator;
	}

	/**
	 * Converts the user object to an editable string, in which the individual labels are separated
	 * by the edit separator.
	 * @see #getEditSeparator()
	 */
	public String toEditString() {
		return Groove.toString(toArray(), "", "", getEditSeparator());
	}

	/**
	 * Loads the user object collection from a given string value. 
	 * This implementation splits the value
	 * using {@link String#split(String)} with as the split expression
	 * <tt>WHITESPACE+trim(getEditSeparator())+WHITESPACE</tt>. This means that edit separators
	 * behave as the lowest-priority operators, lower even than bracketing or quoting.
	 * If {@link #isAllowEmptyLabelSet()} is <tt>false</tt>, then an empty <tt>value</tt>
	 * will result in the empty string.
	 * @param value the value from which to load the user object; may not be <tt>null</tt>
	 * @see #getEditSeparator()
	 * @see #isAllowEmptyLabelSet()
	 */
	public void load(String value) {
		load(Arrays.asList(value.split(loadSeparator, 0)));
	}

	/**
	 * Loads the user object collection from a given label set.
	 * 
	 * @param labelSet
	 *            the label set from which to load the user object
	 */
	public void load(Collection<String> labelSet) {
	    clear();
	    boolean load = ! isAllowEmptyLabelSet();
	    if (!load) {
	    	load = labelSet.size() != 1;
	    }
	    if (! load) {
	    	// only one label; load it if it is not empty
	    	load = labelSet.iterator().next().length() > 0;
	    }
		if (load) {
			addAll(labelSet);
		}
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
     * Indicates if an empty label set is allows.
     * If so, the empty string will be interpreted as an empty set.
     */
    private boolean allowEmptyLabelSet = true;
	/**
	 * The separator used between the labels when turning this user object into an editable string.
	 */
	private final String editSeparator = NEWLINE;
	/**
	 * The separator used in loading the string description of the entire user object, between the descriptions of the individual objects in the collection.
	 */
	private final String loadSeparator = WHITESPACE + editSeparator + WHITESPACE;
	
    /** The default label separator. */
    public static final String NEWLINE = "\n";

//
//    /** Quote character for {@link #getPrintLabel(String)}. */
//    public static final char QUOTE = '\'';

    /** Whitespace recognizer in a regular expression. */
    public static final String WHITESPACE = "\\s*";
}
