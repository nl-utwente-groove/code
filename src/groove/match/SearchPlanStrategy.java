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
 * $Id: SearchPlanStrategy.java,v 1.18 2007-11-29 12:49:37 rensink Exp $
 */
package groove.match;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.algebra.ValueNode;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
import groove.util.Reporter;

import java.util.Arrays;
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
 * @version $Revision$
 */
public class SearchPlanStrategy extends AbstractMatchStrategy<VarNodeEdgeMap> {
	/**
     * Constructs a strategy from a given list of search items.
     * A flag controls if solutions should be injective.
	 * @param plan the search items that make up the search plan
	 * @param injective flag to indicate that the matching should be injective
     */
    public SearchPlanStrategy(GraphShape source, List<SearchItem> plan, boolean injective) {
        this.nodeIxMap = new HashMap<Node,Integer>();
        this.edgeIxMap = new HashMap<Edge,Integer>();
        this.varIxMap = new HashMap<String,Integer>();
        this.plan = plan;
        this.injective = injective;
    }

    public Iterator<VarNodeEdgeMap> getMatchIter(GraphShape host, NodeEdgeMap anchorMap) {
        Iterator<VarNodeEdgeMap> result;
        reporter.start(GET_MATCH_ITER);
        final Search search = createSearch(host, anchorMap);
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
        return plan.toString() + (isInjective() ? " (injective)" : " (non-injective)");
    }
    
    /**
     * Callback factory method for an auxiliary {@link Search} object.
     */
    protected Search createSearch(GraphShape host, NodeEdgeMap anchorMap) {
        testFixed(true);
        return new Search(host, anchorMap);
    }
    
    /** 
     * Indicates if a given node is already matched in this plan.
     * This returns <code>true</code> if the node already has a result index.
     * Callback method from search items, during activation.
     */
    boolean isNodeFound(Node node) {
    	return nodeIxMap.get(node) != null;
    }
    
    /** 
     * Indicates if a given edge is already matched in this plan.
     * This returns <code>true</code> if the edge already has a result index.
     * Callback method from search items, during activation.
     */
    boolean isEdgeFound(Edge edge) {
    	return edgeIxMap.get(edge) != null;
    }
    
