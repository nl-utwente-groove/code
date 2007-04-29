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
 * $Id: StateJGraph.java,v 1.5 2007-04-29 09:22:22 rensink Exp $
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
 * To construct an instance, {@link #fillPopupMenu(JPopupMenu)} should be called
 * after all global final variables have been set.
 */
public class StateJGraph extends JGraph {
    /**
     * Constructs a state graph associated with a given simulator.
     * @param simulator the simulator to which this j-graph is associated
     */
    public StateJGraph(Simulator simulator) {
        super(new GraphJModel(new DefaultGraph(), simulator.getOptions()));
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
    protected void fillPopupMenu(JPopupMenu result) {
		addSeparatorUnlessFirst(result);
		result.add(simulator.getApplyTransitionAction());
		result.addSeparator();
		result.add(simulator.getEditGraphAction());
		super.fillPopupMenu(result);
    }

    /**
	 * Returns the bounds of a set of graph elements.
	 */
    public Rectangle2D getElementBounds(Set<Element> elemSet) {
        Set<JCell> jCellSet = new HashSet<JCell>();
        for (Element elem: elemSet) {
        	JCell jCell = getModel().getJCell(elem);
        	if (jCell != null) {
        		jCellSet.add(jCell);
        	}
        }
        return getCellBounds(jCellSet.toArray());
    }

    /**
     * The simulator to which this j-graph is associated.
     */
    private final Simulator simulator;
}