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
import groove.view.DefaultGrammarView;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
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
        this.popupMenu = createPopupMenu();
        this.addMouseListener(new MyMouseListener());
    }
    
    /**
     * In addition to delegating the method to <tt>super</tt>, sets the
     * background color to <tt>null</tt> when disabled and back to the default
     * when enabled.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            if (!enabled) {
                this.enabledBackground = getBackground();
                setBackground(null);
            } else if (this.enabledBackground != null) {
                setBackground(this.enabledBackground);
            }
        }
        super.setEnabled(enabled);
    }

    /**
     * Creates a popup menu, consisting of "use as start graph" & "preview".
     */
    protected JPopupMenu createPopupMenu() {
        JPopupMenu result = new JPopupMenu();
        JMenuItem item;
        result.add(item = new JMenuItem(Options.START_GRAPH_ACTION_NAME));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String selection = (String)StateJList.this.getSelectedValue();
                File file = StateJList.this.simulator.getCurrentGrammar().getGraphs().get(selection);
                StateJList.this.simulator.doLoadStartGraph(file);
              }
            });
        result.add(item = new JMenuItem(Options.PREVIEW_ACTION_NAME));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                StateJList.this.doPreviewGraph();
              }
            });
        return result;
    }
    
    private void doPreviewGraph() {
        String selection = (String)this.getSelectedValue();
        File file = this.simulator.getCurrentGrammar().getGraphs().get(selection);
        boolean load = Editor.previewGraph(file, Options.START_GRAPH_ACTION_NAME);
        if (load) {
            this.simulator.doLoadStartGraph(file);
        }
    }

    // -----------------------------------------
    // Methods from SimulationListener Interface
    // -----------------------------------------
    
    public void applyTransitionUpdate(GraphTransition transition) {
        // TODO Auto-generated method stub
    }

    public void setGrammarUpdate(DefaultGrammarView grammar) {
        this.removeAll();
        this.setEnabled(false);

        if (grammar != null) {
            Object grammarNames[] = grammar.getGraphs().keySet().toArray();
            Arrays.sort(grammarNames);
            this.setListData(grammarNames);
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

    // --------------
    // Private fields
    // --------------
    
    /**
     * The simulator to which this directory belongs.
     * @invariant simulator != null
     */
    protected final Simulator simulator;

    /**
     * The background colour of this component when it is enabled.
     */
    private Color enabledBackground;
    
    /**
     * The pop-up menu for this states list.
     * @invariant popupMenu != null
     */
    protected final JPopupMenu popupMenu;

    // -----------------------------------------
    // MyMouseListener Class
    // -----------------------------------------
    
    /** Class to deal with mouse events over the label list. */
    private class MyMouseListener extends MouseAdapter {
        
        /** Empty constructor with the correct visibility. */
        MyMouseListener() {
            // empty
        }
        
        @Override
        public void mouseClicked(MouseEvent evt) {
            int index = locationToIndex(evt.getPoint());
            if (StateJList.this.isEnabled() && index >= 0) {
                if (evt.getClickCount() == 2) { // Left double click
                    StateJList.this.doPreviewGraph();
                } else if (evt.getClickCount() == 1 &&
                           evt.getButton() == MouseEvent.BUTTON3) { // Right click
                    // Adjust list selection accordingly.
                    StateJList.this.setSelectedIndex(index);
                    StateJList.this.popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        }
    }

}
