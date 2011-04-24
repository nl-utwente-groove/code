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
import groove.trans.ForallCondition;
import groove.trans.HostEdge;
import groove.trans.HostNode;
import groove.trans.NotCondition;
import groove.trans.Rule;
import groove.trans.RuleEdge;
import groove.trans.RuleElement;
import groove.trans.RuleMatch;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;
import groove.trans.SPORule;
import groove.util.Fixable;
import groove.util.Visitor;
import groove.util.Visitor.Collector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Encoding of a condition match as a tree structure.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TreeMatch implements Fixable {
    /**
     * Constructs a match for a given condition, based on
     * a given pattern map for that condition.
     */
    public TreeMatch(Condition condition, RuleToHostMap patternMap) {
        this.condition = condition;
        this.patternMap = patternMap;
    }

    /** Returns the condition of this match. */
    public final Condition getCondition() {
        return this.condition;
    }

    /** Returns the pattern map of this match. */
    public final RuleToHostMap getPatternMap() {
        return this.patternMap;
    }

    /** Returns the current submatches of this match. */
    private final Set<TreeMatch> getSubMatches() {
        return this.subMatches;
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
        assert visitor.isContinue();
        if (!(getCondition() instanceof Rule)) {
            throw new UnsupportedOperationException();
        }
        int subMatchCount = getSubMatches().size();
        if (subMatchCount == 0) {
            visitor.visit(createRuleMatch());
        } else {
            @SuppressWarnings("unchecked")
            List<RuleMatch>[] matrix = new List[subMatchCount];
            int[] rowSize = new int[subMatchCount];
            int resultSize = computeForallMatchMatrix(matrix, rowSize);
            if (resultSize > 0) {
                int index[] = new int[subMatchCount];
                do {
                    RuleMatch match = createRuleMatch();
                    for (int row = 0; row < subMatchCount; row++) {
                        match.getSubMatches().add(matrix[row].get(index[row]));
                    }
                    if (!visitor.visit(match)) {
                        break;
                    }
                } while (incVector(index, rowSize));
            }
        }
        return visitor.getResult();
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
        if (!(getCondition() instanceof Rule)) {
            throw new UnsupportedOperationException();
        }
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
                int index[] = new int[subMatchCount];
                do {
                    RuleMatch match = createRuleMatch();
                    for (int row = 0; row < subMatchCount; row++) {
                        match.getSubMatches().add(matrix[row].get(index[row]));
                    }
                    result.add(match);
                } while (incVector(index, rowSize));
            }
        }
        return result;
    }

    /**
     * Computes the matrix of which the rows are the {@link #toForallMatchSet()}
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
            List<RuleMatch> row = subMatch.toForallMatchSet();
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

    /** 
     * Converts this tree match to an array of rule match lists.
     * Each element of the array contains one element for each sub-match 
     * of this tree match.
     * This operation is only allowed if the condition is a {@link NotCondition}
     * or {@link ForallCondition}.
     * @throws UnsupportedOperationException if the condition of this
     * tree match is not of the correct type.
     */
    public List<RuleMatch> toForallMatchSet() {
        setFixed();
        if (getCondition() instanceof Rule) {
            throw new UnsupportedOperationException();
        }
        List<RuleMatch> result = new ArrayList<RuleMatch>();
        Collector<RuleMatch,?> collector = Visitor.newCollector(result);
        for (TreeMatch subMatch : getSubMatches()) {
            subMatch.traverseRuleMatches(collector);
        }
        collector.dispose();
        return result;
        //        int subMatchCount = getSubMatches().size();
        //        if (subMatchCount == 0) {
        //            if (getCondition() instanceof ForallCondition
        //                && ((ForallCondition) getCondition()).isPositive()) {
        //                result = new List[0];
        //            } else {
        //                result = new List[] {Collections.emptyList()};
        //            }
        //        } else {
        //            int resultSize = 1;
        //            List<RuleMatch>[] matrix = new List[subMatchCount];
        //            int[] rowSize = new int[subMatchCount];
        //            int i = 0;
        //            for (TreeMatch subMatch : getSubMatches()) {
        //                List<RuleMatch> row = subMatch.toRuleMatchSet();
        //                matrix[i] = row;
        //                resultSize *= row.size();
        //                if (resultSize == 0) {
        //                    break;
        //                }
        //                rowSize[i] = row.size();
        //                i++;
        //            }
        //            result = new List[resultSize];
        //            if (resultSize > 0) {
        //                int index[] = new int[subMatchCount];
        //                i = 0;
        //                do {
        //                    List<RuleMatch> matches =
        //                        new ArrayList<RuleMatch>(subMatchCount);
        //                    for (int row = 0; row < subMatchCount; row++) {
        //                        matches.add(matrix[row].get(index[row]));
        //                    }
        //                    result[i] = matches;
        //                } while (incVector(index, rowSize));
        //            }
        //        }
        //        return result;
    }

    /** Callback factory method for rule matches base don this tree match. */
    private RuleMatch createRuleMatch() {
        return new RuleMatch((SPORule) getCondition(), getPatternMap());
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
        int patternHashCode = 1;
        if (getCondition() instanceof Rule) {
            // only the anchor images matter to equality of the match
            Map<?,?> nodeMap = getPatternMap().nodeMap();
            Map<?,?> edgeMap = getPatternMap().edgeMap();
            RuleElement[] anchor = ((SPORule) getCondition()).anchor();
            for (int i = 0; i < anchor.length; i++) {
                RuleElement element = anchor[i];
                Map<?,?> map = element instanceof RuleNode ? nodeMap : edgeMap;
                patternHashCode =
                    prime * patternHashCode + map.get(element).hashCode();
            }
        }
        result = prime * result + patternHashCode;
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
        if (!other.getCondition().equals(getCondition())) {
            return false;
        }
        if (getSubMatches().size() != other.getSubMatches().size()) {
            return false;
        }
        if (getCondition() instanceof Rule) {
            // only the anchor images matter to equality of the match
            RuleElement[] anchor = ((SPORule) getCondition()).anchor();
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
                    element instanceof RuleNode ? otherNodeMap : otherEdgeMap;
                if (!myMap.get(element).equals(otherMap.get(element))) {
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

    private final Condition condition;
    private final RuleToHostMap patternMap;
    private final Set<TreeMatch> subMatches = new LinkedHashSet<TreeMatch>();
    private int hashCode;
}
