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
 * $Id: JEdge.java,v 1.13 2008-01-09 16:17:49 rensink Exp $
 */
package groove.gui.jgraph;

import static groove.util.Converter.HTML_TAG;
import static groove.util.Converter.STRONG_TAG;
import groove.util.Converter;
import groove.util.Groove;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultPort;

/**
 * JGraph edge with a set of string labels as its user object,
 * in the form of a fixed {@link groove.gui.jgraph.JCellContent}.
 * The labels are edited as multiline text but printed as a
 * comma-separated list, since the edge view cannot handle
 * multiline labels.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class JEdge extends DefaultEdge implements JCell {
    /**
     * This implementation delegates the method to the user object.
     */
    public String getText() {
    	StringBuilder result = new StringBuilder();
    	for (StringBuilder line: getLines()) {
    		if (result.length() > 0) {
    			result.append(PRINT_SEPARATOR);
    		}
    		result.append(line);
    	}
    	return result.toString();
    }

	/** Returns the j-vertex that is the parent of the source port of this j-edge. */
    public JVertex getSourceVertex() {
    	DefaultPort source = (DefaultPort) getSource();
		return source == null ? null : (JVertex) source.getParent();
    }

    /** Returns the j-vertex that is the parent of the target port of this j-edge. */
    public JVertex getTargetVertex() {
    	DefaultPort target = (DefaultPort) getTarget();
		return target == null ? null : (JVertex) target.getParent();
    }
    
    /** A j-edge is visible if it is not a source label, and its source and target nodes are visible. */
	public boolean isVisible() {
		JVertex sourceVertex = getSourceVertex();
		if (sourceVertex == null) {
			return false;
		}
		JVertex targetVertex = getTargetVertex();
		if (targetVertex == null) {
			return false;
		}
		return sourceVertex.isVisible() && targetVertex.isVisible();
	}

    /** 
     * This implementation always returns <code>true</code>.
     */
    public boolean isListable() {
    	return true;
    }
    
    @Override
	@SuppressWarnings("unchecked")
    public JCellContent<?> getUserObject() {
    	if (! userObjectSet) {
    		userObjectSet = true;
    		super.setUserObject(createUserObject());
    	}
    	return (JCellContent) super.getUserObject();
    }

    /** 
     * Overrides the super method to test for the type of the parameter 
     * (which should be {@link JCellContent}) and records that the object has been set. 
     */
	@Override
	public void setUserObject(Object userObject) {
		if (!(userObject instanceof JCellContent)) {
			throw new IllegalArgumentException(String.format("Cannot set user object %s: incorrect type %s", userObject, userObject.getClass()));
		}
		super.setUserObject(userObject);
		userObjectSet = true;
	}

    /**
     * Callback factory method to create a user object.
     * Called lazily in {@link #getUserObject()}.
     */
    abstract JCellContent<?> createUserObject();

    /**
     * Returns the tool tip text for this edge.
     */
    public String getToolTipText() {
		return HTML_TAG.on(getEdgeDescription()).toString(); // + getLabelDescription());
    }
    
    /**
     * Callback method from {@link #getToolTipText()} to describe the
     * edge: kind of edge, singular or multiple.
     * The resulting string is interpreted as HTML formatted
     */
    StringBuilder getEdgeDescription() {
    	StringBuilder result = getEdgeKindDescription();
    	if (getListLabels().size() > 1) {
    		Converter.toUppercase(result, false);
    		result.insert(0, "Multiple ");
    		result.append("s");
    	}
    	return result;
    }
    
    /**
     * Callback method from {@link #getEdgeDescription()} to describe the
     * kind of edge.
     */
    StringBuilder getEdgeKindDescription() {
        return new StringBuilder("Graph edge");
    }
    
    /**
     * Callback method from {@link #getToolTipText()} to describe the labels
     * on this edge.
     */
    String getLabelDescription() {
    	StringBuffer result = new StringBuffer();
    	String[] displayedLabels = new String[getListLabels().size()];
    	int labelIndex = 0;
    	for (Object label: getListLabels()) {
    		displayedLabels[labelIndex] = STRONG_TAG.on(label.toString(), true);
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
     * The string used to separate arguments when preparing for editing.
     */
    static public final String PRINT_SEPARATOR = ", ";
}