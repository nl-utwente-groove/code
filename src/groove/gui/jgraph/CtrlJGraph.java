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
import groove.gui.Exporter;
import groove.gui.Options;
import groove.gui.SetLayoutMenu;
import groove.gui.Simulator;
import groove.gui.layout.Layouter;
import groove.gui.layout.SpringLayouter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This is the JGraph representation of a ControlAutomaton.
 * @author Tom Staijen
 * @version $Revision $
 */
public class CtrlJGraph extends JGraph {
    /**
     * Creates a ControlJGraph given a ControlJModel
     * @param simulator the simulator that is the context of this jgraph; may be
     *        <code>null</code>.
     */
    public CtrlJGraph(Simulator simulator) {
        super(simulator == null ? null : simulator.getOptions(), true);
        this.exporter = simulator.getExporter();
        getGraphLayoutCache().setSelectsAllInsertedCells(false);
        this.setLayoutMenu.selectLayoutAction(createInitialLayouter().newInstance(
            (this)));
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

    /**
     * Returns the active transition of the LTS, if any. The active transition
     * is the one currently selected in the simulator. Returns <tt>null</tt> if
     * no transition is selected.
     */
    public CtrlTransition getActiveTransition() {
        return this.activeTransition;
    }

    /**
     * Returns the active state of the LTS, if any. The active transition is the
     * one currently displayed in the state frame. Returns <tt>null</tt> if no
     * state is active (which should occur only if no grammar is loaded and
     * hence the LTS is empty).
     */
    public CtrlState getActiveLocation() {
        return this.activeLocation;
    }

    /**
     * Sets the active transition to a new value, and returns the previous
     * value. Both old and new transitions may be <tt>null</tt>.
     * @param trans the new active transition
     * @return the old active transition
     */
    public CtrlTransition setActiveTransition(CtrlTransition trans) {
        CtrlTransition result = this.activeTransition;
        this.activeTransition = trans;
        Set<GraphJCell> changedCells = new HashSet<GraphJCell>();
        if (trans != null) {
            GraphJCell jCell = getModel().getJCellForEdge(trans);
            assert jCell != null : String.format("No image for %s in jModel",
                trans);
            changedCells.add(jCell);
        }
        if (result != null) {
            GraphJCell jCell = getModel().getJCellForEdge(result);
            assert jCell != null : String.format("No image for %s in jModel",
                result);
            changedCells.add(jCell);
        }
        getModel().refresh(changedCells);
        return result;
    }

    /**
     * Sets the active location to a new value, and returns the previous value.
     * Both old and new locations may be <tt>null</tt>.
     * @param location the new active location
     * @return the old active location
     */
    public CtrlState setActiveLocation(CtrlState location) {
        CtrlState result = this.activeLocation;
        this.activeLocation = location;
        Set<GraphJCell> changedCells = new HashSet<GraphJCell>();
        if (result != null) {
            GraphJCell jCell = getModel().getJCellForNode(result);
            assert jCell != null : String.format("No image for %s in jModel",
                result);
            changedCells.add(jCell);
        }
        getModel().refresh(changedCells);
        return result;
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
    protected Exporter getExporter() {
        return this.exporter;
    }

    @Override
    protected String getExportActionName() {
        return Options.EXPORT_CONTROL_ACTION_NAME;
    }

    /** The context of this jgraph; possibly <code>null</code>. */
    private final Exporter exporter;

    /**
     * The active state of the control automaton. Is null if there is no active state.
     */
    private CtrlState activeLocation;

    /**
     * The currently active transition of the control automaton.
     */
    private CtrlTransition activeTransition;

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
        public Layouter newInstance(JGraph jGraph) {
            return new MyForestLayouter(this.name, (CtrlJGraph) jGraph);
        }
    }
}
