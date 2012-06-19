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
 * $Id: StateGenerator.java,v 1.32 2008/03/04 14:48:00 kastenberg Exp $
 */
package groove.explore.util;

import groove.control.ControlState;
import groove.control.Location;
import groove.graph.Graph;
import groove.graph.Node;
import groove.lts.AbstractGraphState;
import groove.lts.DefaultGraphNextState;
import groove.lts.DefaultGraphTransition;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.GraphTransitionStub;
import groove.trans.RuleEvent;
import groove.trans.VirtualEvent;
import groove.util.Reporter;

/**
 * Provides functionality to add states and transitions to a GTS, based on known
 * rule events.
 * @author Arend Rensink
 * @version $Revision$
 */
public class MatchApplier implements RuleEventApplier {
    /**
     * Creates an applier for a given graph transition system.
     */
    public MatchApplier(GTS gts) {
        this.gts = gts;
    }

    /**
     * Returns the underlying GTS.
     */
    public GTS getGTS() {
        return this.gts;
    }

    /**
     * Adds a transition to the GTS, from a given source state and for a given
     * rule event. The event is assumed not to have been explored yet.
     * @return the added (new) transition
     */
    public GraphTransition apply(GraphState source, RuleEvent event,
            Location targetLocation) {
        addTransitionReporter.start();
        GraphTransition transition = null;
        Location sourceLocation = source.getLocation();
        if (sourceLocation == targetLocation
            && !event.getRule().isModifying()
            && (sourceLocation == null || !((ControlState) sourceLocation).getTransition(
                event.getRule()).hasOutputParameters())) {
            transition = createTransition(event, source, source, false);
        } else if (targetLocation == null && event instanceof VirtualEvent<?>) {
            // try to find the target state by walking around three previously
            // generated sides of a confluent diamond
            // the parent state is the source of source
            // the sibling is the child reached by the virtual event
            assert source instanceof GraphNextState;
            VirtualEvent.GraphState virtual = (VirtualEvent.GraphState) event;
            RuleEvent sourceEvent = ((GraphNextState) source).getEvent();
            if (virtual.isConfluent(sourceEvent)) {
                GraphState sibling = virtual.getInnerTarget();
                GraphTransitionStub siblingOut =
                    sibling.getOutStub(sourceEvent);
                if (siblingOut != null) {
                    transition =
                        createTransition(event, source,
                            siblingOut.getTarget(sibling),
                            siblingOut.isSymmetry());
                    confluentDiamondCount++;
                }
            }
        }
        if (transition == null) {
            GraphNextState freshTarget =
                createState(event, source, targetLocation);
            addStateReporter.start();
            GraphState isoTarget = getGTS().addState(freshTarget);
            addStateReporter.stop();
            if (isoTarget == null) {
                transition = freshTarget;
            } else {
                transition = createTransition(event, source, isoTarget, true);
            }
        }
        // add transition to gts
        getGTS().addTransition(transition);
        addTransitionReporter.stop();
        return transition;
    }

    /**
     * Creates a fresh graph state, based on a given rule application and source
     * state.
     */
    private GraphNextState createState(RuleEvent event, GraphState source,
            Location target) {
        Node[] addedNodes;
        if (event instanceof VirtualEvent.GraphState) {
            VirtualEvent.GraphState virtual = (VirtualEvent.GraphState) event;
            event = virtual.getInnerEvent();
            addedNodes = getCreatedNodes(virtual, source);
        } else {
            addedNodes = getCreatedNodes(event, source.getGraph());
        }

        return new DefaultGraphNextState((AbstractGraphState) source, event,
            addedNodes, target);
    }

    /**
     * Creates a fresh graph transition, based on a given rule event and source
     * and target state. A final parameter determines if the target state is
     * directly derived from the source, or modulo a symmetry.
     */
    private GraphTransition createTransition(RuleEvent event,
            GraphState source, GraphState target, boolean symmetry) {
        Node[] addedNodes;
        if (event instanceof VirtualEvent.GraphState) {
            VirtualEvent.GraphState virtual = (VirtualEvent.GraphState) event;
            event = virtual.getInnerEvent();
            addedNodes = getCreatedNodes(virtual, source);
        } else {
            addedNodes = getCreatedNodes(event, source.getGraph());
        }
        return new DefaultGraphTransition(event, addedNodes, source, target,
            symmetry);
    }

    /**
     * Returns the array of nodes created when applying a given virtual event to
     * a given source state.
     * @return the inner added nodes of the virtual event, unless this event
     *         coincides with the source state event; otherwise, the added nodes
     *         are computed from the event.
     */
    private Node[] getCreatedNodes(VirtualEvent.GraphState event,
            GraphState source) {
        Node[] result;
        result = event.getInnerAddedNodes();
        // if this application's event is the same as that of the source,
        // test if the added nodes coincide
        if (result.length > 0
            && ((GraphNextState) source).getEvent() == event.getInnerEvent()) {
            Graph host = source.getGraph();
            Node[] sourceAddedNodes = ((GraphNextState) source).getAddedNodes();
            boolean conflict = false;
            for (int i = 0; !conflict && i < result.length; i++) {
                conflict = result[i] == sourceAddedNodes[i];
                assert conflict || !host.containsElement(result[i]);
            }
            if (conflict) {
                // the nodes coincide, so delegate the method
                result = getCreatedNodes(event, host);
            }
        }
        return result;
    }

    /** Computes the nodes created by applying a given event to a given graph. */
    private Node[] getCreatedNodes(RuleEvent event, Graph graph) {
        return event.getCreatedNodes(graph.nodeSet()).toArray(EMPTY_NODE_ARRAY);
    }

    /** The underlying GTS. */
    private final GTS gts;
    /**
     * The number of confluent diamonds found.
     */
    private static int confluentDiamondCount;

    /**
     * Returns the number of confluent diamonds found during generation.
     */
    public static int getConfluentDiamondCount() {
        return confluentDiamondCount;
    }

    /**
     * Returns the time spent generating successors.
     */
    public static long getGenerateTime() {
        return addTransitionReporter.getTotalTime();
    }

    /**
     * Constant empty node array, to be shared among rule applications that
     * create no nodes.
     */
    private static final Node[] EMPTY_NODE_ARRAY = new Node[0];

    /** Reporter for profiling information. */
    static private final Reporter reporter =
        Reporter.register(MatchApplier.class);
    /** Profiling aid for adding states. */
    static public final Reporter addStateReporter =
        reporter.register("addState");
    /** Profiling aid for adding transitions. */
    static public final Reporter addTransitionReporter =
        reporter.register("addTransition");
}