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
 * $Id: ControlJGraph.java,v 1.3 2008-01-30 09:33:13 iovka Exp $
 */
package groove.gui.jgraph;

import groove.control.CtrlAut;
import groove.control.CtrlState;
import groove.control.CtrlTransition;
import groove.graph.Edge;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.gui.Simulator;
import groove.gui.layout.Layouter;
import groove.gui.layout.SpringLayouter;
import groove.gui.menu.SetLayoutMenu;

import java.util.Collection;
import java.util.Collections;

/**
 * This is the JGraph representation of a ControlAutomaton.
 * @author Tom Staijen
 * @version $Revision $
 */
public class CtrlJGraph extends JGraph<CtrlAut> {
    /**
     * Creates a ControlJGraph given a ControlJModel
     * @param simulator the simulator that is the context of this jgraph; may be
     *        <code>null</code>.
     */
    public CtrlJGraph(Simulator simulator) {
        super(simulator);
        getGraphLayoutCache().setSelectsAllInsertedCells(false);
        getSetLayoutMenu().selectLayoutAction(
            createInitialLayouter().newInstance((this)));
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
    public void setModel(CtrlAut aut) {
        if (getModel() == null || getModel().getGraph() != aut) {
            JModel<CtrlAut> newModel = newModel();
            newModel.loadGraph(aut);
            setModel(newModel);
        }
    }

    @Override
    public boolean isShowNodeIdentities() {
        return true;
    }

    @Override
    public boolean isShowLoopsAsNodeLabels() {
        return false;
    }

    /**
     * Creates the layouter to be used at construction time.
     */
    protected Layouter createInitialLayouter() {
        return new MyForestLayouter();
    }

    /**
     * Overwrites the menu, so the forest layouter takes the Control start state
     * as its root.
     */
    @Override
    public SetLayoutMenu createSetLayoutMenu() {
        SetLayoutMenu result = new SetLayoutMenu(this, new SpringLayouter());
        result.addLayoutItem(createInitialLayouter());
        return result;
    }

    private class MyForestLayouter extends groove.gui.layout.ForestLayouter {
        /**
         * Creates a prototype layouter
         */
        public MyForestLayouter() {
            super();
        }

        /**
         * Creates a new instance, for a given {@link JGraph}.
         */
        public MyForestLayouter(String name, CtrlJGraph jgraph) {
            super(name, jgraph);
        }

        /**
         * This method returns a singleton set consisting of the LTS start
         * state.
         */
        @Override
        protected Collection<?> getSuggestedRoots() {
            CtrlState start = ((CtrlAut) getModel().getGraph()).getStart();
            return Collections.singleton(getModel().getJCellForNode(start));
        }

        /**
         * This implementation returns a {@link MyForestLayouter}.
         */
        @Override
        public Layouter newInstance(JGraph<?> jGraph) {
            return new MyForestLayouter(this.name, (CtrlJGraph) jGraph);
        }
    }

    @Override
    protected JGraphFactory<CtrlAut> createFactory() {
        return new MyFactory();
    }

    private class MyFactory extends JGraphFactory<CtrlAut> {
        public MyFactory() {
            super(CtrlJGraph.this);
        }

        @Override
        public CtrlJVertex newJVertex(Node node) {
            assert node instanceof CtrlState;
            return CtrlJVertex.newInstance();
        }

        @Override
        public CtrlJEdge newJEdge(Edge edge) {
            assert edge instanceof CtrlTransition;
            return CtrlJEdge.newInstance();
        }
    }
}
