/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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

import groove.gui.SimulatorModel.Change;
import groove.gui.jgraph.AspectJGraph;
import groove.view.RuleModel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;

/**
 * Panel that holds the rule panel and rule graph editors.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RuleDisplay extends TabbedDisplay implements SimulatorListener {
    /**
     * Constructs a panel for a given simulator.
     */
    public RuleDisplay(Simulator simulator) {
        super(simulator, DisplayKind.RULE);
        installListeners();
    }

    @Override
    protected void activateListeners() {
        super.activateListeners();
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.RULE,
            Change.ABSTRACT);
    }

    @Override
    protected void suspendListeners() {
        super.suspendListeners();
        getSimulatorModel().removeListener(this);
    }

    @Override
    public JGraphPanel<AspectJGraph> getMainPanel() {
        return getRulePanel();
    }

    @Override
    public JPanel getListPanel() {
        if (this.ruleTreePanel == null) {
            JToolBar labelTreeToolbar = createListToolBar();
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
            this.ruleTreePanel.add(labelTreeToolbar, BorderLayout.NORTH);
            this.ruleTreePanel.add(ruleJTreePanel, BorderLayout.CENTER);
            // make sure tool tips get displayed
            ToolTipManager.sharedInstance().registerComponent(
                this.ruleTreePanel);
        }
        return this.ruleTreePanel;
    }

    /** Creates a tool bar for the rule tree. */
    @Override
    protected JToolBar createListToolBar() {
        JToolBar result = super.createListToolBar(7);
        result.add(getActions().getShiftPriorityAction(true));
        result.add(getActions().getShiftPriorityAction(false));
        return result;
    }

    /**
     * Returns the tree of rules and matches displayed in the simulator.
     */
    public RuleJTree getRuleTree() {
        if (this.ruleJTree == null) {
            this.ruleJTree = new RuleJTree(this);
        }
        return this.ruleJTree;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        if (changes.contains(Change.GRAMMAR)) {
            clearJModelMap();
        }
        if (changes.contains(Change.GRAMMAR) || changes.contains(Change.RULE)) {
            RuleModel rule = source.getRule();
            setSelectedTab(rule == null ? null : rule.getName());
            getEnableButton().setSelected(rule != null && rule.isEnabled());
        }
        if (changes.contains(Change.ABSTRACT) && source.isAbstractionMode()) {
            getRuleTree().dispose();
            this.ruleJTree = null;
            this.ruleTreePanel = null;
        }
        activateListeners();
    }

    @Override
    protected RuleModel getResource(String name) {
        RuleModel result = getSimulatorModel().getGrammar().getRuleModel(name);
        return result;
    }

    /** Returns the rule panel displayed on this tab. */
    public final RulePanel getRulePanel() {
        if (this.rulePanel == null) {
            this.rulePanel = new RulePanel(getSimulator());
        }
        return this.rulePanel;
    }

    @Override
    protected void decorateLabelText(String name, StringBuilder text) {
        if (!getResource(name).isEnabled()) {
            text.insert(0, "(");
            text.append(")");
        }
    }

    @Override
    protected void selectionChanged() {
        getSimulatorModel().setRule(getSelectedName());
    }

    private RulePanel rulePanel;
    /** Production rule directory. */
    private RuleJTree ruleJTree;

    /** Panel with the ruleJTree plus toolbar. */
    private JPanel ruleTreePanel;

    /**
     * Minimum width of the rule tree component.
     */
    static private final int RULE_TREE_MINIMUM_WIDTH = 100;

    /**
     * Minimum height of the rule tree component.
     */
    static private final int RULE_TREE_MINIMUM_HEIGHT = 200;
}
