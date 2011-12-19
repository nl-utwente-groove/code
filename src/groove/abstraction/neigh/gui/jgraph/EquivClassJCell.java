/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.abstraction.neigh.gui.jgraph;

import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.Element;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.JAttr;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

/**
 * @author Eduardo Zambon
 */
public class EquivClassJCell extends DefaultGraphCell implements GraphJCell {

    private boolean layoutable;
    private ShapeJGraph jgraph;

    private EquivClassJCell(ShapeJGraph jgraph, EquivClass<ShapeNode> ec) {
        this.jgraph = jgraph;
        this.setUserObject(ec);
        this.setAttributes(this.createAttributes());
    }

    @Override
    @SuppressWarnings("unchecked")
    public EquivClass<ShapeNode> getUserObject() {
        return (EquivClass<ShapeNode>) super.getUserObject();
    }

    @Override
    public ShapeJGraph getJGraph() {
        return this.jgraph;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public List<StringBuilder> getLines() {
        return null;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public Collection<? extends Element> getKeys() {
        return Collections.emptyList();
    }

    @Override
    final public boolean isLayoutable() {
        return this.layoutable;
    }

    @Override
    final public boolean setLayoutable(boolean layedOut) {
        boolean result = layedOut != this.layoutable;
        if (result) {
            this.layoutable = layedOut;
        }
        return result;
    }

    @Override
    public boolean isGrayedOut() {
        return false;
    }

    @Override
    public boolean setGrayedOut(boolean gray) {
        return false;
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public String getToolTipText() {
        return null;
    }

    @Override
    public void refreshAttributes() {
        // Empty by design.
    }

    /** 
     * Factory method, in case this object is used as a prototype.
     * Returns a fresh {@link EquivClassJCell} of the same type as this one. 
     */
    public EquivClassJCell newJCell(EquivClass<ShapeNode> ec) {
        return new EquivClassJCell(getJGraph(), ec);
    }

    /**
     * Callback method for creating the core attributes.
     * These might be modified by other parameters; don't call this
     * method directly.
     */
    private AttributeMap createAttributes() {
        return DEFAULT_EC_ATTR;
    }

    /**
     * The standard jgraph attributes used for representing equivalence classes.
     */
    public static final JAttr.AttributeMap DEFAULT_EC_ATTR;

    static {
        DEFAULT_EC_ATTR = new JAttr.AttributeMap();
        GraphConstants.setBounds(DEFAULT_EC_ATTR, new Rectangle2D.Double(20,
            20, 40, 20));
        GraphConstants.setAutoSize(DEFAULT_EC_ATTR, true);
        GraphConstants.setGroupOpaque(DEFAULT_EC_ATTR, true);
        GraphConstants.setInset(DEFAULT_EC_ATTR, 8);
        GraphConstants.setBorder(DEFAULT_EC_ATTR, JAttr.NESTED_BORDER);
    }

    /** Returns a prototype {@link EquivClassJCell} for a given {@link ShapeJGraph}. */
    public static EquivClassJCell getPrototype(ShapeJGraph jGraph) {
        return new EquivClassJCell(jGraph, null);
    }
}
