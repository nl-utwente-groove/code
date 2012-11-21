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
import groove.gui.jgraph.AbstractJCell;
import groove.gui.jgraph.GraphJCell;
import groove.gui.look.Look;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Eduardo Zambon
 */
public class EquivClassJCell extends AbstractJCell {
    private EquivClassJCell() {
        setLayoutable(true);
    }

    @Override
    public Collection<? extends GraphJCell> getContext() {
        return Collections.emptyList();
    }

    /** Returns the equivalence class wrapped in this JCell. */
    @SuppressWarnings("unchecked")
    public EquivClass<ShapeNode> getEquivClass() {
        return (EquivClass<ShapeNode>) getUserObject();
    }

    /** Sets the equivalence class wrapped in this JCell. */
    public void setEquivClass(EquivClass<ShapeNode> ec) {
        setUserObject(ec);
        initialise();
    }

    @Override
    public ShapeJGraph getJGraph() {
        return (ShapeJGraph) super.getJGraph();
    }

    @Override
    public Collection<? extends Element> getKeys() {
        return Collections.emptyList();
    }

    @Override
    public String getToolTipText() {
        return null;
    }

    @Override
    protected Look getStructuralLook() {
        return Look.EQUIV_CLASS;
    }

    /** 
     * Returns a fresh, uninitialised instance of this class.
     * To initialise, set the JModel and the user object
     */
    public static EquivClassJCell newInstance() {
        return new EquivClassJCell();
    }
}
