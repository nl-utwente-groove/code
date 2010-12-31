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

import java.awt.Color;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

/**
 * JGraph vertex with a single port, and a fixed set of labels as a user object
 * (through {@link groove.gui.jgraph.JCellContent}).
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class JVertex extends DefaultGraphCell implements JCell {
    /**
     * Creates a vertex for a given {@link JModel},
     * with a {@link JCellContent}as its user object.
     */
    JVertex(JModel jModel) {
        this.jModel = jModel;
        add(new DefaultPort());
    }

    @Override
    public JModel getJModel() {
        return this.jModel;
    }

    /**
     * Returns this graph node's one and only port.
     */
    public DefaultPort getPort() {
        return (DefaultPort) getFirstChild();
    }

    /** Returns the number with which this vertex was initialised. */
    public int getNumber() {
        return getUserObject().getNumber();
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

    @Override
    public String toString() {
        return String.format("JVertex %d with labels %s", getNumber(),
            getListLabels());
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
     * should be {@link JCellContent}) and records that the object has been set.
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

    /** Returns the attributes to be used in displaying this vertex. */
    final public AttributeMap createAttributes(JModel jModel) {
        AttributeMap result = createAttributes();
        if (!jModel.isShowBackground()) {
            GraphConstants.setBackground(result, Color.WHITE);
        }
        if (isGrayedOut()) {
            result.applyMap(JAttr.GRAYED_OUT_ATTR);
        }
        if (getAttributes() != null) {
            getAttributes().applyMap(result);
        }
        return result;
    }

    /**
     * Callback method for creating the core attributes.
     * These might be modified by other parameters; don't call this
     * method directly.
     */
    protected AttributeMap createAttributes() {
        return JAttr.DEFAULT_NODE_ATTR.clone();
    }

    @Override
    final public boolean isGrayedOut() {
        return this.grayedOut;
    }

    @Override
    final public boolean setGrayedOut(boolean grayedOut) {
        boolean result = grayedOut != this.grayedOut;
        if (result) {
            this.grayedOut = grayedOut;
            createAttributes(getJModel());
        }
        return result;
    }

    @Override
    public final boolean isEmphasised() {
        return this.emphasised;
    }

    @Override
    public final boolean setEmphasised(boolean emphasised) {
        boolean oldEmphasised = this.emphasised;
        this.emphasised = emphasised;
        return oldEmphasised != emphasised;
    }

    public boolean hasError() {
        return false;
    }

    private final JModel jModel;
    private boolean grayedOut;
    private boolean emphasised;

    /** Flag indicating that the user object has been initialised. */
    private boolean userObjectSet;
}