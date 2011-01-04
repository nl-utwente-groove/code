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
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * J-Graph vertex for the editor. This has a {@link StringObject} as user
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
        setUserObject(null);
        setNumber(nr);
    }

    /** Constructs a jvertex by cloning another one. */
    public EditableJVertex(EditorJModel jModel, AspectJVertex other) {
        this(jModel, other.getNumber());
        this.proxy = other;
        getUserObject().addLabels(this.proxy.getNode().getNodeLabels());
        getUserObject().addEdges(this.proxy.getSelfEdges());
        refreshAttributes();
    }

    /** This implementation just returns the user object. */
    public List<StringBuilder> getLines() {
        List<StringBuilder> result;
        if (hasError() || this.proxy == null) {
            result = new ArrayList<StringBuilder>();
            for (String label : getUserObject()) {
                result.add(Converter.toHtml(new StringBuilder(label)));
            }
        } else {
            result = this.proxy.getLines();
        }
        return result;
    }

    /**
     * This implementation just returns the user object, or a singleton
     * containing {@link JVertex#NO_LABEL} if the user object is empty.
     */
    public Collection<? extends Label> getListLabels() {
        if (hasError() || this.proxy == null) {
            Collection<Label> result = new ArrayList<Label>();
            for (String text : getUserObject()) {
                result.add(DefaultLabel.createLabel(text));
            }
            if (result.isEmpty()) {
                result = Collections.singleton((Label) NO_LABEL);
            }
            return result;
        } else {
            return this.proxy.getListLabels();
        }
    }

    /**
     * Creates a new used object, and initialises it from a given value.
     * If the value is a collection or a string, loads the user object from it.
     */
    @Override
    public void setUserObject(Object value) {
        // we do need to create a new object, otherwise undos do not work
        StringObject myObject = new StringObject(false);
        if (value instanceof StringObject) {
            myObject.addAll((StringObject) value);
        } else if (value != null) {
            myObject.load(value.toString());
        }
        super.setUserObject(myObject);
    }

    @Override
    public StringObject getUserObject() {
        return (StringObject) super.getUserObject();
    }

    @Override
    protected AttributeMap createAttributes() {
        AttributeMap result;
        if (hasError() || this.proxy == null) {
            result = super.createAttributes();
        } else {
            result = new AttributeMap(this.proxy.getAttributes());
        }
        GraphConstants.setEditable(result, true);
        GraphConstants.setMoveable(result, true);
        return result;
    }

    /** Sets the proxy vertex, from which this one borrows its attributes. */
    public void setProxy(AspectJVertex proxy) {
        this.proxy = proxy;
        refreshAttributes();
        // note that we do not change the user object.
    }

    /** Returns the aspect nodeof the proxy, or
     * {@link AspectKind#NONE} if there is no proxy.
     */
    public AspectNode getNode() {
        return this.proxy == null ? null : this.proxy.getNode();
    }

    @Override
    public final boolean hasError() {
        if (this.proxy == null) {
            return this.error;
        } else {
            return this.proxy.hasError();
        }
    }

    /** Sets the error flag of this vertex. */
    final public void setError(boolean error) {
        this.error = error;
    }

    private boolean error;

    /** The aspect vertex from which we get our data. */
    private AspectJVertex proxy;
}
