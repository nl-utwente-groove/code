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
 * $Id$
 */
package groove.match.plan;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import groove.algebra.Sort;
import groove.grammar.Condition;
import groove.grammar.host.DefaultHostNode;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.host.HostNodeSet;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.rule.VariableNode;
import groove.grammar.type.TypeElement;
import groove.match.SearchEngine;
import groove.match.SearchStrategy;
import groove.match.TreeMatch;
import groove.util.Reporter;
import groove.util.Visitor;

/**
 * This matcher walks through a search tree built up according to a search plan,
 * in which the matching order of the domain elements is determined.
 * @author Arend Rensink
 * @version $Revision$
 */
public class PlanSearchStrategy implements SearchStrategy {
    /**
     * Constructs a strategy from a given list of search items. A flag controls
     * if solutions should be injective.
     * @param plan the search items that make up the search plan
     */
    public PlanSearchStrategy(PlanSearchEngine engine, SearchPlan plan) {
        this.nodeIxMap = new HashMap<>();
        this.edgeIxMap = new HashMap<>();
        this.varIxMap = new HashMap<>();
        this.condIxMap = new HashMap<>();
        this.engine = engine;
        this.plan = plan;
        this.injective = plan.isInjective();
    }

    @Override
    public SearchEngine getEngine() {
        return this.engine;
    }

    @Override
    public <T> T traverse(HostGraph host, RuleToHostMap seedMap, Visitor<TreeMatch,T> visitor) {
        Search search = getSearch(host, seedMap);
        while (search.find() && visitor.visit(search.getMatch())) {
            // do nothing
        }
        return visitor.getResult();
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
    final protected SearchPlan getPlan() {
        return this.plan;
    }

    @Override
    public String toString() {
        return this.plan.toString() + (isInjective() ? " (injective)" : " (non-injective)");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isInjective() ? 1231 : 1237);
        result = prime * result + getPlan().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PlanSearchStrategy other = (PlanSearchStrategy) obj;
        if (isInjective() != other.isInjective()) {
            return false;
        }
        if (!getPlan().equals(other.getPlan())) {
            return false;
        }
        return true;
    }

