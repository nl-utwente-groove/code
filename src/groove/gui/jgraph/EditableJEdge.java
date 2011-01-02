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
 * $Id: EditableJEdge.java,v 1.7 2008-01-30 09:33:11 iovka Exp $
 */
package groove.gui.jgraph;

import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * J-Graph edge for the editor. This has a {@link EditableContent} as user
 * object, which can be loaded from a string or set of strings.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EditableJEdge extends JEdge implements EditableJCell {
    /** Constructs a new, empty empty j-edge. */
    public EditableJEdge(EditorJModel jModel) {
        super(jModel);
        super.setUserObject(new EditableContent(false));
    }

    /** Constructs a j-edge by cloning another one. */
    public <N extends Node,E extends Edge<N>> EditableJEdge(
            EditorJModel jModel, GraphJEdge<N,E> other) {
        super(jModel);
        getAttributes().applyMap(other.getAttributes());
        List<Label> labelList = new ArrayList<Label>();
        for (E edge : other.getEdges()) {
            labelList.add(edge.label());
        }
        getUserObject().load(labelList);
    }

    /** This implementation just returns the user object. */
    public List<StringBuilder> getLines() {
        List<StringBuilder> result = new ArrayList<StringBuilder>();
        for (Label label : getUserObject()) {
            result.add(new StringBuilder(label.toString()));
        }
        return result;
    }

    /** This implementation just returns the user object. */
    public Collection<? extends Label> getListLabels() {
        return getUserObject();
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

    /** Specialises the return type. */
    @Override
    public EditableContent getUserObject() {
        return (EditableContent) super.getUserObject();
    }

    @Override
    protected AttributeMap createAttributes() {
        AttributeMap result = super.createAttributes();
        GraphConstants.setEditable(result, true);
        GraphConstants.setConnectable(result, true);
        GraphConstants.setDisconnectable(result, true);
        return result;
    }

    @Override
    public final boolean hasError() {
        return this.error;
    }

    final public void setError(boolean error) {
        this.error = error;
    }

    private boolean error;
}
