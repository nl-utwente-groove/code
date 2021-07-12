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
package groove.gui.display;

import groove.grammar.model.ResourceKind;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.SimulatorModel;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.CollapseAllAction;
import groove.gui.tree.RuleTree;
import groove.lts.GraphState;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 * Panel that holds the rule panel and rule graph editors.
 * @author Arend Rensink
 * @version $Revision $
 */
final public class RuleDisplay extends ResourceDisplay {
    /**
     * Constructs a panel for a given simulator.
     */
    RuleDisplay(Simulator simulator) {
        super(simulator, ResourceKind.RULE);
    }

    @Override
    protected void installListeners() {
        getSimulatorModel().addListener(this, Change.STATE);
        super.installListeners();
    }

    /** Creates a tool bar for the rule tree. */
    @Override
    protected JToolBar createListToolBar() {
        int separation = 7;
        JToolBar result = super.createListToolBar(separation);
        result.add(getActions().getShiftPriorityAction(true));
        result.add(getActions().getShiftPriorityAction(false));
        result.addSeparator(new Dimension(separation, 0));
        result.add(getCollapseAllButton());
        return result;
    }

    @Override
    public RuleTree getList() {
        return (RuleTree) super.getList();
    }

    /**
     * Returns the tree of rules and matches displayed in the simulator.
     */
    @Override
    public RuleTree createList() {
        return new RuleTree(this);
    }

    @Override
    protected ListPanel createListPanel() {
        ListPanel result = super.createListPanel();
        result.add(this.statusLine, BorderLayout.SOUTH);
        return result;
    }

    @Override
    protected void resetList() {
        getList().dispose();
        super.resetList();
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        super.update(source, oldModel, changes);
        if (suspendListening()) {
            if (changes.contains(Change.STATE)) {
                String statusText;
                GraphState state = source.getState();
                if (state == null) {
                    statusText = "No state selected";
                } else {
                    statusText = "Matches for state " + state;
                }
                this.statusLine.setText(statusText);
            }
            activateListening();
        }
    }

    /**
     * Returns the button for the collapse all action, lazily creating it
     * first.
     */
    private JButton getCollapseAllButton() {
        if (this.collapseAllButton == null) {
            this.collapseAllButton =
                Options.createButton(new CollapseAllAction(getSimulator(), getList()));
        }
        return this.collapseAllButton;
    }

    private final JLabel statusLine = new JLabel(" ");
    private JButton collapseAllButton;
}
