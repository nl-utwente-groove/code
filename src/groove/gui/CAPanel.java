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
 * $Id: CAPanel.java,v 1.13 2007-10-10 08:59:44 rensink Exp $
 */
package groove.gui;

import groove.control.ControlAutomaton;
import groove.control.ControlState;
import groove.control.ControlTransition;
import groove.control.ControlView;
import groove.gui.jgraph.ControlJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JGraph;
import groove.gui.layout.Layouter;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.NameLabel;
import groove.view.DefaultGrammarView;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;

public class CAPanel extends JPanel  implements SimulationListener {

	public CAPanel(Simulator simulator)
	{
		super();
		this.setLayout(new BorderLayout());
		JToolBar toolBar = new JToolBar();
		JButton parseButton = new JButton("parse");
		toolBar.add(parseButton, BorderLayout.SOUTH);
		parseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				ControlView cv = CAPanel.this.grammar.getControl();
				String program = CAPanel.this.textPanel.getText();
				if( program == null )
					return;
				
				if( cv == null ) {
					cv = new ControlView();
					cv.initScope(grammar);
				}

				cv.setProgram(CAPanel.this.textPanel.getText());
				cv.loadProgram();
				CAPanel.this.simulator.getCurrentGrammar().setControl(cv);
				CAPanel.this.simulator.setGrammar(CAPanel.this.simulator.getCurrentGrammar());
				CAPanel.this.simulator.handleSaveControl(cv.program());
			}
		}
		);
		
		this.add(toolBar, BorderLayout.NORTH);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(300);
		splitPane.add(autPanel = new AutomatonPanel(simulator));
		splitPane.add(textPanel = new JTextPane());
		this.add(splitPane, BorderLayout.CENTER);
		
		simulator.addSimulationListener(this);
		this.simulator = simulator;
	}
	
	/** 
	 * Needed by Simulator because this is not a JGraphPanel
	 * @return the JGraphPanel in the JSplitPane
	 */
	public JGraphPanel<?> getJGraphPanel() {
		return autPanel;
	}

	public void applyTransitionUpdate(GraphTransition transition) {
        setStateUpdate(transition.target());
	}

	public void setGrammarUpdate(DefaultGrammarView grammar) {
		this.grammar = grammar;
		
		autPanel.getJGraph().setModel(ControlJModel.EMPTY_CONTROL_JMODEL);
		autPanel.getJGraph().setEnabled(false);
		autPanel.setEnabled(false);
		textPanel.setText("");

		if( grammar.getControl() != null )
		{
			ControlView cv = grammar.getControl();
			textPanel.setText(cv.program());
			
			autPanel.setEnabled(true);
			JGraph jGraph = autPanel.getJGraph();

			jGraph.setEnabled(true);
			//GraphJModel model = GraphJModel.newInstance(cv.getAutomaton(), autPanel.getOptions());
			
			GraphJModel model = new ControlJModel(cv.getAutomaton(), autPanel.getOptions());
			
			jGraph.setModel(model);

			autPanel.refreshStatus();
		}
	}

	public void setRuleUpdate(NameLabel name) {
		// noting happens
	}

	public void setStateUpdate(GraphState state) {
		if( state.getControl() != null ) {
			autPanel.getJModel().setActiveTransition(null);
			// emphasize state if it isn't already done
			autPanel.getJModel().setActiveState((ControlState)state.getControl());
			// we do layouting here because it's too expensive to do it
			// every time a new state is added
			if (autPanel.getJGraph().getLayouter() != null) {
				autPanel.getJModel().freeze();
				autPanel.getJGraph().getLayouter().start(false);
			}
			// addUpdate(lts, state);
			//autPanel.getJGraph().scrollTo(state);

		}
	}

	public void setTransitionUpdate(GraphTransition transition) {
    	
		ControlState source = (ControlState) transition.source().getControl();
		
		if( source != null ) {
			autPanel.getJModel().setActiveState(source);
			ControlTransition ct = source.getTransitions(transition.getEvent().getRule()).iterator().next();
			
			ControlTransition parent = ct.getVisibleParent();
			if( parent != null )
				ct = parent;
			
			autPanel.getJModel().setActiveTransition(ct);
		}
	}

	public void startSimulationUpdate(GTS gts) {
		// TODO Auto-generated method stub
	}

	Simulator simulator;
	AutomatonPanel autPanel;
	JTextPane textPanel;
	DefaultGrammarView grammar;	
}
	
class AutomatonPanel extends JGraphPanel<JGraph> 
{	
	private Layouter layouter;
	private ControlAutomaton control;
	
	public AutomatonPanel(Simulator simulator){
		super(new JGraph(ControlJModel.EMPTY_CONTROL_JMODEL,false), true , simulator.getOptions());
		this.getJGraph().setConnectable(false);
		this.getJGraph().setDisconnectable(false);
		this.getJGraph().setEnabled(false);
		layouter = new MyForestLayouter().newInstance(getJGraph());
		this.getJGraph().setLayouter(layouter);
	}

	@Override
	public ControlJModel getJModel()
	{
		return (ControlJModel) super.getJModel();
	}
	
	/**
	 * A specialization of the forest layouter that takes the LTS start graph
	 * as its suggested root.
	 */
	private class MyForestLayouter extends groove.gui.layout.ForestLayouter {
	    /**
	     * Creates a prototype layouter
	     */
	    public MyForestLayouter() {
	        super();
	    }
	
	    /**
	     * Creates a new instance, for a given {@link JGraph}.
	     */
	    public MyForestLayouter(String name, JGraph jgraph) {
	        super(name, jgraph);
	    }
	
	    /**
	     * This method returns a singleton set consisting of the LTS start state.
	     */
	    @Override
	    protected Collection<?> getSuggestedRoots() {
	        ControlJModel jModel = getJModel();
	        return Collections.singleton(jModel.getJCell(jModel.getGraph().startState()));
	    }
	
	    /**
	     * This implementation returns a {@link MyForestLayouter}.
	     */
	    @Override
	    public Layouter newInstance(JGraph jGraph) {
	        return new MyForestLayouter(name, jGraph);
	    }
	}
	
}