    /** 
     * Indicates if a given variable is already matched in this plan.
     * This returns <code>true</code> if the variable already has a result index.
     * Callback method from search items, during activation.
     */
    boolean isVarFound(String var) {
    	return varIxMap.get(var) != null;
    }

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
        Integer value = edgeIxMap.get(edge);
        if (value == null) {
            testFixed(false);
            edgeIxMap.put(edge, value = edgeIxMap.size());
        }
        return value;
    }
    
    /** 
     * Returns the index of a given variable in the node index map.
     * Adds an index for the variable to the map if it was not yet there.
     * @param var the variable to be looked up
     * @return an index for <code>var</code>
     */
    int getVarIx(String var) {
        Integer value = varIxMap.get(var);
        if (value == null) {
            testFixed(false);
            varIxMap.put(var, value = varIxMap.size());
        }
    	return value;
    }

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
	final List<SearchItem> plan;
    /** Flag indicating that the matching should be injective. */
	final boolean injective;
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
    Node[] nodeKeys;
    /** Array of source graph edges, which is the inverse of {@link #edgeIxMap} .*/
    Edge[] edgeKeys;
    /** Array of source graph variables, which is the inverse of {@link #varIxMap} .*/
    String[] varKeys;
	/** 
	 * Flag to indicate that the construction of the object has finished,
	 * so that it can now be used for searching.
	 */
	private boolean fixed;

	/** Reporter instance to profile matcher methods. */
    static public final Reporter reporter = Reporter.register(SearchPlanStrategy.class);
    /** Handle for profiling {@link #getMatchSet(Graph, NodeEdgeMap)} */
    static final int GET_MATCH_SET = reporter.newMethod("getMatchSet()");
    /** Handle for profiling {@link #getMatchIter(GraphShape, NodeEdgeMap)} */
    static final int GET_MATCH_ITER = reporter.newMethod("getMatchIter()");
    /** Handle for profiling {@link Search#find()} */
    static public final int SEARCH_FIND = reporter.newMethod("Search.find()");
    /** Handle for profiling {@link AbstractSearchItem.SingularRecord#find()} */
    static final int RECORD_FIND_SINGULAR = reporter.newMethod("Record.find()");
    /** Handle for profiling {@link AbstractSearchItem.MultipleRecord#find()} */
    static final int RECORD_FIND_MULTIPLE = reporter.newMethod("Record.find()");

  
    /** Class implementing an instantiation of the search plan algorithm for a given graph. */
    public class Search {
        /** Constructs a new record for a given graph and partial match. */
        public Search(GraphShape host, NodeEdgeMap anchorMap) {
            this.host = host;
            this.records = new SearchItem.Record[plan.size()];
            this.lastSingular = -1;
            this.nodeImages = new Node[nodeKeys.length];
            this.edgeImages = new Edge[edgeKeys.length];
            this.varImages = new Label[varKeys.length];
            this.nodeAnchors = new Node[nodeKeys.length];
            this.edgeAnchors = new Edge[edgeKeys.length];
            this.varAnchors = new Label[varKeys.length];
            this.noMatches = false;
            if (anchorMap != null) {
				for (Map.Entry<Node, Node> nodeEntry : anchorMap.nodeMap().entrySet()) {
					assert isNodeFound(nodeEntry.getKey());
					int i = getNodeIx(nodeEntry.getKey());
					nodeImages[i] = nodeAnchors[i] = nodeEntry.getValue();
					if (injective) {
						getUsedNodes().add(nodeEntry.getValue());
					}
				}
				// In case of non injectivity of the anchorMap and injective strategy, ensure that no matches are found
				// this cannot be maintained, since the anchorMap may include attribute nodes
//				if (injective && getUsedNodes().size() < anchorMap.nodeMap().size()) {
//					noMatches = true;
//				}
				for (Map.Entry<Edge, Edge> edgeEntry : anchorMap.edgeMap().entrySet()) {
					assert isEdgeFound(edgeEntry.getKey());
					int i = getEdgeIx(edgeEntry.getKey());
					edgeImages[i] = edgeAnchors[i] = edgeEntry.getValue();
				}
				if (anchorMap instanceof VarNodeEdgeMap) {
					for (Map.Entry<String, Label> varEntry : ((VarNodeEdgeMap) anchorMap).getValuation().entrySet()) {
						assert isVarFound(varEntry.getKey());
						int i = getVarIx(varEntry.getKey());
						varImages[i] = varAnchors[i] = varEntry.getValue();
					}
				}
			}
        }
        
        @Override
        public String toString() {
            return Arrays.toString(records);
        }

        /**
         * Computes the next search result.
         * If the method returns <code>true</code>, the result can be obtained by
         * {@link #getMatch()}.
         * @return <code>true</code> if there is a next result.
         */
        public boolean find() {
            reporter.start(SEARCH_FIND);
            if (noMatches) { return false; }
            final int planSize = plan.size();
            final boolean filtered = getFilter() != null;
            boolean found = this.found;
            boolean exhausted;
            int current = found ? planSize-1 : 0;
            do {
            	// the outer loop is to filter solutions through
				while (current > lastSingular && current < planSize) {
					current += getRecord(current).find() ? +1 : -1;
				}
				// now check if a found solution passes the filter (if there is one)
				found = current == planSize;
				exhausted = !found;
				if (found && filtered && !satisfiesFilter()) {
					// we have to go on searching
					current--;
					found = false;
					match = null;
				}
			} while (!found && !exhausted);
            this.found = found;
			reporter.stop();
            return found;
        }

        /**
		 * Returns the currently active search item record.
         * @param current the index of the requested record
		 */
        private SearchItem.Record getRecord(int current) {
            SearchItem.Record result = records[current];
            if (result == null) {
                // make a new one
                result = plan.get(current).getRecord(this);
                records[current] = result;
                if (lastSingular == current-1 && result.isSingular()) {
                    lastSingular++;
                }
            }
            return result;
        }

        /** Tests if the current search result satisfies the additional filter (if any). */
        private boolean satisfiesFilter() {
        	return getFilter() == null || getFilter().isSatisfied(getMatch());
        }
        
        /** Sets the node image for the node key with a given index. */
        final boolean putNode(int index, Node image) {
        	assert nodeAnchors[index] == null : String.format("Assignment %s=%s replaces pre-matched image %s", nodeKeys[index], image, nodeAnchors[index]);
        	// value nodes only matched by value nodes
        	boolean isValueNode = image instanceof ValueNode;
        	if (nodeKeys[index] instanceof ValueNode != isValueNode) {
        		return false;
        	}
        	if (injective && !isValueNode) {
        		Node oldImage = nodeImages[index];
        		if (oldImage != null) {
        			usedNodes.remove(oldImage);
        		}
        		Set<Node> usedNodes = getUsedNodes();
				if (image != null && !usedNodes.add(image)) { 
					return false;
				}
        	}
            nodeImages[index] = image;
            return true;
        }
        
        /** Sets the edge image for the edge key with a given index. */
        final boolean putEdge(int index, Edge image) {
            edgeImages[index] = image;
            return true;
        }
        
        /** Sets the variable image for the graph variable with a given index. */
        final boolean putVar(int index, Label image) {
            varImages[index] = image;
            return true;
        }

        /** Returns the current node image at a given index. */
        final Node getNode(int index) {
            return nodeImages[index];
        }
        
        /** Returns the current edge image at a given index. */
        final Edge getEdge(int index) {
            return edgeImages[index];
        }
        
        /** Returns the current variable image at a given index. */
        final Label getVar(int index) {
            return varImages[index];
        }

        /** Indicates if the node at a given index was pre-matched in this search. */
        final Node getNodeAnchor(int index) {
            return nodeAnchors[index];
        }
        
        /** Indicates if the edge at a given index was pre-matched in this search. */
        final Edge getEdgeAnchor(int index) {
            return edgeAnchors[index];
        }
        
        /** Indicates if the variable at a given index was pre-matched in this search. */
        final Label getVarAnchor(int index) {
            return varAnchors[index];
        }
        
        /** 
         * Returns a copy of the search result, or <code>null</code> if 
         * the last invocation of {@link #find()} was not successful. 
         */
        public VarNodeEdgeMap getMatch() {
            if (found && match == null) {
                VarNodeEdgeMap result = new VarNodeEdgeHashMap();
                for (int i = 0; i < nodeImages.length; i++) {
                    Node image = nodeImages[i];
                    if (image != null) {
                        result.putNode(nodeKeys[i], image);
                    }
                }
                for (int i = 0; i < edgeImages.length; i++) {
                    Edge image = edgeImages[i];
                    if (image != null) {
                        result.putEdge(edgeKeys[i], image);
                    }
                }
                for (int i = 0; i < varImages.length; i++) {
                    Label image = varImages[i];
                    if (image != null) {
                        result.putVar(varKeys[i], image);
                    }
                }
                return result;
            } else {
                return match;
            }
        }
        
        /** Returns the target graph of the search. */
        public GraphShape getHost() {
            return host;
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

        /** Array of node images. */
        private final Node[] nodeImages;
        /** Array of edge images. */
        private final Edge[] edgeImages;
        /** Array of variable images. */
        private final Label[] varImages;
        /** Array indicating, for each index, if the node with that image was pre-matched in the search. */
        private final Node[] nodeAnchors;
        /** Array indicating, for each index, if the variable with that image was pre-matched in the search. */
        private final Edge[] edgeAnchors;
        /** Array indicating, for each index, if the edge with that image was pre-matched in the search. */
        private final Label[] varAnchors;
        /** Flag indicating that a solution has already been found. */
        private boolean found;
        /** Index of the last search record known to be singular. */
        private int lastSingular;
        /** The host graph of the search. */
        private final GraphShape host;
        /** 
         * The set of non-value nodes already used as images, used for the injectivity test.
         */
        private Set<Node> usedNodes;
        /** */
        private boolean noMatches;
        /** Search stack. */
        private final SearchItem.Record[] records;
        /** 
         * The match found at the last invocation of {@link #find()},
         * if it has been computed (in {@link #getMatch()}). 
         */
        private VarNodeEdgeMap match;
    }

}