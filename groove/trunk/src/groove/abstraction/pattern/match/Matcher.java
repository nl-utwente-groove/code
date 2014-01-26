/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.abstraction.pattern.match;

import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.lts.MatchResult;
import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.trans.PatternRule;
import groove.abstraction.pattern.trans.RuleEdge;
import groove.abstraction.pattern.trans.RuleNode;
import groove.control.CtrlStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Matcher for pattern graph rules. 
 * 
 * @author Eduardo Zambon
 */
public final class Matcher {

    /** Flag indicating that the matching should be injective. */
    private final boolean injective;
    /**
     * A list of domain elements, in the order in which they are to be matched.
     */
    private final PatternSearchPlan plan;
    /** The fixed search object. */
    private final Search search;
    /** Map from source graph nodes to (distinct) indices. */
    private final Map<RuleNode,Integer> nodeIxMap;
    /** Map from source graph edges to (distinct) indices. */
    private final Map<RuleEdge,Integer> edgeIxMap;
    /**
     * Array of source graph nodes, which is the inverse of {@link #nodeIxMap}.
     */
    private RuleNode[] nodeKeys;
    /**
     * Array of source graph edges, which is the inverse of {@link #edgeIxMap}.
     */
    private RuleEdge[] edgeKeys;

    /** Default constructor. */
    public Matcher(PatternRule pRule, boolean injective) {
        this.injective = injective;
        this.plan = new PatternSearchPlan(pRule, injective);
        this.nodeIxMap = new HashMap<RuleNode,Integer>();
        this.edgeIxMap = new HashMap<RuleEdge,Integer>();
        for (SearchItem item : this.plan) {
            item.activate(this);
        }
        // Now create the inverse of the index maps.
        this.nodeKeys = new RuleNode[this.nodeIxMap.size()];
        for (Entry<RuleNode,Integer> nodeIxEntry : this.nodeIxMap.entrySet()) {
            this.nodeKeys[nodeIxEntry.getValue()] = nodeIxEntry.getKey();
        }
        this.edgeKeys = new RuleEdge[this.edgeIxMap.size()];
        for (Entry<RuleEdge,Integer> edgeIxEntry : this.edgeIxMap.entrySet()) {
            this.edgeKeys[edgeIxEntry.getValue()] = edgeIxEntry.getKey();
        }
        this.search = new Search();
    }

    /** Indicates if this matching is (to be) injective. */
    private final boolean isInjective() {
        return this.injective;
    }

