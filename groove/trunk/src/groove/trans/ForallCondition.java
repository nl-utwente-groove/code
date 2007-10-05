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
 * $Id: ForallCondition.java,v 1.2 2007-10-05 11:44:55 rensink Exp $
 */
package groove.trans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class ForallCondition extends AbstractCondition<CompositeMatch> {
    /**
     * @param pattern
     * @param name
     * @param properties
     */
    public ForallCondition(Morphism pattern, NameLabel name, SystemProperties properties) {
        super(pattern.cod(), pattern.elementMap(), name, properties);
    }

	@Override
    public Iterator<CompositeMatch> getMatchIter(Graph host, NodeEdgeMap contextMap) {
        Collection<CompositeMatch> result = new ArrayList<CompositeMatch>();
        List<Iterable<? extends Match>> subMatches = new ArrayList<Iterable<? extends Match>>();
        int subConditionCount = getSubConditions().size();
        List<Match> matchSet = new ArrayList<Match>();
        for (int i = 0; i < subConditionCount; i++) {
            subMatches.add(new ArrayList<Match>());
            matchSet.add(null);
        }
        Iterator<VarNodeEdgeMap> matchMapIter = getMatcher().getMatchIter(host, contextMap);
        while (matchMapIter.hasNext()) {
            VarNodeEdgeMap matchMap = matchMapIter.next();
            for (AbstractCondition<?> condition: getSubConditions()) {
                subMatches.add(condition.getMatches(host, matchMap));
            }
            Stack<Iterator<? extends Match>> subMatchIters = new Stack<Iterator<? extends Match>>();
            int i = 0;
            do {
                while (i >= 0 && i < subConditionCount) {
                    Iterator< ? extends Match> subMatchIter;
                    if (subMatchIters.size() <= i) {
                        subMatchIters.push(subMatchIter = subMatches.get(i).iterator());
                    } else {
                        subMatchIter = subMatchIters.peek();
                    }
                    if (subMatchIter.hasNext()) {
                        matchSet.set(i, subMatchIters.get(i).next());
                        i++;
                    } else {
                        subMatchIters.pop();
                        i--;
                    }
                }
                result.add(createMatch(matchSet));
            } while (i >= 0);
        }
        return result.iterator();
    }

    /** Creates a composite match on the basis if a set of matches of sub-conditions. */
    protected CompositeMatch createMatch(Collection<Match> subMatches) {
        CompositeMatch result = new CompositeMatch();
        for (Match match: subMatches) {
            result.addMatch(match);
        }
        return result;
    }
    
    /** 
     * Turns a collection of iterators into an iterator of collections.
     * The collections returned by the resulting iterator are tuples of elements from the
     * original iterators.
     */
    static public class TransposedIterator<X> implements Iterator<Collection<X>> {
        /** Creates an iterator from a given collection of iterators. */
        public TransposedIterator(Collection<Iterator<X>> matrix) {
            this.matrix = new ArrayList<List<X>>();
            this.solution = new ArrayList<X>();
            for (Iterator<X> iter: matrix) {
                List<X> row = new ArrayList<X>();
                while (iter.hasNext()) {
                    row.add(iter.next());
                }
                this.matrix.add(row);
                this.solution.add(null);
            }
            rowCount = matrix.size();
            columnIxs = new int[rowCount];
            rowIx = 0;
        }
        
        public boolean hasNext() {
            int rowIx = this.rowIx;
            while (rowIx >= 0 && rowIx < rowCount) {
                List<X> row = matrix.get(rowIx);
                int columnIx = columnIxs[rowIx];
                if (columnIx < row.size()) {
                    solution.set(rowIx, row.get(columnIx));
                    columnIxs[rowIx] = columnIx+1;
                    rowIx++;
                } else {
                    columnIxs[rowIx] = 0;
                    rowIx--;
                }
            }
            this.rowIx = rowIx;
            return rowIx >= 0;
        }
        
        public Collection<X> next() {
            if (hasNext()) {
                rowIx--;
                return new ArrayList<X>(solution);
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
        /** index in {@link #matrix} indicating to where the current solution has been built. */
        private int rowIx;
        /** The next element to be returned by <code>next</code>, if any. */
        private int[] columnIxs;
    }
}