    /**
     * Callback factory method for an auxiliary {@link Search} object.
     */
    private Search getSearch(HostGraph host, RuleToHostMap seedMap) {
        this.search = createSearch();
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
     * Returns the index of a given variable in the variable index map. Adds an
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
     * Returns the index of a given subcondition in the index map. Adds an
     * index for the variable to the map if it was not yet there.
     * @param cond the condition to be looked up
     * @return an index for <code>cond</code>
     */
    int getCondIx(Condition cond) {
        Integer value = this.condIxMap.get(cond);
        if (value == null) {
            testFixed(false);
            this.condIxMap.put(cond, value = this.condIxMap.size());
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
            throw new IllegalStateException(
                String.format("Search plan is %s fixed", fixed ? "not yet" : ""));
        }
    }

    /** The fixed search object. */
    private Search search;
    /** The engine used to create this strategy. */
    private final PlanSearchEngine engine;
    /**
     * A list of domain elements, in the order in which they are to be matched.
     */
    final SearchPlan plan;
    /** Flag indicating that the matching should be injective. */
    final boolean injective;
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
     * Map from subconditions to (distinct) indices.
     */
    private final Map<Condition,Integer> condIxMap;
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
    static private final Reporter reporter = Reporter.register(PlanSearchStrategy.class);
    /** Handle for profiling {@link Search#find()} */
    static public final Reporter searchFindReporter = reporter.register("Search.find()");

    /**
     * Class implementing an instantiation of the search plan algorithm for a
     * given graph.
     */
    public class Search {
        /** Constructs a new record for a given graph and partial match. */
        public Search() {
            int planSize = PlanSearchStrategy.this.plan.size();
            this.records = new SearchItem.Record[planSize];
            this.influence = new SearchItem.Record[planSize][];
            this.influenceCount = new int[planSize];
            this.nodeImages = new HostNode[PlanSearchStrategy.this.nodeKeys.length];
            this.edgeImages = new HostEdge[PlanSearchStrategy.this.edgeKeys.length];
            this.varImages = new TypeElement[PlanSearchStrategy.this.varKeys.length];
            this.nodeSeeds = new HostNode[PlanSearchStrategy.this.nodeKeys.length];
            this.edgeSeeds = new HostEdge[PlanSearchStrategy.this.edgeKeys.length];
            this.varSeeds = new TypeElement[PlanSearchStrategy.this.varKeys.length];
            this.subMatches = new TreeMatch[PlanSearchStrategy.this.condIxMap.size()];
        }

        /** Initialises the search for a given host graph and seed map. */
        public void initialise(HostGraph host, RuleToHostMap seedMap) {
            this.host = host;
            if (isInjective()) {
                getUsedNodes().clear();
            }
            if (seedMap != null) {
                for (Map.Entry<RuleNode,? extends HostNode> nodeEntry : seedMap.nodeMap()
                    .entrySet()) {
                    assert isNodeFound(nodeEntry.getKey());
                    int i = getNodeIx(nodeEntry.getKey());
                    this.nodeImages[i] = this.nodeSeeds[i] = nodeEntry.getValue();
                    if (isInjective()) {
                        getUsedNodes().add(nodeEntry.getValue());
                    }
                }
                for (Map.Entry<RuleEdge,? extends HostEdge> edgeEntry : seedMap.edgeMap()
                    .entrySet()) {
                    assert isEdgeFound(edgeEntry.getKey());
                    int i = getEdgeIx(edgeEntry.getKey());
                    this.edgeImages[i] = this.edgeSeeds[i] = edgeEntry.getValue();
                }
                for (Map.Entry<LabelVar,TypeElement> varEntry : seedMap.getValuation()
                    .entrySet()) {
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
            final int planSize = PlanSearchStrategy.this.plan.size();
            boolean found = this.found;
            // if an image was found before, roll back the result
            // until the last relevant search item
            int current;
            if (found) {
                current = planSize - 1;
                SearchItem.Record currentRecord;
                while (current >= 0 && !(currentRecord = getRecord(current)).isRelevant()) {
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
                    int dependency = PlanSearchStrategy.this.plan.getDependency(current);
                    for (current--; current > dependency; current--) {
                        getRecord(current).repeat();
                    }
                } else {
                    current--;
                }
            }
            found = current == planSize;
            boolean oldFound = this.found;
            this.found = found;
            if (PRINT_MATCHES) {
                Condition condition = PlanSearchStrategy.this.plan.getCondition();
                if (condition.hasRule() && condition.getRule()
                    .isTop()) {
                    System.out.printf("Next match for %s%s%n",
                        condition.getName(),
                        oldFound ? ": " : " in " + this.host);
                    if (found) {
                        System.out.print("  " + getMatch());
                    } else {
                        System.out.println("  None");
                    }
                }
            }
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
                SearchItem item = PlanSearchStrategy.this.plan.get(current);
                // make a new record
                result = item.createRecord(this);
                result.initialise(this.host);
                this.records[current] = result;
                this.influence[current] = new SearchItem.Record[this.influence.length - current];
                int dependency = PlanSearchStrategy.this.plan.getDependency(current);
                assert dependency < current;
                if (dependency >= 0) {
                    this.influence[dependency][this.influenceCount[dependency]] = result;
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
            if (CHECK_IMAGES) {
                if (image instanceof DefaultHostNode && !this.host.containsNode(image)) {
                    assert false : String.format("Node %s does not occur in graph %s",
                        image,
                        this.host);
                }
            }
            RuleNode nodeKey = PlanSearchStrategy.this.nodeKeys[index];
            assert image == null || this.nodeSeeds[index] == null : String.format(
                "Assignment %s=%s replaces pre-matched image %s",
                nodeKey,
                image,
                this.nodeSeeds[index]);
            boolean keyIsVariableNode = nodeKey instanceof VariableNode;
            if (image instanceof ValueNode) {
                // value nodes only matched by value nodes without signature or of the
                // same signature
                if (!keyIsVariableNode) {
                    return false;
                } else {
                    Sort keySignature = ((VariableNode) nodeKey).getSort();
                    if (((ValueNode) image).getSort() != keySignature) {
                        return false;
                    }
                }
            } else if (keyIsVariableNode) {
                return false;
            } else if (isInjective()) {
                HostNode oldImage = this.nodeImages[index];
                if (oldImage != null) {
                    boolean removed = getUsedNodes().remove(oldImage);
                    assert removed : String.format("Node image %s not in used nodes %s",
                        oldImage,
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
            if (CHECK_IMAGES) {
                if (image != null && !this.host.containsEdge(image)) {
                    assert false : String.format("Edge %s does not occur in graph %s",
                        image,
                        this.host);
                }
            }
            this.edgeImages[index] = image;
            return true;
        }

        /** Sets the variable image for the graph variable with a given index. */
        final boolean putVar(int index, TypeElement image) {
            this.varImages[index] = image;
            return true;
        }

        /** Sets the composite match for a given index. */
        final boolean putSubMatch(int index, TreeMatch match) {
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
        final TypeElement getVar(int index) {
            return this.varImages[index];
        }

        /** Returns the composite match at a given index. */
        final TreeMatch getSubMatch(int index) {
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
        final TypeElement getVarSeed(int index) {
            return this.varSeeds[index];
        }

        /**
         * Returns a copy of the search result, or <code>null</code> if the last
         * invocation of {@link #find()} was not successful.
         */
        public TreeMatch getMatch() {
            TreeMatch result = null;
            if (this.found) {
                RuleToHostMap patternMap = this.host.getFactory()
                    .createRuleToHostMap();
                for (int i = 0; i < this.nodeImages.length; i++) {
                    HostNode image = this.nodeImages[i];
                    if (image != null) {
                        patternMap.putNode(PlanSearchStrategy.this.nodeKeys[i], image);
                    }
                }
                for (int i = 0; i < this.edgeImages.length; i++) {
                    HostEdge image = this.edgeImages[i];
                    if (image != null) {
                        patternMap.putEdge(PlanSearchStrategy.this.edgeKeys[i], image);
                    }
                }
                for (int i = 0; i < this.varImages.length; i++) {
                    TypeElement image = this.varImages[i];
                    if (image != null) {
                        patternMap.putVar(PlanSearchStrategy.this.varKeys[i], image);
                    }
                }
                result = new TreeMatch(PlanSearchStrategy.this.plan.getCondition(), patternMap);
                for (int i = 0; i < this.subMatches.length; i++) {
                    result.addSubMatch(this.subMatches[i]);
                }
            }
            return result;
        }

        /**
         * Returns the set of nodes already used as images. This is needed for
         * the injectivity check, if any.
         */
        private HostNodeSet getUsedNodes() {
            if (this.usedNodes == null) {
                this.usedNodes = new HostNodeSet();
            }
            return this.usedNodes;
        }

        /** Array of node images. */
        private final HostNode[] nodeImages;
        /** Array of edge images. */
        private final HostEdge[] edgeImages;
        /** Array of variable images. */
        private final TypeElement[] varImages;
        /** Array of variable images. */
        private final TreeMatch[] subMatches;
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
        private final TypeElement[] varSeeds;
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
        private HostNodeSet usedNodes;
        /** Search stack. */
        private final SearchItem.Record[] records;
        /** Forward influences of the records. */
        private final SearchItem.Record[][] influence;
        /** Forward influence count of the records. */
        private final int[] influenceCount;

        private final static boolean PRINT_MATCHES = false;
        private final static boolean CHECK_IMAGES = true;
    }

}