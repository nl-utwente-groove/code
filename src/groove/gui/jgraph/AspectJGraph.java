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
 * $Id: AspectJGraph.java,v 1.4 2007-05-08 10:57:55 rensink Exp $
 */
package groove.gui.jgraph;

import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.layout.SpringLayouter;
import groove.trans.RuleNameLabel;
import groove.view.aspect.AspectElement;
import groove.view.aspect.RuleAspect;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

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
    	super(AspectJModel.EMPTY_JMODEL);
        this.simulator = simulator;
        setLayouter(new SpringLayouter());
        doGraphLayout();
    }

    /** Specialises the return type to a {@link AspectJModel}. */
    @Override
	public AspectJModel getModel() {
    	return (AspectJModel) super.getModel();
	}

    /**
     * Propagates some types of changes from model to view. Reacts in particular to
     * {@link JModel.RefreshEdit}-events: every refreshed cell with an empty attribute set gets its
     * view attributes refreshed by a call to {@link JModel#createTransientJAttr(JCell)}; moreover, hidden cells
     * are deselected. by a call to {@link JModel#createTransientJAttr(JCell)}.
     * @see JModel.RefreshEdit#getRefreshedJCells()
     */
    @Override
    public void graphChanged(GraphModelEvent evt) {
    	super.graphChanged(evt);
        if (evt.getSource() == getModel() && evt.getChange() instanceof JModel.RefreshEdit) {
        	Set<JCell> remarkCells = new HashSet<JCell>();
            for (JCell jCell: ((JModel.RefreshEdit) evt.getChange()).getRefreshedJCells()) {
            	AspectElement elem = null;
            	if (jCell instanceof AspectJModel.AspectJVertex) {
            		elem = ((AspectJModel.AspectJVertex) jCell).getNode();
            	} else if (jCell instanceof AspectJModel.AspectJEdge) {
            		elem = ((AspectJModel.AspectJEdge) jCell).getEdge();
            	}
            	if (RuleAspect.isRemark(elem)) {
            		remarkCells.add(jCell);
            	}
            }
        	getGraphLayoutCache().setVisible(remarkCells.toArray(), simulator.getOptions().getItem(Options.SHOW_REMARKS_OPTION).isSelected());
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
                    for (RuleNameLabel ruleName: simulator.getCurrentGrammar().getRuleMap().keySet()) {
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
                simulator.setRule(ruleName);
            }
        };
    }

    /**
     * The simulator with which this j-graph is associated.
     */
    private final Simulator simulator;
}