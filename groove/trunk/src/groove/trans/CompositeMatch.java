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
 * $Id: CompositeMatch.java,v 1.6 2008-01-30 09:32:37 iovka Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Node;
import groove.rel.VarNodeEdgeMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Match wrapping a set of sub-matches.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CompositeMatch implements Match, Cloneable {
    /** Constructs a match wrapping a given element map. */
    public CompositeMatch(VarNodeEdgeMap elementMap) {
        this.elementMap = elementMap;
        this.subMatches = new HashSet<Match>();
    }

    /** Constructs a match for a given {@link SPORule}. */
    public CompositeMatch() {
        this(null);
    }

    /** Returns the element map constituting the match. */
    public VarNodeEdgeMap getElementMap() {
        return this.elementMap;
    }

    public Collection<Edge> getEdgeValues() {
        Set<Edge> result =
            new HashSet<Edge>(getElementMap().edgeMap().values());
        for (Match subMatch : this.subMatches) {
            result.addAll(subMatch.getEdgeValues());
        }
        return result;
    }

    public Collection<Node> getNodeValues() {
        Set<Node> result =
            new HashSet<Node>(getElementMap().nodeMap().values());
        for (Match subMatch : this.subMatches) {
            result.addAll(subMatch.getNodeValues());
        }
        return result;
    }

    /** Returns the set of matches stored in this composite match. */
    public Collection<Match> getSubMatches() {
        return this.subMatches;
    }

    /** Adds a match to those stored in this composite match. */
    public void addSubMatch(Match match) {
        // flatten pure composite matches
        if (match.getClass() == CompositeMatch.class) {
            this.subMatches.addAll(((CompositeMatch) match).getSubMatches());
        } else {
            this.subMatches.add(match);
        }
    }

    /**
     * Returns a set of copies of this composite match, each augmented with an
     * additional sub-match taken from a given set of choices. For efficiency,
     * the last match in the result is actually a (modified) alias of this
     * object, meaning that no references to this object should be kept after
     * invoking this method.
     */
    public Collection<? extends CompositeMatch> addSubMatchChoice(
            Iterable<? extends Match> choices) {
        Collection<CompositeMatch> result = new ArrayList<CompositeMatch>();
        Iterator<? extends Match> choiceIter = choices.iterator();
        while (choiceIter.hasNext()) {
            Match choice = choiceIter.next();
            CompositeMatch copy = choiceIter.hasNext() ? clone() : this;
            copy.addSubMatch(choice);
            result.add(copy);
        }
        return result;
    }

    /** Equality is determined by rule and element map. */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CompositeMatch
            && ((CompositeMatch) obj).getElementMap().equals(getElementMap())
            && ((CompositeMatch) obj).getSubMatches().equals(getSubMatches());
    }

    @Override
    public int hashCode() {
        // pre-compute the value, if not yet done
        if (this.hashCode == 0) {
            this.hashCode = computeHashCode();
            if (this.hashCode == 0) {
                this.hashCode = 1;
            }
        }
        return this.hashCode;
    }

    @Override
    protected CompositeMatch clone() {
        CompositeMatch result = createMatch();
        result.hashCode = this.hashCode;
        result.subMatches.addAll(this.subMatches);
        return result;
    }

    /** Callback factory method for a cloned match. */
    protected CompositeMatch createMatch() {
        return new CompositeMatch();
    }

    /** Computes a value for the hash code. */
    protected int computeHashCode() {
        return getSubMatches().hashCode() ^ getElementMap().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Composite match");
    }

    /** The map constituting the match. */
    private final Collection<Match> subMatches;
    /** The (pre-computed) hash code of this match. */
    private int hashCode;
    /** The map constituting the match. */
    private final VarNodeEdgeMap elementMap;
}
