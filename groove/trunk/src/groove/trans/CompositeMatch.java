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
 * $Id: CompositeMatch.java,v 1.1 2007-10-03 16:08:40 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Node;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Match wrapping a set of sub-matches.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CompositeMatch implements Match {
    /** Constructs a match for a given {@link SPORule}. */
    public CompositeMatch() {
        this.matches = new HashSet<Match>();
    }

    public Collection<Edge> getEdgeValues() {
        Set<Edge> result = new HashSet<Edge>();
        for (Match subMatch: matches) {
            result.addAll(subMatch.getEdgeValues());
        }
        return result;
    }

    public Collection<Node> getNodeValues() {
        Set<Node> result = new HashSet<Node>();
        for (Match subMatch: matches) {
            result.addAll(subMatch.getNodeValues());
        }
        return result;
    }
    
    /** Returns the set of matches stored in this composite match. */
    public Collection<Match> getMatches() {
        return matches;
    }
    
    /** Adds a match to those stored in this composite match. */
    public void addMatch(Match match) {
        // flatten pure composite matches
        if (match.getClass() == CompositeMatch.class) {
            matches.addAll(((CompositeMatch) match).getMatches());
        } else {
            matches.add(match);
        }
    }
    
    /** Equality is determined by rule and element map. */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CompositeMatch
                && ((CompositeMatch) obj).getMatches().equals(getMatches());
    }

    @Override
    public int hashCode() {
        // pre-compute the value, if not yet done
        if (hashCode == 0) {
            hashCode = computeHashCode();
            if (hashCode == 0) {
                hashCode = 1;
            }
        }
        return hashCode;
    }
    
    /** Computes a value for the hash code. */
    protected int computeHashCode() {
        return getMatches().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Composite match");
    }

    /** The map constituting the match. */
    private final Collection<Match> matches;
    /** The (pre-computed) hash code of this match. */
    private int hashCode;
}
