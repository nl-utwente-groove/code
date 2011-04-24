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
 * $Id: SearchPlanStrategy.java,v 1.18 2007-11-29 12:49:37 rensink Exp $
 */
package groove.match;

import groove.graph.TypeLabel;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.rel.LabelVar;
import groove.trans.ForallCondition;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;
import groove.util.Reporter;
import groove.util.Visitor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This matcher walks through a search tree built up according to a search plan,
 * in which the matching order of the domain elements is determined.
 * @author Arend Rensink
 * @version $Revision$
 */
public class SearchPlanStrategy extends MatchStrategy<TreeMatch> {
    /**
     * Constructs a strategy from a given list of search items. A flag controls
     * if solutions should be injective.
     * @param plan the search items that make up the search plan
     */
    public SearchPlanStrategy(SearchPlan plan) {
        this.nodeIxMap = new HashMap<RuleNode,Integer>();
        this.edgeIxMap = new HashMap<RuleEdge,Integer>();
        this.varIxMap = new HashMap<LabelVar,Integer>();
        this.plan = plan;
        this.injective = plan.isInjective();
        this.forallCount = plan.getForallCount();
    }

    @Override
    public <T> T traverse(HostGraph host, RuleToHostMap seedMap,
            Visitor<TreeMatch,T> visitor) {
        Search search = getSearch(host, seedMap);
        while (search.find() && visitor.visit(search.getMatch())) {
            // do nothing
        }
        return visitor.getResult();
    }

