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
 * $Id: ForallCondition.java,v 1.10 2007-11-29 12:52:09 rensink Exp $
 */
package groove.trans;

import groove.graph.Graph;
import groove.graph.GraphShape;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class ForallCondition extends AbstractCondition<CompositeMatch> {
    /** Constructs an instance based on a given target and root map. */
    public ForallCondition(Graph target, NodeEdgeMap rootMap, NameLabel name,
            SystemProperties properties) {
        super(target, rootMap, name, properties);
    }

    @Override
    final public Collection<CompositeMatch> getMatches(GraphShape host,
            NodeEdgeMap contextMap) {
        Collection<CompositeMatch> result = null;
        reporter.start(GET_MATCHING);
        testFixed(true);
        // lift the pattern match to a pre-match of this condition's target
        final VarNodeEdgeMap anchorMap;
        if (contextMap == null) {
            testGround();
            anchorMap = EMPTY_ANCHOR_MAP;
        } else {
            anchorMap = createAnchorMap(contextMap);
        }
        if (anchorMap == null) {
            result = Collections.emptySet();
        } else {
            result =
                computeMatches(host, getMatcher().getMatchIter(host, anchorMap));
        }
        reporter.stop();
        return result;
    }

    /**
     * Returns the matches of this condition, given an iterator of match maps.
     */
    Collection<CompositeMatch> computeMatches(GraphShape host,
            Iterator<VarNodeEdgeMap> matchMapIter) {
        Collection<CompositeMatch> result = new ArrayList<CompositeMatch>();
        // add the empty match if the condition is not positive
        if (!this.positive) {
            result.add(new CompositeMatch());
        }
        boolean first = this.positive;
        while (matchMapIter.hasNext() && (first || !result.isEmpty())) {
            // add the empty match if the condition is positive
            if (first) {
                result.add(new CompositeMatch());
                first = false;
            }
            VarNodeEdgeMap matchMap = matchMapIter.next();
            Collection<Match> subResults = new ArrayList<Match>();
            for (Condition subCondition : getSubConditions()) {
                if (subCondition instanceof PositiveCondition) {
                    Iterator<? extends Match> subResultIter =
                        subCondition.getMatchIter(host, matchMap);
                    while (subResultIter.hasNext()) {
                        subResults.add(subResultIter.next());
                    }
                }
            }
            Collection<CompositeMatch> newResult =
                new ArrayList<CompositeMatch>();
            for (CompositeMatch current : result) {
                newResult.addAll(current.addSubMatchChoice(subResults));
            }
            result = newResult;
        }
        return result;
    }

    /**
     * This implementation iterates over the result of
     * {@link #getMatches(GraphShape, NodeEdgeMap)}.
     */
    @Override
    public Iterator<CompositeMatch> computeMatchIter(GraphShape host,
            Iterator<VarNodeEdgeMap> matchMapIter) {
        return computeMatches(host, matchMapIter).iterator();
    }

    @Override
    public String toString() {
        return "Universal " + super.toString();
    }

    /** Sets this universal condition to positive. */
    public void setPositive() {
        this.positive = true;
    }

    /**
     * Indicates if this condition is positive. A universal condition is
     * positive if it cannot be vacuously fulfilled; i.e., there must always be
     * at least one match.
     */
    public boolean isPositive() {
        return this.positive;
    }

    /**
     * Flag indicating whether the condition is positive, i.e., cannot be
     * vacuously true.
     */
    private boolean positive;

    /**
     * Turns a collection of iterators into an iterator of collections. The
     * collections returned by the resulting iterator are tuples of elements
     * from the original iterators.
     */
    static public class TransposedIterator<X> implements
            Iterator<Collection<X>> {
        /** Creates an iterator from a given collection of iterators. */
        public TransposedIterator(Collection<Iterator<X>> matrix) {
            this.matrix = new ArrayList<List<X>>();
            this.solution = new ArrayList<X>();
            for (Iterator<X> iter : matrix) {
                List<X> row = new ArrayList<X>();
                while (iter.hasNext()) {
                    row.add(iter.next());
                }
                this.matrix.add(row);
                this.solution.add(null);
            }
            this.rowCount = matrix.size();
            this.columnIxs = new int[this.rowCount];
            this.rowIx = 0;
        }

        public boolean hasNext() {
            int rowIx = this.rowIx;
            while (rowIx >= 0 && rowIx < this.rowCount) {
                List<X> row = this.matrix.get(rowIx);
                int columnIx = this.columnIxs[rowIx];
                if (columnIx < row.size()) {
                    this.solution.set(rowIx, row.get(columnIx));
                    this.columnIxs[rowIx] = columnIx + 1;
                    rowIx++;
                } else {
                    this.columnIxs[rowIx] = 0;
                    rowIx--;
                }
            }
            this.rowIx = rowIx;
            return rowIx >= 0;
        }

        public Collection<X> next() {
            if (hasNext()) {
                this.rowIx--;
                return new ArrayList<X>(this.solution);
            } else {
                throw new UnsupportedOperationException();
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        /** The matrix to be transposed. */
        private final List<List<X>> matrix;
        /** The number of rows in {@link #matrix}. */
        private final int rowCount;
        /** Structure in which the solution is built up. */
        private final List<X> solution;
        /**
         * index in {@link #matrix} indicating to where the current solution has
         * been built.
         */
        private int rowIx;
        /** The next element to be returned by <code>next</code>, if any. */
        private final int[] columnIxs;
    }
}
