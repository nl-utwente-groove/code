// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/* 
 * $Id: RulePanel.java,v 1.1.1.2 2007-03-20 10:42:45 kastenberg Exp $
 */
package groove.gui;

import groove.gui.jgraph.*;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.view.RuleGraph;
import groove.trans.view.RuleViewGrammar;
import groove.util.Groove;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JCheckBoxMenuItem;


/**
 * Window that displays and controls the current rule graph.
 * Auxiliary class for Simulator.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class RulePanel extends JGraphPanel<RuleJGraph> implements SimulationListener {
	/** Frame name when no rule is selected. */
    protected static final String INITIAL_FRAME_NAME = "No rule selected";

    /**
     * Constructs a new rule frame on the basis of a given graph.
     */
    public RulePanel(Simulator simulator) {
        super(new RuleJGraph(simulator), true);
        this.simulator = simulator;
        anchorsOptionItem = createAnchorsOptionItem();
        nodeIdsOptionItem = createNodeIdsOptionItem();
        simulator.addSimulationListener(this);
        jGraph.setToolTipEnabled(true);
    }

    /**
     * Returns the simulator to which this component is permanently associated.
     */
    public Simulator getSimulator() {
        return simulator;
    }

    /** 
     * Sets the frame to a given rule system.
     * Resets the display, and
     * creates and stores a model for each rule in the system.
     */
    public synchronized void setGrammarUpdate(RuleViewGrammar grammar) {
        // reset the display
        jGraph.setModel(RuleJModel.EMPTY_JMODEL);
        setEnabled(false);
        // see if the new grammar provides rule graphs
        setStatus();
        // create a mapping from rule names to (fresh) rule models
        ruleJModelMap.clear();
        for (NameLabel ruleName: grammar.getRuleNames()) {
            RuleJModel jModel = createRuleModel((RuleGraph) grammar.getRuleView(ruleName));
            ruleJModelMap.put(ruleName, jModel);
        }
    }

    /**
     * Retrieves a named rule from this component's store,
     * and puts it on display.
     * @throws IllegalArgumentException if <code>name</code> is not a known rule name
     */
    public synchronized void setRuleUpdate(NameLabel name) {
        if (! ruleJModelMap.containsKey(name)) {
            throw new IllegalArgumentException("Unknown rule: "+name);
        }
        JModel ruleJModel = ruleJModelMap.get(name);
        // currentRule = ruleJModel.getRule();
        jGraph.setModel(ruleJModel);
        // display new rule
        setStatus();
        setEnabled(ruleJModel != RuleJModel.EMPTY_JMODEL);
    }

    /**
     * Has no effect.
     */
    public synchronized void setStateUpdate(GraphState state) {
    	// nothing happens here
    }

    /**
     * Has the effect of {@link #setRuleUpdate(NameLabel)} for the new transition's rule.
     * @see #setRuleUpdate(NameLabel)
     */
    public synchronized void setTransitionUpdate(GraphTransition transition) {
        setRuleUpdate(transition.getRule().getName());
    }

    /**
     * Has no effect.
     */
    public synchronized void applyTransitionUpdate(GraphTransition transition) {
    	// nothing happens here
    }
    
    /**
     * Sets a listener to the rule anchor option, if that has not yet been done.
     */
    protected JCheckBoxMenuItem createAnchorsOptionItem() {
		JCheckBoxMenuItem result = simulator.getOptions().getItem(Options.SHOW_ANCHORS_OPTION);
		// listen to the option controlling the rule anchor display
		result.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setStatus();
				RulePanel.this.revalidate();
			}
		});
		return result;
	}
    
    /**
     * Sets a listener to the node ids option.
     */
    protected JCheckBoxMenuItem createNodeIdsOptionItem() {
		final JCheckBoxMenuItem result = simulator.getOptions().getItem(Options.SHOW_NODE_IDS_OPTION);
		// listen to the option controlling the rule anchor display
		result.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean value = result.getState();
				for (RuleJModel jModel: ruleJModelMap.values()) {
					jModel.setShowNodeIdentities(value);
				}
				getJGraph().refreshView();
			}
		});
		return result;
	}
    
    /**
     * Callback factory method for a new rule model.
     */
    protected RuleJModel createRuleModel(RuleGraph ruleGraph) {
        RuleJModel result = ruleGraph == null ? RuleJModel.EMPTY_JMODEL : new RuleJModel(ruleGraph);
        result.setShowNodeIdentities(nodeIdsOptionItem.getState());
        return result;
    }
    
    /**
	 * Returns a description of the currently selected rule, including the
	 * anchor if this is to be displayed.
	 */
    protected String getRuleDescription() {
    	String result;
    	Rule rule = getJGraph().getModel().getRule();
		result = "Rule " + rule.getName().name();
		if (anchorsOptionItem.getState()) {
			result += "; anchor "
					+ Groove.toString(rule.anchor(), "(", ")", ",");
		}
		return result;
    }

    /**
	 * Displays a text in the status bar
	 */
    protected void setStatus() {
        getStatusBar().setText(getJGraph().isEnabled() ? getRuleDescription() : INITIAL_FRAME_NAME);
    }

    /**
     * The production simulator to which this frame belongs.
     */
    private final Simulator simulator;
    /** The menu item indicating that anchors are to be shown in the status bar. */
    private final JCheckBoxMenuItem anchorsOptionItem;
    /** The menu item indicating that node identities are to be shown in the rule graph. */
    private final JCheckBoxMenuItem nodeIdsOptionItem;
    /**
     * Contains graph models for the production system's rules.
     * @invariant ruleJModels: RuleName --> RuleJModel
     */
    private final Map<NameLabel,RuleJModel> ruleJModelMap = new TreeMap<NameLabel,RuleJModel>();
}
