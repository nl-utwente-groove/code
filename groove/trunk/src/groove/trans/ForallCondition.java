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
    /**
     * Constructs an instance based on a given target and root map. 
     * @param countNode node specifying the number of matches of this condition.
     */
    public ForallCondition(RuleName name, RuleGraph target,
            RuleGraphMorphism rootMap, SystemProperties properties,
            VariableNode countNode) {
        super(name, target, rootMap, properties);
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
    final public Collection<CompositeMatch> getAllMatches(HostGraph host,
            RuleToHostMap contextMap) {
        Collection<CompositeMatch> result = null;
        testFixed(true);
        // lift the pattern match to a pre-match of this condition's target
        final RuleToHostMap anchorMap;
        if (contextMap == null) {
            testGround();
            anchorMap = host.getFactory().createRuleToHostMap();
        } else {
            anchorMap = createAnchorMap(contextMap);
        }
        if (anchorMap == null) {
            result = Collections.emptySet();
        } else {
            result =
                computeMatches(host, getMatcher().getMatchIter(host, anchorMap));
        }
        return result;
    }

    /**
     * Returns the matches of this condition, given an iterator of match maps.
     */
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
            Collection<RuleMatch> subResults = new ArrayList<RuleMatch>();
            for (SPORule subCondition : getComplexSubConditions()) {
                subCondition.visitMatches(host, matchMap,
                    Visitor.useCollector(subResults));
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
