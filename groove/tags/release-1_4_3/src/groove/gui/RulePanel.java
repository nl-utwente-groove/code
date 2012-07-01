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
 * $Id: RulePanel.java,v 1.4 2007-03-30 15:50:35 rensink Exp $
 */
package groove.gui;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;

import groove.gui.jgraph.*;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.view.AspectualRuleView;
import groove.trans.view.RuleViewGrammar;
import groove.util.Groove;

import java.util.Map;
import java.util.TreeMap;


/**
 * Window that displays and controls the current rule graph.
 * Auxiliary class for Simulator.
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public class RulePanel extends JGraphPanel<RuleJGraph> implements SimulationListener {
	/** Frame name when no rule is selected. */
    protected static final String INITIAL_FRAME_NAME = "No rule selected";

    /**
     * Constructs a new rule frame on the basis of a given graph.
     */
    public RulePanel(Simulator simulator) {
        super(new RuleJGraph(simulator), true, simulator.getOptions());
        this.simulator = simulator;
        addRefreshListener(SHOW_ANCHORS_OPTION);
        addRefreshListener(SHOW_ASPECTS_OPTION);
        addRefreshListener(SHOW_NODE_IDS_OPTION);
        simulator.addSimulationListener(this);
        jGraph.setToolTipEnabled(true);
    }

    /** 
     * Sets the frame to a given rule system.
     * Resets the display, and
     * creates and stores a model for each rule in the system.
     */
    public synchronized void setGrammarUpdate(GTS gts) {
        // reset the display
        jGraph.setModel(AspectJModel.EMPTY_JMODEL);
        setEnabled(false);
        // create a mapping from rule names to (fresh) rule models
        ruleJModelMap.clear();
        RuleViewGrammar grammar = (RuleViewGrammar) gts.ruleSystem();
        for (NameLabel ruleName: grammar.getRuleNames()) {
            AspectJModel jModel = computeRuleModel((AspectualRuleView) grammar.getRuleView(ruleName));
            ruleJModelMap.put(ruleName, jModel);
        }
        refreshStatus();
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
        // display new rule
        jGraph.setModel(ruleJModel);
        setEnabled(ruleJModel != AspectJModel.EMPTY_JMODEL);
        refreshStatus();
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
     * Callback factory method to construct a new rule model.
     */
    protected AspectJModel computeRuleModel(AspectualRuleView ruleGraph) {
        AspectJModel result = ruleGraph == null ? AspectJModel.EMPTY_JMODEL : new AspectJModel(ruleGraph, getOptions());
        return result;
    }
    
    /**
	 * Returns the rule description of the currently selected rule, if any.
	 */
    @Override
    protected String getStatusText() {
    	String text;
    	if (isEnabled()) {
        	Rule rule = simulator.getCurrentRule();
    		text = "Rule " + rule.getName().name();
    		if (getOptionsItem(SHOW_ANCHORS_OPTION).getState()) {
    			text += "; anchor "
    					+ Groove.toString(rule.anchor(), "(", ")", ",");
    		}
    	} else {
    		text = INITIAL_FRAME_NAME;
    	}
    	return text;
    }

    /**
     * The production simulator to which this frame belongs.
     */
    private final Simulator simulator;
    /**
     * Contains graph models for the production system's rules.
     * @invariant ruleJModels: RuleName --> RuleJModel
     */
    private final Map<NameLabel,AspectJModel> ruleJModelMap = new TreeMap<NameLabel,AspectJModel>();
}