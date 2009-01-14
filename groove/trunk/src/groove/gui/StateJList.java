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
 * $Id$
 */
package groove.gui;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.NameLabel;
import groove.trans.RuleMatch;
import groove.view.AspectualGraphView;
import groove.view.DefaultGrammarView;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

/**
 * @author Tom Staijen
 * @version $Revision $
 */
public class StateJList extends JList implements SimulationListener {

    /**
     * Creates a new state list viewer.
     * @param simulator
     */
    protected StateJList(final Simulator simulator) {
        this.simulator = simulator;
        this.simulator.addSimulationListener(this);
        this.setEnabled(false);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        MouseListener mouseListener = new MouseAdapter() {
            /** Update the state when a list element is double-clicked */
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selection =
                        (String) StateJList.this.getSelectedValue();
                    File file =
                        StateJList.this.simulator.getCurrentGrammar().getGraphs().get(
                            selection);
                    StateJList.this.simulator.doLoadStartGraph(file);
                    StateJList.this.simulator.getStateFileChooser().setSelectedFile(
                        file);
                } else if (e.getClickCount() == 1) {
                    try {
                        wait(100);
                    } catch (Exception ex) {
                        // riiiiiiight...
                    }
                    AspectualGraphView start =
                        StateJList.this.simulator.getCurrentGrammar().getStartGraph();
                    if (start != null) {
                        StateJList.this.setSelectedValue(start.getName(), false);
                    }
                }
            }
        };
        this.addMouseListener(mouseListener);

    }

    public void applyTransitionUpdate(GraphTransition transition) {
        // TODO Auto-generated method stub
    }

    public void setGrammarUpdate(DefaultGrammarView grammar) {
        this.removeAll();
        this.setEnabled(false);

        if (grammar != null) {
            this.setListData(grammar.getGraphs().keySet().toArray());
            if (grammar.getStartGraph() != null) {
                String startGraph = grammar.getStartGraph().getName();
                this.setSelectedValue(startGraph, true);
            }
            this.setEnabled(true);
        }
    }

    public void setMatchUpdate(RuleMatch match) {
        // TODO Auto-generated method stub

    }

    public void setRuleUpdate(NameLabel name) {
        // TODO Auto-generated method stub

    }

    public void setStateUpdate(GraphState state) {
        // TODO Auto-generated method stub

    }

    public void setTransitionUpdate(GraphTransition transition) {
        // TODO Auto-generated method stub

    }

    public void startSimulationUpdate(GTS gts) {
        // TODO Auto-generated method stub

    }

    /**
     * The simulator to which this directory belongs.
     * @invariant simulator != null
     */
    protected final Simulator simulator;

}
