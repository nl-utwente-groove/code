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
abstract public class AbstractMatch implements Match, Cloneable {
    public Collection<HostEdge> getEdgeValues() {
        Set<HostEdge> result = new HashSet<HostEdge>();
        for (Match subMatch : this.subMatches) {
            result.addAll(subMatch.getEdgeValues());
        }
        return result;
    }

    public Collection<HostNode> getNodeValues() {
        Set<HostNode> result = new HashSet<HostNode>();
        for (Match subMatch : this.subMatches) {
            result.addAll(subMatch.getNodeValues());
        }
        return result;
    }

    @Override
    public Collection<RuleMatch> getSubMatches() {
        return this.subMatches;
    }

    @Override
    public void addSubMatch(Match match) {
        // flatten pure composite matches
        if (match instanceof CompositeMatch) {
            this.subMatches.addAll(match.getSubMatches());
        } else {
            assert match instanceof RuleMatch;
            this.subMatches.add((RuleMatch) match);
        }
    }

    @Override
    public Collection<? extends Match> addSubMatchChoice(
            Iterable<? extends Match> choices) {
        Collection<Match> result = new ArrayList<Match>();
        Iterator<? extends Match> choiceIter = choices.iterator();
        while (choiceIter.hasNext()) {
            Match choice = choiceIter.next();
            Match copy = choiceIter.hasNext() ? clone() : this;
            copy.addSubMatch(choice);
            result.add(copy);
        }
        return result;
    }

    /** Equality is determined by rule and element map. */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Match
            && ((Match) obj).getSubMatches().equals(getSubMatches());
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
    protected AbstractMatch clone() {
        AbstractMatch result = createMatch();
        result.subMatches.addAll(getSubMatches());
        return result;
    }

    /** Callback factory method for a cloned match. */
    abstract protected AbstractMatch createMatch();

    /** Computes a value for the hash code. */
    protected int computeHashCode() {
        return getSubMatches().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Composite match");
    }

    /** The map constituting the match. */
    private final Collection<RuleMatch> subMatches =
        new java.util.LinkedHashSet<RuleMatch>();
    /** The (pre-computed) hash code of this match. */
    private int hashCode;
}
