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

import groove.rel.RegExpr;
import groove.rel.RegExpr.Empty;
import groove.rel.RegExpr.Neg;
import groove.rel.RegExpr.Star;
import groove.rel.Valuation;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleElement;
import groove.trans.RuleFactory;
import groove.trans.RuleLabel;
import groove.trans.RuleNode;
import groove.util.Duo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    protected final PathMatchCache cache;

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
        RuleNode n1 = f.createNode(f.getMaxNodeNr());
        RuleNode n2 = (isLoop) ? n1 : f.createNode(f.getMaxNodeNr());
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
            || ((node instanceof WildcardPathChecker)
                && this.getOwner().equals(node.getOwner()) && this.expression.equals(((WildcardPathChecker) node).getExpression()));
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
        int repeatedSuccessorIndex = 0;

        CacheEntry ent = null;
        if (!((RetePathMatch) m).isEmpty()) {
            ent = this.cache.addMatch((RetePathMatch) m);
        }
        for (ReteNetworkNode n : this.getSuccessors()) {

            repeatedSuccessorIndex =
                (n != previous) ? 0 : (repeatedSuccessorIndex + 1);
            if ((n instanceof AbstractPathChecker)
                || ((RetePathMatch) m).isEmpty()) {
                n.receive(this, repeatedSuccessorIndex, m);
            } else if (ent.count == 1) {
                n.receive(this, repeatedSuccessorIndex, ent.representative);
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

    protected static class CacheEntry {
        protected RetePathMatch representative;
        protected int count;

        public CacheEntry(RetePathMatch rep) {
            this.representative = rep;
            this.count = 0;
        }

        public int getCount() {
            return this.count;
        }

        public RetePathMatch getRepresentative() {
            return this.representative;
        }

        @Override
        public String toString() {
            return String.format("Cache Entry key for %s. count: %d",
                new EndPointPair(this.representative), this.count);
        }
    }

    /** Pair of host nodes, serving as a key in the path match cache. */
    protected static class EndPointPair extends Duo<HostNode> {
        /** Constructs a new pair from a given match. */
        public EndPointPair(RetePathMatch pm) {
            super(pm.start(), pm.end());
            assert !pm.isEmpty();
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

        private HashMap<EndPointPair,Set<CacheEntry>> entries =
            new HashMap<EndPointPair,Set<CacheEntry>>();

        @Override
        public void matchRemoved(AbstractReteMatch match) {
            CacheEntry ent = this.removeMatch((RetePathMatch) match);
            if (ent.count == 0) {
                ent.representative.dominoDelete(null);
            }
        }

        public void clear() {
            this.entries.clear();
        }

        /**
         * Adds a path match to the cache.
         * 
         * @param pm
         * @return The number of patches with the same
         * start and end nodes as <code>pm</code> (including 
         * <code>pm</code>).
         */
        public CacheEntry addMatch(RetePathMatch pm) {
            EndPointPair pair = new EndPointPair(pm);
            Set<CacheEntry> ents = this.entries.get(pair);
            CacheEntry ent = findEntryWithCompatibleValuation(ents, pm);
            if (ents == null) {
                ents = new HashSet<CacheEntry>();
                this.entries.put(pair, ents);
            }
            if (ent == null) {
                ent = new CacheEntry(RetePathMatch.duplicate(pm));
                ents.add(ent);
            }
            pm.addDominoListener(this);
            ent.count++;
            return ent;
        }

        private CacheEntry findEntryWithCompatibleValuation(
                Set<CacheEntry> ents, RetePathMatch pm) {
            CacheEntry result = null;
            Valuation v = pm.getValuation();
            if (ents != null) {
                for (CacheEntry e : ents) {
                    Valuation vr = e.representative.getValuation();
                    if (vr == v
                        || ((v != null) && (vr != null) && v.equals(vr))) {
                        result = e;
                        break;
                    }
                }
            }
            return result;
        }

        /**
         * Removes a match from the cache
         * @param pm
         */
        public CacheEntry removeMatch(RetePathMatch pm) {
            EndPointPair pair = new EndPointPair(pm);
            Set<CacheEntry> ents = this.entries.get(pair);
            assert ents != null;
            CacheEntry ent = findEntryWithCompatibleValuation(ents, pm);
            assert ent != null;
            ent.count--;
            if (ent.count == 0) {
                ents.remove(ent);
                if (ents.isEmpty()) {
                    this.entries.remove(pair);
                }
            }
            return ent;
        }

        @Override
        public String toString() {
            return String.format("Path Cache size=%d", this.entries.size());
        }
    }

}
