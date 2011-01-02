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
 * JGraph vertex with a single port and added functionality for display.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class JVertex extends DefaultGraphCell implements JCell {
    /**
     * Creates a vertex for a given {@link JModel}.
     * @param nr the number of this vertex
     */
    JVertex(JModel jModel, int nr) {
        this.jModel = jModel;
        this.nr = nr;
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

    /** Sets the number of this vertex. */
    public void setNumber(int nr) {
        this.nr = nr;
    }

    /** Returns the number with which this vertex was initialised. */
    public int getNumber() {
        return this.nr;
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
    private int nr;
    private boolean grayedOut;
    private boolean emphasised;
}