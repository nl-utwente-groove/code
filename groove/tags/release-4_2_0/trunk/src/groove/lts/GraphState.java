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
 * $Id: GraphState.java,v 1.11 2008-02-22 13:02:44 rensink Exp $
 */
package groove.lts;

import groove.control.CtrlCall;
import groove.control.CtrlSchedule;
import groove.control.CtrlState;
import groove.graph.Node;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleEvent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Combination of graph and node functionality, used to store the state of a
 * graph transition system.
 * 
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-22 13:02:44 $
 */
public interface GraphState extends Node {
    /** Returns the Graph Transition System of which this is a state. */
    public GTS getGTS();

    /** Returns the graph contained in this state. */
    public HostGraph getGraph();

    /** Returns the (non-{@code null}) control state associated with this state. */
    public CtrlState getCtrlState();

    /** Returns the (possibly {@code null}) control schedule associated with this state. */
    public CtrlSchedule getSchedule();

    /** Sets a new control schedule for this state. */
    public void setSchedule(CtrlSchedule schedule);

    /**
     * Retrieves an outgoing transition with a given event, if it exists. Yields
     * <code>null</code> otherwise.
     */
    public GraphTransitionStub getOutStub(RuleEvent prime);

    /**
     * Returns an iterator over the current set of outgoing transitions starting
     * in this state, as {@link GraphTransition}s.
     */
    public Iterator<GraphTransition> getTransitionIter();

    /**
     * Returns an unmodifiable set view on the currently generated outgoing
     * transitions starting in this state.
     */
    public Set<GraphTransition> getTransitionSet();

    /**
     * Returns an unmodifiable map from rules to the 
     * currently generated outgoing transitions.
     */
    public Map<CtrlCall,Collection<GraphTransition>> getTransitionMap();

    /**
     * Returns (a copy of) the set of next states reachable from this state,
     * according to the currently generated outgoing transitions.
     */
    public Collection<? extends GraphState> getNextStateSet();

    /**
     * Returns an iterator over the next states reachable from this state,
     * according to the currently generated outgoing transitions.
     */
    public Iterator<? extends GraphState> getNextStateIter();

    /**
     * Adds an outgoing transition to this state, if it is not yet there.
     * @return <code>true</code> if the transition was added,
     *         <code>false</code> otherwise
     */
    public boolean addTransition(GraphTransition transition);

    /**
     * Tests if a certain transition is among the currently generated outgoing
     * transitions of this state.
     */
    public boolean containsTransition(GraphTransition transition);

    /**
     * Tests if this state is fully explored, i.e., all outgoing transitions
     * have been generated.
     */
    public boolean isClosed();

    /**
     * Closes this state. This announces that no more outgoing transitions will
     * be generated. The return value indicates if the state was already closed.
     * @ensure <tt>isClosed()</tt>
     * @param finished indicates that all transitions for this state have been added.
     * @return <code>true</code> if the state was closed as a result of this
     *         call; <code>false</code> if it was already closed
     * @see #isClosed()
     */
    public boolean setClosed(boolean finished);

    /**
     * Returns a list of values of the bound variables.
     */
    public HostNode[] getBoundNodes();
}