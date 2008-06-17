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
 * $Id: AspectJGraph.java,v 1.10 2008-01-30 09:33:14 iovka Exp $
 */
package groove.gui.jgraph;

import groove.gui.Simulator;
import groove.gui.layout.SpringLayouter;
import groove.trans.RuleNameLabel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import org.jgraph.event.GraphModelEvent;

/**
 * Extension of {@link JGraph} that provides the proper popup menu.
 */
public class AspectJGraph extends JGraph {
	/** Creates a j-graph for a given simulator, with an initially empty j-model. */
    public AspectJGraph(Simulator simulator) {
    	super(AspectJModel.EMPTY_ASPECT_JMODEL, false);
        this.simulator = simulator;
        setLayouter(new SpringLayouter());
        doGraphLayout();
    }

    /** Specialises the return type to a {@link AspectJModel}. */
    @Override
	public AspectJModel getModel() {
    	return (AspectJModel) super.getModel();
	}

    /** In addition to the super method, synchronises layout changes. */
    @Override
    public void graphChanged(GraphModelEvent evt) {
        super.graphChanged(evt);
        for (Object jCell: evt.getChange().getChanged()) {
            if (jCell instanceof GraphJCell) {
                getModel().synchroniseLayout((GraphJCell) jCell);
            }
        }
    }

    @Override
	protected void fillPopupMenu(JPopupMenu result) {
        addSeparatorUnlessFirst(result);
        result.add(computeSetMenu());
        result.addSeparator();
        result.add(simulator.getEditRuleAction());
        super.fillPopupMenu(result);
    }

	/**
	 * @return Returns the simulator.
	 */
	final Simulator getSimulator() {
		return this.simulator;
	}

	/**
	 * The simulator with which this j-graph is associated.
	 */
	private final Simulator simulator;

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
                    for (RuleNameLabel ruleName: getSimulator().getCurrentGrammar().getRuleMap().keySet()) {
                        add(createSetRuleAction(ruleName));                        
                    }
                }
            }
        };
		return setMenu;
	}

    /** Action to change the display to a given (named) rule. */
    protected Action createSetRuleAction(final RuleNameLabel ruleName) {
        return new AbstractAction(ruleName.toString()) {
            public void actionPerformed(ActionEvent evt) {
                getSimulator().setRule(ruleName);
            }
        };
    }
}