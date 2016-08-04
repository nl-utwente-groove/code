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

import groove.grammar.rule.Anchor;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.match.plan.PlanSearchStrategy.Search;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Search item that reflects (and optionally checks) that a set of elements
 * (nodes, variables and edges) have already been matched.
 * @author Arend Rensink
 * @version $Revision $
 */
class SeedSearchItem extends AbstractSearchItem {
    /**
     * Creates an instance with given sets of pre-matched nodes, edges and
     * variables.
     * @param seed the set of pre-matched nodes; not <code>null</code>
     */
    SeedSearchItem(Anchor seed) {
        this.seed = seed;
        this.boundNodes = Arrays.asList(seed.nodeSet().toArray(new RuleNode[0]));
        this.boundEdges = Arrays.asList(seed.edgeSet().toArray(new RuleEdge[0]));
        this.boundVars = Arrays.asList(seed.varSet().toArray(new LabelVar[0]));
    }

    /** This implementation returns the set of pre-matched edges. */
    @Override
    public Collection<? extends RuleEdge> bindsEdges() {
        return this.boundEdges;
    }

    /** This implementation returns the set of pre-matched nodes. */
    @Override
    public Collection<? extends RuleNode> bindsNodes() {
        return this.boundNodes;
    }

    /** This implementation returns the set of pre-matched variables. */
    @Override
    public Collection<LabelVar> bindsVars() {
        return this.boundVars;
    }

    @Override
    public int compareTo(SearchItem item) {
        int result = super.compareTo(item);
        if (result != 0) {
            return result;
        }
        SeedSearchItem other = (SeedSearchItem) item;
        result = this.seed.compareTo(other.seed);
        return result;
    }

    @Override
    int computeHashCode() {
        return super.computeHashCode() + 31 * this.seed.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        SeedSearchItem other = (SeedSearchItem) obj;
        return this.seed.equals(other.seed);
    }

    /**
     * This item gets the highest rating since it should be scheduled first.
     */
    @Override
    int getRating() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void activate(PlanSearchStrategy strategy) {
        this.nodeIxMap = new HashMap<>();
        for (RuleNode node : this.boundNodes) {
            assert !strategy.isNodeFound(node) : String.format("Node %s is not fresh", node);
            this.nodeIxMap.put(node, strategy.getNodeIx(node));
        }
        this.edgeIxMap = new HashMap<>();
        for (RuleEdge edge : this.boundEdges) {
            assert !strategy.isEdgeFound(edge) : String.format("Edge %s is not fresh", edge);
            this.edgeIxMap.put(edge, strategy.getEdgeIx(edge));
        }
        this.varIxMap = new HashMap<>();
        for (LabelVar var : this.boundVars) {
            assert !strategy.isVarFound(var) : String.format("Variable %s is not fresh", var);
            this.varIxMap.put(var, strategy.getVarIx(var));
        }
    }

    @Override
    public String toString() {
        return String.format("Check %s", this.seed);
    }

    @Override
    public Record createRecord(Search search) {
        assert allElementsMatched(search) : String.format("Elements %s not pre-matched",
            this.unmatched);
        return new DummyRecord();
    }

    private boolean allElementsMatched(Search search) {
        if (this.unmatched == null) {
            this.unmatched = new HashSet<>();
            for (Map.Entry<RuleNode,Integer> nodeEntry : this.nodeIxMap.entrySet()) {
                if (search.getNode(nodeEntry.getValue()) == null) {
                    this.unmatched.add(nodeEntry.getKey());
                }
            }
            for (Map.Entry<RuleEdge,Integer> edgeEntry : this.edgeIxMap.entrySet()) {
                if (search.getEdge(edgeEntry.getValue()) == null) {
                    this.unmatched.add(edgeEntry.getKey());
                }
            }
            for (Map.Entry<LabelVar,Integer> varEntry : this.varIxMap.entrySet()) {
                if (search.getVar(varEntry.getValue()) == null) {
                    this.unmatched.add(varEntry.getKey());
                }
            }
        }
        return this.unmatched.isEmpty();
    }

    /** The collection of pre-matched elements. */
    private final Anchor seed;
    private final List<RuleNode> boundNodes;
    private final List<RuleEdge> boundEdges;
    private final List<LabelVar> boundVars;
    /**
     * Mapping from seeded nodes to their indices in
     * the result.
     */
    private Map<RuleNode,Integer> nodeIxMap;
    /**
     * Mapping from seeded edges to their indices in
     * the result.
     */
    private Map<RuleEdge,Integer> edgeIxMap;
    /**
     * Mapping from seeded variables to their indices
     * in the result.
     */
    private Map<LabelVar,Integer> varIxMap;
    /** The set of unmatched graph elements (that should have been pre-matched) . */
    private Set<Object> unmatched;
}
