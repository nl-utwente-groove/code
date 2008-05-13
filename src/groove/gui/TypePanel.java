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
 * $Id$
 */
package groove.gui;

import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.StateJGraph;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.NameLabel;
import groove.type.TypeReconstructor;
import groove.util.Groove;
import groove.view.DefaultGrammarView;
import groove.view.FormatException;

import java.io.IOException;

/**
 * @author Frank van Es
 * @version $Revision $
 */
public class TypePanel extends JGraphPanel<StateJGraph> implements SimulationListener {
	/** Display name of this panel. */
    public static final String FRAME_NAME = "Type graph";
	
    // --------------------- INSTANCE DEFINITIONS ----------------------
    
	/**
	 * @param simulator
	 */
	public TypePanel(final Simulator simulator) {
		super(new StateJGraph(simulator), true, simulator.getOptions());
		this.simulator = simulator;
		simulator.addSimulationListener(this);
	}
	
	/** Does nothing (according to contract, the grammar has already been set). */
    public synchronized void startSimulationUpdate(GTS gts) {}
    public synchronized void setStateUpdate(GraphState state) {}
    public synchronized void setTransitionUpdate(GraphTransition trans) {}
    public synchronized void applyTransitionUpdate(GraphTransition transition) {}
    public synchronized void setRuleUpdate(NameLabel rule) {}
    
    public synchronized void setGrammarUpdate(DefaultGrammarView grammar) {
    	if (grammar == null || grammar.getStartGraph() == null) {
            jGraph.setModel(AspectJModel.EMPTY_ASPECT_JMODEL);
            setEnabled(false);
        } else {
        	try {
        		Graph typeGraph = TypeReconstructor.reconstruct(grammar.toModel());
        		
        		Groove.saveGraph(typeGraph,simulator.getCurrentGrammarFile().getAbsolutePath() + "/typeGraph");
        		GraphInfo.setName(typeGraph, "Type graph");
        		
        		jGraph.setModel(GraphJModel.newInstance(typeGraph, getOptions()));
        	}
        	catch (FormatException fe) {}
        	catch (IOException ioe) {
        		System.err.println("Error storing the type graph.");
        	}
        	catch (NullPointerException npe) {
        		System.err.println("Type graph cannot be displayed for this model.");
        	}
            setEnabled(true);
        }
        refreshStatus();
    }
    
    /** The simulator to which this panel belongs. */
	private final Simulator simulator;
}
