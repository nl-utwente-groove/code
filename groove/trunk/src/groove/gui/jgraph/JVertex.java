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
 * $Id: JVertex.java,v 1.7 2007-05-21 22:19:16 rensink Exp $
 */
package groove.gui.jgraph;

import groove.util.Converter;
import groove.util.Groove;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;

/**
 * JGraph vertex with a single port, and a fixed set of labels as a user object (through
 * {@link groove.gui.jgraph.JUserObject}).
 * @author Arend Rensink
 * @version $Revision: 1.7 $
 */
abstract public class JVertex extends DefaultGraphCell implements JCell {
    /**
     * Creates a vertex with a {@link JUserObject}as its user object.
     */
    JVertex() {
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
    	String userObjectString = getUserObject().toString();
    	return Converter.toHtml(userObjectString);
//    	if (userObjectString.length() > 0) {
//    		return strongTag.on(userObjectString, true);
//    	} else {
//    		return userObjectString;
//    	}
    }

    /** 
     * This implementation always returns <code>true</code>.
     */
    public boolean isVisible() {
    	return true;
    }

    /** 
     * This implementation always returns <code>true</code>.
     */
    public boolean isListable() {
    	return true;
    }

    /**
     * This implementation delegates the query to the user object.
     */
    @Override
    public String toString() {
        return getUserObject().toString();
    }

	/**
     * Returns the collection of elements of the user object, converted to strings
     * using {@link #getLabel(Object)}
     */
    public Collection<String> getLabelSet() {
        Set<String> result = new LinkedHashSet<String>();
        for (Object obj: getUserObject()) {
        	result.add(getLabel(obj));
        }
        return result;
    }

    /** 
     * Callback method to get the text that is to be printed in the 
     * j-vertex for a given object in the label set.
     * @param object an object from the user object (hence of the type
     * of the user object's elements)
     */
    public String getLabel(Object object) {
    	return object.toString();
    }

    @Override
    public JUserObject<?> getUserObject() {
    	if (! userObjectSet) {
    		userObjectSet = true;
    		super.setUserObject(createUserObject());
    	}
    	return (JUserObject) super.getUserObject();
    }

    /** 
     * Overrides the super method to test for the type of the parameter 
     * (which should be {@link JUserObject}) and records that the object has been set. 
     */
	@Override
	public void setUserObject(Object userObject) {
		if (!(userObject instanceof JUserObject)) {
			throw new IllegalArgumentException(String.format("Cannot set user object %s: incorrect type %s", userObject, userObject.getClass()));
		}
		super.setUserObject(userObject);
		userObjectSet = true;
	}

	/**
     * Callback factory method to create a user object.
     * Called lazily in {@link #getUserObject()}.
     */
    JUserObject<?> createUserObject() {
    	return new JUserObject(this, JUserObject.NEWLINE, true);
    }

    /**
     * Returns the tool tip text for this vertex.
     */
    public String getToolTipText() {
//        StringBuilder res = new StringBuilder();
//        res.append(getNodeDescription());
//        Collection<String> labelSet = getLabelSet();
//        if (labelSet.size() == 1) {
//            // cell has a non-empty label set; this indicates self-edges
//            res.append(" with label ");
//        } else if (labelSet.size() > 1) {
//            res.append(" with labels ");
//        }
//        res.append(Groove.toString(strongTag.on(labelSet.toArray(), true), "", "", ", ", " and "));
        return htmlTag.on(getNodeDescription());
    }
    
    /**
     * Hook to allow subclasses to give a more precise description of the node.
     */
    StringBuilder getNodeDescription() {
    	return new StringBuilder("Graph node");
    }

    /**
     * Constructs a new jvertex, with cloned attributes and user object.
     */
    @Override
    public JVertex clone() {
        JVertex result = (JVertex) super.clone();
        result.getAttributes().applyMap(getAttributes());
        result.userObject = getUserObject().clone();
        return result;
    }

    /** Flag indicating that the user object has been initialised. */
    private boolean userObjectSet;
    
	/** HTML tag to make text bold. */
    static Converter.HTMLTag strongTag = Converter.createHtmlTag("strong");
	/** HTML tag to indicate HTML formatting. */
    static Converter.HTMLTag htmlTag = Converter.createHtmlTag("html");
}