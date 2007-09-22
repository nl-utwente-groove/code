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
 * $Id: SearchPlanStrategy.java,v 1.6 2007-09-22 09:10:36 rensink Exp $
 */
package groove.match;

import groove.calc.Property;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
import groove.rel.VarSupport;
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
 * @version $Revision: 1.6 $
 */
public class SearchPlanStrategy implements MatchStrategy {
	/**
     * Constructs a strategy from a given list of search items.
     * A flag controls if solutions should be injective.
	 * @param plan the search items that make up the search plan
	 * @param injective flag to indicate that the matching should be injective
     */
    public SearchPlanStrategy(Graph source, List<SearchItem> plan, boolean injective) {
        this.nodeIxMap = new HashMap<Node,Integer>();
        int nodeCount = 0;
        for (Node node: source.nodeSet()) {
            nodeIxMap.put(node, nodeCount);
            nodeCount++;
        }
        sourceNodeCount = nodeCount;
        this.edgeIxMap = new HashMap<Edge,Integer>();
        int edgeCount = 0;
        for (Edge edge: source.edgeSet()) {
            edgeIxMap.put(edge, edgeCount);
            edgeCount++;
        }
        sourceEdgeCount = edgeCount;
        this.varIxMap = new HashMap<String,Integer>();
        int varCount = 0;
        for (String var: VarSupport.getAllVars(source)) {
            varIxMap.put(var, varCount);
            varCount++;
        }
        sourceVarCount = varCount;
        this.plan = plan;
        this.injective = injective;
    }

    public void setFilter(Property<VarNodeEdgeMap> filter) {
    	this.filter = filter;
    }
    
    /**
     * Returns the filter currently set for the matches found by this strategy.
     * @return a property which may be <code>null</code>, but if not,
     * is guaranteed to hold for all matches returned by any of the 
     * search methods.
     * @see #setFilter(Property)
     */
    protected Property<VarNodeEdgeMap> getFilter() {
    	return filter;
    }
    
