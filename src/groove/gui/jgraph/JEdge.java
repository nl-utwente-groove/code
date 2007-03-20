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
 * $Id: JEdge.java,v 1.1.1.1 2007-03-20 10:05:32 kastenberg Exp $
 */
package groove.gui.jgraph;

import groove.util.Converter;
import groove.util.Groove;

import java.util.Collection;
import java.util.Map;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.GraphConstants;

/**
 * JGraph edge with a set of string labels as its user object,
 * in the form of a fixed {@link groove.gui.jgraph.JUserObject}.
 * The labels are edited as multiline text but printed as a
 * comma-separated list, since the edge view cannot handle
 * multiline labels.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
abstract public class JEdge extends DefaultEdge implements JCell {
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
    static protected final Converter.HTMLTag strongTag = Converter.createHtmlTag("strong");
    /**
     * HTML formatting tag for the tool tip text
     */
    static protected final Converter.HTMLTag htmlTag = Converter.createHtmlTag("html");

    /**
     * Creates an edge with a {@link JUserObject} as its user object.
     */
    public JEdge() {
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
    public String toString() {
        return getUserObject().toString();
    }

	/**
     * Returns the label set from the user object.
     */
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
////        } else if (value instanceof JUserObject) {
////        	super.setUserObject(value);
//        } else if (value instanceof Collection) {
//            if (value != getUserObject()) {
//                getUserObject().load((Collection) value);
//            }
//        } else {
//            getUserObject().load(value.toString());
//        }
//    }

    /**
     * Adapts the method so the value attribute is never changed
     * (but is always set to the immutable user object)
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
     * Returns the tool tip text for this edge.
     */
    public String getToolTipText() {
    	return htmlTag.on(getEdgeDescription() + getLabelDescription());
    }
    
    /**
     * Callback method from {@link #getToolTipText()} to describe the
     * edge: kind of edge, singular or multiple.
     */
    protected String getEdgeDescription() {
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
    protected String getEdgeKindDescription() {
        return getTarget() == null ? "self-edge" : "edge";
    }
    
    /**
     * Callback method from {@link #getToolTipText()} to describe the labels
     * on this edge.
     */
    protected String getLabelDescription() {
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
    public JEdge clone() {
        JEdge result = (JEdge) super.clone();
        result.getAttributes().applyMap(getAttributes());
        result.userObject = getUserObject().clone();
        return result;
    }

    /** Flag indicating that the user object has been initialised. */
    private boolean userObjectSet;
}