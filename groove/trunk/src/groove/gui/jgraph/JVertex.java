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
 * $Id: JVertex.java,v 1.1.1.1 2007-03-20 10:05:32 kastenberg Exp $
 */
package groove.gui.jgraph;

import groove.util.Converter;
import groove.util.Groove;

import java.util.Collection;
import java.util.Map;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

/**
 * JGraph vertex with a single port, and a fixed set of labels as a user object (through
 * {@link groove.gui.jgraph.JUserObject}).
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
abstract public class JVertex extends DefaultGraphCell implements JCell {
	/** HTML tag to make text bold. */
    protected static Converter.HTMLTag strongTag = Converter.createHtmlTag("strong");
	/** HTML tag to indicate HTML formatting. */
    protected static Converter.HTMLTag htmlTag = Converter.createHtmlTag("html");

    /**
     * Creates a vertex with a {@link JUserObject}as its user object.
     */
    public JVertex() {
    	// empty constructor
        add(new DefaultPort());
    }

    /**
     * Returns this graph node's one and only port.
     */
    public DefaultPort getPort() {
        return (DefaultPort) getFirstChild();
    }

    /**
     * Returns the text to be displayed on the vertex, in <code>html</code> format.
     * This implementation returns a string description of the
     * user object.
     */
    public String getHtmlText() {
        return strongTag.on(getUserObject().toString(), true);
    }
    
    /**
     * This implementation delegates the query to the user object.
     */
    public String toString() {
        return getUserObject().toString();
    }

    public Collection<String> getLabelSet() {
        return getUserObject().getLabelSet();
    }

    public JUserObject<?> getUserObject() {
    	if (! userObjectSet) {
    		userObjectSet = true;
    		super.setUserObject(createUserObject());
    	}
    	return (JUserObject) super.getUserObject();
    }

    /**
     * Callback factory method to create a user object.
     * Called lazily in {@link #getUserObject()}.
     */
    abstract protected JUserObject<?> createUserObject();
//
//    public void setUserObject(Object value) {
//        if (value == null) {
//            getUserObject().clear();
//        } else if (value instanceof Collection) {
//            if (value != getUserObject()) {
//                getUserObject().load((Collection) value);
//            }
//        } else {
//            getUserObject().load(value.toString());
//        }
//    }

    /**
     * Adapts the method so that, if <tt>change</tt> specifies a change of user object (in its
     * {@link GraphConstants#VALUE}-attribute), then <tt>result</tt> receives a clone of the
     * previous user object.
     */
    @Deprecated
    public Map changeAttributes(Map change) {
        Map result;
        // check if the change specifies a change in user object
        if (change.get(GraphConstants.VALUE) != null) {
            // clone the current user object (before change)
            Object userObjectClone = getUserObject().clone();
            result = super.changeAttributes(change);
            // make sure the current value attribute is identical to the user object
            GraphConstants.setValue(getAttributes(), getUserObject());
            result.put(GraphConstants.VALUE, userObjectClone);
        } else {
            result = super.changeAttributes(change);
            result.remove(GraphConstants.VALUE);
        }
        return result;
    }

    /**
     * Returns the tool tip text for this vertex.
     */
    public String getToolTipText() {
        StringBuffer res = new StringBuffer();
        Collection<String> labelSet = getLabelSet();
        res.append(getNodeDescription());
        if (labelSet.size() == 1) {
            // cell has a non-empty label set; this indicates self-edges
            res.append(" with a self-edge labelled ");
        } else if (labelSet.size() > 1) {
            res.append(" with self-edges, labelled ");
        }
        res.append(Groove.toString(strongTag.on(labelSet.toArray(), true), "", "", ", ", " and "));
        return htmlTag.on(res);
    }
    
    /**
     * Hook to allow subclasses to give a more precise description of the node.
     */
    protected String getNodeDescription() {
    	return "Graph node";
    }

    /**
     * Constructs a new jvertex, with cloned attributes and user object.
     */
    public JVertex clone() {
        JVertex result = (JVertex) super.clone();
        result.getAttributes().applyMap(getAttributes());
        result.userObject = getUserObject().clone();
        return result;
    }

    /** Flag indicating that the user object has been initialised. */
    private boolean userObjectSet;
}