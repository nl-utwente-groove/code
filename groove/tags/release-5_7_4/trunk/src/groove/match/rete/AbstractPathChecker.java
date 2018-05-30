/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.match.rete;

import groove.automaton.RegExpr;
import groove.automaton.RegExpr.Empty;
import groove.automaton.RegExpr.Neg;
import groove.automaton.RegExpr.Star;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleElement;
import groove.grammar.rule.RuleFactory;
import groove.grammar.rule.RuleLabel;
import groove.grammar.rule.RuleNode;

import java.util.HashMap;
import java.util.List;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public abstract class AbstractPathChecker extends ReteNetworkNode implements
        ReteStateSubscriber {

    /**
     * The static pattern representing this path's regular expression edge.
     */
    protected RuleEdge[] pattern;

    /**
     * The regular path expression checked by this checker node
     */
    protected RegExpr expression;

    /**
     * Determines if the path matches produced by this
     * checker should have the same end and starting node.
     */
    protected final boolean loop;

    /** Cache of representative path matches. */
    private final PathMatchCache cache;

    /**
     * Creates a path checker node based on a given regular expression 
     * and a flag that determines if this checker is loop path checker.
     */
    public AbstractPathChecker(ReteNetwork network, RegExpr expression,
            boolean isLoop) {
        super(network);
        assert (network != null) && (expression != null);
        this.expression = expression;
        RuleFactory f = RuleFactory.newInstance();
        RuleNode n1 = f.createNode();
        RuleNode n2 = (isLoop) ? n1 : f.createNode();
        this.pattern =
            new RuleEdge[] {f.createEdge(n1, new RuleLabel(expression), n2)};
        this.loop = isLoop;
        this.cache = new PathMatchCache();
        this.getOwner().getState().subscribe(this);
    }

    @Override
    public RuleElement[] getPattern() {
        return this.pattern;
    }

    /**
     * @return The regular expression object associated with this checker.
     */
    public RegExpr getExpression() {
        return this.expression;
    }

    /**
     * @return <code>true</code> if this checker node
     * always generates positive matches, i.e. matches
     * which correspond with actual series of edges with concrete
     * end points. The {@link Empty} path operator, 
     * the kleene ({@link Star}) operator, and the negation
     * operator {@link Neg}) are operators that sometimes/always 
     * generate non-positive matches.
     */
    public boolean isPositivePathGenerator() {
        return this.getExpression().isAcceptsEmptyWord()
            || (this.getExpression().getNegOperand() != null);
    }

    @Override
    public void receive(ReteNetworkNode source, int repeatIndex,
            AbstractReteMatch match) {
        assert match instanceof RetePathMatch;
        this.receive(source, repeatIndex, (RetePathMatch) match);
    }

    /**
     * Should be called by the antecedents to hand in a new match 
     * @param source The antecedent that is calling this method
     * @param repeatedIndex The counter index in case the given <code>source</code>
     * occurs more than once in the list of this node's antecedents.
     * @param newMatch The match produced by the antecedent. 
     */
    public abstract void receive(ReteNetworkNode source, int repeatedIndex,
            RetePathMatch newMatch);

    @Override
    public boolean equals(ReteNetworkNode node) {
        return (this == node)
            || ((node instanceof AbstractPathChecker)
                && this.getOwner().equals(node.getOwner()) && this.expression.equals(((AbstractPathChecker) node).getExpression()));
    }

    @Override
    public int size() {
        return -this.getExpression().getOperands().size();
    }

    @Override
    public String toString() {
        return "- Path-checker for: " + this.getExpression().toString();
    }

    /** Indicates if path matches must have the same start and end node. */
    public boolean isLoop() {
        return this.loop;
    }

    /**
     * Passes down a given match to the successors.
     * @param m the given match
     */

    @Override
    protected void passDownMatchToSuccessors(AbstractReteMatch m) {
        ReteNetworkNode previous = null;
        int repeatCount = 0;

        RetePathMatch ent = null;
        if (!((RetePathMatch) m).isEmpty()) {
            ent = this.cache.addMatch((RetePathMatch) m);
        }
        for (ReteNetworkNode n : this.getSuccessors()) {

            repeatCount = (n != previous) ? 0 : (repeatCount + 1);
            if ((n instanceof AbstractPathChecker)
                || ((RetePathMatch) m).isEmpty()) {
                n.receive(this, repeatCount, m);
            } else if (ent != null) {
                n.receive(this, repeatCount, ent);
            }
            previous = n;
        }
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

    @Override
    public List<? extends Object> initialize() {
        return null;
    }

    @Override
    public void updateBegin() {
        //Do nothing
    }

    @Override
    public void updateEnd() {
        //Do nothing        
    }

    /**
     * Entry in the path match cache, holding a representative match and a
     * count of the number of comparable instances.
     * @author Arend Rensink
     * @version $Revision $
     */
    protected static class CacheEntry {
        private final RetePathMatch representative;
        private int count;

        /** Constructs a new cache entry, for a given path match representative.
         * The count is initially set to 1. 
         */
        public CacheEntry(RetePathMatch rep) {
            this.representative = rep;
            this.count = 1;
        }

        /** Increments the count of this entry. */
        public void increment() {
            this.count++;
        }

        /** 
         * Decrements the count of this entry.
         * @return {@code true} if the count is now 0
         */
        public boolean decrement() {
            assert this.count >= 0;
            this.count--;
            return this.count == 0;
        }

        /** Returns the number of matches corresponding to this cache entry. */
        public int getCount() {
            return this.count;
        }

        /** Returns the representative match of this cache entry. */
        public RetePathMatch getRepresentative() {
            return this.representative;
        }

        @Override
        public String toString() {
            return String.format("Cache Entry key for %s. count: %d",
                this.representative.getCacheKey(), this.count);
        }
    }

    /**
     * A cache of path matches produced by a path-checker
     * This cache is used to keep track of path matches
     * that are identical in terms of start and end
     * nodes and the path checker just passes one representative
     * for each group of identical path matches to its
     * non-path-checker successors for efficiency purposes.
     * 
     * @author Arash Jalali
     * @version $Revision $
     */
    public static class PathMatchCache implements DominoEventListener {

        private HashMap<Object,CacheEntry> entries =
            new HashMap<>();

        @Override
        public void matchRemoved(AbstractReteMatch match) {
            RetePathMatch pm = this.removeMatch((RetePathMatch) match);
            if (pm != null) {
                pm.dominoDelete(null);
            }
        }

        /** Clears the cache. */
        public void clear() {
            this.entries.clear();
        }

        /**
         * Adds a path match to the cache.
         * Returns the added path match if it is the first with
         * the given key, or {@code null} otherwise.
         * @param pm the match to be added
         * @return Either {@code pm} or {@code null}, depending
         * on whether {@code pm} is the first path match with the 
         * given key.
         */
        public RetePathMatch addMatch(RetePathMatch pm) {
            RetePathMatch result = null;
            Object pair = pm.getCacheKey();
            CacheEntry entry = this.entries.get(pair);
            if (entry == null) {
                result = RetePathMatch.duplicate(pm);
                this.entries.put(pair, entry = new CacheEntry(result));
            } else {
                entry.increment();
            }
            pm.addDominoListener(this);
            return result;
        }

        /**
         * Removes a match from the cache.
         * @param pm the match to be removed
         * @return the representative path match, if the last instance was removed;
         * {@code null} otherwise
         */
        public RetePathMatch removeMatch(RetePathMatch pm) {
            RetePathMatch result = null;
            Object pair = pm.getCacheKey();
            CacheEntry entry = this.entries.get(pair);
            assert entry != null;
            if (entry.decrement()) {
                this.entries.remove(pair);
                result = entry.getRepresentative();
            }
            return result;
        }

        @Override
        public String toString() {
            return String.format("Path Cache size=%d", this.entries.size());
        }
    }

}
