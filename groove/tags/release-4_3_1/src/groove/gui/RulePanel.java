/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
import groove.graph.GraphRole;
import groove.graph.LabelStore;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.JGraphMode;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.Rule;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.trans.SPORule;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.RuleView;
import groove.view.StoredGrammarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import javax.swing.JToolBar;

/**
 * Window that displays and controls the current rule graph. Auxiliary class for
 * Simulator.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class RulePanel extends JGraphPanel<AspectJGraph> implements
        SimulationListener {
    /** Frame name when no rule is selected. */
    protected static final String INITIAL_FRAME_NAME = "No rule selected";

    /**
     * Constructs a new rule frame on the basis of a given graph.
     */
    public RulePanel(final Simulator simulator) {
        super(new AspectJGraph(simulator, GraphRole.RULE), true);
        this.simulator = simulator;
        initialise();
    }

    @Override
    protected JToolBar createToolBar() {
        JToolBar result = new JToolBar();
        result.add(this.simulator.getNewRuleAction());
        result.add(this.simulator.getEditRuleAction());
        result.addSeparator();
        result.add(getJGraph().getModeButton(JGraphMode.SELECT_MODE));
        result.add(getJGraph().getModeButton(JGraphMode.PAN_MODE));
        return result;
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        this.simulator.addSimulationListener(this);
        addRefreshListener(SHOW_ANCHORS_OPTION);
        addRefreshListener(SHOW_ASPECTS_OPTION);
        addRefreshListener(SHOW_NODE_IDS_OPTION);
        addRefreshListener(SHOW_REMARKS_OPTION);
        addRefreshListener(SHOW_VALUE_NODES_OPTION);
        getJGraph().setToolTipEnabled(true);
        getJGraph().getLabelTree().addLabelStoreObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                assert arg instanceof LabelStore;
                SystemProperties newProperties =
                    RulePanel.this.simulator.getGrammarView().getProperties().clone();
                newProperties.setSubtypes(((LabelStore) arg).toDirectSubtypeString());
                RulePanel.this.simulator.doSaveProperties(newProperties);
            }
        });
    }

    /**
     * Sets the frame to a given rule system. Resets the display, and creates
     * and stores a model for each rule in the system.
     */
    public synchronized void setGrammarUpdate(StoredGrammarView grammar) {
        // create a mapping from rule names to (fresh) rule models
        this.ruleJModelMap.clear();
        if (grammar != null) {
            // reset the graph model so it doesn't get mixed up with the new type
            getJGraph().setModel(null);
            // set either the type or the label store of the associated JGraph
            if (grammar.getActiveTypeNames().isEmpty()) {
                getJGraph().setLabelStore(grammar.getLabelStore());
            } else {
                try {
                    getJGraph().setType(grammar.toModel().getType(), null);
                } catch (FormatException e) {
                    getJGraph().setLabelStore(grammar.getLabelStore());
                }
            }
            for (RuleName ruleName : grammar.getRuleNames()) {
                RuleView ruleView = grammar.getRuleView(ruleName);
                AspectJModel jModel = getJGraph().newModel();
                jModel.loadGraph(ruleView.getAspectGraph());
                this.ruleJModelMap.put(ruleName, jModel);
            }
        }
        // reset the display
        RuleView currentRule = this.simulator.getCurrentRule();
        displayRule(currentRule == null ? null : currentRule.getRuleName(),
            true);
    }

    /** Does nothing (according to contract, the grammar has already been set). */
    public synchronized void startSimulationUpdate(GTS gts) {
        // empty
    }

    /**
     * Retrieves a named rule from this component's store, and puts it on
     * display.
     * @throws IllegalArgumentException if <code>name</code> is not a known rule
     *         name
     */
    public synchronized void setRuleUpdate(RuleName name) {
        if (!this.ruleJModelMap.containsKey(name)) {
            throw new IllegalArgumentException("Unknown rule: " + name);
        }
        displayRule(name, false);
    }

    /**
     * Has no effect.
     */
    public synchronized void setStateUpdate(GraphState state) {
        // nothing happens here
    }

    /**
     * Has the effect of {@link #setRuleUpdate(RuleName)} for the new
     * transition's rule.
     * @see #setRuleUpdate(RuleName)
     */
    public synchronized void setTransitionUpdate(GraphTransition transition) {
        setRuleUpdate(transition.getEvent().getRule().getName());
    }

    /**
     * Has the effect of {@link #setRuleUpdate(RuleName)} for the new match's
     * rule.
     * @see #setRuleUpdate(RuleName)
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

    /** 
     * Sets the rule with a given name as model, and refreshes the view.
     * Also updates the {@link #displayedRule} accordingly.
     * @param ruleName the name of the rule to be displayed
     * @param reload if {@code true}, always reloads the rule with the given
     * name; otherwise, only loads it if {@code ruleName} is different from
     * {@link #displayedRule}.
     */
    private void displayRule(RuleName ruleName, boolean reload) {
        if (reload || ruleName == null && this.displayedRule != null
            || !ruleName.equals(this.displayedRule)) {
            AspectJModel ruleJModel =
                ruleName == null ? null : this.ruleJModelMap.get(ruleName);
            // display new rule
            setEnabled(ruleJModel != null);
            this.displayedRule = ruleName;
            this.jGraph.setModel(ruleJModel);
            refreshStatus();
        }
    }

    /**
     * Returns the rule description of the currently selected rule, if any.
     */
    @Override
    protected String getStatusText() {
        StringBuilder text = new StringBuilder();
        RuleView view = this.simulator.getCurrentRule();
        if (view != null) {
            text.append("Rule ");
            text.append(HTMLConverter.STRONG_TAG.on(view.getName()));
            try {
                Rule rule = view.toRule();
                if (rule instanceof SPORule
                    && getOptionsItem(SHOW_ANCHORS_OPTION).isSelected()) {
                    text.append(", anchor ");
                    text.append(getAnchorString((SPORule) rule));
                }
            } catch (FormatException exc) {
                // don't add the anchor
            }
            String remark = GraphProperties.getRemark(view.getAspectGraph());
            if (remark != null) {
                text.append(": ");
                text.append(HTMLConverter.toHtml(remark));
            }
        } else {
            text.append(INITIAL_FRAME_NAME);
        }
        return HTMLConverter.HTML_TAG.on(text).toString();
    }

    /** Returns a string description of the anchors of a given rule. */
    private String getAnchorString(SPORule rule) {
        if (rule.getSubRules(false).isEmpty()) {
            return Groove.toString(rule.anchor(), "(", ")", ",");
        } else {
            List<String> result = new ArrayList<String>();
            for (SPORule subRule : rule.getSubRules(true)) {
                result.add(subRule.getName().toString()
                    + Groove.toString(subRule.anchor(), "(", ")", ","));
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
    private final Map<RuleName,AspectJModel> ruleJModelMap =
        new TreeMap<RuleName,AspectJModel>();
    // /** The currently displayed grammar, if any. */
    // private GrammarView displayedGrammar;
    /** The name of the currently displayed rule, if any. */
    private RuleName displayedRule;
}
