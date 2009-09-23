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
 * $Id: JVertex.java,v 1.11 2008-01-30 09:33:11 iovka Exp $
 */
package groove.gui.jgraph;

import groove.util.Converter;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;

/**
 * JGraph vertex with a single port, and a fixed set of labels as a user object
 * (through {@link groove.gui.jgraph.JCellContent}).
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class JVertex extends DefaultGraphCell implements JCell {
    /**
     * Creates a vertex with a {@link JCellContent}as its user object.
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
     * Returns HTML-formatted text, without a surrounding HTML tag.
     */
    public String getText() {
        StringBuilder result = new StringBuilder();
        for (StringBuilder line : getLines()) {
            if (result.length() > 0) {
                result.append(Converter.HTML_LINEBREAK);
            }
            result.append(line);
        }
        return result.toString();
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

    @Override
    public String toString() {
        return "JVertex with labels " + getListLabels();
    }

    @Override
    public JCellContent<?> getUserObject() {
        if (!this.userObjectSet) {
            this.userObjectSet = true;
            super.setUserObject(createUserObject());
        }
        return (JCellContent<?>) super.getUserObject();
    }

    /**
     * Overrides the super method to test for the type of the parameter (which
     * should be {@link JCellContent}) and records that the object has been
     * set.
     */
    @Override
    public void setUserObject(Object userObject) {
        if (!(userObject instanceof JCellContent<?>)) {
            throw new IllegalArgumentException(String.format(
                "Cannot set user object %s: incorrect type %s", userObject,
                userObject.getClass()));
        }
        super.setUserObject(userObject);
        this.userObjectSet = true;
    }

    /**
     * Callback factory method to create a user object. Called lazily in
     * {@link #getUserObject()}.
     */
    abstract JCellContent<?> createUserObject();

    /**
     * Returns the tool tip text for this vertex.
     */
    public String getToolTipText() {
        return Converter.HTML_TAG.on(getNodeDescription()).toString();
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

    /** Pseudo-label for cells with an empty list label set. */
    static public final String NO_LABEL = "\u0000";
}