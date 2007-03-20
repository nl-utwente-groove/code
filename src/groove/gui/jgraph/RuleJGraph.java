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
 * $Id: RuleJGraph.java,v 1.1.1.1 2007-03-20 10:05:33 kastenberg Exp $
 */
package groove.gui.jgraph;

import groove.gui.Simulator;
import groove.gui.layout.SpringLayouter;
import groove.trans.NameLabel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/**
 * Implementation of MyJGraph that provides the proper popup menu.
 * To construct an instance, setupPopupMenu() should be called
 * after all global final variables have been set.
 */
public class RuleJGraph extends JGraph {
    public RuleJGraph(Simulator simulator) {
    	super(RuleJModel.EMPTY_JMODEL);
        this.simulator = simulator;
        setLayouter(new SpringLayouter());
        doGraphLayout();
    }

    /** Specialises the return type to a {@link RuleJModel}. */
    @Override
	public RuleJModel getModel() {
    	return (RuleJModel) super.getModel();
	}

	protected void initPopupMenu(JPopupMenu toMenu) {
        // add actions to set the rule display to each production rule
        JMenu setMenu = new JMenu("Set rule to") {
            public void menuSelectionChanged(boolean selected) {
                if (selected) {
                    removeAll();
                    for (NameLabel ruleName: simulator.getCurrentGrammar().getRuleNames()) {
                        add(createSetRuleAction(ruleName));                        
                    }
                }
            }
        };
        addSeparatorUnlessFirst(toMenu);
        popupMenu.add(setMenu);
        toMenu.addSeparator();
        toMenu.add(simulator.getEditGraphAction());
        super.initPopupMenu(toMenu);
    }

    /** Action to change the display to a given (named) rule. */
    protected Action createSetRuleAction(final NameLabel ruleName) {
        return new AbstractAction(ruleName.toString()) {
            public void actionPerformed(ActionEvent evt) {
                simulator.setRule(ruleName);
            }
        };
    }

    /**
     * The simulator with which this j-graph is associated.
     */
    private final Simulator simulator;
}