    @Override
    @Deprecated
    public Iterator<TreeMatch> getMatchIter(HostGraph host,
            RuleToHostMap seedMap) {
        Iterator<TreeMatch> result;
        getMatchIterReporter.start();
        final Search search = createSearch();
        search.initialise(host, seedMap);
        result = new Iterator<TreeMatch>() {
            public boolean hasNext() {
                // test if there is an unreturned next or if we are done
                if (this.next == null && !this.atEnd) {
                    // search for the next solution
                    if (search.find()) {
                        this.next = search.getMatch();
                    } else {
                        // there is none and will be none; give up
                        this.atEnd = true;
                    }
                }
                return !this.atEnd;
            }

            public TreeMatch next() {
                if (hasNext()) {
                    TreeMatch result = this.next;
                    this.next = null;
                    return result;
                } else {
                    throw new NoSuchElementException();
                }
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            /** The next refinement to be returned. */
            private TreeMatch next;
            /**
             * Flag to indicate that the last refinement has been returned, so
             * {@link #next()} henceforth will return <code>false</code>.
             */
            private boolean atEnd = false;
        };
        getMatchIterReporter.stop();
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
    final protected List<? extends SearchItem> getPlan() {
        return this.plan;
    }

    @Override
    public String toString() {
        return this.plan.toString()
            + (isInjective() ? " (injective)" : " (non-injective)");
    }

    /**
     * Callback factory method for an auxiliary {@link Search} object.
     */
    private Search getSearch(HostGraph host, RuleToHostMap seedMap) {
        //        if (this.search == null) {
        this.search = createSearch();
        //        }
        this.search.initialise(host, seedMap);
        return this.search;
    }

    /**
     * Callback factory method for an auxiliary {@link Search} object.
     */
    private Search createSearch() {
        testFixed(true);
        return new Search();
    }

    /**
     * Indicates if a given node is already matched in this plan. This returns
     * <code>true</code> if the node already has a result index. Callback method
     * from search items, during activation.
     */
    boolean isNodeFound(RuleNode node) {
        return this.nodeIxMap.get(node) != null;
    }

    /**
     * Indicates if a given edge is already matched in this plan. This returns
     * <code>true</code> if the edge already has a result index. Callback method
     * from search items, during activation.
     */
    boolean isEdgeFound(RuleEdge edge) {
        return this.edgeIxMap.get(edge) != null;
    }

    /**
     * Indicates if a given variable is already matched in this plan. This
     * returns <code>true</code> if the variable already has a result index.
     * Callback method from search items, during activation.
     */
    boolean isVarFound(LabelVar var) {
        return this.varIxMap.get(var) != null;
    }

    /**
     * Returns the index of a given node in the node index map. Adds an index
     * for the node to the map if it was not yet there.
     * @param node the node to be looked up
     * @return an index for <code>node</code>
     */
    int getNodeIx(RuleNode node) {
        Integer result = this.nodeIxMap.get(node);
        if (result == null) {
            testFixed(false);
            this.nodeIxMap.put(node, result = this.nodeIxMap.size());
        }
        return result;
    }

    /**
     * Returns the index of a given edge in the edge index map. Adds an index
     * for the edge to the map if it was not yet there.
     * @param edge the edge to be looked up
     * @return an index for <code>edge</code>
     */
    int getEdgeIx(RuleEdge edge) {
        Integer value = this.edgeIxMap.get(edge);
        if (value == null) {
            testFixed(false);
            this.edgeIxMap.put(edge, value = this.edgeIxMap.size());
        }
        return value;
    }

    /**
     * Returns the index of a given variable in the node index map. Adds an
     * index for the variable to the map if it was not yet there.
     * @param var the variable to be looked up
     * @return an index for <code>var</code>
     */
    int getVarIx(LabelVar var) {
        Integer value = this.varIxMap.get(var);
        if (value == null) {
            testFixed(false);
            this.varIxMap.put(var, value = this.varIxMap.size());
        }
        return value;
    }

    /**
     * Indicates that the strategy is now fixed, meaning that it has been
     * completely constructed.
     */
    public void setFixed() {
        if (!this.fixed) {
            for (SearchItem item : this.plan) {
                item.activate(this);
            }
            // now create the inverse of the index maps
            this.nodeKeys = new RuleNode[this.nodeIxMap.size()];
            for (Map.Entry<RuleNode,Integer> nodeIxEntry : this.nodeIxMap.entrySet()) {
                this.nodeKeys[nodeIxEntry.getValue()] = nodeIxEntry.getKey();
            }
            this.edgeKeys = new RuleEdge[this.edgeIxMap.size()];
            for (Map.Entry<RuleEdge,Integer> edgeIxEntry : this.edgeIxMap.entrySet()) {
                this.edgeKeys[edgeIxEntry.getValue()] = edgeIxEntry.getKey();
            }
            this.varKeys = new LabelVar[this.varIxMap.size()];
            for (Map.Entry<LabelVar,Integer> varIxEntry : this.varIxMap.entrySet()) {
                this.varKeys[varIxEntry.getValue()] = varIxEntry.getKey();
            }
            this.fixed = true;
        }
    }

    /**
     * Method that tests the fixedness of the search plan and throws an
     * exception if it is not as expected.
     * @param fixed indication whether or not the plan is expected to be
     *        currently fixed
     */
    private void testFixed(boolean fixed) {
        if (this.fixed != fixed) {
            throw new IllegalStateException(String.format(
                "Search plan is %s fixed", fixed ? "not yet" : ""));
        }
    }

    /** The fixed search object. */
    private Search search;
    /**
     * A list of domain elements, in the order in which they are to be matched.
     */
    final SearchPlan plan;
    /** Flag indicating that the matching should be injective. */
    final boolean injective;
    /** Number of {@link ForallCondition}s in the search plan. */
    final int forallCount;
    /**
     * Map from source graph nodes to (distinct) indices.
     */
    private final Map<RuleNode,Integer> nodeIxMap;
    /**
     * Map from source graph edges to (distinct) indices.
     */
    private final Map<RuleEdge,Integer> edgeIxMap;
    /**
     * Map from source graph variables to (distinct) indices.
     */
    private final Map<LabelVar,Integer> varIxMap;
    /**
     * Array of source graph nodes, which is the inverse of {@link #nodeIxMap} .
     */
    RuleNode[] nodeKeys;
    /**
     * Array of source graph edges, which is the inverse of {@link #edgeIxMap} .
     */
    RuleEdge[] edgeKeys;
    /**
     * Array of source graph variables, which is the inverse of
     * {@link #varIxMap} .
     */
    LabelVar[] varKeys;
    /**
     * Flag to indicate that the construction of the object has finished, so
     * that it can now be used for searching.
     */
    private boolean fixed;

    /** Reporter instance to profile matcher methods. */
    static private final Reporter reporter =
        Reporter.register(SearchPlanStrategy.class);
    /** Handle for profiling {@link #getMatchIter(HostGraph, RuleToHostMap)} */
    static final Reporter getMatchIterReporter =
        reporter.register("getMatchIter()");
    /** Handle for profiling {@link Search#find()} */
    static public final Reporter searchFindReporter =
        reporter.register("Search.find()");

    /**
     * Class implementing an instantiation of the search plan algorithm for a
     * given graph.
     */
    public class Search {
        /** Constructs a new record for a given graph and partial match. */
        @SuppressWarnings("unchecked")
        public Search() {
            int planSize = SearchPlanStrategy.this.plan.size();
            this.records = new SearchItem.Record[planSize];
            this.influence = new SearchItem.Record[planSize][];
            this.influenceCount = new int[planSize];
            this.nodeImages =
                new HostNode[SearchPlanStrategy.this.nodeKeys.length];
            this.edgeImages =
                new HostEdge[SearchPlanStrategy.this.edgeKeys.length];
            this.varImages =
                new TypeLabel[SearchPlanStrategy.this.varKeys.length];
            this.nodeSeeds =
                new HostNode[SearchPlanStrategy.this.nodeKeys.length];
            this.edgeSeeds =
                new HostEdge[SearchPlanStrategy.this.edgeKeys.length];
            this.varSeeds =
                new TypeLabel[SearchPlanStrategy.this.varKeys.length];
            this.subMatches =
                new Collection[SearchPlanStrategy.this.forallCount];
        }

        /** Initialises the search for a given host graph and anchor map. */
        public void initialise(HostGraph host, RuleToHostMap seedMap) {
            this.host = host;
            if (isInjective()) {
                getUsedNodes().clear();
            }
            if (seedMap != null) {
                for (Map.Entry<RuleNode,? extends HostNode> nodeEntry : seedMap.nodeMap().entrySet()) {
                    assert isNodeFound(nodeEntry.getKey());
                    int i = getNodeIx(nodeEntry.getKey());
                    this.nodeImages[i] =
                        this.nodeSeeds[i] = nodeEntry.getValue();
                    if (isInjective()) {
                        getUsedNodes().add(nodeEntry.getValue());
                    }
                }
                for (Map.Entry<RuleEdge,? extends HostEdge> edgeEntry : seedMap.edgeMap().entrySet()) {
                    assert isEdgeFound(edgeEntry.getKey());
                    int i = getEdgeIx(edgeEntry.getKey());
                    this.edgeImages[i] =
                        this.edgeSeeds[i] = edgeEntry.getValue();
                }
                for (Map.Entry<LabelVar,TypeLabel> varEntry : seedMap.getValuation().entrySet()) {
                    assert isVarFound(varEntry.getKey());
                    int i = getVarIx(varEntry.getKey());
                    this.varImages[i] = this.varSeeds[i] = varEntry.getValue();
                }
            }
            for (int i = 0; i < this.records.length && this.records[i] != null; i++) {
                this.records[i].initialise(host);
            }
            this.found = false;
            this.lastSingular = -1;
        }

        @Override
        public String toString() {
            return Arrays.toString(this.records);
        }

        /**
         * Computes the next search result. If the method returns
         * <code>true</code>, the result can be obtained by {@link #getMatch()}.
         * @return <code>true</code> if there is a next result.
         */
        public boolean find() {
            searchFindReporter.start();
            final int planSize = SearchPlanStrategy.this.plan.size();
            boolean found = this.found;
            // if an image was found before, roll back the result
            // until the last relevant search item
            int current;
            if (found) {
                current = planSize - 1;
                SearchItem.Record currentRecord;
                while (current >= 0
                    && !(currentRecord = getRecord(current)).isRelevant()) {
                    currentRecord.repeat();
                    current--;
                }
            } else {
                current = 0;
            }
            while (current > this.lastSingular && current < planSize) {
                boolean success = getRecord(current).next();
                if (success) {
                    for (int i = 0; i < this.influenceCount[current]; i++) {
                        this.influence[current][i].reset();
                    }
                    current++;
                } else if (getRecord(current).isEmpty()) {
                    // go back to the last dependency to have any hope
                    // of finding a match
                    int dependency =
                        SearchPlanStrategy.this.plan.getDependency(current);
                    for (current--; current > dependency; current--) {
                        getRecord(current).repeat();
                    }
                } else {
                    current--;
                }
            }
            found = current == planSize;
            this.found = found;
            searchFindReporter.stop();
            return found;
        }

        /**
         * Returns the currently active search item record.
         * @param current the index of the requested record
         */
        private SearchItem.Record getRecord(int current) {
            SearchItem.Record result = this.records[current];
            if (result == null) {
                SearchItem item =
                    SearchPlanStrategy.this.plan.get(current);
                // make a new record
                result = item.createRecord(this);
                result.initialise(this.host);
                this.records[current] = result;
                this.influence[current] =
                    new SearchItem.Record[this.influence.length - current];
                int dependency =
                    SearchPlanStrategy.this.plan.getDependency(current);
                assert dependency < current;
                if (dependency >= 0) {
                    this.influence[dependency][this.influenceCount[dependency]] =
                        result;
                    this.influenceCount[dependency]++;
                }
                if (this.lastSingular == current - 1 && result.isSingular()) {
                    this.lastSingular++;
                }
            }
            return result;
        }

        /** Sets the node image for the node key with a given index. */
        final boolean putNode(int index, HostNode image) {
            RuleNode nodeKey = SearchPlanStrategy.this.nodeKeys[index];
            assert image == null || this.nodeSeeds[index] == null : String.format(
                "Assignment %s=%s replaces pre-matched image %s", nodeKey,
                image, this.nodeSeeds[index]);
            boolean keyIsVariableNode = nodeKey instanceof VariableNode;
            if (image instanceof ValueNode) {
                // value nodes only matched by value nodes without signature or of the
                // same signature
                if (!keyIsVariableNode) {
                    return false;
                } else {
                    String keySignature =
                        ((VariableNode) nodeKey).getSignature();
                    if (keySignature != null
                        && !((ValueNode) image).getSignature().equals(
                            keySignature)) {
                        return false;
                    }
                }
            } else if (keyIsVariableNode) {
                return false;
            } else if (isInjective()) {
                HostNode oldImage = this.nodeImages[index];
                if (oldImage != null) {
                    boolean removed = getUsedNodes().remove(oldImage);
                    assert removed : String.format(
                        "Node image %s not in used nodes %s", oldImage,
                        getUsedNodes());
                }
                if (image != null && !getUsedNodes().add(image)) {
                    this.nodeImages[index] = null;
                    return false;
                }
            }
            this.nodeImages[index] = image;
            return true;
        }

        /** Sets the edge image for the edge key with a given index. */
        final boolean putEdge(int index, HostEdge image) {
            this.edgeImages[index] = image;
            return true;
        }

        /** Sets the variable image for the graph variable with a given index. */
        final boolean putVar(int index, TypeLabel image) {
            this.varImages[index] = image;
            return true;
        }

        /** Sets the composite match for a given index. */
        final boolean putSubMatch(int index, Collection<TreeMatch> match) {
            this.subMatches[index] = match;
            return true;
        }

        /** Returns the current node image at a given index. */
        final HostNode getNode(int index) {
            return this.nodeImages[index];
        }

        /** Returns the current edge image at a given index. */
        final HostEdge getEdge(int index) {
            return this.edgeImages[index];
        }

        /** Returns the current variable image at a given index. */
        final TypeLabel getVar(int index) {
            return this.varImages[index];
        }

        /** Returns the composite match at a given index. */
        final Collection<TreeMatch> getSubMatch(int index) {
            return this.subMatches[index];
        }

        /**
         * Returns the node seed (i.e., the pre-matched image) at a given index.
         */
        final HostNode getNodeSeed(int index) {
            return this.nodeSeeds[index];
        }

        /**
         * Returns the edge seed (i.e., the pre-matched image) at a given index.
         */
        final HostEdge getEdgeSeed(int index) {
            return this.edgeSeeds[index];
        }

        /**
         * Returns the variable seed (i.e., the pre-matched image) at a given index.
         */
        final TypeLabel getVarSeed(int index) {
            return this.varSeeds[index];
        }

        /**
         * Returns a copy of the search result, or <code>null</code> if the last
         * invocation of {@link #find()} was not successful.
         */
        public TreeMatch getMatch() {
            TreeMatch result = null;
            if (this.found) {
                RuleToHostMap patternMap =
                    this.host.getFactory().createRuleToHostMap();
                for (int i = 0; i < this.nodeImages.length; i++) {
                    HostNode image = this.nodeImages[i];
                    if (image != null) {
                        patternMap.putNode(
                            SearchPlanStrategy.this.nodeKeys[i], image);
                    }
                }
                for (int i = 0; i < this.edgeImages.length; i++) {
                    HostEdge image = this.edgeImages[i];
                    if (image != null) {
                        patternMap.putEdge(
                            SearchPlanStrategy.this.edgeKeys[i], image);
                    }
                }
                for (int i = 0; i < this.varImages.length; i++) {
                    TypeLabel image = this.varImages[i];
                    if (image != null) {
                        patternMap.putVar(
                            SearchPlanStrategy.this.varKeys[i], image);
                    }
                }
                result =
                    new TreeMatch(
                        SearchPlanStrategy.this.plan.getCondition(),
                        patternMap);
                for (int i = 0; i < this.subMatches.length; i++) {
                    result.addSubMatches(this.subMatches[i]);
                }
            }
            return result;
        }

        /**
         * Returns the set of nodes already used as images. This is needed for
         * the injectivity check, if any.
         */
        private Set<HostNode> getUsedNodes() {
            if (this.usedNodes == null) {
                this.usedNodes = new HashSet<HostNode>();
            }
            return this.usedNodes;
        }

        /** Array of node images. */
        private final HostNode[] nodeImages;
        /** Array of edge images. */
        private final HostEdge[] edgeImages;
        /** Array of variable images. */
        private final TypeLabel[] varImages;
        /** Array of variable images. */
        private final Collection<TreeMatch>[] subMatches;
        /**
         * Array indicating, for each index, if the node with that image was
         * pre-matched in the search.
         */
        private final HostNode[] nodeSeeds;
        /**
         * Array indicating, for each index, if the variable with that image was
         * pre-matched in the search.
         */
        private final HostEdge[] edgeSeeds;
        /**
         * Array indicating, for each index, if the edge with that image was
         * pre-matched in the search.
         */
        private final TypeLabel[] varSeeds;
        /** Flag indicating that a solution has already been found. */
        private boolean found;
        /** Index of the last search record known to be singular. */
        private int lastSingular;
        /** The host graph of the search. */
        private HostGraph host;
        /**
         * The set of non-value nodes already used as images, used for the
         * injectivity test.
         */
        private Set<HostNode> usedNodes;
        /** Search stack. */
        private final SearchItem.Record[] records;
        /** Forward influences of the records. */
        private final SearchItem.Record[][] influence;
        /** Forward influence count of the records. */
        private final int[] influenceCount;
    }

}