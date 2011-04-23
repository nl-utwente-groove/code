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

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.graph.algebra.VariableNode;
import groove.util.Visitor;
import groove.util.Visitor.Collector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Universally matched condition.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ForallCondition extends AbstractCondition<CompositeMatch> {
    /**
     * Constructs an instance based on a given pattern graph and root map. 
     * @param countNode node specifying the number of matches of this condition.
     */
    public ForallCondition(RuleName name, RuleGraph pattern,
            RuleGraphMorphism rootMap, SystemProperties properties,
            VariableNode countNode) {
        super(name, pattern, rootMap, properties);
        this.countNode = countNode;
        this.intAlgebra =
            AlgebraFamily.getInstance(getSystemProperties().getAlgebraFamily()).getAlgebra(
                "int");
    }

    @Override
    public void addSubCondition(Condition condition) {
        // sub-conditions of universal conditions must be rules or negatives
        assert !(condition instanceof ForallCondition);
        super.addSubCondition(condition);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Collection<SPORule> getComplexSubConditions() {
        return super.getComplexSubConditions();
    }

    @Override
    public <R> R traverseMatches(HostGraph host, RuleToHostMap contextMap,
            Visitor<CompositeMatch,R> visitor) {
        // for a universal condition, the matches cannot be incrementally
        // constructed; therefore, we first collect all of them
        List<CompositeMatch> matches = getAllMatches(host, contextMap);
        boolean cont = true;
        for (int i = 0; cont && i < matches.size(); i++) {
            cont = visitor.visit(matches.get(i));
        }
        return visitor.getResult();
    }

    @Deprecated
    <R> R traverseSeededMatches(HostGraph host, RuleToHostMap seedMap,
            Visitor<CompositeMatch,R> visitor) {
        assert visitor.isContinue();
        // for a universal condition, the matches cannot be incrementally
        // constructed; therefore, we first collect all of them
        List<RuleToHostMap> patternMaps =
            getMatcher().findAll(host, seedMap, null);
        int patternCount = patternMaps.size();
        // construct a matrix of sub-matches extending the pattern maps
        @SuppressWarnings("unchecked")
        List<RuleMatch>[] matchMatrix = new List[patternCount];
        int[] rowSize = new int[patternCount];
        int matchCount = 1;
        for (int i = 0; matchCount > 0 && i < patternCount; i++) {
            List<RuleMatch> subMatches = new ArrayList<RuleMatch>();
            for (SPORule subRule : getComplexSubConditions()) {
                subRule.traverseMatches(host, patternMaps.get(i),
                    Visitor.newCollector(subMatches));
            }
            matchMatrix[i] = subMatches;
            rowSize[i] = subMatches.size();
            matchCount = matchCount * subMatches.size();
        }
        if (matchCount > 0) {
            int[] vector = new int[patternCount];
            do {
                CompositeMatch match = new CompositeMatch(this);
                for (int i = 0; i < patternCount; i++) {
                    match.getSubMatches().add(matchMatrix[i].get(vector[i]));
                }
                visitor.visit(match);
            } while (visitor.isContinue() && incVector(vector, rowSize));
        }
        return visitor.getResult();
    }

    @Override
    final public List<CompositeMatch> getAllMatches(HostGraph host,
            RuleToHostMap contextMap) {
        List<CompositeMatch> result;
        testFixed(true);
        // lift the pattern match to a pre-match of this condition's target
        RuleToHostMap seedMap = computeSeedMap(host, contextMap);
        if (seedMap == null) {
            result = Collections.emptyList();
        } else {
            result =
                getMatcher().traverse(host, seedMap, getMatchCombiner(host));
        }
        return result;
    }

    /** 
     * Returns a visitor that constructs composite matches for the universal
     * condition, by combining matches of the condition's target graph. 
     * At any point, each composite match in the intermediate result
     * contains one {@link RuleMatch} (for some sub-rule of this universal 
     * condition) per target graph match visited so far.
     * @param host the host graph to which the subrules are matched
     * @return the visitor that will construct the composite match
     */
    private Visitor<RuleToHostMap,List<CompositeMatch>> getMatchCombiner(
            final HostGraph host) {
        List<CompositeMatch> matchSet = new ArrayList<CompositeMatch>();
        if (!this.positive) {
            matchSet.add(new CompositeMatch(this));
        }
        return new Visitor<RuleToHostMap,List<CompositeMatch>>(matchSet) {
            @Override
            protected boolean process(RuleToHostMap patternMap) {
                boolean result = this.firstVisit || !getResult().isEmpty();
                if (result) {
                    // add the empty match if the condition is positive
                    if (getResult().isEmpty()) {
                        assert this.firstVisit && ForallCondition.this.positive;
                        getResult().add(
                            new CompositeMatch(ForallCondition.this));
                    }
                    this.firstVisit = false;
                    // the subconditions are interpreted disjunctively,
                    // so we have to collect all of their possible matches
                    Collector<RuleMatch,List<RuleMatch>> matchCollector =
                        ForallCondition.this.matchCollector;
                    List<RuleMatch> subRuleMatches = new ArrayList<RuleMatch>();
                    for (SPORule subCondition : getComplexSubConditions()) {
                        subCondition.traverseMatches(host, patternMap,
                            matchCollector.newInstance(subRuleMatches));
                    }
                    matchCollector.dispose();
                    List<CompositeMatch> newMatchSet =
                        new ArrayList<CompositeMatch>();
                    for (CompositeMatch current : getResult()) {
                        newMatchSet.addAll(current.addMatchChoice(subRuleMatches));
                    }
                    setResult(newMatchSet);
                }
                return result;
            }

            private boolean firstVisit = true;
        };
    }

    /**
     * Returns the matches of this condition, given an iterator of match maps.
     */
    @Deprecated
    Collection<CompositeMatch> computeMatches(HostGraph host,
            Iterator<RuleToHostMap> matchMapIter) {
        List<CompositeMatch> result = new ArrayList<CompositeMatch>();
        // add the empty match if the condition is not positive
        if (!this.positive) {
            result.add(new CompositeMatch(this));
        }
        boolean first = this.positive;
        while (matchMapIter.hasNext() && (first || !result.isEmpty())) {
            // add the empty match if the condition is positive
            if (first) {
                result.add(new CompositeMatch(this));
                first = false;
            }
            RuleToHostMap matchMap = matchMapIter.next();
            // the subconditions are interpreted disjunctively,
            // so we have to collect all of their possible matches
            List<RuleMatch> subResults = new ArrayList<RuleMatch>();
            for (SPORule subCondition : getComplexSubConditions()) {
                subCondition.traverseMatches(host, matchMap,
                    this.matchCollector.newInstance(subResults));
            }
            List<CompositeMatch> newResult = new ArrayList<CompositeMatch>();
            for (CompositeMatch current : result) {
                newResult.addAll(current.addMatchChoice(subResults));
            }
            result = newResult;
        }
        return result;
    }

    /**
     * This implementation iterates over the result of
     * {@link #getAllMatches(HostGraph, RuleToHostMap)}.
     */
    @Override
    @Deprecated
    public Iterator<CompositeMatch> computeMatchIter(HostGraph host,
            Iterator<RuleToHostMap> matchMapIter) {
        return computeMatches(host, matchMapIter).iterator();
    }

    @Override
    public String toString() {
        return "Universal " + super.toString();
    }

    /** Returns the match count node of this universal condition, if any. */
    public RuleNode getCountNode() {
        return this.countNode;
    }

    /** Returns the integer algebra corresponding to the system properties. */
    public Algebra<?> getIntAlgebra() {
        return this.intAlgebra;
    }

    /** Sets this universal condition to positive (meaning that
     * it should have at least one match). */
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

    /** The fixed visitor used in {@link #computeMatches(HostGraph, Iterator)}. */
    private final Collector<RuleMatch,List<RuleMatch>> matchCollector =
        Visitor.newCollector();
    /** Node capturing the match count of this condition. */
    private final RuleNode countNode;
    /** The integer algebra corresponding to the system properties. */
    private final Algebra<?> intAlgebra;

    /**
     * Flag indicating whether the condition is positive, i.e., cannot be
     * vacuously true.
     */
    private boolean positive;
}