    /** Returns a list of all matches found on the given graph. */
    public List<MatchResult> findMatches(PatternGraph pGraph, CtrlStep ctrlTrans) {
        List<MatchResult> result = new ArrayList<MatchResult>();
        this.search.initialise(pGraph);
        while (this.search.find()) {
            Match match = this.search.getMatch();
            // EZ says: this deviates from the original matching implementation.
            // It is possible for a match to be discarded after being found,
            // for example, if we have a pre-match that violates multiplicities.
            // In this case the call to getMatch fails and we have an null
            // reference.
            if (match != null) {
                result.add(new MatchResult(match, ctrlTrans));
            }
        }
        return result;
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
     * Returns the index of a given node in the node index map. Adds an index
     * for the node to the map if it was not yet there.
     * @param node the node to be looked up
     * @return an index for <code>node</code>
     */
    int getNodeIx(RuleNode node) {
        Integer result = this.nodeIxMap.get(node);
        if (result == null) {
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
            this.edgeIxMap.put(edge, value = this.edgeIxMap.size());
        }
        return value;
    }

    /**
     * Class implementing an instantiation of the search plan algorithm for a
     * given graph.
     */
    public final class Search {

        /** Array of node images. */
        private final PatternNode[] nodeImages;
        /** Array of edge images. */
        private final PatternEdge[] edgeImages;
        /** Search stack. */
        private final SearchItem.Record[] records;
        /** Forward influences of the records. */
        private final SearchItem.Record[][] influence;
        /** Forward influence count of the records. */
        private final int[] influenceCount;
        /**
         * The set of non-value nodes already used as images, used for the
         * injectivity test.
         */
        private Set<PatternNode> usedNodes;
        /** The host graph of the search. */
        private PatternGraph host;
        /** Flag indicating that a solution has already been found. */
        private boolean found;
        /** Index of the last search record known to be singular. */
        private int lastSingular;

        /** Constructs a new search . */
        Search() {
            int planSize = Matcher.this.plan.size();
            this.records = new SearchItem.Record[planSize];
            this.nodeImages = new PatternNode[Matcher.this.nodeKeys.length];
            this.edgeImages = new PatternEdge[Matcher.this.edgeKeys.length];
            this.influence = new SearchItem.Record[planSize][];
            this.influenceCount = new int[planSize];
        }

        /**
         * Returns the set of nodes already used as images. This is needed for
         * the injectivity check, if any.
         */
        private Set<PatternNode> getUsedNodes() {
            if (this.usedNodes == null) {
                this.usedNodes = new MyHashSet<PatternNode>();
            }
            return this.usedNodes;
        }

        /** Initialises the search for a pattern graph. */
        void initialise(PatternGraph host) {
            this.host = host;
            if (isInjective()) {
                getUsedNodes().clear();
            }
            // EZ says: We are reusing the search object so we have to clear
            // the images array to avoid garbage references.
            for (int i = 0; i < this.nodeImages.length; i++) {
                this.nodeImages[i] = null;
            }
            for (int i = 0; i < this.edgeImages.length; i++) {
                this.edgeImages[i] = null;
            }
            for (int i = 0; i < this.records.length && this.records[i] != null; i++) {
                this.records[i].initialise(host);
            }
            this.found = false;
            this.lastSingular = -1;
        }

        /**
         * Computes the next search result. If the method returns
         * <code>true</code>, the result can be obtained by {@link #getMatch()}.
         * @return <code>true</code> if there is a next result.
         */
        boolean find() {
            final int planSize = Matcher.this.plan.size();
            // If an image was found before, roll back the result
            // until the last relevant search item.
            int current;
            if (this.found) {
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
                    // Go back to the last dependency to have any hope
                    // of finding a match.
                    int dependency = Matcher.this.plan.getDependency(current);
                    for (current--; current > dependency; current--) {
                        getRecord(current).repeat();
                    }
                } else {
                    current--;
                }
            }
            this.found = current == planSize;
            return this.found;
        }

        /**
         * Returns the currently active search item record.
         * @param current the index of the requested record
         */
        private SearchItem.Record getRecord(int current) {
            SearchItem.Record result = this.records[current];
            if (result == null) {
                SearchItem item = Matcher.this.plan.get(current);
                // make a new record
                result = item.createRecord(this);
                result.initialise(this.host);
                this.records[current] = result;
                this.influence[current] = new SearchItem.Record[this.influence.length - current];
                int dependency = Matcher.this.plan.getDependency(current);
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

        /**
         * Returns a copy of the search result, or <code>null</code> if the last
         * invocation of {@link #find()} was not successful.
         */
        Match getMatch() {
            Match result = null;
            if (this.found) {
                result = createEmptyMatch();
                for (int i = 0; i < this.nodeImages.length; i++) {
                    PatternNode image = this.nodeImages[i];
                    if (image != null) {
                        result.putNode(Matcher.this.nodeKeys[i], image);
                    }
                }
                for (int i = 0; i < this.edgeImages.length; i++) {
                    PatternEdge image = this.edgeImages[i];
                    if (image != null) {
                        result.putEdge(Matcher.this.edgeKeys[i], image);
                    }
                }
                assert result.isFinished();
                result.setFixed();
                if (!result.isValid()) {
                    // We have an invalid match, for instance a pre-match that
                    // doesn't respect multiplicities.
                    result = null;
                }
            }
            return result;
        }

        Match createEmptyMatch() {
            // EZ says: I don't like this test but it's a fast hack...
            if (this.host instanceof PatternShape) {
                return new PreMatch(Matcher.this.plan.getRule(), (PatternShape) this.host);
            } else {
                return new Match(Matcher.this.plan.getRule(), this.host);
            }
        }

        /** Sets the node image for the node key with a given index. */
        boolean putNode(int index, PatternNode image) {
            if (isInjective()) {
                PatternNode oldImage = this.nodeImages[index];
                if (oldImage != null) {
                    boolean removed = getUsedNodes().remove(oldImage);
                    assert removed : String.format("Node image %s not in used nodes %s", oldImage,
                        this.usedNodes);
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
        boolean putEdge(int index, PatternEdge image) {
            this.edgeImages[index] = image;
            return true;
        }

        /** Returns the current node image at a given index. */
        PatternNode getNode(int index) {
            return this.nodeImages[index];
        }

        /** Returns the current edge image at a given index. */
        PatternEdge getEdge(int index) {
            return this.edgeImages[index];
        }
    }

}
