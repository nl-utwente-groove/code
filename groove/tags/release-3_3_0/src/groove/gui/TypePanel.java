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

import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.StateJGraph;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.type.TypeReconstructor;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.GrammarView;
import groove.view.StoredGrammarView;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * @author Frank van Es
 * @version $Revision $
 */
public class TypePanel extends JGraphPanel<StateJGraph> implements
        SimulationListener {
    /** Display name of this panel. */
    public static final String FRAME_NAME = "Type graph";

    private final JButton createButton;
    private final JGraphPanel<StateJGraph> typeGraphPanel;
    private GrammarView grammar;

    // --------------------- INSTANCE DEFINITIONS ----------------------

    /**
     * Constructor for this TypePanel Creates a new TypePanel instance and
     * instantiates all necessary variables.
     * @param simulator The simulator this type panel belongs to.
     */
    public TypePanel(final Simulator simulator) {
        super(new StateJGraph(simulator), true, true, simulator.getOptions());
        this.simulator = simulator;

        // create the layout for this JPanel
        setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar();

        this.createButton = new JButton("Compute type graph");
        this.createButton.setEnabled(false);
        toolBar.add(this.createButton);
        this.createButton.addActionListener(new CreateButtonListener());
        this.add(toolBar, BorderLayout.NORTH);

        this.typeGraphPanel =
            new JGraphPanel<StateJGraph>(new StateJGraph(simulator), true,
                true, simulator.getOptions());

        this.add(this.typeGraphPanel, BorderLayout.CENTER);

        simulator.addSimulationListener(this);
    }

    /** Does nothing (according to contract, the grammar has already been set). */
    public synchronized void startSimulationUpdate(GTS gts) {
        // nothing happens
    }

    public synchronized void setStateUpdate(GraphState state) {
        // nothing happens
    }

    public synchronized void setTransitionUpdate(GraphTransition trans) {
        // nothing happens
    }

    public void setMatchUpdate(RuleMatch match) {
        // nothing happens
    }

    public synchronized void applyTransitionUpdate(GraphTransition transition) {
        // nothing happens
    }

    public synchronized void setRuleUpdate(RuleName rule) {
        // nothing happens
    }

    /**
     * This method is executed when the grammar in the Simulator is updated. It
     * basically executes one of the following 3 actions: - If no valid grammar
     * is loaded, all fields are disabled. - If a grammar is loaded for which a
     * type graph is saved already, this saved type graph is loaded. - If a
     * grammar is loaded for which no saved type graph exists, all fields are
     * disabled, except the "create type graph" button, which can be used to
     * compute a new type graph.
     */
    public synchronized void setGrammarUpdate(StoredGrammarView grammar) {

        this.typeGraphPanel.jGraph.setModel(AspectJModel.EMPTY_ASPECT_JMODEL);
        this.typeGraphPanel.setEnabled(false);
        this.grammar = grammar;

        if (grammar == null || grammar.getStartGraphView() == null) {
            this.createButton.setEnabled(false);
        } else {
            try {
                // Tries to load a previously saved type graph. If found,
                // this type graph will be displayed.
                Graph typeGraph;
                File file =
                    new File(this.simulator.getLastGrammarFile()
                        + Groove.FILE_SEPARATOR + Groove.TGR_NAME
                        + Groove.GXL_EXTENSION);

                if ((typeGraph = Groove.loadGraph(file)) != null) {
                    GraphInfo.setName(typeGraph, "Type graph");

                    this.typeGraphPanel.jGraph.setModel(GraphJModel.newInstance(
                        typeGraph, this.typeGraphPanel.getOptions()));
                    this.typeGraphPanel.setEnabled(true);
                }
            } catch (IOException e) {
                System.err.println("Error reading the type graph.");
            }
            this.createButton.setEnabled(true);
        }
        this.typeGraphPanel.refreshStatus();
    }

    /**
     * Action listener class for the "create type graph" button
     * @author Frank van Es
     * @version $Revision $
     */
    class CreateButtonListener implements ActionListener {
        /**
         * This method is executed when the "create type graph" button is
         * clicked. Then a new type graph for the current grammar is being
         * computed; the type graph will be displayed and saved inside the graph
         * grammar directory.
         */
        public void actionPerformed(ActionEvent e) {
            if (TypePanel.this.grammar != null
                && TypePanel.this.grammar.getStartGraphView() != null) {
                try {
                    Graph typeGraph =
                        TypeReconstructor.reconstruct(TypePanel.this.grammar.toGrammar());
                    Groove.saveGraph(typeGraph,
                        TypePanel.this.simulator.getLastGrammarFile()
                            + Groove.FILE_SEPARATOR + Groove.TGR_NAME
                            + Groove.GXL_EXTENSION);
                    displayTypeGraph(typeGraph);

                } catch (FormatException fe) {
                    System.err.printf("Graph format error: %s", fe.getMessage());
                } catch (IOException ioe) {
                    System.err.println("Error storing the type graph.");
                }
            }
        }
    }

    /**
     * Displays a type graph inside the typeGraphPanel.
     * @param typeGraph The type graph to be displayed.
     */
    public void displayTypeGraph(Graph typeGraph) {

        GraphInfo.setName(typeGraph, "Type graph");

        this.typeGraphPanel.jGraph.setModel(GraphJModel.newInstance(typeGraph,
            this.typeGraphPanel.getOptions()));

        this.typeGraphPanel.setEnabled(true);
    }

    /** The simulator to which this panel belongs. */
    private final Simulator simulator;
}
