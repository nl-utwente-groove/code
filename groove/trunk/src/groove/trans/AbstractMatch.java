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
    /** Constructs a match wrapping a given element map. */
    protected AbstractMatch(RuleToHostMap elementMap) {
        this.elementMap = elementMap;
    }

    /** Returns the element map constituting the match. */
    public RuleToHostMap getElementMap() {
        return this.elementMap;
    }

    public Collection<HostEdge> getEdgeValues() {
        Set<HostEdge> result =
            new HashSet<HostEdge>(getElementMap().edgeMap().values());
        for (Match subMatch : this.subMatches) {
            result.addAll(subMatch.getEdgeValues());
        }
        return result;
    }

    public Collection<HostNode> getNodeValues() {
        Set<HostNode> result =
            new HashSet<HostNode>(getElementMap().nodeMap().values());
        for (Match subMatch : this.subMatches) {
            result.addAll(subMatch.getNodeValues());
        }
        return result;
    }

    /** Returns the set of matches stored in this composite match. */
    public Collection<RuleMatch> getSubMatches() {
        return this.subMatches;
    }

    /** Adds a match to those stored in this composite match. */
    public void addSubMatch(Match match) {
        // flatten pure composite matches
        if (match instanceof CompositeMatch) {
            this.subMatches.addAll(((CompositeMatch) match).getSubMatches());
            this.elementMap.putAll(((CompositeMatch) match).getElementMap());
        } else {
            assert match instanceof RuleMatch;
            this.subMatches.add((RuleMatch) match);
        }
    }

    /**
     * Returns a set of copies of this composite match, each augmented with an
     * additional sub-match taken from a given set of choices. For efficiency,
     * the last match in the result is actually a (modified) alias of this
     * object, meaning that no references to this object should be kept after
     * invoking this method.
     */
    public Collection<? extends AbstractMatch> addSubMatchChoice(
            Iterable<? extends Match> choices) {
        Collection<AbstractMatch> result = new ArrayList<AbstractMatch>();
        Iterator<? extends Match> choiceIter = choices.iterator();
        while (choiceIter.hasNext()) {
            Match choice = choiceIter.next();
            AbstractMatch copy = choiceIter.hasNext() ? clone() : this;
            copy.addSubMatch(choice);
            result.add(copy);
        }
        return result;
    }

    /** Equality is determined by rule and element map. */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractMatch
            && ((AbstractMatch) obj).getElementMap().equals(getElementMap())
            && ((AbstractMatch) obj).getSubMatches().equals(getSubMatches());
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
        result.elementMap.putAll(getElementMap());
        return result;
    }

    /** Callback factory method for a cloned match. */
    abstract protected AbstractMatch createMatch();

    /** Computes a value for the hash code. */
    protected int computeHashCode() {
        return getSubMatches().hashCode() ^ getElementMap().hashCode();
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
    /** The map constituting the match. */
    private final RuleToHostMap elementMap;
}
