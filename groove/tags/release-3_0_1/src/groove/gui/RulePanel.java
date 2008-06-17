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
 * $Id: RulePanel.java,v 1.21 2008-01-30 09:33:35 iovka Exp $
 */
package groove.gui;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_REMARKS_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import groove.graph.GraphProperties;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.JModel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.RuleMatch;
import groove.trans.RuleNameLabel;
import groove.trans.SPORule;
import groove.util.Converter;
import groove.util.Groove;
import groove.view.AspectualRuleView;
import groove.view.DefaultGrammarView;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Window that displays and controls the current rule graph.
 * Auxiliary class for Simulator.
 * @author Arend Rensink
 * @version $Revision: 1.21 $
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
        addRefreshListener(SHOW_VALUE_NODES_OPTION);
        simulator.addSimulationListener(this);
        jGraph.setToolTipEnabled(true);
    }

    /** 
     * Sets the frame to a given rule system.
     * Resets the display, and
     * creates and stores a model for each rule in the system.
     */
    public synchronized void setGrammarUpdate(DefaultGrammarView grammar) {
		// create a mapping from rule names to (fresh) rule models
		ruleJModelMap.clear();
		if (grammar != null) {
			for (RuleNameLabel ruleName : grammar.getRuleMap().keySet()) {
				AspectJModel jModel = AspectJModel.newInstance(grammar.getRule(ruleName), getOptions());
				ruleJModelMap.put(ruleName, jModel);
			}
		}
		// reset the display
		if (displayedRule != null) {
			if (ruleJModelMap.containsKey(displayedRule)) {
				jGraph.setModel(ruleJModelMap.get(displayedRule));
				refresh();
			} else {
				jGraph.setModel(AspectJModel.EMPTY_ASPECT_JMODEL);
				displayedRule = null;
				refresh();
			}
		}
		// displayedGrammar = grammar;
    }

    /** Does nothing (according to contract, the grammar has already been set). */
    public synchronized void startSimulationUpdate(GTS gts) {
    	// empty
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
        if (displayedRule != name) {
			JModel ruleJModel = ruleJModelMap.get(name);
			// display new rule
			jGraph.setModel(ruleJModel);
			displayedRule = name;
			refresh();
		}
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
        setRuleUpdate(transition.getEvent().getRule().getName());
    }

    /**
     * Has the effect of {@link #setRuleUpdate(NameLabel)} for the new match's rule.
     * @see #setRuleUpdate(NameLabel)
     */
    public synchronized void setMatchUpdate(RuleMatch match) {
        setRuleUpdate(match.getRule().getName());
    }

    /**
     * Has no effect.
     */
    public synchronized void applyTransitionUpdate(GraphTransition transition) {
    	// nothing happens here
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
    	StringBuilder text = new StringBuilder();
    	AspectualRuleView view = simulator.getCurrentRule();
    	if (view != null) {
    		text.append("Rule ");
    		text.append(Converter.STRONG_TAG.on(view.getNameLabel().name()));
			try {
				Rule rule = view.toRule();
				if (rule instanceof SPORule && getOptionsItem(SHOW_ANCHORS_OPTION).isSelected()) {
					text.append(", anchor ");
					text.append(getAnchorString((SPORule) rule));
				}
			} catch (FormatException exc) {
				// don't add the anchor
			}
			String remark = GraphProperties.getRemark(view.getAspectGraph());
			if (remark != null) {
    			text.append(": ");
    			text.append(Converter.toHtml(remark));
    		}
    	} else {
    		text.append(INITIAL_FRAME_NAME);
    	}
    	return Converter.HTML_TAG.on(text).toString();
    }
    
    /** Returns a string description of the anchors of a given rule. */
    private String getAnchorString(SPORule rule) {
    	if (rule.getSubRules(false).isEmpty()) {
    		return Groove.toString(rule.anchor(), "(", ")", ",");
    	} else {
        	List<String> result = new ArrayList<String>();
        	for (SPORule subRule: rule.getSubRules(true)) {
        		result.add(subRule.getName().name()+Groove.toString(subRule.anchor(), "(", ")", ","));
        	}
        	return Groove.toString(result.toArray());
    	}
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
//    /** The currently displayed grammar, if any. */
//    private GrammarView displayedGrammar;
    /** The name of the currently displayed rule, if any. */
    private NameLabel displayedRule;
}
