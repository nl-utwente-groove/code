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
 * $Id: AspectJGraph.java,v 1.10 2008-01-30 09:33:14 iovka Exp $
 */
package groove.gui.jgraph;

import groove.graph.GraphRole;
import groove.gui.Exporter;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.RuleName;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

/**
 * Extension of {@link JGraph} that provides the proper popup menu.
 */
final public class AspectJGraph extends JGraph {
    /**
     * Creates a j-graph for a given simulator, with an initially empty j-model.
     */
    public AspectJGraph(Simulator simulator, GraphRole role) {
        super(null, role != GraphRole.RULE);
        this.simulator = simulator;
        assert role.inGrammar();
        this.graphRole = role;
        setModel(AspectJModel.EMPTY_JMODEL);
    }

    @Override
    public Simulator getSimulator() {
        return this.simulator;
    }

    @Override
    public JMenu createPopupMenu(Point atPoint) {
        JMenu result = new JMenu("Popup");
        switch (this.graphRole) {
        case HOST:
            result.add(this.simulator.getApplyTransitionAction());
            result.addSeparator();
            result.add(this.simulator.getEditGraphAction());
            addSubmenu(result, super.createPopupMenu(atPoint));
            break;
        case RULE:
            result.add(computeSetMenu());
            result.addSeparator();
            result.add(this.simulator.getEditRuleAction());
            addSubmenu(result, super.createPopupMenu(atPoint));
            break;
        case TYPE:
            result.add(this.simulator.getEditTypeAction());
            addSubmenu(result, super.createPopupMenu(atPoint));
        }
        return result;
    }

    @Override
    protected Exporter getExporter() {
        return this.simulator.getExporter();
    }

    @Override
    protected String getExportActionName() {
        switch (this.graphRole) {
        case HOST:
            return Options.EXPORT_STATE_ACTION_NAME;
        case RULE:
            return Options.EXPORT_RULE_ACTION_NAME;
        case TYPE:
            return Options.EXPORT_TYPE_ACTION_NAME;
        }
        throw new IllegalStateException();
    }

    /**
     * Computes and returns a menu that allows setting the display to another
     * rule.
     */
    protected JMenu computeSetMenu() {
        // add actions to set the rule display to each production rule
        JMenu setMenu = new JMenu("Set rule to") {
            @Override
            public void menuSelectionChanged(boolean selected) {
                super.menuSelectionChanged(selected);
                if (selected) {
                    removeAll();
                    for (RuleName ruleName : getSimulator().getGrammarView().getRuleNames()) {
                        add(createSetRuleAction(ruleName));
                    }
                }
            }
        };
        return setMenu;
    }

    /** Action to change the display to a given (named) rule. */
    protected Action createSetRuleAction(final RuleName ruleName) {
        return new AbstractAction(ruleName.toString()) {
            public void actionPerformed(ActionEvent evt) {
                getSimulator().setRule(ruleName);
            }
        };
    }

    /**
     * The simulator with which this j-graph is associated.
     */
    private final Simulator simulator;

    /** The role for which this {@link JGraph} will display graphs. */
    private final GraphRole graphRole;
}