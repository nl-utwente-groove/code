/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: EditableContent.java,v 1.3 2008-01-30 09:33:11 iovka Exp $
 */
package groove.gui.jgraph;

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.util.Groove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * Content object that is a collection of strings, and can be reloaded from an
 * object of collection.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EditableContent extends TreeSet<Label> {
    /**
     * Constructs an object whose string description uses a given string as a
     * separator between labels (in {@link #toString()}, and which uses another
     * separator when editing the object and loading the object from a string
     * (in {@link #load(String)}. The behaviour on loading from an empty set or
     * string can also be set.
     * @param allowEmptyLabelSet set to <code>true</code> if the label set
     *        should not be empty.
     */
    public EditableContent(boolean allowEmptyLabelSet) {
        this.allowEmptyLabelSet = allowEmptyLabelSet;
    }

    /**
     * Converts the user object to an editable string, in which the individual
     * labels are separated by newlines
     */
    public String toEditString() {
        return Groove.toString(toArray(), "", "", NEWLINE);
    }

    /**
     * Loads the user object collection from a given string value. This
     * implementation splits the value using newlines, and trims the
     * individual labels. This means that
     * edit separators behave as the lowest-priority operators, lower even than
     * bracketing or quoting. If {@link #isAllowEmptyLabelSet()} is
     * <tt>false</tt>, then an empty <tt>value</tt> will result in the empty
     * string.
     * @param value the value from which to load the user object; may not be
     *        <tt>null</tt>
     * @see #isAllowEmptyLabelSet()
     */
    public void load(String value) {
        List<Label> labelList = new ArrayList<Label>();
        for (String text : value.split(NEWLINE)) {
            text = text.trim();
            if (text.length() > 0) {
                labelList.add(DefaultLabel.createLabel(text));
            }
        }
        load(labelList);
    }

    /**
     * Loads the user object collection from a given label set.
     * 
     * @param labelSet the label set from which to load the user object
     */
    public void load(Collection<Label> labelSet) {
        clear();
        if (isAllowEmptyLabelSet() || !labelSet.isEmpty()) {
            addAll(labelSet);
        }
    }

    /**
     * Indicates if this user object may have an empty label set. If so, then
     * loading it with an empty string results in the empty label set; if not,
     * then the initial value is set to the empty string. The property is set to
     * <tt>true</tt> by default.
     * @return <tt>true</tt> if the user object may have an empty label set
     */
    public boolean isAllowEmptyLabelSet() {
        return this.allowEmptyLabelSet;
    }

    /**
     * Indicates if an empty label set is allows. If so, the empty string will
     * be interpreted as an empty set.
     */
    private boolean allowEmptyLabelSet = true;
    /** The default label separator. */
    public static final String NEWLINE = "\n";
}
