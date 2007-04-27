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
 * $Id: DefaultGraphCalculator.java,v 1.7 2007-04-27 22:06:59 rensink Exp $
 */
package groove.calc;

import groove.graph.Graph;
import groove.lts.ExploreStrategy;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.State;
import groove.lts.explore.LinearStrategy;
import groove.trans.GraphGrammar;
import groove.trans.GraphTest;
import groove.trans.Rule;
import groove.util.FormatException;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Default implementation of a graph calculator.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultGraphCalculator implements GraphCalculator {
//    /**
//     * Creates a graph calculator for a given rule set and start graph.
//     * @param rules the rule system for the calculator
//     * @param start the start graph for the calculator
//     */
//    public DefaultGraphCalculator(RuleSystem rules, Graph start) {
//        this(new GraphGrammar(rules, start), false);
//    }
//    
    /**
     * Creates a graph calculator for a given, fixed graph grammar.
     * @param grammar the graph grammar for the calculator
     */
    public DefaultGraphCalculator(GraphGrammar grammar) {
        this(grammar, false);
    }
//
//    /**
//     * Creates a prototype calculator, with a <code>null</code> start graph.
//     * @param rules the rule set of the prototype
//     */
//    public DefaultGraphCalculator(RuleSystem rules) {
//        this(new GraphGrammar(rules), true);
//    }

    /**
     * Creates a (possibly prototype) calculator on the basis of a given,
     * fixed graph grammar.
     * @param grammar the pre-existing graph grammar
     * @param prototype flag to indicate whether the constructed calculator is to be used
     * as a prototype
     */
    protected DefaultGraphCalculator(GraphGrammar grammar, boolean prototype) {
    	grammar.testFixed(true);
        this.grammar = grammar;
        this.gts = new GTS(grammar);
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
        	strategy.setGTS(getGTS());
            try {
            	strategy.setAtState(gts.getOpenStateIter().next());
            	strategy.explore();
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
        return getGenerator().get(isMaximal, toResult);
    }

    public Collection<GraphResult> getAllMax() {
        testPrototype();
        return getGenerator().getAll(isMaximal, toResult);
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
        return getGenerator().get(getMatcher(condition), toResult);
    }

    public Collection<GraphResult> getAll(final GraphTest condition) {
        testPrototype();
        return getGenerator().getAll(getMatcher(condition), toResult);
    }

    public Graph getBasis() {
        if (isPrototype()) {
            return null;
        } else {
            return gts.startState().getGraph();
        }
    }

    public GraphCalculator newInstance(Graph start) throws IllegalArgumentException {
    	try {
			GraphGrammar newGrammar = new GraphGrammar(grammar, start);
			grammar.setFixed();
			return new DefaultGraphCalculator(newGrammar);
		} catch (FormatException exc) {
			throw new IllegalArgumentException(exc.getMessage(), exc);
		}
    }
    
    public GTS getGTS() {
        return gts;
    }
    
    /** Lazily constructs and returns a state generator for the GTS. */
	private Explorer getGenerator() {
		if (generator == null) {
			generator = new Explorer();
			generator.setGTS(getGTS());
		}
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
                    for (GraphState succ: getGenerator().getSuccessors(result)) {
                        if ((succ instanceof GraphNextState)) {
                            GraphState source = ((GraphNextState) succ).source();
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
                if (state instanceof GraphNextState) {
                    return depth(((GraphNextState) state).source())+1;
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
        Iterator<? extends GraphState> nextStateIter = getGenerator().getSuccessorIter(state);
        while (result && nextStateIter.hasNext()) {
            result = nextStateIter.next() == state;
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

    /** Returns a property that tests for the matching of a graph to a test. */
    private final Property<GraphState> getMatcher(final GraphTest test) {
    	return new Property<GraphState>() {
        	@Override
			public boolean isSatisfied(GraphState state) {
				return test.matches(state.getGraph());
			}
        };
    }
    
    /** Property that tests for the maximality of a state, using {@link #isMaximal(GraphState)}. */
    private final Property<GraphState> isMaximal = new Property<GraphState>() {
    	@Override
		boolean isSatisfied(GraphState state) {
			return isMaximal(state);
		}
    };
    
    /** Function from graph states to graph results, using {@link #createResult(GraphState)}. */
    private final Function<GraphState, GraphResult> toResult = new Function<GraphState, GraphResult>() {
		@Override
		public GraphResult apply(GraphState arg) {
			return arg == null ? null : createResult(arg);
		}
	};
    
    /**
	 * Method to test if the calculator is a prototype.
	 * 
	 * @throws IllegalStateException
	 *             if the calculator is a prototype
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
    private Explorer generator;
    /**
     * Switch indicating if the calculator is a prototype only.
     */
    private final boolean prototype;
}
