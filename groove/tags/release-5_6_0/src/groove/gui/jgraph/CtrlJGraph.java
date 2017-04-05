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
package groove.gui.jgraph;

import groove.control.graph.ControlEdge;
import groove.control.graph.ControlGraph;
import groove.control.graph.ControlNode;
import groove.control.template.Template;
import groove.graph.Edge;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.gui.Simulator;
import groove.gui.layout.ForestLayouter;
import groove.gui.layout.Layouter;

/**
 * This is the JGraph representation of a ControlAutomaton.
 * @author Tom Staijen
 * @version $Revision $
 */
public class CtrlJGraph extends JGraph<ControlGraph> {
    /**
     * Creates a ControlJGraph given a ControlJModel
     * @param simulator the simulator that is the context of this jgraph; may be
     *        <code>null</code>.
     */
    public CtrlJGraph(Simulator simulator) {
        super(simulator);
        getGraphLayoutCache().setSelectsAllInsertedCells(false);
        setConnectable(false);
        setDisconnectable(false);
        setEnabled(true);
        setToolTipEnabled(true);
    }

    @Override
    public GraphRole getGraphRole() {
        return GraphRole.CTRL;
    }

    /** Creates a new model based on a given control automaton. */
    public void setModel(Template template) {
        if (getModel() == null || getModel().getGraph().getTemplate() != template) {
            JModel<ControlGraph> newModel = newModel();
            newModel.loadGraph(template.toGraph(true));
            setModel(newModel);
        }
    }

    @Override
    public boolean isShowNodeIdentities() {
        return true;
    }

    @Override
    public boolean isShowLoopsAsNodeLabels() {
        return true;
    }

    @Override
    public Layouter getDefaultLayouter() {
        return ForestLayouter.PROTOTYPE;
    }

    @Override
    protected JGraphFactory<ControlGraph> createFactory() {
        return new MyFactory();
    }

    private class MyFactory extends JGraphFactory<ControlGraph> {
        public MyFactory() {
            super(CtrlJGraph.this);
        }

        @Override
        public CtrlJVertex newJVertex(Node node) {
            assert node instanceof ControlNode;
            return CtrlJVertex.newInstance();
        }

        @Override
        public CtrlJEdge newJEdge(Edge edge) {
            assert edge instanceof ControlEdge;
            return CtrlJEdge.newInstance();
        }
    }
}
