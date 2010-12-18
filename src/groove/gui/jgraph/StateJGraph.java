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

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.gui.Exporter;
import groove.gui.Options;
import groove.gui.Simulator;

import javax.swing.JPopupMenu;

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
        this(simulator, new GraphJModel<DefaultNode,DefaultEdge>(
            new DefaultGraph(), simulator.getOptions()));
    }

    /**
     * Constructs a state graph associated with a given simulator, and with
     * pre-defined underlying model.
     */
    protected StateJGraph(Simulator simulator, GraphJModel<?,?> graphModel) {
        super(graphModel, true);
        setConnectable(false);
        setDisconnectable(false);
        setEnabled(false);
        this.simulator = simulator;
    }

    /** Specialises the return type to a {@link JModel}. */
    @Override
    public GraphJModel<?,?> getModel() {
        return (GraphJModel<?,?>) super.getModel();
    }

    @Override
    protected void fillPopupMenu(JPopupMenu result) {
        addSeparatorUnlessFirst(result);
        result.add(this.simulator.getApplyTransitionAction());
        result.addSeparator();
        result.add(this.simulator.getEditGraphAction());
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

}