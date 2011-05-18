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
import groove.gui.SimulatorModel.Change;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.JGraphMode;
import groove.io.HTMLConverter;
import groove.trans.Rule;
import groove.trans.RuleElement;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.RuleView;
import groove.view.StoredGrammarView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JToolBar;

/**
 * Window that displays and controls the current rule graph. Auxiliary class for
 * Simulator.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class RulePanel extends JGraphPanel<AspectJGraph> implements
        SimulatorListener {
    /** Frame name when no rule is selected. */
    private static final String INITIAL_FRAME_NAME = "No rule selected";

    /**
     * Constructs a new rule frame on the basis of a given graph.
     */
    public RulePanel(final Simulator simulator) {
        super(new AspectJGraph(simulator, GraphRole.RULE), true);
        initialise();
    }

    @Override
    protected JToolBar createToolBar() {
        JToolBar result = new JToolBar();
        result.add(getActions().getNewRuleAction());
        result.add(getActions().getEditRuleAction());
        result.add(getActions().getSaveGraphAction());
        result.addSeparator();
        result.add(getJGraph().getModeButton(JGraphMode.SELECT_MODE));
        result.add(getJGraph().getModeButton(JGraphMode.PAN_MODE));
        return result;
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.RULE);
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
                    getGrammar().getProperties().clone();
                newProperties.setSubtypes(((LabelStore) arg).toDirectSubtypeString());
                try {
                    getSimulator().getModel().doSetProperties(newProperties);
                } catch (IOException exc) {
                    getSimulator().showErrorDialog(
                        "Error while modifying type hierarchy", exc);
                }
            }
        });
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(Change.GRAMMAR)) {
            setGrammarUpdate(source.getGrammar());
        } else if (changes.contains(Change.RULE) && source.getRule() != null) {
            displayRule(source.getRule().getName(), false);
        }
    }

    /**
     * Sets the frame to a given rule system. Resets the display, and creates
     * and stores a model for each rule in the system.
     */
    private synchronized void setGrammarUpdate(StoredGrammarView grammar) {
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
            for (String ruleName : grammar.getRuleNames()) {
                RuleView ruleView = grammar.getRuleView(ruleName);
                AspectJModel jModel = getJGraph().newModel();
                jModel.loadGraph(ruleView.getAspectGraph());
                this.ruleJModelMap.put(ruleName, jModel);
            }
        }
        // reset the display
        RuleView currentRule = getSimulatorModel().getRule();
        displayRule(currentRule == null ? null : currentRule.getName(), true);
    }

    /** 
     * Sets the rule with a given name as model, and refreshes the view.
     * Also updates the {@link #displayedRule} accordingly.
     * @param ruleName the name of the rule to be displayed
     * @param reload if {@code true}, always reloads the rule with the given
     * name; otherwise, only loads it if {@code ruleName} is different from
     * {@link #displayedRule}.
     */
    private void displayRule(String ruleName, boolean reload) {
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
        RuleView view = getSimulatorModel().getRule();
        if (view != null) {
            text.append("Rule ");
            text.append(HTMLConverter.STRONG_TAG.on(view.getName()));
            try {
                Rule rule = view.toRule();
                if (getOptionsItem(SHOW_ANCHORS_OPTION).isSelected()) {
                    text.append(", anchor ");
                    text.append(getAnchorString(rule));
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
    private String getAnchorString(Rule rule) {
        if (!rule.hasSubRules()) {
            return toAnchorString(rule);
        } else {
            List<String> result = new ArrayList<String>();
            // collect all subrules
            Queue<Rule> ruleQueue = new LinkedList<Rule>();
            ruleQueue.add(rule);
            while (!ruleQueue.isEmpty()) {
                Rule next = ruleQueue.poll();
                result.add(next.getName() + toAnchorString(next));
                ruleQueue.addAll(next.getSubRules());
            }
            return Groove.toString(result.toArray());
        }
    }

    /** Constructs a string listing all anchors of a given rule. */
    private String toAnchorString(Rule rule) {
        List<RuleElement> anchor = new ArrayList<RuleElement>();
        anchor.addAll(Arrays.asList(rule.getAnchorNodes()));
        anchor.addAll(Arrays.asList(rule.getAnchorEdges()));
        return Groove.toString(anchor.toArray(), "(", ")", ",");
    }

    /** Convenience method to retrieve the current grammar view. */
    private StoredGrammarView getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /**
     * Contains graph models for the production system's rules.
     * @invariant ruleJModels: RuleName --> RuleJModel
     */
    private final Map<String,AspectJModel> ruleJModelMap =
        new TreeMap<String,AspectJModel>();
    // /** The currently displayed grammar, if any. */
    // private GrammarView displayedGrammar;
    /** The name of the currently displayed rule, if any. */
    private String displayedRule;
}
