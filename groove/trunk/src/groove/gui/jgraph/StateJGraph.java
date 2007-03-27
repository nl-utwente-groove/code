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
 * $Id: StateJGraph.java,v 1.2 2007-03-27 14:18:29 rensink Exp $
 */
package groove.gui.jgraph;

import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import groove.graph.DefaultGraph;
import groove.graph.Element;
import groove.gui.Simulator;

import javax.swing.JPopupMenu;

/**
 * Implementation of {@link JGraph} that provides the proper popup menu.
 * To construct an instance, {@link #initPopupMenu(JPopupMenu)} should be called
 * after all global final variables have been set.
 */
public class StateJGraph extends JGraph {
    /**
     * Constructs a state graph associated with a given simulator.
     * @param simulator the simulator to which this j-graph is associated
     */
    public StateJGraph(Simulator simulator) {
        super(new GraphJModel(new DefaultGraph()));
        setConnectable(false);
        setDisconnectable(false);
        setEnabled(false);
        this.simulator = simulator;
    }
    
    /** Specialises the return type to a {@link JModel}. */
    @Override
    public GraphJModel getModel() {
    	return (GraphJModel) graphModel;
    }

    @Override
    protected void initPopupMenu(JPopupMenu toMenu) {
        if (toMenu != null) {
            addSeparatorUnlessFirst(toMenu);
            toMenu.add(simulator.getApplyTransitionAction());
            toMenu.addSeparator();
            toMenu.add(simulator.getEditGraphAction());
            super.initPopupMenu(toMenu);
        }
    }

    /**
     * Returns the bounds of a set of graph elements.
     */
    public Rectangle2D getElementBounds(Set<Element> elemSet) {
        Set<JCell> jCellSet = new HashSet<JCell>();
        for (Element elem: elemSet) {
            jCellSet.add(getModel().getJCell(elem));
        }
        return getCellBounds(jCellSet.toArray());
    }

    /**
     * The simulator to which this j-graph is associated.
     */
    private final Simulator simulator;
}