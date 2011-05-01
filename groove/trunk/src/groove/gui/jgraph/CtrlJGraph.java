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
import groove.gui.Options;
import groove.gui.SetLayoutMenu;
import groove.gui.Simulator;
import groove.gui.layout.Layouter;
import groove.gui.layout.SpringLayouter;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;

import org.jgraph.graph.GraphConstants;

/**
 * This is the JGraph representation of a ControlAutomaton.
 * @author Tom Staijen
 * @version $Revision $
 */
public class CtrlJGraph extends GraphJGraph {
    /**
     * Creates a ControlJGraph given a ControlJModel
     * @param simulator the simulator that is the context of this jgraph; may be
     *        <code>null</code>.
     */
    public CtrlJGraph(Simulator simulator) {
        super(simulator == null ? null : simulator.getOptions(), true);
        getGraphLayoutCache().setSelectsAllInsertedCells(false);
        getSetLayoutMenu().selectLayoutAction(
            createInitialLayouter().newInstance((this)));
        setConnectable(false);
        setDisconnectable(false);
        setEnabled(true);
        setToolTipEnabled(true);
    }

    /** Creates a new model based on a given control automaton. */
    public void setModel(CtrlAut aut) {
        if (getModel() == null || getModel().getGraph() != aut) {
            GraphJModel<CtrlState,CtrlTransition> newModel = newModel();
            newModel.loadGraph(aut);
            setModel(newModel);
        }
    }

    @Override
    public GraphJModel<CtrlState,CtrlTransition> newModel() {
        return new GraphJModel<CtrlState,CtrlTransition>(
            CtrlJVertex.getPrototype(this), CtrlJEdge.getPrototype(this));
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

    @Override
    protected String getExportActionName() {
        return Options.EXPORT_CONTROL_ACTION_NAME;
    }

    /** The default node attributes of the control automaton */
    static public final JAttr.AttributeMap CONTROL_NODE_ATTR;
    /** The start node attributes of the control automaton */
    static public final JAttr.AttributeMap CONTROL_START_NODE_ATTR;
    /** The sucess node attributes of the control automaton */
    static public final JAttr.AttributeMap CONTROL_SUCCESS_NODE_ATTR;
    /** The default edge attributes of the control automaton */
    static public final JAttr.AttributeMap CONTROL_EDGE_ATTR;
    /** The internal lambda edge attributes of the control automaton */
    static public final JAttr.AttributeMap CONTROL_LAMBDA_EDGE_ATTR;
    /** The internal lambda edge attributes of the control automaton */
    static public final JAttr.AttributeMap CONTROL_FAILURE_EDGE_ATTR;
    /** The procedure edge attributes of the control automaton automaton */
    static public final JAttr.AttributeMap CONTROL_SHAPE_EDGE_ATTR;

    static {
        JAttr ctrlValues = new JAttr() {
            {
                this.connectable = false;
                this.lineEnd = GraphConstants.ARROW_CLASSIC;
            }
        };
        CONTROL_NODE_ATTR = ctrlValues.getNodeAttrs();
        CONTROL_EDGE_ATTR = ctrlValues.getEdgeAttrs();

        // special nodes
        CONTROL_START_NODE_ATTR = new JAttr() {
            {
                this.backColour = Color.green;
            }
        }.getNodeAttrs();
        CONTROL_SUCCESS_NODE_ATTR = new JAttr() {
            {
                this.borderColour = Color.RED;
                this.backColour = Color.RED;
                this.linewidth = 3;
                this.lineColour = Color.BLUE;
            }
        }.getNodeAttrs();

        // special edges
        CONTROL_LAMBDA_EDGE_ATTR = new JAttr() {
            {
                this.lineColour = Color.GREEN;
            }
        }.getEdgeAttrs();
        CONTROL_FAILURE_EDGE_ATTR = new JAttr() {
            {
                this.lineColour = Color.RED;
            }
        }.getEdgeAttrs();
        CONTROL_SHAPE_EDGE_ATTR = new JAttr() {
            {
                this.font = ITALIC_FONT;
                this.lineColour = Color.GRAY;
            }
        }.getEdgeAttrs();
    }

    private class MyForestLayouter extends groove.gui.layout.ForestLayouter {
        /**
         * Creates a prototype layouter
         */
        public MyForestLayouter() {
            super();
        }

        /**
         * Creates a new instance, for a given {@link GraphJGraph}.
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
        public Layouter newInstance(GraphJGraph jGraph) {
            return new MyForestLayouter(this.name, (CtrlJGraph) jGraph);
        }
    }
}
