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
 * $Id: SearchPlanStrategy.java,v 1.1 2007-08-24 17:34:58 rensink Exp $
 */
package groove.match;

import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
import groove.util.Reporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This matcher walks through a search tree built up according to
 * a search plan, in which the matching order of the domain elements
 * is determined.
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
public class SearchPlanStrategy implements MatchStrategy {
	/**
     * Constructs a strategy from a given list of search items.
     * A flag controls if solutions should be injective.
     * @param plan the search items that make up the search plan
     * @param injective flag to indicate that the matching should be injective
     */
    public SearchPlanStrategy(List<SearchItem> plan, boolean injective) {
        this.plan = plan;
        this.injective = injective;
    }

    public VarNodeEdgeMap getMatch(Graph graph, NodeEdgeMap preMatch) {
        VarNodeEdgeMap result;
        reporter.start(GET_MATCH);
        Search search = createSearch(graph, preMatch);
        if (search.find()) {
            result = search.getResult();
        } else {
            result = null;
        }
        reporter.stop();
        return result;
    }

    public Iterator<VarNodeEdgeMap> getMatchIter(Graph graph, NodeEdgeMap preMatch) {
        Iterator<VarNodeEdgeMap> result;
        reporter.start(GET_MATCH_ITER);
        final Search search = createSearch(graph, preMatch);
        result = new Iterator<VarNodeEdgeMap>() {
            public boolean hasNext() {
                // test if there is an unreturned next or if we are done
                if (next == null && !atEnd) {
                    // search for the next solution
                    if (search.find()) {
                        next = search.getResult();
                    } else {
                        // there is none and will be none; give up
                        atEnd = true;
                    }
                }
                return !atEnd;
            }
            
            public VarNodeEdgeMap next() {
                if (hasNext()) {
                    VarNodeEdgeMap result = next;
                    next = null;
                    return result;
                } else {
                    throw new NoSuchElementException();
                }
            }
            
            
            public void remove() {
                throw new UnsupportedOperationException();
            }

            /** The next refinement to be returned. */
            private VarNodeEdgeMap next;
            /**
             *  Flag to indicate that the last refinement has been returned,
             * so {@link #next()} henceforth will return <code>false</code>.
             */
            private boolean atEnd = false;
        };
        reporter.stop();
        return result;
    }

    public Collection<VarNodeEdgeMap> getMatchSet(Graph graph, NodeEdgeMap preMatch) {
        reporter.start(GET_MATCH_SET);
        Collection<VarNodeEdgeMap> result = new ArrayList<VarNodeEdgeMap>();
        Search searchRecord = createSearch(graph, preMatch);
        while (searchRecord.find()) {
            result.add(searchRecord.getResult());
        }
        reporter.stop();
        return result;
    }
    
    /**
	 * Indicates if this matching is (to be) injective.
	 */
	protected final boolean isInjective() {
		return this.injective;
	}

	/**
     * Retrieves the search plan for this strategy.
     */
    final protected List<SearchItem> getPlan() {
    	return plan;
    }

    @Override
    public String toString() {
        return plan.toString() + (isInjective() ? "(injective)" : "(non-injective)");
    }
    
    /**
     * Callback factory method for an auxiliary {@link Search} object.
     */
    protected Search createSearch(Graph target, NodeEdgeMap preMatch) {
        return new Search(this, target, preMatch);
    }

    /**
	 * A list of domain elements, in the order in which they are to be matched.
	 */
	private final List<SearchItem> plan;
    /** Flag indicating that the matching should be injective. */
	private final boolean injective;
    
    /** Reporter instance to profile matcher methods. */
    static protected final Reporter reporter = Reporter.register(SearchPlanStrategy.class);
    /** Handle for profiling {@link #getMatch(Graph, NodeEdgeMap)} */
    static protected final int GET_MATCH = reporter.newMethod("getMatch()");
    /** Handle for profiling {@link #getMatchSet(Graph, NodeEdgeMap)} */
    static protected final int GET_MATCH_SET = reporter.newMethod("getMatchSet()");
    /** Handle for profiling {@link #getMatchIter(Graph, NodeEdgeMap)} */
    static protected final int GET_MATCH_ITER = reporter.newMethod("getMatchIter()");
    /** Handle for profiling {@link Search#find()} */
    static protected final int FIND = reporter.newMethod("find()");

