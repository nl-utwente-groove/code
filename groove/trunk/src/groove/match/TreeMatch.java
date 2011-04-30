/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.match;

import groove.trans.Condition;
import groove.trans.Condition.Op;
import groove.trans.HostEdge;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleElement;
import groove.trans.RuleMatch;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;
import groove.util.Fixable;
import groove.util.HashBag;
import groove.util.Property;
import groove.util.Visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Encoding of a condition match as a tree structure.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TreeMatch implements Fixable {
    /**
     * Constructs an initially empty match for a given condition, based on
     * an optional pattern map for that condition.
     */
    public TreeMatch(Condition condition, RuleToHostMap patternMap) {
        this(condition.getOp(), condition, patternMap);
    }

    /**
     * Constructs an initially empty match for a given condition, based on
     * a given top-level operator and an optional pattern map for that condition.
     */
    public TreeMatch(Condition.Op op, Condition condition,
            RuleToHostMap patternMap) {
        this.condition = condition;
        this.op = op;
        this.subMatches = createSubMatches(op);
        this.patternMap = patternMap;
        this.collector =
            op.isQuantifier() ? new RuleMatchWrapperCollector(null)
                    : Visitor.<RuleMatch,List<RuleMatch>>newCollector();
        assert op.isQuantifier() == (patternMap != null && condition.getOp() == op);
    }

    /** Returns the condition of this match. */
    public final Condition getCondition() {
        return this.condition;
    }

    /**
     * Returns the operator of this tree match.
     * Note that this may be different from the operator of the condition:
     * in particular, the match operator may be {@link Condition.Op#AND} when
     * the condition is {@link Condition.Op#FORALL}, or {@link Condition.Op#OR} when
     * the condition is {@link Condition.Op#EXISTS}.
     */
    public final Condition.Op getOp() {
        return this.op;
    }

    /** Returns the pattern map of this match. */
    public final RuleToHostMap getPatternMap() {
        return this.patternMap;
    }

    /** Returns the current submatches of this match. */
    private final Collection<TreeMatch> getSubMatches() {
        return this.subMatches;
    }

    /** Callback factory method for the submatch collection object. */
    private Collection<TreeMatch> createSubMatches(Condition.Op op) {
        // the collection is a bag if the operator is conjunctive,
        // and a set if it is conjunctive
        // Since the matches are unordered, for more efficient comparison
        // the bag is a HashBag
        return op.isConjunctive() ? new HashBag<TreeMatch>()
                : new HashSet<TreeMatch>();
    }

    /** Adds a sub-match to this tree match. */
    public final boolean addSubMatch(TreeMatch subMatch) {
        assert !isFixed();
        return this.subMatches.add(subMatch);
    }

    /** Adds a sub-match to this tree match. */
    public final boolean addSubMatches(Collection<TreeMatch> subMatches) {
        assert !isFixed();
        return this.subMatches.addAll(subMatches);
    }

    /** 
     * Traverses the rule matches based on this tree match.
     * Each visited rule match consists of the pattern map of this
     * tree match, together with a choice among the submatches of
     * all universal sub-conditions.
     * This operation is only allowed if the match condition is a rule.
     * @throws UnsupportedOperationException if the condition of this
     * tree match is not of the correct type.
     */
    public <R> R traverseRuleMatches(Visitor<RuleMatch,R> visitor) {
        setFixed();
        if (this.op.isConjunctive()) {
            traverseAndMatches(visitor);
        } else {
            traverseOrMatches(visitor);
        }
        return visitor.getResult();
    }

    /** Traverses the matches for a conjunctive tree match. */
    private void traverseAndMatches(Visitor<RuleMatch,?> visitor) {
        int subMatchCount = getSubMatches().size();
        if (subMatchCount == 0) {
            assert this.op == Op.EXISTS;
            visitor.visit(createRuleMatch());
        } else {
            @SuppressWarnings("unchecked")
            List<RuleMatch>[] matrix = new List[subMatchCount];
            int[] rowSize = new int[subMatchCount];
            int resultSize = computeForallMatchMatrix(matrix, rowSize);
            if (resultSize > 0) {
                traverseMatrix(matrix, rowSize, visitor);
            }
        }
    }

    /**
     * Traverses the rule matches constructed from this tree match,
     * if the operator of this tree match is disjunctive.
     */
    @SuppressWarnings("unchecked")
    private void traverseOrMatches(Visitor<RuleMatch,?> visitor) {
        Visitor<RuleMatch,?> subVisitor;
        if (getOp().isQuantifier()) {
            subVisitor = this.wrapper.newInstance(visitor);
        } else {
            subVisitor = visitor;
        }
        for (TreeMatch subMatch : this.subMatches) {
            subMatch.traverseRuleMatches(subVisitor);
            if (!subVisitor.isContinue()) {
                break;
            }
        }
    }

    /** 
     * Converts this tree match to a set of rule matches.
     * Each resulting rule match consists of the pattern map of this
     * tree match, together with a choice among the submatches of
     * all universal sub-conditions.
     * This operation is only allowed if the match condition is a rule.
     * @throws UnsupportedOperationException if the condition of this
     * tree match is not of the correct type.
     */
    public List<RuleMatch> toRuleMatchSet() {
        setFixed();
        if (this.op.isConjunctive()) {
            return toAndMatchSet();
        } else {
            return toOrMatchSet();
        }
    }

    /** Returns the list of matches for a conjunctive tree match. */
    private List<RuleMatch> toAndMatchSet() {
        List<RuleMatch> result;
        int subMatchCount = getSubMatches().size();
        if (subMatchCount == 0) {
            result = new ArrayList<RuleMatch>(1);
            result.add(createRuleMatch());
        } else {
            @SuppressWarnings("unchecked")
            List<RuleMatch>[] matrix = new List[subMatchCount];
            int[] rowSize = new int[subMatchCount];
            int resultSize = computeForallMatchMatrix(matrix, rowSize);
            if (resultSize == 0) {
                result = Collections.emptyList();
            } else {
                result = new ArrayList<RuleMatch>(resultSize);
                Visitor<RuleMatch,?> collector = Visitor.newCollector(result);
                traverseMatrix(matrix, rowSize, collector);
                collector.dispose();
            }
        }
        return result;
    }

    /**
     * Returns the rule matches constructed from this tree match,
     * if the operator of this tree match is disjunctive.
     */
    private List<RuleMatch> toOrMatchSet() {
        List<RuleMatch> result = new ArrayList<RuleMatch>();
        Visitor<RuleMatch,?> collector = this.collector.newInstance(result);
        for (TreeMatch subMatch : this.subMatches) {
            subMatch.traverseRuleMatches(collector);
        }
        collector.dispose();
        return result;
    }

    /** 
     * Computes and traverses all rule matches
     * consisting of one sub-match from each row of a given matrix.
     */
    private void traverseMatrix(List<RuleMatch>[] matrix, int[] rowSize,
            Visitor<RuleMatch,?> visitor) {
        int rowCount = rowSize.length;
        int index[] = new int[rowCount];
        do {
            RuleMatch match = createRuleMatch();
            Collection<RuleMatch> subMatches = match.getSubMatches();
            for (int row = 0; row < rowCount; row++) {
                RuleMatch subMatch = matrix[row].get(index[row]);
                if (subMatch.isComposite()) {
                    subMatches.addAll(subMatch.getSubMatches());
                } else {
                    subMatches.add(subMatch);
                }
            }
            // stop the traversal if the visitor asks for it
            if (!visitor.visit(match)) {
                break;
            }
        } while (incVector(index, rowSize));
    }

    /**
     * Computes the matrix of which the rows are the {@link #toRuleMatchSet()}
     * arrays of the submatches.
     * The construction stops prematurely if an empty row is found.
     * @param matrix the matrix of submatches. Should be initialised to 
     * an array of the correct length (the number of submatches)
     * @param rowSize the number of elements per matrix row. Should 
     * be initialised to an array of the correct length (the number of submatches)
     * @return the product of the row sizes
     */
    private int computeForallMatchMatrix(List<RuleMatch>[] matrix, int[] rowSize) {
        int resultSize = 1;
        int i = 0;
        for (TreeMatch subMatch : getSubMatches()) {
            List<RuleMatch> row = subMatch.toRuleMatchSet();
            matrix[i] = row;
            resultSize *= row.size();
            if (resultSize == 0) {
                break;
            }
            rowSize[i] = row.size();
            i++;
        }
        return resultSize;
    }

    /** Callback factory method for rule matches base don this tree match. */
    private RuleMatch createRuleMatch() {
        return new RuleMatch(getCondition(), getPatternMap());
    }

    /** 
     * Increments a given vector of values lexicographically,
     * up to a given maximum value at each index.
     * @param vector the vector to be incremented
     * @param size array of maximum values at each vector index
     * @return {@code true} if the new value is positive
     */
    private boolean incVector(int[] vector, int[] size) {
        boolean result;
        assert vector.length == size.length;
        int dim = size.length - 1;
        // search for the lest significant dimension for which the
        // vector value does not yet equal the row size
        while (dim >= 0 && vector[dim] == size[dim] - 1) {
            vector[dim] = 0;
            dim--;
        }
        result = dim >= 0;
        if (result) {
            vector[dim]++;
        }
        return result;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int result = computeHashCode();
            if (result == 0) {
                result = 1;
            }
            this.hashCode = result;
        }
        return this.hashCode;
    }

    /** Computes the hash code of the match. */
    private int computeHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getCondition().hashCode();
        if (this.op.isQuantifier()) {
            int patternHashCode = 1;
            if (getCondition().hasRule()) {
                // only the anchor images matter to equality of the match
                Map<?,?> nodeMap = getPatternMap().nodeMap();
                Map<?,?> edgeMap = getPatternMap().edgeMap();
                RuleElement[] anchor = getCondition().getRule().anchor();
                for (int i = 0; i < anchor.length; i++) {
                    RuleElement element = anchor[i];
                    Map<?,?> map =
                        element instanceof RuleNode ? nodeMap : edgeMap;
                    patternHashCode =
                        prime * patternHashCode + map.get(element).hashCode();
                }
            } else {
                // the entire pattern map is relevant
                patternHashCode = getPatternMap().hashCode();
            }
            result = prime * result + patternHashCode;
        }
        result = prime * result + getSubMatches().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        assert isFixed();
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TreeMatch)) {
            return false;
        }
        TreeMatch other = (TreeMatch) obj;
        assert other.isFixed();
        if (!other.getOp().equals(getOp())) {
            return false;
        }
        if (!other.getCondition().equals(getCondition())) {
            return false;
        }
        if (getSubMatches().size() != other.getSubMatches().size()) {
            return false;
        }
        if (getOp().isQuantifier()) {
            // only the anchor images matter to equality of the match
            if (getCondition().hasRule()) {
                RuleElement[] anchor = getCondition().getRule().anchor();
                Map<RuleNode,? extends HostNode> myNodeMap =
                    getPatternMap().nodeMap();
                Map<RuleNode,? extends HostNode> otherNodeMap =
                    other.getPatternMap().nodeMap();
                Map<RuleEdge,? extends HostEdge> myEdgeMap =
                    getPatternMap().edgeMap();
                Map<RuleEdge,? extends HostEdge> otherEdgeMap =
                    other.getPatternMap().edgeMap();
                for (int i = 0; i < anchor.length; i++) {
                    RuleElement element = anchor[i];
                    Map<?,?> myMap =
                        element instanceof RuleNode ? myNodeMap : myEdgeMap;
                    Map<?,?> otherMap =
                        element instanceof RuleNode ? otherNodeMap
                                : otherEdgeMap;
                    if (!myMap.get(element).equals(otherMap.get(element))) {
                        return false;
                    }
                }
            } else {
                if (!getPatternMap().equals(other.getPatternMap())) {
                    return false;
                }
            }
        }
        if (!getSubMatches().equals(other.getSubMatches())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return toString(0).toString();
    }

    /**
     * Recursively concatenates the pattern map and the
     * string descriptions of the submatches, indented to a certain level. 
     * @param level The level of indentation
     */
    private StringBuilder toString(int level) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < level; i++) {
            result.append("  ");
        }
        result.append(getCondition().getName());
        result.append(": ");
        result.append(getPatternMap());
        result.append('\n');
        for (TreeMatch subMatch : getSubMatches()) {
            result.append(subMatch.toString(level + 1));
        }
        return result;
    }

    @Override
    public void setFixed() {
        hashCode();
    }

    @Override
    public boolean isFixed() {
        return this.hashCode != 0;
    }

    @Override
    public void testFixed(boolean fixed) {
        if (isFixed() != fixed) {
            throw new IllegalStateException();
        }
    }

    private final Condition.Op op;
    private final Condition condition;
    private final RuleToHostMap patternMap;
    private final Collection<TreeMatch> subMatches;
    private final Visitor.Collector<RuleMatch,List<RuleMatch>> collector;
    @SuppressWarnings("rawtypes")
    private final RuleMatchWrapperVisitor wrapper =
        new RuleMatchWrapperVisitor();
    private int hashCode;

    /** Collector class for rule matches
     * that wraps its visited matches into a rule match of this condition.
     */
    private class RuleMatchWrapperCollector extends
            Visitor.Collector<RuleMatch,List<RuleMatch>> {
        /** Constructor for a prototype object. */
        public RuleMatchWrapperCollector() {
            super(null);
        }

        private RuleMatchWrapperCollector(List<RuleMatch> collection) {
            super(collection);
        }

        /** 
         * Wraps the visited match into a rule match of the top level
         * pattern, if the operator is a quantifier.
         */
        @Override
        protected boolean process(RuleMatch match) {
            RuleMatch newMatch = createRuleMatch();
            newMatch.getSubMatches().add(match);
            return super.process(newMatch);
        }

        @Override
        protected Collector<RuleMatch,List<RuleMatch>> createInstance(
                List<RuleMatch> collection, Property<RuleMatch> property) {
            return new RuleMatchWrapperCollector(collection);
        }
    }

    /** Visitor class for rule matches
     * that wraps its visited matches into a rule match of this condition
     * and then passes on the match to an inner visitor.
     */
    private class RuleMatchWrapperVisitor<R> extends Visitor<RuleMatch,R> {
        /** Constructor for a prototype object. */
        public RuleMatchWrapperVisitor() {
            this(null);
        }

        private RuleMatchWrapperVisitor(Visitor<RuleMatch,R> visitor) {
            this.visitor = visitor;
        }

        /** 
         * Wraps the visited match into a rule match of the top level
         * pattern, if the operator is a quantifier.
         */
        @Override
        protected boolean process(RuleMatch match) {
            RuleMatch newMatch = createRuleMatch();
            newMatch.getSubMatches().add(match);
            return this.visitor.visit(newMatch);
        }

        /**
         * Returns a new wrapped visitor, by either reusing this one 
         * if it has been disposed, or constructing a fresh one. 
         */
        public <T> RuleMatchWrapperVisitor<R> newInstance(
                Visitor<RuleMatch,R> visitor) {
            if (isDisposed()) {
                this.visitor = visitor;
                resurrect();
                return this;
            } else {
                return new RuleMatchWrapperVisitor<R>(visitor);
            }
        }

        private Visitor<RuleMatch,R> visitor;
    }
}
