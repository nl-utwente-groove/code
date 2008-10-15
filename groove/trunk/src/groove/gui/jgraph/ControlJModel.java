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

import groove.control.ControlAutomaton;
import groove.control.ControlShape;
import groove.control.ControlState;
import groove.control.ControlTransition;
import groove.control.Location;
import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.gui.Options;
import groove.lts.GraphTransition;
import groove.util.Converter;
import groove.util.Groove;

import java.util.HashSet;
import java.util.Set;

import org.jgraph.graph.AttributeMap;

/**
 * JModel for a ControlAutomaton
 * @author Tom Staijen
 * @version $Revision $
 */
public class ControlJModel extends GraphJModel {

    /**
     * The active state of the LTS. Is null if there is no active state.
     * @invariant activeState == null || ltsJModel.graph().contains(activeState)
     */

    private Location activeLocation;

    /**
     * The currently active transition of the LTS. The source node of
     * emphasizedEdge (if non-null) is also emphasized. Is null if there is no
     * currently emphasized edge.
     * @invariant activeTransition == null ||
     *            ltsJModel.graph().contains(activeTransition)
     */
    private ControlTransition activeTransition;

    /**
     * Creates a controlJmodel given a control automaton
     * @param shape
     * @param options
     */
    public ControlJModel(GraphShape shape, Options options) {
        super(shape, JAttr.CONTROL_NODE_ATTR, JAttr.CONTROL_EDGE_ATTR, options);
        this.reload();
    }

    @Override
    public ControlAutomaton getGraph() {
        if (super.getGraph() instanceof ControlAutomaton)
            return (ControlAutomaton) super.getGraph();
        else {
            return null;
        }
    }

    /**
     * Returns the active transition of the LTS, if any. The active transition
     * is the one currently selected in the simulator. Returns <tt>null</tt>
     * if no transition is selected.
     */
    public ControlTransition getActiveTransition() {
        return this.activeTransition;
    }

    /**
     * Returns the active state of the LTS, if any. The active transition is the
     * one currently displayed in the state frame. Returns <tt>null</tt> if no
     * state is active (which should occur only if no grammar is loaded and
     * hence the LTS is empty).
     */
    public Location getActiveLocation() {
        return this.activeLocation;
    }

    /**
     * Sets the active transition to a new value, and returns the previous
     * value. Both old and new transitions may be <tt>null</tt>.
     * @param trans the new active transition
     * @return the old active transition
     */
    public ControlTransition setActiveTransition(ControlTransition trans) {
        ControlTransition result = this.activeTransition;
        this.activeTransition = trans;
        Set<JCell> changedCells = new HashSet<JCell>();
        if (trans != null) {
            JCell jCell = getJCell(trans);
            assert jCell != null : String.format("No image for %s in jModel",
                trans);
            changedCells.add(jCell);
        }
        if (result != null) {
            JCell jCell = getJCell(result);
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
    public Location setActiveLocation(Location location) {
        Location result = this.activeLocation;
        this.activeLocation = location;
        Set<JCell> changedCells = new HashSet<JCell>();

        // TODO: get changed cells from old and new location

        // FIXME: Tom
        // if( result != null ) {
        // for( ControlState cs : result ) {
        // changedCells.add(getJCell(cs));
        // }
        // }
        // if( location != null ) {
        // for( ControlState cs : location ) {
        // changedCells.add(getJCell(cs));
        // }
        // }

        refresh(changedCells);

        return result;
    }

    @Override
    public boolean isShowNodeIdentities() {
        return true;
    }

    /**
     * This implementation returns a {@link ControlJModel.TransitionJEdge}.
     */

    @Override
    protected TransitionJEdge createJEdge(BinaryEdge edge) {
        return new TransitionJEdge(edge);
    }

    /**
     * This implementation returns a {@link ControlJModel.StateJVertex}.
     */
    @Override
    protected StateJVertex createJVertex(Node node) {
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
    protected AttributeMap createJVertexAttr(Node node) {
        AttributeMap result;
        ControlState state = (ControlState) node;
        if (state.equals(getGraph().startState())) {
            result = JAttr.CONTROL_START_NODE_ATTR.clone();
        } else if (state.isSuccess()) {
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
    protected AttributeMap createJEdgeAttr(Set<? extends Edge> edgeSet) {
        AttributeMap result;

        // get the first node
        ControlTransition t = (ControlTransition) edgeSet.iterator().next();

        if (t instanceof ControlShape) {
            result = JAttr.CONTROL_SHAPE_EDGE_ATTR.clone();
        } else if (t.isLambda()) {
            result = JAttr.CONTROL_LAMBDA_EDGE_ATTR.clone();
        } else if (t.hasFailures()) {
            result = JAttr.CONTROL_FAILURE_EDGE_ATTR.clone();
        } else {
            result = JAttr.CONTROL_EDGE_ATTR.clone();
        }
        return result;
    }

    /**
     * JEdge class that describes the underlying edge as a graph transition.
     * @author Tom Staijen
     * @version $Revision $
     */
    private class TransitionJEdge extends GraphJEdge {
        /**
         * Creates a new instance from a given edge (required to be a
         * {@link GraphTransition}).
         */
        TransitionJEdge(BinaryEdge edge) {
            super(ControlJModel.this, edge);
        }

        @Override
        StringBuilder getEdgeKindDescription() {
            return new StringBuilder("transition");
        }

        @Override
        public StringBuilder getLine(Edge edge) {
            if (edge instanceof ControlTransition
                && ((ControlTransition) edge).isLambda()) {
                return new StringBuilder("\u03BB");
            } else {
                return super.getLine(edge);
            }
        }

        @Override
        String getLabelDescription() {
            StringBuffer result = new StringBuffer(", generated by ");
            String[] displayedLabels = new String[getUserObject().size()];
            int labelIndex = 0;
            for (Object part : getUserObject()) {
                ControlTransition trans = (ControlTransition) part;
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
    private class StateJVertex extends GraphJVertex {
        /**
         * Creates a new instance for a given node (required to be a
         * {@link ControlState}) in an LTS model.
         */
        StateJVertex(ControlJModel jModel, Node node) {
            super(jModel, node, false);
        }

        /**
         * Specialises the return type to {@link ControlState}.
         */
        @Override
        public ControlState getNode() {
            return (ControlState) super.getNode();
        }
    }
}
