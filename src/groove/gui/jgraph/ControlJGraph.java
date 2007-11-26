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
 * $Id: ControlJGraph.java,v 1.2 2007-11-26 08:58:39 fladder Exp $
 */
package groove.gui.jgraph;

import groove.control.ControlShape;
import groove.control.ControlTransition;
import groove.gui.Simulator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jgraph.graph.DefaultGraphCell;

public class ControlJGraph extends JGraph {

	private Simulator simulator;
	
    public ControlJGraph(Simulator simulator) {
    	super(ControlJModel.EMPTY_CONTROL_JMODEL, true);
        this.simulator = simulator;
        addMouseListener(new MyMouseListener());
        getGraphLayoutCache().setSelectsAllInsertedCells(false);
        setEnabled(false);
        
        
    }
    
    private Simulator getSimulator() {
    	return this.simulator;
    }
    
    @Override
    public ControlJModel getModel() {
    	return (ControlJModel) super.getModel();
    }
    
    
    class MyMouseListener extends MouseAdapter {
    	@Override
		public void mousePressed(MouseEvent evt) {
			if (evt.getButton() == MouseEvent.BUTTON1) {
				// scale from screen to model
				java.awt.Point loc = evt.getPoint();
				// find cell in model coordinates
				DefaultGraphCell cell = (DefaultGraphCell) getFirstCellForLocation(loc.x, loc.y);
				if( evt.getClickCount() == 2 ) {
					if (cell instanceof GraphJEdge) {
						ControlTransition edge = (ControlTransition) ((GraphJEdge) cell).getEdge();
						//getSimulator().setTransition(edge);
						if( edge instanceof ControlShape ) {
							getSimulator().getCurrentGrammar().getControl().getAutomaton().toggleActive((ControlShape)edge);
							ControlJGraph.this.getModel().reload();
						}
					}
				}
				
//				if( evt.getButton() == 2 ) {
//					if (cell instanceof GraphJVertex) {
//						GraphState node = (GraphState) ((GraphJVertex) cell).get();
//						if (!getSimulator().getCurrentState().equals(node)) {
//							getSimulator().setState(node);
//						}
//					}
//				}
			}
    	}
    }
}
