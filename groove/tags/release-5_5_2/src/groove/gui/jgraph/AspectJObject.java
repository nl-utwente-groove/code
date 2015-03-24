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
 * $Id$
 */
package groove.gui.jgraph;

import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectKind;
import groove.grammar.aspect.AspectLabel;
import groove.gui.look.MultiLabel;
import groove.gui.look.MultiLabel.Direct;
import groove.util.Groove;
import groove.util.line.Line;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Content object that is a collection of strings, and can be reloaded from an
 * object of collection.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectJObject extends ArrayList<String> {
    /**
     * Converts the user object to an editable string, in which the individual
     * labels are separated by newlines
     */
    public String toEditString() {
        return Groove.toString(toArray(), "", "", NEWLINE);
    }

    /**
     * Returns a list of lines constituting the node or edge label
     * in case this object is displayed directly.
     */
    public MultiLabel toLines() {
        MultiLabel result = new MultiLabel();
        for (String text : this) {
            result.add(Line.atom(text), Direct.NONE);
        }
        return result;
    }

    /**
     * Loads the user object collection from a given string value. This
     * implementation splits the value using newlines, and trims the
     * individual labels. This means that
     * edit separators behave as the lowest-priority operators, lower even than
     * bracketing or quoting.
     * @param value the value from which to load the user object; may not be
     *        <tt>null</tt>
     */
    public void load(String value) {
        for (String text : value.split(NEWLINE)) {
            text = text.trim();
            if (text.length() > 0) {
                add(text);
            }
        }
        if (isEmpty()) {
            add("");
        }
    }

    /**
     * Loads the user object collection from a given label set.
     *
     * @param labelSet the label set from which to load the user object
     */
    public void addLabels(Collection<AspectLabel> labelSet) {
        for (AspectLabel label : labelSet) {
            add(label.toString());
        }
    }

    /**
     * Loads the user object collection from a given edge set.
     *
     * @param edgeSet the edge set from which to load the user object
     */
    public void addEdges(Collection<AspectEdge> edgeSet) {
        for (AspectEdge edge : edgeSet) {
            addEdge(edge);
        }
    }

    /**
     * Adds the label of a given edge to the user object.
     * @param edge the edge from which to load the user object
     */
    private void addEdge(AspectEdge edge) {
        if (edge.getKind() == AspectKind.REMARK) {
            // Add remark prefixes to every line of the comment
            for (String line : edge.label().getInnerText().split("\n")) {
                add(AspectKind.REMARK.getPrefix() + line);
            }
        } else {
            add(edge.label().toString());
        }
    }

    @Override
    public AspectJObject clone() {
        return (AspectJObject) super.clone();
    }

    /** The default label separator. */
    public static final String NEWLINE = "\n";
}
