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
import groove.graph.TypeGraph;
import groove.gui.SimulatorModel.Change;
import groove.gui.SimulatorPanel.TabKind;
import groove.gui.dialog.ErrorDialog;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.io.HTMLConverter;
import groove.trans.Rule;
import groove.trans.RuleElement;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.RuleView;
import groove.view.StoredGrammarView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;

/**
 * Window that displays and controls the current rule graph. Auxiliary class for
 * Simulator.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class RulePanel extends JGraphPanel<AspectJGraph> implements
        SimulatorListener, SimulatorTab {
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
    public TabKind getKind() {
        return TabKind.RULE;
    }

    @Override
    public JPanel getMainPanel() {
        return this;
    }

    @Override
    public JPanel getListPanel() {
        return null;
    }

    @Override
    public String getCurrent() {
        return getJModel() == null ? null : getJModel().getName();
    }

    @Override
    protected JToolBar createToolBar() {
        return null;
        //        JToolBar result = new JToolBar();
        //        result.add(getActions().getNewRuleAction());
        //        result.add(getActions().getEditRuleAction());
        //        result.addSeparator();
        //        result.add(getJGraph().getModeButton(JGraphMode.SELECT_MODE));
        //        result.add(getJGraph().getModeButton(JGraphMode.PAN_MODE));
        //        return result;
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
                    new ErrorDialog(RulePanel.this,
                        "Error while modifying type hierarchy", exc).setVisible(true);
                }
            }
        });
    }

    /**
     * Lazily creates and returns the panel with the rule tree.
     */
    public JPanel getTreePanel() {
        if (this.ruleTreePanel == null) {
            // set title and toolbar
            JLabel labelPaneTitle =
                new JLabel(" " + Options.RULES_PANE_TITLE + " ");
            labelPaneTitle.setAlignmentX(JLabel.LEFT_ALIGNMENT);
            JToolBar labelTreeToolbar = createRuleTreeToolBar();
            labelTreeToolbar.setAlignmentX(JLabel.LEFT_ALIGNMENT);

            Box labelPaneTop = Box.createVerticalBox();
            labelPaneTop.add(labelPaneTitle);
            labelPaneTop.add(labelTreeToolbar);

            // make sure the preferred width is not smaller than the minimum
            // width
            JScrollPane ruleJTreePanel = new JScrollPane(getRuleTree()) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension superSize = super.getPreferredSize();
                    return new Dimension((int) Math.max(superSize.getWidth(),
                        RULE_TREE_MINIMUM_WIDTH), (int) superSize.getHeight());
                }
            };
            ruleJTreePanel.setMinimumSize(new Dimension(
                RULE_TREE_MINIMUM_WIDTH, RULE_TREE_MINIMUM_HEIGHT));

            this.ruleTreePanel = new JPanel(new BorderLayout(), false);
            this.ruleTreePanel.add(labelPaneTop, BorderLayout.NORTH);
            this.ruleTreePanel.add(ruleJTreePanel, BorderLayout.CENTER);
            // make sure tool tips get displayed
            ToolTipManager.sharedInstance().registerComponent(
                this.ruleTreePanel);
        }
        return this.ruleTreePanel;
    }

    /** Creates a tool bar for the rule tree. */
    private JToolBar createRuleTreeToolBar() {
        JToolBar result = getSimulator().createToolBar();
        result.add(getActions().getNewRuleAction());
        result.add(getActions().getEditRuleAction());
        result.addSeparator(new Dimension(7, 0));
        result.add(getActions().getCopyRuleAction());
        result.add(getActions().getDeleteRuleAction());
        result.add(getActions().getRenameRuleAction());
        result.addSeparator(new Dimension(7, 0));
        result.add(getActions().getShiftPriorityAction(true));
        result.add(getActions().getShiftPriorityAction(false));
        return result;
    }

    /**
     * Returns the tree of rules and matches displayed in the simulator.
     */
    public RuleJTree getRuleTree() {
        if (this.ruleJTree == null) {
            this.ruleJTree = new RuleJTree(getSimulator());
        }
        return this.ruleJTree;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(Change.GRAMMAR)) {
            setGrammarUpdate(source.getGrammar());
        } else if (changes.contains(Change.RULE) && source.getRule() != null) {
            displayRule(source.getRule().getName(), false);
        }
        if (changes.contains(Change.ABSTRACT) && source.isAbstractionMode()) {
            getRuleTree().dispose();
            this.ruleJTree = null;
            this.ruleTreePanel = null;
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
            TypeGraph type;
            try {
                type = grammar.toModel().getType();
            } catch (FormatException e) {
                type = null;
            }
            if (type == null) {
                getJGraph().setLabelStore(grammar.getLabelStore());
            } else {
                getJGraph().setType(type, null);
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
            setGraphBackground(isEnabled() ? Color.white : null);
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

    /** Production rule directory. */
    private RuleJTree ruleJTree;

    /** Panel with the ruleJTree plus toolbar. */
    private JPanel ruleTreePanel;

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

    /**
     * Minimum width of the rule tree component.
     */
    static private final int RULE_TREE_MINIMUM_WIDTH = 100;

    /**
     * Minimum height of the rule tree component.
     */
    static private final int RULE_TREE_MINIMUM_HEIGHT = 200;
}