    public VarNodeEdgeMap getMatch(Graph graph, NodeEdgeMap preMatch) {
        VarNodeEdgeMap result;
        reporter.start(GET_MATCH);
        Search search = createSearch(graph, preMatch);
        if (search.find()) {
            result = search.getMatch();
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
                        next = search.getMatch();
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
            result.add(searchRecord.getMatch());
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
        testFixed(true);
        return new Search(this, target, preMatch);
    }
//
//    /**
//     * Returns the mapping from source graph nodes to distinct indices.
//     * This is set at construction time.
//     */
//    protected Map<Node, Integer> getNodeIxMap() {
//        return nodeIxMap;
//    }
//
//    /**
//     * Returns the mapping from source graph edges to distinct indices.
//     * This is set at construction time.
//     */
//    protected Map<Edge, Integer> getEdgeIxMap() {
//        return edgeIxMap;
//    }
//
//    /**
//     * Returns the mapping from source graph edges to distinct indices.
//     * This is set at construction time.
//     */
//    protected Map<String, Integer> getVarIxMap() {
//        return varIxMap;
//    }
    
    /** 
     * Returns the index of a given node in the node index map.
     * Adds an index for the node to the map if it was not yet there.
     * @param node the node to be looked up
     * @return an index for <code>node</code>
     */
    int getNodeIx(Node node) {
        Integer result = nodeIxMap.get(node);
        if (result == null) {
            testFixed(false);
            nodeIxMap.put(node, result = nodeIxMap.size());
        }
        return result;
    }
    
    /** 
     * Returns the index of a given edge in the edge index map.
     * Adds an index for the edge to the map if it was not yet there.
     * @param edge the edge to be looked up
     * @return an index for <code>edge</code>
     */
    int getEdgeIx(Edge edge) {
        Integer result = edgeIxMap.get(edge);
        if (result == null) {
            testFixed(false);
            edgeIxMap.put(edge, result = edgeIxMap.size());
        }
        return result;
    }
    
    /** 
     * Returns the index of a given variable in the node index map.
     * Adds an index for the variable to the map if it was not yet there.
     * @param var the variable to be looked up
     * @return an index for <code>var</code>
     */
    int getVarIx(String var) {
        Integer result = varIxMap.get(var);
        if (result == null) {
            testFixed(false);
            varIxMap.put(var, result = varIxMap.size());
        }
        return result;
    }
//
//    /**
//     * Sets the expected numbers of pre-matched nodes and edges.
//     * If these counts are never set, no pre-matches are expected.
//     */
//    public final void setPreMatchCounts(int nodePreMatchCount, int edgePreMatchCount) {
//        testFixed(false);
//        this.nodePreMatchCount = nodePreMatchCount;
//        this.edgePreMatchCount = edgePreMatchCount;
//    }
//
//    /**
//     * Returns the expected number of pre-matched nodes.
//     */
//    public final int getNodePreMatchCount() {
//        return nodePreMatchCount;
//    }
//
//    /**
//     * Returns the expected number of pre-matched edges.
//     */
//    public final int getEdgePreMatchCount() {
//        return edgePreMatchCount;
//    }

    /**
     * Indicates that the strategy is now fixed, meaning that it has been completely constructed.
     */
    public void setFixed() {
        if (!fixed) {
            for (SearchItem item : plan) {
                item.activate(this);
            }
            // now create the inverse of the index maps
            nodeKeys = new Node[nodeIxMap.size()];
            for (Map.Entry<Node,Integer> nodeIxEntry: nodeIxMap.entrySet()) {
                nodeKeys[nodeIxEntry.getValue()] = nodeIxEntry.getKey();
            }
            edgeKeys = new Edge[edgeIxMap.size()];
            for (Map.Entry<Edge,Integer> edgeIxEntry: edgeIxMap.entrySet()) {
                edgeKeys[edgeIxEntry.getValue()] = edgeIxEntry.getKey();
            }
            varKeys = new String[varIxMap.size()];
            for (Map.Entry<String,Integer> varIxEntry: varIxMap.entrySet()) {
                varKeys[varIxEntry.getValue()] = varIxEntry.getKey();
            }
            this.fixed = true;
        }
    }

    /** 
     * Method that tests the fixedness of the search plan
     * and throws an exception if it is not as expected.
     * @param fixed indication whether or not the plan is expected to be currently fixed
     */
    private void testFixed(boolean fixed) {
        if (this.fixed != fixed) {
            throw new IllegalStateException(String.format("Search plan is %s fixed", fixed ? "not yet" : ""));
        }
    }
    
    /**
	 * A list of domain elements, in the order in which they are to be matched.
	 */
	private final List<SearchItem> plan;
    /** Flag indicating that the matching should be injective. */
	private final boolean injective;
	/** 
	 * Additional property that has to be satisfied by all matches returned
	 * by the matcher.
	 */
	private Property<VarNodeEdgeMap> filter;
    /** 
     * Map from source graph nodes to (distinct) indices.
     */
    private final Map<Node,Integer> nodeIxMap;
    /** 
     * Map from source graph edges to (distinct) indices.
     */
    private final Map<Edge,Integer> edgeIxMap;
    /** 
     * Map from source graph variables to (distinct) indices.
     */
    private final Map<String,Integer> varIxMap;
    /** Array of source graph nodes, which is the inverse of {@link #nodeIxMap} .*/
    private Node[] nodeKeys;
    /** Array of source graph edges, which is the inverse of {@link #edgeIxMap} .*/
    private Edge[] edgeKeys;
    /** Array of source graph variables, which is the inverse of {@link #varIxMap} .*/
    private String[] varKeys;
    /** The number of nodes in the source graph. */
    private final int sourceNodeCount;
    /** The number of edge in the source graph. */
    private final int sourceEdgeCount;
    /** The number of variables in the source graph. */
    private final int sourceVarCount;
//	/** 
//	 * Number of pre-matched nodes.
//	 * All nodes with an index (in {@link #nodeIndexMap}) lower than this count should be pre-matched.
//	 */
//	private int nodePreMatchCount;
//    /** 
//     * Number of pre-matched edges.
//     * All edges with an index (in {@link #edgeIndexMap}) lower than this count should be pre-matched.
//     */
//	private int edgePreMatchCount;
	/** 
	 * Flag to indicate that the construction of the object has finished,
	 * so that it can now be used for searching.
	 */
	private boolean fixed;
    
    /** Reporter instance to profile matcher methods. */
    static public final Reporter reporter = Reporter.register(SearchPlanStrategy.class);
    /** Handle for profiling {@link #getMatch(Graph, NodeEdgeMap)} */
    static final int GET_MATCH = reporter.newMethod("getMatch()");
    /** Handle for profiling {@link #getMatchSet(Graph, NodeEdgeMap)} */
    static final int GET_MATCH_SET = reporter.newMethod("getMatchSet()");
    /** Handle for profiling {@link #getMatchIter(Graph, NodeEdgeMap)} */
    static final int GET_MATCH_ITER = reporter.newMethod("getMatchIter()");
    /** Handle for profiling {@link Search#find()} */
    static public final int SEARCH_FIND = reporter.newMethod("Search.find()");
    /** Handle for profiling {@link SearchItem.Record#find()} */
    static final int RECORD_FIND = reporter.newMethod("Record.find()");

    /** Class implementing an instantiation of the search plan algorithm for a given graph. */
    public class Search {
        /** Constructs a new record for a given graph and partial match. */
        public Search(SearchPlanStrategy strategy, Graph target, NodeEdgeMap preMatch) {
            this.plan = strategy.getPlan();
            this.filter = strategy.getFilter();
            this.injective = strategy.isInjective();
            this.target = target;
            this.current = 0;
            this.result = createElementMap(preMatch);
            this.records = new ArrayList<SearchItem.Record>(plan.size());
            this.lastSingular = -1;
            this.nodeKeys = strategy.nodeKeys;
            this.edgeKeys = strategy.edgeKeys;
            this.varKeys = strategy.varKeys;
            this.nodeImages = new Node[strategy.nodeKeys.length];
            this.edgeImages = new Edge[strategy.edgeKeys.length];
            this.varImages = new Label[strategy.varKeys.length];
            for (Map.Entry<Node,Node> nodeEntry: preMatch.nodeMap().entrySet()) {
                int i = strategy.getNodeIx(nodeEntry.getKey());
                nodeImages[i] = nodeEntry.getValue();
            }
            for (Map.Entry<Edge,Edge> edgeEntry: preMatch.edgeMap().entrySet()) {
                int i = strategy.getEdgeIx(edgeEntry.getKey());
                edgeImages[i] = edgeEntry.getValue();
            }
            if (preMatch instanceof VarNodeEdgeMap) {
            for (Map.Entry<String,Label> varEntry: ((VarNodeEdgeMap) preMatch).getValuation().entrySet()) {
                int i = strategy.getVarIx(varEntry.getKey());
                varImages[i] = varEntry.getValue();
            }
            }
        }
        
        @Override
        public String toString() {
            return records.toString();
        }

        /**
         * Computes the next search result.
         * If the method returns <code>true</code>, the result can be obtained by
         * {@link #getMatch()}.
         * @return <code>true</code> if there is a next result.
         */
        public boolean find() {
            reporter.start(SEARCH_FIND);
            do {
            if (found) {
                // we already found a solution
                // clone the previous result to avoid sharing problems
//                result = createElementMap(result);
                current--;
            }
            while (current > lastSingular && current < plan.size()) {
                current += getCurrentRecord().find() ? +1 : -1;
            }
            reporter.stop();
            found = current > lastSingular;
            } while (found && !satisfiesFilter());
            return found;
        }

        /** 
         * Returns the currently active search item record,
         * i.e., belonging to the current value of <code>index</code>.
         */
        private SearchItem.Record getCurrentRecord() {
            SearchItem.Record result;
            if (current < records.size()) {
                // take it from the existing records
                result = records.get(current);
            } else {
                // make a new one
                result = plan.get(current).getRecord(this);
                records.add(result);
                if (lastSingular == current-1 && result.isSingular()) {
                    lastSingular++;
                }
            }
            return result;
        }

        /** Tests if the current search result satisfies the additional filter (if any). */
        private boolean satisfiesFilter() {
        	return filter == null || filter.isSatisfied(result);
        }
        
        /** Sets the node image for the source node with a given index. */
        Node putNode(int index, Node image) {
            Node result = nodeImages[index];
            nodeImages[index] = image;
            return result;
        }
        
        /** Sets the edge image for the source edge with a given index. */
        Edge putEdge(int index, Edge image) {
            Edge result = edgeImages[index];
            edgeImages[index] = image;
            return result;
        }
        
        /** Sets the variable image for the source graph variable with a given index. */
        Label putVar(int index, Label image) {
            Label result = varImages[index];
            varImages[index] = image;
            return result;
        }
        
        /** Returns the current node image at a given index. */
        Node getNode(int index) {
            return nodeImages[index];
        }
        
        /** Returns the current edge image at a given index. */
        Edge getEdge(int index) {
            return edgeImages[index];
        }
        
        /** Returns the current variable image at a given index. */
        Label getVar(int index) {
            return varImages[index];
        }
        
        /** 
         * Returns a copy of the search result, or <code>null</code> if 
         * the last invocation of {@link #find()} was not successful. 
         */
        public VarNodeEdgeMap getMatch() {
            if (found) {
                VarNodeEdgeMap result = new VarNodeEdgeHashMap();
                for (int i = 0; i < sourceNodeCount; i++) {
                    Node image = nodeImages[i];
                    if (image != null) {
                        result.putNode(nodeKeys[i], image);
                    }
                }
                for (int i = 0; i < sourceEdgeCount; i++) {
                    Edge image = edgeImages[i];
                    if (image != null) {
                        result.putEdge(edgeKeys[i], image);
                    }
                }
                for (int i = 0; i < sourceVarCount; i++) {
                    Label image = varImages[i];
                    if (image != null) {
                        result.putVar(varKeys[i], image);
                    }
                }
                return result;
            } else {
                return null;
            }
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
        /** Array of node images. */
        private final Node[] nodeKeys;
        /** Array of edge images. */
        private final Edge[] edgeKeys;
        /** Array of variable images. */
        private final String[] varKeys;
        /** Array of node images. */
        private final Node[] nodeImages;
        /** Array of edge images. */
        private final Edge[] edgeImages;
        /** Array of variable images. */
        private final Label[] varImages;
        /** Property to be satisfied by all search results. May be <code>null</code>. */
        private final Property<VarNodeEdgeMap> filter;
        /** Flag indicating that the match should be injective. */
        private final boolean injective;
        /** Flag indicating that a solution has already been found. */
        private boolean found;
        /** The index of the currently active search item. */
        private int current;
        /** Index of the last search record known to be singular. */
        private int lastSingular;
        /** The target graph of the search. */
        private final Graph target;
//        /** The initial pre-match map. */
//        private NodeEdgeMap preMatch;
        /**
         * The element map built up during the search process.
         */
        private VarNodeEdgeMap result;
        /** 
         * The set of nodes already used as images, used for the injectivity test.
         */
        private Set<Node> usedNodes;
        /** Search stack. */
        private final List<SearchItem.Record> records;
    }
}