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
import groove.control.CtrlVar;
import groove.gui.Options;
import groove.lts.GraphTransition;
import groove.util.Converter;
import groove.util.Groove;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgraph.graph.AttributeMap;

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
        super(shape, JAttr.CONTROL_NODE_ATTR, JAttr.CONTROL_EDGE_ATTR, options);
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
     * This implementation returns a {@link CtrlJModel.TransitionJEdge}.
     */
    @Override
    protected TransitionJEdge createJEdge(CtrlTransition edge) {
        return new TransitionJEdge(this, edge);
    }

    /**
     * This implementation returns a {@link CtrlJModel.StateJVertex}.
     */
    @Override
    protected StateJVertex createJVertex(CtrlState node) {
        return new StateJVertex(this, node);
    }

    /**
     * This implementation adds special attributes for the start state, open
     * states, final states, and the active state.
     * @see JAttr#LTS_NODE_ATTR
     * @see JAttr#LTS_START_NODE_ATTR
     * @see JAttr#LTS_OPEN_NODE_ATTR
     * @see JAttr#LTS_FINAL_NODE_ATTR
     * @see JAttr#LTS_NODE_ACTIVE_CHANGE
     */
    @Override
    protected AttributeMap createJVertexAttr(CtrlState state) {
        AttributeMap result;
        if (state.equals(getGraph().getStart())) {
            result = JAttr.CONTROL_START_NODE_ATTR.clone();
        } else if (state.equals(getGraph().getFinal())) {
            result = JAttr.CONTROL_SUCCESS_NODE_ATTR.clone();
        } else {
            result = JAttr.CONTROL_NODE_ATTR.clone();
        }

        return result;
    }

    /**
     * This implementation adds special attributes for the active transition.
     * @see JAttr#LTS_EDGE_ATTR
     * @see JAttr#LTS_EDGE_ACTIVE_CHANGE
     */
    @Override
    protected void modifyJEdgeAttr(AttributeMap result,
            Set<CtrlTransition> edgeSet) {
        super.modifyJEdgeAttr(result, edgeSet);
        // get the first node
        CtrlTransition t = edgeSet.iterator().next();

        if (!t.label().getGuard().isEmpty()) {
            result.applyMap(JAttr.CONTROL_FAILURE_EDGE_ATTR);
        } else {
            result.applyMap(JAttr.CONTROL_EDGE_ATTR);
        }
    }

    /**
     * JEdge class that describes the underlying edge as a graph transition.
     * @author Tom Staijen
     * @version $Revision $
     */
    public class TransitionJEdge extends GraphJEdge<CtrlState,CtrlTransition> {
        /**
         * Creates a new instance from a given edge (required to be a
         * {@link GraphTransition}).
         */
        TransitionJEdge(CtrlJModel jModel, CtrlTransition edge) {
            super(jModel, edge);
        }

        @Override
        StringBuilder getEdgeKindDescription() {
            return new StringBuilder("transition");
        }

        @Override
        String getLabelDescription() {
            StringBuffer result = new StringBuffer(", generated by ");
            String[] displayedLabels = new String[getUserObject().size()];
            int labelIndex = 0;
            for (Object part : getUserObject()) {
                CtrlTransition trans = (CtrlTransition) part;
                String description;
                description = trans.label().text();
                displayedLabels[labelIndex] =
                    Converter.STRONG_TAG.on(description, true);
                labelIndex++;
            }
            if (displayedLabels.length == 1) {
                result.append(displayedLabels[0]);
            } else {
                result.append(Groove.toString(displayedLabels, "<br>- ", "",
                    "<br>- "));
            }
            return result.toString();
        }
    }

    /**
     * JVertex class that describes the underlying node as a graph state.
     * @author Tom Staijen
     * @version $Revision $
     */
    static public class StateJVertex extends
            GraphJVertex<CtrlState,CtrlTransition> {
        /**
         * Creates a new instance for a given node (required to be a
         * {@link CtrlState}) in an LTS model.
         */
        StateJVertex(CtrlJModel jModel, CtrlState node) {
            super(jModel, node, false);
        }

        /**
         * Appends the bound variables to the lines, if this list is not empty
         */
        @Override
        public java.util.List<StringBuilder> getLines() {
            List<StringBuilder> result = super.getLines();
            List<CtrlVar> boundVars = getNode().getBoundVars();
            if (boundVars.size() > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(boundVars.toString());
                result.add(sb);
            }
            return result;
        }

        /** Indicates if this jVertex represents the start state of the control automaton. */
        public boolean isStart() {
            return ((CtrlJModel) getGraphJModel()).getGraph().getStart().equals(
                getNode());
        }

        /** Indicates if this jVertex represents the start state of the control automaton. */
        public boolean isFinal() {
            return ((CtrlJModel) getGraphJModel()).getGraph().getFinal().equals(
                getNode());
        }
    }
}