    /** Class implementing an instantiation of the search plan algorithm for a given graph. */
    static public class Search {
        /** Constructs a new record for a given graph and partial match. */
        public Search(SearchPlanStrategy strategy, Graph target, NodeEdgeMap preMatch) {
            this.plan = strategy.getPlan();
            this.injective = strategy.isInjective();
            this.target = target;
            this.index = 0;
            this.result = createElementMap(preMatch);
            this.itemRecords = new ArrayList<SearchItem.Record>();
        }
        
        /**
         * Computes the next search result.
         * @return <code>true</code> if there is a next result.
         */
        public boolean find() {
            reporter.start(FIND);
            if (found) {
                // we already found a solution
                // clone the previous result to avoid sharing problems
                result = createElementMap(result);
                index--;
            }
            while (index >= 0 && index < plan.size()) {
                index += getItemRecord().find() ? +1 : -1;
            }
            reporter.stop();
            boolean result = index >= 0;
            found |= result;
            return result;
        }

        /** 
         * Returns the currently active search item record,
         * i.e., belonging to the current value of <code>index</code>.
         */
        private SearchItem.Record getItemRecord() {
            SearchItem.Record result;
            if (index < itemRecords.size()) {
                // take it from the existing records
                result = itemRecords.get(index);
            } else {
                // make a new one
                result = plan.get(index).getRecord(this);
                itemRecords.add(result);
            }
            return result;
        }
        
        /** Returns the (partial) result of the search. */
        public VarNodeEdgeMap getResult() {
            return result;
        }
        
        /** Returns the target graph of the search. */
        public Graph getTarget() {
            return target;
        }

        /**
         * Callback factory method to create the node-edge map to store the
         * final result of the simulation.
         */
        protected VarNodeEdgeMap createElementMap(NodeEdgeMap basis) {
            if (basis == null) {
                basis = new VarNodeEdgeHashMap();
            }
            if (injective) {
                // returns a hash map that maintains the usedNodes
                // when nodes are added or removed.
                return new VarNodeEdgeHashMap(basis) {
                    @Override
                    protected Map<Node, Node> createNodeMap() {
                        return createUsedNodeSensitiveNodeMap();
                    }
                };
            } else {
                return new VarNodeEdgeHashMap(basis);
            }
        }
        
        /** 
         * Callback method to create the node map part of the singular map.
         * This implementation returns a map that maintains the {@link #usedNodes}
         * whenever an entry is inserted into or removed from the map.
         */
        protected Map<Node,Node> createUsedNodeSensitiveNodeMap() {
            return new HashMap<Node,Node>() {
                @Override
                public Node put(Node key, Node value) {
                    // also adds the node to the used nodes
                    getUsedNodes().add(value);
                    return super.put(key, value);
                }

                @Override
                public Node remove(Object key) {
                    Node result = super.remove(key);
                    // also remove the node from the used nodes
                    getUsedNodes().remove(result);
                    return result;
                }
            };
        }

        /** 
         * Indicates if a given node of the codomain is available for use
         * as an image of the match under construction.
         * @return <code>true</code> if the match is not injective, or 
         * the node has not yet been used as image.
         */
        public boolean isAvailable(Node node) {
            return !(injective && getUsedNodes().contains(node));
        }
        
        /** 
         * Returns the set of nodes already used as images.
         * This is needed for the injectivity check, if any.
         */
        private Set<Node> getUsedNodes() {
            if (usedNodes == null) {
                usedNodes = new HashSet<Node>();
            }
            return usedNodes;
        }

        /** The search plan for this record. */
        private final List<SearchItem> plan;
        /** Flag indicating that the match should be injective. */
        private final boolean injective;
        /** Flag indicating that a solution has already been found. */
        private boolean found;
        /** The index of the currently active search item. */
        private int index;
        /** The target graph of the search. */
        private final Graph target;
        /**
         * The element map built up during the search process.
         */
        private VarNodeEdgeMap result;
        /** 
         * The set of nodes already used as images, used for the injectivity test.
         */
        private Set<Node> usedNodes;
        /** Search stack. */
        private final List<SearchItem.Record> itemRecords;
    }
}