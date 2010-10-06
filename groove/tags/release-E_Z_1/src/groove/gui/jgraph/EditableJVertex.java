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
 * $Id: EditableJVertex.java,v 1.7 2008-01-30 09:33:13 iovka Exp $
 */
package groove.gui.jgraph;

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.util.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * J-Graph vertex for the editor. This has a {@link EditableContent} as user
 * object, which can be loaded from a string or set of strings.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EditableJVertex extends JVertex implements EditableJCell {
    /**
     * Constructs a new, empty empty j-vertex with a given number.
     * @param nr the number of the new vertex
     */
    public EditableJVertex(int nr) {
        getUserObject().setNumber(nr);
    }

    /** Constructs a jvertex by cloning another one. */
    public EditableJVertex(JVertex other) {
        getAttributes().applyMap(other.getAttributes());
        setUserObject(other.getUserObject());
    }

    /** This implementation just returns the user object. */
    public List<StringBuilder> getLines() {
        List<StringBuilder> result = new ArrayList<StringBuilder>();
        for (String label : getUserObject()) {
            result.add(Converter.toHtml(new StringBuilder(label)));
        }
        return result;
    }

    /**
     * This implementation just returns the user object, or a singleton
     * containing {@link JVertex#NO_LABEL} if the user object is empty.
     */
    public Collection<Label> getListLabels() {
        Collection<Label> result = new ArrayList<Label>();
        for (String labelString : getUserObject()) {
            result.add(DefaultLabel.createLabel(labelString));
        }
        if (result.isEmpty()) {
            result = Collections.singleton(NO_LABEL);
        }
        return result;
    }

    /** This implementation just returns the user object. */
    public Collection<String> getPlainLabels() {
        return getUserObject();
    }

    /**
     * If the value is a collection or a string, loads the user object from it.
     */
    @Override
    public void setUserObject(Object value) {
        EditableContent newObject = createUserObject();
        newObject.setNumber(getNumber());
        super.setUserObject(newObject);
        if (value instanceof JCellContent) {
            newObject.load(((JCellContent<?>) value).getLabelSet());
            newObject.setNumber(((JCellContent<?>) value).getNumber());
        } else if (value != null) {
            newObject.load(value.toString());
        }
    }

    /** Specialises the return type. */
    @Override
    public EditableContent getUserObject() {
        return (EditableContent) super.getUserObject();
    }

    /**
     * Callback factory method to create a user object. Called lazily in
     * {@link #getUserObject()}.
     */
    @Override
    protected EditableContent createUserObject() {
        return new EditableContent(true);
    }
}
