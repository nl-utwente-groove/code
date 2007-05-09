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
 * $Id: EditableJUserObject.java,v 1.7 2007-05-09 22:53:35 rensink Exp $
 */
package groove.gui.jgraph;

import groove.util.ExprParser;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.FormatException;

import java.util.Collection;
import java.util.List;

/**
 * J-user object that is a collection of strings, and can be reloaded 
 * from an object of collection.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EditableJUserObject extends JUserObject<String> {
    /**
     * Constructs an object whose string description uses a given string as a separator between
     * labels (in {@link #toString()}, and which uses another separator when editing the object and
     * loading the object from a string (in {@link #load(String)}.
     * @param jCell the cell for which this is the user object
     * @param printSeparator the intended label print separator
     * @param editSeparator the intended label edit separator
     * @see #getEditSeparator()
     * @ensure <tt>getPrintSeparator().equals(printSeparator) && getEditSeparator().equals(editSeparator)</tt>
     */
    public EditableJUserObject(EditableJCell jCell, String printSeparator, String editSeparator) {
    	this(jCell, printSeparator, editSeparator, true);
    }

    /**
     * Constructs an object whose string description uses a given string as a separator between
     * labels (in {@link #toString()}, and which uses another separator when editing the object and
     * loading the object from a string (in {@link #load(String)}.
     * The behaviour on loading from an empty set or string can also be set.
     * @param jCell the cell for which this is the user object
     * @param printSeparator the intended label print separator
     * @param editSeparator the intended label edit separator
     * @param allowEmptyLabelSet set to <code>true</code> if the label set should not be empty.
     * @see #getEditSeparator()
     * @ensure <tt>getPrintSeparator().equals(printSeparator) && getEditSeparator().equals(editSeparator)</tt>
     */
    public EditableJUserObject(EditableJCell jCell, String printSeparator, String editSeparator, boolean allowEmptyLabelSet) {
        super(jCell, printSeparator, allowEmptyLabelSet);
        this.editSeparator = editSeparator;
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
	 * Tests if this user object can be loaded from a given string value. This implementation tests
	 * if the substrings of <tt>value</tt> obtained by splitting it according to the edit
	 * separator are parsable expressions according to {@link ExprParser#isParsable(String)}.
	 * The method returns <tt>null</tt> if the user object is loadable, or
	 * an exception specifying while it is not loadable.
	 * @param value the value that is tested
	 * @return  <tt>null</tt> if the user object is loadable, or
	 * an exception specifying while it is not loadable.
	 * @see #load(String)
	 */
	public Exception isLoadable(String value) {
//	    String[] labels = value.split(WHITESPACE + trim(getEditSeparator()) + WHITESPACE);
//	    for (int i = 0; i < labels.length; i++) {
//	        try {
//	            ExprParser.parseExpr(value);
//	        } catch (FormatException exc) {
//	            return exc;
//	        }
//	    }
	    return null;
	}

	/**
	 * Loads the user object collection from a given string value, if it is loadable according to
	 * {@link #isLoadable(String)}; does nothing otherwise. This implementation splits the value
	 * using {@link String#split(String)} with as the split expression
	 * <tt>WHITESPACE+trim(getEditSeparator())+WHITESPACE</tt>. This means that edit separators
	 * behave as the lowest-priority operators, lower even than bracketing or quoting.
	 * If {@link #isAllowEmptyLabelSet()} is <tt>false</tt>, then an empty <tt>value</tt>
	 * will result in the empty string.
	 * @param value the value from which to load the user object; may not be <tt>null</tt>
	 * @see #isLoadable(String)
	 * @see #getEditSeparator()
	 * @see #isAllowEmptyLabelSet()
	 */
	public void load(String value) {
	    if (isLoadable(value) == null) {
	        clear();
	        if (value.length() > 0 || ! isAllowEmptyLabelSet()) {
	            String[] labels = value
	                    .split(WHITESPACE + trim(getEditSeparator()) + WHITESPACE, 0);
	            for (String label: labels) {
	            	add(label);
//	            	if (label.length() > 0) {
//						try {
//							Pair<String, List<String>> parseResult = ExprParser.parseExpr(label);
//							add(ExprParser.toString(parseResult.first(),
//									parseResult.second()));
//						} catch (FormatException e) {
//							assert false : "Error in label expression " + value
//									+ ": " + e;
//						}
//					}
	            }
	        }
	    }
	}

	/**
	 * Loads the user object collection from a given label set.
	 * 
	 * @param labelSet
	 *            the label set from which to load the user object
	 */
	public void load(Collection<String> labelSet) {
	    clear();
	    addAll(labelSet);
	}

	/**
	 * The separator, used in loading the string description of the entire user object, between the descriptions of the individual objects in the collection.
	 */
	private final String editSeparator;
}
