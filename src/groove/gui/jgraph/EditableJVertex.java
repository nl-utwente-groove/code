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

import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.util.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

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
    public EditableJVertex(EditorJModel jModel, int nr) {
        super(jModel, nr);
        setNumber(nr);
    }

    /** Constructs a jvertex by cloning another one. */
    public <N extends Node,E extends Edge<N>> EditableJVertex(
            EditorJModel jModel, GraphJVertex<N,E> other) {
        super(jModel, other.getNumber());
        getAttributes().applyMap(other.getAttributes());
        setNumber(other.getNumber());
        List<Label> labelList = new ArrayList<Label>();
        for (E edge : other.getSelfEdges()) {
            labelList.add(edge.label());
        }
        getUserObject().load(labelList);
    }

    /** This implementation just returns the user object. */
    public List<StringBuilder> getLines() {
        List<StringBuilder> result = new ArrayList<StringBuilder>();
        for (Label label : getUserObject()) {
            result.add(Converter.toHtml(new StringBuilder(label.toString())));
        }
        return result;
    }

    /**
     * This implementation just returns the user object, or a singleton
     * containing {@link JVertex#NO_LABEL} if the user object is empty.
     */
    public Collection<? extends Label> getListLabels() {
        Collection<Label> result = new ArrayList<Label>();
        for (Label label : getUserObject()) {
            result.add(label);
        }
        if (result.isEmpty()) {
            result = Collections.singleton((Label) NO_LABEL);
        }
        return result;
    }

    /**
     * If the value is a collection or a string, loads the user object from it.
     */
    @Override
    public void setUserObject(Object value) {
        EditableContent myObject = getUserObject();
        if (value instanceof EditableContent) {
            myObject.load((EditableContent) value);
        } else {
            myObject.load(value.toString());
        }
    }

    @Override
    public EditableContent getUserObject() {
        if (!this.userObjectSet) {
            this.userObjectSet = true;
            super.setUserObject(createUserObject());
        }
        return (EditableContent) super.getUserObject();
    }

    /**
     * Callback factory method to create a user object. Called lazily in
     * {@link #getUserObject()}.
     */
    protected EditableContent createUserObject() {
        return new EditableContent(true);
    }

    @Override
    protected AttributeMap createAttributes() {
        AttributeMap result = super.createAttributes();
        GraphConstants.setEditable(result, true);
        GraphConstants.setMoveable(result, true);
        return result;
    }

    @Override
    public final boolean hasError() {
        return this.error;
    }

    /** Sets the error flag of this vertex. */
    final public void setError(boolean error) {
        this.error = error;
    }

    private boolean error;

    /** Flag indicating that the user object has been initialised. */
    private boolean userObjectSet;
}
