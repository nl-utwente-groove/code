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
 * $Id: ControlJModel.java,v 1.10 2008-01-30 09:33:13 iovka Exp $
 */
package groove.gui.jgraph;

import groove.control.CtrlAut;
import groove.control.CtrlState;
import groove.control.CtrlTransition;
import groove.gui.Options;

import java.util.HashSet;
import java.util.Set;


/**
 * JModel for a ControlAutomaton
 * @author Tom Staijen
 * @version $Revision $
 */
public class CtrlJModel extends GraphJModel<CtrlState,CtrlTransition> {

    /**
     * The active state of the LTS. Is null if there is no active state.
     * @invariant activeState == null || ltsJModel.graph().contains(activeState)
     */

    private CtrlState activeLocation;

    /**
     * The currently active transition of the LTS. The source node of
     * emphasizedEdge (if non-null) is also emphasized. Is null if there is no
     * currently emphasized edge.
     * @invariant activeTransition == null ||
     *            ltsJModel.graph().contains(activeTransition)
     */
    private CtrlTransition activeTransition;

    /**
     * Creates a controlJmodel given a control automaton
     */
    public CtrlJModel(CtrlAut shape, Options options) {
        super(shape, options);
        this.reload();
    }

    @Override
    public CtrlAut getGraph() {
        return (CtrlAut) super.getGraph();
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
        Set<JCell> changedCells = new HashSet<JCell>();
        if (trans != null) {
            JCell jCell = getJCellForEdge(trans);
            assert jCell != null : String.format("No image for %s in jModel",
                trans);
            changedCells.add(jCell);
        }
        if (result != null) {
            JCell jCell = getJCellForEdge(result);
            assert jCell != null : String.format("No image for %s in jModel",
                result);
            changedCells.add(jCell);
        }
        refresh(changedCells);
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
        Set<JCell> changedCells = new HashSet<JCell>();
        refresh(changedCells);
        return result;
    }

    @Override
    public boolean isShowNodeIdentities() {
        return true;
    }

    /**
     * This implementation returns a {@link CtrlJEdge}.
     */
    @Override
    protected CtrlJEdge createJEdge(CtrlTransition edge) {
        return new CtrlJEdge(this, edge);
    }

    /**
     * This implementation returns a {@link CtrlJVertex}.
     */
    @Override
    protected CtrlJVertex createJVertex(CtrlState node) {
        return new CtrlJVertex(this, node);
    }
}
