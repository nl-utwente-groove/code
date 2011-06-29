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
import groove.trans.Proof;
import groove.trans.RuleEdge;
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
            op.hasPattern() ? new ProofWrapperCollector(null)
                    : Visitor.<Proof,List<Proof>>newCollector();
        assert op.hasPattern() == (patternMap != null && condition.getOp() == op);
    }

    /** Returns the condition of this match. */
    public final Condition getCondition() {
        return this.condition;
    }

    /**
     * Returns the operator of this tree match.
     * Note that this may be different from the operator of the condition:
     * in particular, the match operator may be {@link groove.trans.Condition.Op#AND} when
     * the condition is {@link groove.trans.Condition.Op#FORALL}, or
     * {@link groove.trans.Condition.Op#OR} when the condition is
     * {@link groove.trans.Condition.Op#EXISTS}.
     */
    public final Condition.Op getOp() {
        return this.op;
    }

    /** Returns the pattern map of this match. */
    public final RuleToHostMap getPatternMap() {
        return this.patternMap;
    }

    /** Returns the current submatches of this match. */
    public final Collection<TreeMatch> getSubMatches() {
        return this.subMatches;
    }

    /** Callback factory method for the submatch collection object. */
    private Collection<TreeMatch> createSubMatches(Condition.Op op) {
        // the collection is a bag if the operator is conjunctive,
        // and a set if it is disjunctive
        // Since the matches are unordered, for more efficient quality test
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
    public <R> R traverseProofs(Visitor<Proof,R> visitor) {
        setFixed();
        switch (this.op) {
        case FORALL:
        case OR:
            traverseOrProofs(visitor);
            break;
        case EXISTS:
        case AND:
            traverseAndProofs(visitor);
            break;
        case TRUE:
            visitor.visit(Proof.TrueProof);
            break;
        case FALSE:
            break;
        default:
            assert false;
            return null;
        }
        return visitor.getResult();
    }

    /** Traverses the matches for a conjunctive tree match. */
    private void traverseAndProofs(Visitor<Proof,?> visitor) {
        int subMatchCount = getSubMatches().size();
        if (subMatchCount == 0) {
            assert this.op == Op.EXISTS;
            visitor.visit(createProof());
        } else {
            @SuppressWarnings("unchecked")
            List<Proof>[] matrix = new List[subMatchCount];
            int[] rowSize = new int[subMatchCount];
            int resultSize = computeProofMatrix(matrix, rowSize);
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
    private void traverseOrProofs(Visitor<Proof,?> visitor) {
        Visitor<Proof,?> subVisitor;
        if (getOp().hasPattern()) {
            // this tree match is for an existential or universal condition
            subVisitor = this.wrapper.newInstance(visitor);
        } else {
            subVisitor = visitor;
        }
        for (TreeMatch subMatch : this.subMatches) {
            subMatch.traverseProofs(subVisitor);
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
    public List<Proof> toProofSet() {
        setFixed();
        switch (this.op) {
        case FORALL:
        case OR:
            return toOrProofSet();
        case EXISTS:
        case AND:
            return toAndProofSet();
        case TRUE:
            return Collections.singletonList(Proof.TrueProof);
        case FALSE:
            return Collections.emptyList();
        default:
            assert false;
            return null;
        }
    }

    /** 
     * Constructs the list of proofs from this tree match.
     * if the operator of this tree match is conjunctive.
     */
    private List<Proof> toAndProofSet() {
        List<Proof> result;
        int subMatchCount = getSubMatches().size();
        if (subMatchCount == 0) {
            result = new ArrayList<Proof>(1);
            result.add(createProof());
        } else {
            @SuppressWarnings("unchecked")
            List<Proof>[] matrix = new List[subMatchCount];
            int[] rowSize = new int[subMatchCount];
            int resultSize = computeProofMatrix(matrix, rowSize);
            if (resultSize == 0) {
                result = Collections.emptyList();
            } else {
                result = new ArrayList<Proof>(resultSize);
                Visitor<Proof,?> collector = Visitor.newCollector(result);
                traverseMatrix(matrix, rowSize, collector);
                collector.dispose();
            }
        }
        return result;
    }

    /**
     * Constructs the proofs constructed from this tree match,
     * if the operator of this tree match is disjunctive.
     */
    private List<Proof> toOrProofSet() {
        List<Proof> result = new ArrayList<Proof>();
        Visitor<Proof,?> collector = this.collector.newInstance(result);
        for (TreeMatch subMatch : this.subMatches) {
            subMatch.traverseProofs(collector);
        }
        collector.dispose();
        return result;
    }

    /** 
     * Computes and traverses all rule matches
     * consisting of one sub-match from each row of a given matrix.
     */
    private void traverseMatrix(List<Proof>[] matrix, int[] rowSize,
            Visitor<Proof,?> visitor) {
        int rowCount = rowSize.length;
        int index[] = new int[rowCount];
        do {
            Proof proof = createProof();
            Collection<Proof> subMatches = proof.getSubProofs();
            for (int row = 0; row < rowCount; row++) {
                Proof subProof = matrix[row].get(index[row]);
                if (subProof.isComposite()) {
                    subMatches.addAll(subProof.getSubProofs());
                } else {
                    subMatches.add(subProof);
                }
            }
            // stop the traversal if the visitor asks for it
            if (!visitor.visit(proof)) {
                break;
            }
        } while (incVector(index, rowSize));
    }

    /**
     * Computes the matrix of which the rows are the {@link #toProofSet()}
     * arrays of the submatches.
     * The construction stops prematurely if an empty row is found.
     * @param matrix the matrix of submatches. Should be initialised to 
     * an array of the correct length (the number of submatches)
     * @param rowSize the number of elements per matrix row. Should 
     * be initialised to an array of the correct length (the number of submatches)
     * @return the product of the row sizes
     */
    private int computeProofMatrix(List<Proof>[] matrix, int[] rowSize) {
        int resultSize = 1;
        int i = 0;
        for (TreeMatch subMatch : getSubMatches()) {
            List<Proof> row = subMatch.toProofSet();
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

    /** Callback factory method for proofs base don this tree match. */
    private Proof createProof() {
        return new Proof(getCondition(), getPatternMap());
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
        if (this.op.hasPattern()) {
            int patternHashCode = 1;
            if (getCondition().hasRule()) {
                // only the anchor images matter to equality of the match
                Map<?,?> nodeMap = getPatternMap().nodeMap();
                RuleNode[] anchorNodes =
                    getCondition().getRule().getAnchorNodes();
                for (int i = 0; i < anchorNodes.length; i++) {
                    patternHashCode =
                        prime * patternHashCode
                            + nodeMap.get(anchorNodes[i]).hashCode();
                }
                Map<?,?> edgeMap = getPatternMap().edgeMap();
                RuleEdge[] anchorEdges =
                    getCondition().getRule().getAnchorEdges();
                for (int i = 0; i < anchorEdges.length; i++) {
                    patternHashCode =
                        prime * patternHashCode
                            + edgeMap.get(anchorEdges[i]).hashCode();
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
        if (getOp().hasPattern()) {
            if (getCondition().hasRule()) {
                // only the anchor images matter to equality of the match
                Map<RuleNode,? extends HostNode> myNodeMap =
                    getPatternMap().nodeMap();
                Map<RuleNode,? extends HostNode> otherNodeMap =
                    other.getPatternMap().nodeMap();
                RuleNode[] anchorNodes =
                    getCondition().getRule().getAnchorNodes();
                for (int i = 0; i < anchorNodes.length; i++) {
                    RuleNode element = anchorNodes[i];
                    if (!myNodeMap.get(element).equals(
                        otherNodeMap.get(element))) {
                        return false;
                    }
                }
                Map<RuleEdge,? extends HostEdge> myEdgeMap =
                    getPatternMap().edgeMap();
                Map<RuleEdge,? extends HostEdge> otherEdgeMap =
                    other.getPatternMap().edgeMap();
                RuleEdge[] anchorEdges =
                    getCondition().getRule().getAnchorEdges();
                for (int i = 0; i < anchorEdges.length; i++) {
                    RuleEdge element = anchorEdges[i];
                    if (!myEdgeMap.get(element).equals(
                        otherEdgeMap.get(element))) {
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
        result.append(" (");
        result.append(getOp());
        result.append("): ");
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
    private final Visitor.Collector<Proof,List<Proof>> collector;
    @SuppressWarnings("rawtypes")
    private final ProofWrapperVisitor wrapper = new ProofWrapperVisitor();
    private int hashCode;

    /** Collector class for rule matches
     * that wraps its visited matches into a rule match of this condition.
     */
    private class ProofWrapperCollector extends
            Visitor.Collector<Proof,List<Proof>> {
        /** Constructor for a prototype object. */
        public ProofWrapperCollector() {
            super(null);
        }

        private ProofWrapperCollector(List<Proof> collection) {
            super(collection);
        }

        /** 
         * Wraps the visited match into a rule match of the top level
         * pattern, if the operator is a quantifier.
         */
        @Override
        protected boolean process(Proof match) {
            Proof newMatch = createProof();
            newMatch.getSubProofs().add(match);
            return super.process(newMatch);
        }

        @Override
        protected Collector<Proof,List<Proof>> createInstance(
                List<Proof> collection, Property<Proof> property) {
            return new ProofWrapperCollector(collection);
        }
    }

    /** Visitor class for rule matches
     * that wraps its visited matches into a rule match of this condition
     * and then passes on the match to an inner visitor.
     */
    private class ProofWrapperVisitor<R> extends Visitor<Proof,R> {
        /** Constructor for a prototype object. */
        public ProofWrapperVisitor() {
            this(null);
        }

        private ProofWrapperVisitor(Visitor<Proof,R> visitor) {
            this.visitor = visitor;
        }

        /** 
         * Wraps the visited match into a rule match of the top level
         * pattern, if the operator is a quantifier.
         */
        @Override
        protected boolean process(Proof match) {
            Proof newMatch = createProof();
            newMatch.getSubProofs().add(match);
            return this.visitor.visit(newMatch);
        }

        /**
         * Returns a new wrapped visitor, by either reusing this one 
         * if it has been disposed, or constructing a fresh one. 
         */
        public <T> ProofWrapperVisitor<R> newInstance(Visitor<Proof,R> visitor) {
            if (isDisposed()) {
                this.visitor = visitor;
                resurrect();
                return this;
            } else {
                return new ProofWrapperVisitor<R>(visitor);
            }
        }

        private Visitor<Proof,R> visitor;
    }
}
