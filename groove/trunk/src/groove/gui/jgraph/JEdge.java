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
 * $Id: JEdge.java,v 1.5 2007-05-20 07:17:49 rensink Exp $
 */
package groove.gui.jgraph;

import groove.util.Converter;
import groove.util.Groove;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultPort;

/**
 * JGraph edge with a set of string labels as its user object,
 * in the form of a fixed {@link groove.gui.jgraph.JUserObject}.
 * The labels are edited as multiline text but printed as a
 * comma-separated list, since the edge view cannot handle
 * multiline labels.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
abstract public class JEdge extends DefaultEdge implements JCell {
    /**
     * Creates an edge with a {@link JUserObject} as its user object.
     */
    JEdge() {
        getUserObject().setAllowEmptyLabelSet(false);
    }
//    
//    /** Initializes a j-edge with a given user object. */
//    protected JEdge(JUserObject userObject) {
//    	this();
//    	setUserObject(userObject);
//    }

    /**
     * This implementation delegates the method to the user object.
     */
    @Override
    public String toString() {
        return getUserObject().toString();
    }

    /** Returns the j-vertex that is the parent of the source port of this j-edge. */
    JVertex getSourceVertex() {
		return (JVertex) ((DefaultPort) getSource()).getParent();
    }

    /** Returns the j-vertex that is the parent of the target port of this j-edge. */
    JVertex getTargetVertex() {
		return (JVertex) ((DefaultPort) getTarget()).getParent();
    }
    
    /** A j-edge is visible if its source and target nodes are visible. */
	public boolean isVisible() {
		return getSourceVertex().isVisible() && getTargetVertex().isVisible();
	}

    /** 
     * This implementation always returns <code>true</code>.
     */
    public boolean isListable() {
    	return true;
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
    	return new JUserObject(this, PRINT_SEPARATOR, false);
    }

    /**
     * Returns the tool tip text for this edge.
     */
    public String getToolTipText() {
    	return htmlTag.on(getEdgeDescription() + getLabelDescription());
    }
    
    /**
     * Callback method from {@link #getToolTipText()} to describe the
     * edge: kind of edge, singular or multiple.
     */
    String getEdgeDescription() {
        String result;
        if (getLabelSet().size() <= 1) {
        	result = "Singular "+getEdgeKindDescription();
        } else {
            result = "Multiple "+getEdgeKindDescription()+"s";
        }
    	return result;
    }
    
    /**
     * Callback method from {@link #getEdgeDescription()} to describe the
     * kind of edge.
     */
    String getEdgeKindDescription() {
        return getTarget() == null ? "self-edge" : "edge";
    }
    
    /**
     * Callback method from {@link #getToolTipText()} to describe the labels
     * on this edge.
     */
    String getLabelDescription() {
    	StringBuffer result = new StringBuffer();
    	String[] displayedLabels = new String[getLabelSet().size()];
    	int labelIndex = 0;
    	for (Object label: getLabelSet()) {
    		displayedLabels[labelIndex] = strongTag.on(label.toString(), true);
    		labelIndex++;
    	}
        if (displayedLabels.length == 0) {
            result.append(" (unlabelled)");
        } else {
            result.append(", labelled ");
        	result.append(Groove.toString(displayedLabels, "", "", ", ", " and "));
        }
        return result.toString();
    }

    /**
     * Constructs a new edge, with cloned attributes and user object.
     */
    @Override
    public JEdge clone() {
        JEdge result = (JEdge) super.clone();
        result.getAttributes().applyMap(getAttributes());
        result.userObject = getUserObject().clone();
        return result;
    }

    /** Flag indicating that the user object has been initialised. */
    private boolean userObjectSet;
    
    /**
     * The character used to separate graph labels.
     */
    static public final String EDIT_SEPARATOR = JUserObject.NEWLINE;
    /**
     * The string used to separate arguments when preparing for editing.
     */
    static public final String PRINT_SEPARATOR = ", ";
    /**
     * HTML formatting tag for the tool tip text
     */
    static final Converter.HTMLTag strongTag = Converter.createHtmlTag("strong");
    /**
     * HTML formatting tag for the tool tip text
     */
    static final Converter.HTMLTag htmlTag = Converter.createHtmlTag("html");
}