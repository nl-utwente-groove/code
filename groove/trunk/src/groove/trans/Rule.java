// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: Rule.java,v 1.25 2007-11-29 12:52:09 rensink Exp $ $Date: 2007-11-29
 * 12:52:09 $
 */
package groove.trans;

import groove.control.CtrlPar;
import groove.graph.GraphProperties;
import groove.match.MatchStrategy;
import groove.util.Visitor;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Interface of a production rule. The rule essentially consists of a left hand
 * side graph, a right hand side graph, a rule morphism and a set of NACs. [AR:
 * In the future the interface might provide less functionality; instead there
 * will be a sub-interface GraphRule or similar. ]
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Rule extends Comparable<Rule>, Condition {
    /** Returns the name of this rule. */
    public RuleName getName();

    /** Returns the label to be used in the LTS when this rule is applied.
     *  Defaults to the rule name, if the property is undefined.  
     */
    public String getTransitionLabel();

    /** 
     * Returns a format string for the standard output.
     * Whenever a transition with this rule is added to a GTS, a 
     * corresponding string is sent to the standard output.
     */
    public String getFormatString();

    /**
     * Returns the priority of this object. A higher number means higher
     * priority, with {@link #DEFAULT_PRIORITY} the lowest.
     */
    public int getPriority();

    /**
     * @return the properties of the rule.
     */
    public GraphProperties getRuleProperties();

    /**
     * Returns the left hand side of this Rule.
     * @ensure <tt>result == morphism().source()</tt>
     */
    public RuleGraph lhs();

    /**
     * Returns the right hand side of this Rule.
     * @ensure <tt>result == morphism().cod()</tt>
     */
    public RuleGraph rhs();

    /**
     * Returns the rule morphism, which is the partial morphism from LHS to RHS.
     * @see #lhs()
     * @see #rhs()
     */
    public RuleGraphMorphism getMorphism();

    /**
     * Indicates if application of this rule actually changes the host graph. If
     * <code>false</code>, this means the rule is essentially a graph
     * condition.
     */
    public boolean isModifying();

    /**
     * Indicates if this rule has a confluency property. If this method returns
     * <code>true</code>, this means the rule can be applied only once, to an
     * arbitrary match.
     */
    public boolean isConfluent();

    /** Indicates if the rule has (node or edge) creators. */
    public boolean hasCreators();

    /** Indicates if the rule has node mergers. */
    public boolean hasMergers();

    /** Returns the signature of this rule. */
    public List<CtrlPar.Var> getSignature();

    /**
     * Returns an iterator over the matches for a given host graph, given a
     * matching of the root context.
     * @param host the graph in which the match is to be found
     * @param contextMap a matching of the pattern of this condition; may be
     *        <code>null</code> if the condition is ground.
     * @throws IllegalArgumentException if <code>contextMap</code> is
     *         <code>null</code> and the condition is not ground, or if
     *         <code>contextMap</code> is not compatible with the root map
     */
    public Iterator<RuleMatch> getMatchIter(HostGraph host,
            RuleToHostMap contextMap);

    /**
     * Traverses the matches of this rule on a given host graph and for
     * a given context map, until the first time the visit method returns 
     * {@code false}.
     * @param host the graph in which the match is to be found
     * @param contextMap a matching of the pattern of this condition; may be
     *        <code>null</code> if the condition is ground.
     * @param visitor the visitor invoked for all the matches
     * @return the result of the visitor after the traversal
     * @throws IllegalArgumentException if <code>patternMatch</code> is
     *         <code>null</code> and the condition is not ground, or if
     *         <code>patternMatch</code> is not compatible with the pattern
     *         graph
     */
    public <T> T visitMatches(HostGraph host, RuleToHostMap contextMap,
            Visitor<RuleMatch,T> visitor);

    /**
     * Returns the collection of all matches for a given host graph, given a
     * matching of the root context.
     * @param host the graph in which the match is to be found
     * @param contextMap a matching of the pattern of this condition; may be
     *        <code>null</code> if the condition is ground.
     * @throws IllegalArgumentException if <code>contextMap</code> is
     *         <code>null</code> and the condition is not ground, or if
     *         <code>contextMap</code> is not compatible with the root map
     */
    public Collection<RuleMatch> getAllMatches(HostGraph host,
            RuleToHostMap contextMap);

    /**
     * Lazily creates and returns a matcher for rule events of this rule. The
     * matcher will try to extend anchor maps to full matches.
     */
    public MatchStrategy<RuleToHostMap> getEventMatcher();

    /**
     * The lowest rule priority, which is also the default value if no explicit
     * priority is given.
     */
    static public final int DEFAULT_PRIORITY = 0;
    /**
     * A comparator for priorities, encoded as {@link Integer} objects. This
     * implementation orders priorities from high to low.
     */
    static final public Comparator<Integer> PRIORITY_COMPARATOR =
        new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o2.intValue() - o1.intValue();
            }

        };
}
