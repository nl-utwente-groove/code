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
 * $Id$
 */
package groove.transform;

import static groove.transform.RuleEvent.Reuse.EVENT;

import java.util.Iterator;

import groove.grammar.Rule;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostEdgeSet;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.host.HostNodeSet;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.RuleToHostMap;
import groove.match.TreeMatch;
import groove.util.Visitor;
import groove.util.cache.AbstractCacheHolder;
import groove.util.cache.CacheReference;

/**
 * Abstract class providing basic rule event functionality.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class AbstractRuleEvent<R extends Rule,C extends AbstractRuleEvent<R,C>.AbstractEventCache>
    extends AbstractCacheHolder<C> implements RuleEvent {
    /** Constructs an event for a given rule. */
    protected AbstractRuleEvent(CacheReference<C> template, R rule) {
        super(template);
        this.rule = rule;
    }

    @Override
    public R getAction() {
        return getRule();
    }

    @Override
    public R getRule() {
        return this.rule;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getRule().getTransitionLabel());
        result.append(getAnchorImageString());
        return result.toString();
    }

    /**
     * The hash code is based on that of the rule and an initial fragment of the
     * anchor images.
     */
    @Override
    public int hashCode() {
        return getReuse() == EVENT ? System.identityHashCode(this) : eventHashCode();
    }

    /**
     * Two events are equal if they have the same rule and anchor
     * images.
     * If events are reused, this method only tests for object equality.
     * Use {@link #equalsEvent(RuleEvent)} to always test for the
     * content.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractRuleEvent)) {
            return false;
        }
        AbstractRuleEvent<?,?> other = (AbstractRuleEvent<?,?>) obj;
        if (getReuse() != EVENT) {
            return equalsEvent(other);
        }
        if (other.getReuse() != EVENT) {
            return other.equalsEvent(this);
        }
        assert !equalsEvent(other);
        return false;
    }

    /**
     * Tests if the content of this event coincides with that of the other. The
     * content consists of the rule and the anchor images. Callback method from
     * {@link #equals(Object)}.
     */
    abstract boolean equalsEvent(RuleEvent other);

    /**
     * The event hash code is based on that of the rule and an initial fragment
     * of the anchor images.
     */
    int eventHashCode() {
        if (this.hashCode == 0) {
            this.hashCode = computeEventHashCode();
            if (this.hashCode == 0) {
                this.hashCode = 1;
            }
        }
        return this.hashCode;
    }

    /**
     * Callback method to compute the event hash code.
     */
    abstract int computeEventHashCode();

    /**
     * Callback factory method to create a fresh, empty node set.
     */
    protected HostNodeSet createNodeSet() {
        return new HostNodeSet();
    }

    /**
     * Callback factory method to create a fresh, empty node set with a given
     * initial capacity.
     */
    protected HostNodeSet createNodeSet(int capacity) {
        return new HostNodeSet(capacity);
    }

    /**
     * Callback factory method to create a fresh, empty edge set.
     */
    protected HostEdgeSet createEdgeSet() {
        return new HostEdgeSet();
    }

    /**
     * Callback factory method to create a fresh, empty edge set with a given
     * initial capacity.
     */
    protected HostEdgeSet createEdgeSet(int capacity) {
        return new HostEdgeSet(capacity);
    }

    @Override
    final public Proof getMatch(final HostGraph source) {
        assert isCorrectFor(source);
        // visitor that selects a proof that corresponds to this event
        Visitor<TreeMatch,Proof> matchVisitor = new Visitor<TreeMatch,Proof>() {
            @Override
            protected boolean process(TreeMatch match) {
                if (getRule().isValidPatternMap(source, match.getPatternMap())) {
                    setResult(extractProof(match));
                }
                return !hasResult();
            }
        };
        Proof result = getRule().getEventMatcher(source.isSimple())
            .traverse(source, getAnchorMap(), matchVisitor);
        return result;
    }

    /**
     * Tests if the anchor map fits into a given host graph.
     * @param host the graph to be tested
     * @return <code>true</code> if the anchor map images are all in
     *         <code>host</code>
     */
    private boolean isCorrectFor(HostGraph host) {
        RuleToHostMap anchorMap = getAnchorMap();
        boolean correct = true;
        Iterator<? extends HostEdge> edgeImageIter = anchorMap.edgeMap()
            .values()
            .iterator();
        while (correct && edgeImageIter.hasNext()) {
            correct = host.containsEdge(edgeImageIter.next());
        }
        if (correct) {
            Iterator<? extends HostNode> nodeImageIter = anchorMap.nodeMap()
                .values()
                .iterator();
            while (correct && nodeImageIter.hasNext()) {
                HostNode nodeImage = nodeImageIter.next();
                correct = nodeImage instanceof ValueNode || host.containsNode(nodeImage);
            }
        }
        return correct;
    }

    /**
     * Extracts a proof corresponding to this event from a given match.
     * @return a proof constructed from {@code match} whose events equals this one,
     * or {@code null} if there is no such proof
     */
    abstract protected Proof extractProof(TreeMatch match);

    /** The rule for which this is an event. */
    private final R rule;
    /**
     * The precomputed hash code.
     */
    private int hashCode;
    /** Global empty set of nodes. */
    static final HostNode[] EMPTY_NODE_ARRAY = new HostNode[0];

    /** Cache holding the anchor map. */
    abstract protected class AbstractEventCache {
        // nothing here
    }
}
