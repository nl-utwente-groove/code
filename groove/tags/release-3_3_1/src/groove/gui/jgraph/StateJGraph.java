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
 * $Id: StateJGraph.java,v 1.9 2008-02-05 13:28:03 rensink Exp $
 */
package groove.gui.jgraph;

import groove.abs.AbstrGraph;
import groove.abs.GraphPattern;
import groove.graph.DefaultGraph;
import groove.graph.Node;
import groove.gui.Exporter;
import groove.gui.GraphPatternPopupWindow;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.lts.GraphState;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import org.jgraph.graph.DefaultGraphCell;

/**
 * Implementation of {@link JGraph} that provides the proper popup menu. To
 * construct an instance, {@link #fillPopupMenu(JPopupMenu)} should be called
 * after all global final variables have been set.
 */
public class StateJGraph extends JGraph {
    /**
     * Constructs a state graph associated with a given simulator.
     * @param simulator the simulator to which this j-graph is associated
     */
    public StateJGraph(Simulator simulator) {
        this(simulator, new GraphJModel(new DefaultGraph(),
            simulator.getOptions()));
    }

    /**
     * Constructs a state graph associated with a given simulator, and with
     * pre-defined underlying model.
     * @param simulator
     * @param graphModel
     */
    protected StateJGraph(Simulator simulator, GraphJModel graphModel) {
        super(graphModel, true, null);
        setConnectable(false);
        setDisconnectable(false);
        setEnabled(false);
        this.simulator = simulator;

        // add a mouse listener used in case of abstract simulation
        addMouseListener(new MyMouseListener());

    }

    /** Specialises the return type to a {@link JModel}. */
    @Override
    public GraphJModel getModel() {
        return (GraphJModel) this.graphModel;
    }

    @Override
    protected void fillPopupMenu(JPopupMenu result) {
        addSeparatorUnlessFirst(result);
        result.add(this.simulator.getApplyTransitionAction());
        // IOVKA editing a graph is not allowed for abstract simulation
        if (!this.simulator.isAbstractSimulation()) {
            result.addSeparator();
            result.add(this.simulator.getEditGraphAction());
        }
        super.fillPopupMenu(result);
    }

    @Override
    protected Exporter getExporter() {
        return this.simulator.getExporter();
    }

    @Override
    protected String getExportActionName() {
        return Options.EXPORT_STATE_ACTION_NAME;
    }

    @Override
    public Simulator getSimulator() {
        return this.simulator;
    }

    /**
     * The simulator to which this j-graph is associated.
     */
    final private Simulator simulator;

    /**
     * Mouse listener that creates the pop-up menu and switches the view to the
     * rule panel on double-clicks.
     */
    private class MyMouseListener extends MouseAdapter {
        /** Empty constructor with the correct visibility. */
        MyMouseListener() {
            // empty
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            if (!StateJGraph.this.simulator.isAbstractSimulation()) {
                return;
            }
            if (evt.getButton() == MouseEvent.BUTTON1
                && evt.getClickCount() == 2) {
                // scale from screen to model
                java.awt.Point loc = evt.getPoint();
                // find cell in model coordinates
                DefaultGraphCell cell =
                    (DefaultGraphCell) getFirstCellForLocation(loc.x, loc.y);
                if (cell instanceof GraphJVertex) {
                    GraphJVertex vertex = (GraphJVertex) cell;
                    Node node = vertex.getNode();
                    GraphState state =
                        StateJGraph.this.simulator.getCurrentState();
                    GraphPattern pattern =
                        ((AbstrGraph) vertex.getGraphJModel().getGraph()).typeOf(node);
                    new GraphPatternPopupWindow(pattern,
                        vertex.getGraphJModel().getOptions(), "Type of node "
                            + node.toString()
                            + (state == null ? "" : " of state "
                                + state.toString()));
                }

            }
        }
    }

}