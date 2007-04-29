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
 * $Id: RulePanel.java,v 1.7 2007-04-29 09:22:28 rensink Exp $
 */
package groove.gui;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_REMARKS_OPTION;

import groove.gui.jgraph.*;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.GraphGrammar;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.util.Groove;
import groove.view.AspectualRuleView;
import groove.view.RuleViewGrammar;

import java.util.Map;
import java.util.TreeMap;


/**
 * Window that displays and controls the current rule graph.
 * Auxiliary class for Simulator.
 * @author Arend Rensink
 * @version $Revision: 1.7 $
 */
public class RulePanel extends JGraphPanel<AspectJGraph> implements SimulationListener {
	/** Frame name when no rule is selected. */
    protected static final String INITIAL_FRAME_NAME = "No rule selected";

    /**
     * Constructs a new rule frame on the basis of a given graph.
     */
    public RulePanel(Simulator simulator) {
        super(new AspectJGraph(simulator), true, simulator.getOptions());
        this.simulator = simulator;
        setEnabled(false);
        addRefreshListener(SHOW_ANCHORS_OPTION);
        addRefreshListener(SHOW_ASPECTS_OPTION);
        addRefreshListener(SHOW_NODE_IDS_OPTION);
        addRefreshListener(SHOW_REMARKS_OPTION);
        simulator.addSimulationListener(this);
        jGraph.setToolTipEnabled(true);
    }

    /** 
     * Sets the frame to a given rule system.
     * Resets the display, and
     * creates and stores a model for each rule in the system.
     */
    public synchronized void setGrammarUpdate(RuleViewGrammar grammar) {
    	if (setDisplayedGrammar(grammar)) {
			// create a mapping from rule names to (fresh) rule models
			ruleJModelMap.clear();
			if (grammar != null) {
				for (NameLabel ruleName : grammar.getRuleNames()) {
					AspectJModel jModel = computeRuleModel((AspectualRuleView) grammar.getRuleView(ruleName));
					ruleJModelMap.put(ruleName, jModel);
				}
			}
    		// reset the display
			if (displayedRule != null) {
				if (ruleJModelMap.containsKey(displayedRule)) {
					setRuleUpdate(displayedRule);
				} else {
					jGraph.setModel(AspectJModel.EMPTY_JMODEL);
					displayedRule = null;
					refresh();
				}
			}
    	}
    }

    /** Has the same effect as setting the grammar. */
    public synchronized void activateGrammarUpdate(GTS gts) {
    	setGrammarUpdate((RuleViewGrammar) gts.getGrammar());
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
        displayedRule = name;
        refresh();
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
        setRuleUpdate(transition.getEvent().getName());
    }

    /**
     * Has no effect.
     */
    public synchronized void applyTransitionUpdate(GraphTransition transition) {
    	// nothing happens here
    }

    /**
     * Sets the value of the {@link #displayedGrammar} field.
     * The return value indicates if the new value differs from the old.
     * @param grammar the new displayed grammar
     * @return <code>true</code> if the new value is different from the old
     */
    private boolean setDisplayedGrammar(GraphGrammar grammar) {
    	boolean result = this.displayedGrammar != grammar;
    	this.displayedGrammar = grammar;
    	return result;
    }
    /**
     * Callback factory method to construct a new rule model.
     */
    protected AspectJModel computeRuleModel(AspectualRuleView ruleGraph) {
        AspectJModel result = ruleGraph == null ? AspectJModel.EMPTY_JMODEL : new AspectJModel(ruleGraph, getOptions());
        return result;
    }
    
    @Override
	protected void refresh() {
    	setEnabled(displayedRule != null);
    	super.refresh();
	}

	/**
	 * Returns the rule description of the currently selected rule, if any.
	 */
    @Override
    protected String getStatusText() {
    	String text;
    	Rule rule = simulator.getCurrentRule();
    	if (rule != null) {
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
    /** The currently displayed grammar, if any. */
    private GraphGrammar displayedGrammar;
    /** The name of the currently displayed rule, if any. */
    private NameLabel displayedRule;
}
