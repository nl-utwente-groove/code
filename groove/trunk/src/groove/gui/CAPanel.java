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
 * $Id: CAPanel.java,v 1.18 2008-03-18 12:18:19 fladder Exp $
 */
package groove.gui;

import groove.control.ControlAutomaton;
import groove.control.ControlView;
import groove.gui.jgraph.ControlJGraph;
import groove.gui.jgraph.ControlJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JGraph;
import groove.gui.layout.Layouter;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.NameLabel;
import groove.trans.RuleMatch;
import groove.view.DefaultGrammarView;
import groove.view.FormatException;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;

/**
 * The Simulator panel that shows the control program and the corresponding 
 * ControlAutomaton.
 * 
 * @author Tom Staijen
 * @version $0.9$
 */
public class CAPanel extends JPanel implements SimulationListener {

	Simulator simulator;
	AutomatonPanel autPanel;
	JTextPane textPanel;
	DefaultGrammarView grammar;	
	
	JButton editButton, doneButton; //, saveButton;
	
	/**
	 * @param simulator The Simulator the panel is added to.
	 */
	public CAPanel(Simulator simulator)
	{
		super();
		this.simulator = simulator;
		
		// create the layout for this JPanel
		this.setLayout(new BorderLayout());
		JToolBar toolBar = new JToolBar();

		editButton = new JButton("Edit");
		toolBar.add(editButton);
		editButton.addActionListener(new EditButtonListener());

		doneButton = new JButton("Done");
		toolBar.add(doneButton);
		doneButton.addActionListener(new DoneButtonListener());
		doneButton.setEnabled(false);

		this.add(toolBar, BorderLayout.NORTH);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(300);
		autPanel = new AutomatonPanel(simulator);
		splitPane.add(autPanel);
		
		textPanel = new JTextPane();
		splitPane.add(new JScrollPane(textPanel));
		textPanel.setFont(textPanel.getFont().deriveFont((float)16));
		textPanel.setEditable(false);
		textPanel.setEnabled(false);
		this.add(splitPane, BorderLayout.CENTER);
		simulator.addSimulationListener(this);
		
	}
	
	/** 
	 * Needed by Simulator because this is not a JGraphPanel
	 * @return the JGraphPanel in the JSplitPane
	 */
	public JGraphPanel<?> getJGraphPanel() {
		return autPanel;
	}

	/**
	 * We do nothing when a transition is applied
	 */
	public void applyTransitionUpdate(GraphTransition transition) {
//        // do nothing
	}

	public void setGrammarUpdate(DefaultGrammarView grammar) {
		this.grammar = grammar;
		
		autPanel.getJGraph().setModel(ControlJModel.EMPTY_CONTROL_JMODEL);
		autPanel.getJGraph().setEnabled(false);
		autPanel.setEnabled(false);
		textPanel.setText("");

		if( grammar.getControl() != null ) {
			try {
				ControlView cv = grammar.getControl();
				ControlAutomaton automaton = cv.toAutomaton(grammar.toGrammar());
				GraphJModel model = new ControlJModel(automaton, autPanel.getOptions());
				autPanel.setEnabled(true);
				autPanel.refreshStatus();

				JGraph jGraph = autPanel.getJGraph();
				jGraph.setModel(model);
				jGraph.setEnabled(true);
				jGraph.setToolTipEnabled(true);

				textPanel.setText(cv.getProgram());				
			} catch (FormatException exc) {
				// do nothing
			}
		}
	}

	public void setRuleUpdate(NameLabel name) {
		// noting happens
	}

	public void setStateUpdate(GraphState state) {
//		if( state.getLocation() != null ) {
//			
//			// thus, there is control
//			
//			// disable the active transition
//			// autPanel.getJModel().setActiveTransition(null);
//			
//			// set the active Location (set of control states)
//
//			// emphasize state if it isn't already done
//			autPanel.getJModel().setActiveLocation((Location)state.getLocation());
//			// we do layouting here because it's too expensive to do it
//			// every time a new state is added
//			if (autPanel.getJGraph().getLayouter() != null) {
//				autPanel.getJModel().freeze();
//				autPanel.getJGraph().getLayouter().start(false);
//			}
//			// addUpdate(lts, state);
//			//autPanel.getJGraph().scrollTo(state);
//
//		}
	}

	public void setMatchUpdate(RuleMatch match) {
        // nothing happens
    }

    public void setTransitionUpdate(GraphTransition transition) {
	    // nothing happens
	}

	public void startSimulationUpdate(GTS gts) {
        // nothing happens
	}


	class DoneButtonListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e)
		{
			ControlView cv = CAPanel.this.grammar.getControl();
			
			String program = CAPanel.this.textPanel.getText();
			if( program == null || program.length() == 0) {
				return;
			} 
			if( cv == null ) {
				// return;
				// we had no CV yet.
			}
			//cv.setProgram(CAPanel.this.textPanel.getText());
			
			CAPanel.this.simulator.handleSaveControl(program);
			CAPanel.this.simulator.doRefreshGrammar();
			
//			if( CAPanel.this.grammar.getControl().toAutomaton() != null ) {
				CAPanel.this.textPanel.setEditable(false);
				CAPanel.this.textPanel.setEnabled(false);
				CAPanel.this.editButton.setEnabled(true);
				CAPanel.this.doneButton.setEnabled(false);
//				CAPanel.this.saveButton.setEnabled(true);
//			}
		}
	}

	class EditButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			CAPanel.this.textPanel.setEditable(true);
			CAPanel.this.textPanel.setEnabled(true);
			CAPanel.this.editButton.setEnabled(false);
			CAPanel.this.doneButton.setEnabled(true);
//			CAPanel.this.saveButton.setEnabled(false);
		}
	}
	
	class SaveButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//CAPanel.this.simulator.handleSaveControl();
			//CAPanel.this.saveButton.setEnabled(false);
		}
	}
	
}
	
class AutomatonPanel extends JGraphPanel<ControlJGraph> 
{	
	private Layouter layouter;

	/**
	 * The constructor of this panel creates a panel with the Control Automaton of the current grammar.
	 * @param simulator
	 */
	public AutomatonPanel(Simulator simulator){
		super(new ControlJGraph(), true , simulator.getOptions());
		this.getJGraph().setConnectable(false);
		this.getJGraph().setDisconnectable(false);
		this.getJGraph().setEnabled(false);
		layouter = new MyForestLayouter().newInstance(getJGraph());
		this.getJGraph().setLayouter(layouter);
		getJGraph().setToolTipEnabled(true);
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
	class MyForestLayouter extends groove.gui.layout.ForestLayouter {
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
	        if( jModel.getGraph() == null ) {
	        	System.err.println("jModel has no underlying graph...");
	        }
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
