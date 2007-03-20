/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: DefaultGraphCalculator.java,v 1.1.1.1 2007-03-20 10:05:37 kastenberg Exp $
 */
package groove.calc;

import groove.graph.Graph;
import groove.graph.GraphListener;
import groove.lts.DerivedGraphState;
import groove.lts.ExploreStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.State;
import groove.lts.StateGenerator;
import groove.lts.Transition;
import groove.lts.explore.FullStrategy;
import groove.lts.explore.LinearStrategy;
import groove.trans.GraphGrammar;
import groove.trans.GraphTest;
import groove.trans.Rule;
import groove.trans.RuleSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Default implementation of a graph calculator.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultGraphCalculator implements GraphCalculator {
    /**
     * Creates a graph calculator for a given rule set and start graph.
     * @param rules the rule system for the calculator
     * @param start the start graph for the calculator
     */
    public DefaultGraphCalculator(RuleSystem rules, Graph start) {
        this(new GraphGrammar(rules, start), false);
    }
    
    /**
     * Creates a graph calculator for a given graph grammar.
     * @param grammar the graph grammar for the calculator
     */
    public DefaultGraphCalculator(GraphGrammar grammar) {
        this(grammar, false);
    }

    /**
     * Creates a prototype calculator, with a <code>null</code> start graph.
     * @param rules the rule set of the prototype
     */
    public DefaultGraphCalculator(RuleSystem rules) {
        this(new GraphGrammar(rules), true);
    }

    /**
     * Creates a (possibly prototype) calculator on the basis of a given,
     * existing graph grammar.
     * @param grammar the pre-existing graph grammar
     * @param prototype flag to indicate whether the constructed calculator is to be used
     * as a prototype
     */
    protected DefaultGraphCalculator(GraphGrammar grammar, boolean prototype) {
        this.grammar = grammar;
        this.gts = grammar.gts();
        this.generator = new StateGenerator(gts);
        this.prototype = prototype;
    }

    public GraphResult getMax() {
        testPrototype();
        GraphState result = null;
        // any final state is maximal; try that first
        Collection<? extends GraphState> finalStates = gts.getFinalStates();
        if (!finalStates.isEmpty()) {
            result = finalStates.iterator().next();
        } else if (gts.hasOpenStates()) {
            // first do a linear exploration from any open state
            // (by the assumption of uniqueness, the choice of open state should not matter)
        	ExploreStrategy strategy = new LinearStrategy();
        	strategy.setLTS(gts);
//            gts.setExploreStrategy(new LinearStrategy());
            try {
            	strategy.setAtState(gts.getOpenStateIter().next());
            	strategy.explore();
//                gts.explore(gts.getOpenStateIter().next());
            } catch (InterruptedException exc) {
                // empty catch block
            }
            // again test for final states (this is anyway the most efficient)
            if (!finalStates.isEmpty()) {
                result = finalStates.iterator().next();
            }
        }
        // no go; do an explicit maximality test
        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        while (result == null && stateIter.hasNext()) {
            GraphState graph = stateIter.next();
            if (isMaximal(graph)) {
                result = graph;
            }
        }
        return createResult(result);
    }
    
    /**
	 * Returns the first "maximal" graph, i.e., that cannot evolve further,
	 * where <i>first</i> means that it has the shortest path from the original
	 * graph (as given by {@link #getBasis()}). The method will fail to
	 * terminate if there is no graph meeting the requirements.
	 * 
	 * @return A result wrapping a state whose only outgoing transitions are to
	 *         itself, or <code>null</code> if there is no such state. The
	 *         resulting state is guaranteed to be the one closest to the
	 *         original graph.
	 * @see #getMax()
	 * @see #getAllMax()
	 * @throws IllegalStateException
	 *             if the basis has not been initialised
	 */
	public GraphResult getFirstMax() {
        testPrototype();
        GraphState result = null;
        Iterator<? extends GraphState> stateIter = getBreadthFirstIterator();
        while (result == null && stateIter.hasNext()) {
            GraphState graph = stateIter.next();
            if (isMaximal(graph)) {
                result = graph;
            }
        }
        return createResult(result);
    }

    public Collection<GraphResult> getAllMax() {
        testPrototype();
        // first do a full exploration
        ExploreStrategy strategy = new FullStrategy(gts);
//        gts.setExploreStrategy(new FullStrategy());
        try {
            strategy.explore();
        } catch (InterruptedException exc) {
            // empty catch block
        }
        Collection<GraphResult> result = new HashSet<GraphResult>();
        // go over the states and do a maximality test
        for (GraphState graph: gts.nodeSet()) {
            if (isMaximal(graph)) {
                result.add(createResult(graph));
            }
        }
        return result;
    }

    public Collection<GraphResult> getAll(String conditionName) {
        return getAll(getRule(conditionName));
    }

    public GraphResult getFirst(String conditionName) {
        return getFirst(getRule(conditionName));
    }

    /**
     * Returns the rule in the underlying grammar with a certain name.
     * @throws IllegalArgumentException if no such condition exists
     */
    protected Rule getRule(String name) {
        Rule result = getGrammar().getRule(name);
        if (result == null) {
            throw new IllegalArgumentException("No rule \""+name+"\" in grammar");
        }
        return result;
    }

    public GraphResult getFirst(GraphTest condition) {
        testPrototype();
        GraphState result = null;
        Iterator<GraphState> stateIter = getBreadthFirstIterator();
        while (result == null && stateIter.hasNext()) {
            GraphState state = stateIter.next();
            if (condition.hasMatching(state.getGraph())) {
                result = state;
            }
        }
        return result == null ? null : createResult(result);
    }

    public Collection<GraphResult> getAll(GraphTest condition) {
        testPrototype();
        // first do a full exploration
        ExploreStrategy strategy = new FullStrategy(gts);
//        gts.setExploreStrategy(new FullStrategy());
        try {
//            gts.explore();
        	strategy.explore();
        } catch (InterruptedException exc) {
            // empty catch block
        }
        Collection<GraphResult> result = new HashSet<GraphResult>();
        // go over the states and do a maximality test
        for (GraphState state: gts.nodeSet()) {
            if (condition.hasMatching(state.getGraph())) {
                result.add(createResult(state));
            }
        }
        return result;
    }

    public Graph getBasis() {
        if (isPrototype()) {
            return null;
        } else {
            return gts.startState().getGraph();
        }
    }

    public GraphCalculator newInstance(Graph start) {
        return new DefaultGraphCalculator(grammar, start);
    }
    
    public void addGTSListener(GraphListener listener) {
        gts.addGraphListener(listener);
    }

    public GTS getGTS() {
        return gts;
    }
    
	public StateGenerator getGenerator() {
		return generator;
	}

	public GraphGrammar getGrammar() {
        return grammar;
    }

    /**
     * Returns a breadth-first iterator over all the states of the LTS,
     * starting with the initial state,
     * generating previously unexplored parts where necessary.
     */
    protected Iterator<GraphState> getBreadthFirstIterator() {
        return new Iterator<GraphState>() {
            /** If this method returns <code>true</code>, the next element is stored in <code>next</code>. */
            public boolean hasNext() {
                if (next == null) {
                    next = new LinkedList<GraphState>();
                    next.add(gts.startState());
                }
                return !next.isEmpty();
            }

            public GraphState next() {
                if (hasNext()) {
                    // the first state of the list is the one we want
                    GraphState result = next.get(0);
                    next.remove(0);
                    // now add those states that are really derived from result to the list
                    for (GraphState succ: generator.getSuccessors(result)) {
                        if ((succ instanceof DerivedGraphState)) {
                            GraphState source = ((DerivedGraphState) succ).source();
                            if (source == result) {
                                next.add(succ);
                            } else {
                                assert depth(source) <= depth(result) : "Depth decreases from "+depth(source)+" to "+depth(result);    
                            }
                        }
                    }
                    return result;
                } else {
                    throw new IllegalStateException();
                }
            }
            
            int depth(State state) {
                if (state instanceof DerivedGraphState) {
                    return depth(((DerivedGraphState) state).source())+1;
                } else {
                    return 0;
                }
            }

            /** Operation not supported. */
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            private List<GraphState> next;
        };
    }

    /**
     * Factory method for a graph result.
     */
    protected GraphResult createResult(GraphState state) {
        return new DefaultGraphResult(this, state);
    }
    
    /**
     * Tests if a certain state is maximal in the LTS,
     * in the sense that there are no outgoing transitions to any other state except itself.
     */
    protected boolean isMaximal(GraphState state) {
        boolean result = true;
        Iterator<GraphTransition> outTransIter = state.getTransitionIter();
        while (result && outTransIter.hasNext()) {
            Transition element = outTransIter.next();
            result = element.opposite() == state;
        }
        return result;
    }

    /**
     * Indicates if this calculator is a prototype.
     * If <code>true</code>, then it is only to be used to create new instances.
     */
    protected boolean isPrototype() {
        return prototype;
    }
    
    /**
     * Method to test if the calculator is a prototype.
     * @throws IllegalStateException if the calculator is a prototype
     */
    private void testPrototype() {
        if (isPrototype()) {
            throw new IllegalStateException();
        }
    }
    /** 
     * The grammar underlying this calculator.
     */
    private final GraphGrammar grammar;
    /**
     * The transition system underlying this calculator.
     */
    private final GTS gts;
    /** The state explorer for the transition system. */
    private final StateGenerator generator;
    /**
     * Switch indicating if the calculator is a prototype only.
     */
    private final boolean prototype;
}
