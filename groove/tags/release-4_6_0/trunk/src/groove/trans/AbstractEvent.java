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
 * $Id: AbstractEvent.java,v 1.8 2008-02-29 11:02:20 fladder Exp $
 */
package groove.trans;

import static groove.trans.RuleEvent.Reuse.EVENT;
import groove.graph.algebra.ValueNode;
import groove.lts.DefaultRuleTransition;
import groove.lts.GraphState;
import groove.lts.RuleTransition;
import groove.match.TreeMatch;
import groove.util.AbstractCacheHolder;
import groove.util.CacheReference;
import groove.util.Visitor;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.Set;

/**
 * Abstract class providing basic rule event functionality.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class AbstractEvent<R extends Rule,C extends AbstractEvent<R,C>.AbstractEventCache>
        extends AbstractCacheHolder<C> implements RuleEvent {
    /** Constructs an event for a given rule. */
    protected AbstractEvent(CacheReference<C> template, R rule) {
        super(template);
        this.rule = rule;
    }

    /** This implementation returns the event itself. */
    @Override
    public RuleEvent getEvent() {
        return this;
    }

    @Override
    public RuleEffect getEffect(HostGraph host) {
        RuleEffect result = new RuleEffect(host);
        recordEffect(result);
        return result;
    }

    abstract Reuse getReuse();

    /** Returns a string showing this event as a rule call. */
    public String getLabelText(HostNode[] addedNodes) {
        StringBuilder result = new StringBuilder();
        result.append(getRule().getTransitionLabel());
        if (getRule().getSystemProperties().isUseParameters()) {
            result.append('(');
            boolean first = true;
            for (HostNode arg : getArguments(addedNodes)) {
                if (!first) {
                    result.append(',');
                }
                first = false;
                if (arg == null) {
                    result.append('_');
                } else if (arg instanceof ValueNode) {
                    result.append(((ValueNode) arg).getSymbol());
                } else {
                    result.append(arg);
                }
            }
            result.append(')');
        }
        return result.toString();
    }

    /**
     * Returns the instantiated output string for this rule, if any. 
     * @throws FormatException if the format string of the rule
     * does not correspond to the actual rule parameters. 
     */
    public String getOutputString(HostNode[] addedNodes) throws FormatException {
        String result = null;
        String formatString = getRule().getFormatString();
        if (formatString != null) {
            List<Object> args = new ArrayList<Object>();
            for (HostNode arg : getArguments(addedNodes)) {
                if (arg instanceof ValueNode) {
                    args.add(((ValueNode) arg).getValue());
                } else {
                    args.add(arg.toString());
                }
            }
            try {
                result = String.format(formatString, args.toArray());
            } catch (MissingFormatArgumentException e) {
                throw new FormatException("Error in rule output string: %s",
                    e.getMessage());
            }
        }
        return result;
    }

    /**
     * Constructs an argument array for this event, with respect to
     *  given array of added nodes (which are the images of the creator nodes).
     * @param addedNodes the added nodes; if {@code null}, the creator
     * node images will be set to {@code null}
     */
    abstract HostNode[] getArguments(HostNode[] addedNodes);

    public R getRule() {
        return this.rule;
    }

    public RuleApplication newApplication(HostGraph source) {
        return new RuleApplication(this, source);
    }

    @Override
    public String toString() {
        return getLabelText(null);
    }

    /**
     * The hash code is based on that of the rule and an initial fragment of the
     * anchor images.
     */
    @Override
    public int hashCode() {
        return getReuse() == EVENT ? System.identityHashCode(this)
                : eventHashCode();
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
        if (!(obj instanceof AbstractEvent)) {
            return false;
        }
        AbstractEvent<?,?> other = (AbstractEvent<?,?>) obj;
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
     * Always returns an empty array. The method does not check if this event is
     * actually applicable at <code>source</code>.
     * @throws UnsupportedOperationException if the rule is not unmodifying
     */
    public HostNode[] getAddedNodes(GraphState source) {
        if (getRule().isModifying()) {
            throw new UnsupportedOperationException(
                "Only unmodifying events can be used as graph transition stubs");
        }
        return EMPTY_NODE_ARRAY;
    }

    /**
     * Returns this event. The method does not check if this event is actually
     * applicable at <code>source</code>.
     * @throws UnsupportedOperationException if the rule is not unmodifying
     */
    public RuleEvent getEvent(GraphState source) {
        if (getRule().isModifying()) {
            throw new UnsupportedOperationException(
                "Only unmodifying events can be used as graph transition stubs");
        }
        return this;
    }

    /**
     * Returns the passed-in source event. The method does not check if this
     * event is actually applicable at <code>source</code>.
     * @throws UnsupportedOperationException if the rule is not unmodifying
     */
    public GraphState getTarget(GraphState source) {
        if (getRule().isModifying()) {
            throw new UnsupportedOperationException(
                "Only unmodifying events can be used as graph transition stubs");
        }
        return source;
    }

    /**
     * Always returns <code>false</code>. The method does not check if this
     * event is actually applicable at <code>source</code>.
     * @throws UnsupportedOperationException if the rule is not unmodifying
     */
    public boolean isSymmetry() {
        if (getRule().isModifying()) {
            throw new UnsupportedOperationException(
                "Only unmodifying events can be used as graph transition stubs");
        }
        return false;
    }

    /**
     * Returns a {@link DefaultRuleTransition}. The method does not check if
     * this event is actually applicable at <code>source</code>.
     * @throws UnsupportedOperationException if the rule is not unmodifying
     */
    public RuleTransition toTransition(GraphState source) {
        if (getRule().isModifying()) {
            throw new UnsupportedOperationException(
                "Only unmodifying events can be used as graph transition stubs");
        }
        return new DefaultRuleTransition(source, this, EMPTY_NODE_ARRAY,
            source, false);
    }

    /**
     * Callback factory method to create a fresh, empty node set.
     */
    protected Set<HostNode> createNodeSet() {
        return new HostNodeSet();
    }

    /**
     * Callback factory method to create a fresh, empty node set with a given
     * initial capacity.
     */
    protected Set<HostNode> createNodeSet(int capacity) {
        return new HostNodeSet(capacity);
    }

    /**
     * Callback factory method to create a fresh, empty edge set.
     */
    protected Set<HostEdge> createEdgeSet() {
        return new HostEdgeSet();
    }

    /**
     * Callback factory method to create a fresh, empty edge set with a given
     * initial capacity.
     */
    protected Set<HostEdge> createEdgeSet(int capacity) {
        return new HostEdgeSet(capacity);
    }

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
        Proof result =
            getRule().getEventMatcher().traverse(source, getAnchorMap(),
                matchVisitor);
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
        Iterator<? extends HostEdge> edgeImageIter =
            anchorMap.edgeMap().values().iterator();
        while (correct && edgeImageIter.hasNext()) {
            correct = host.containsEdge(edgeImageIter.next());
        }
        if (correct) {
            Iterator<? extends HostNode> nodeImageIter =
                anchorMap.nodeMap().values().iterator();
            while (correct && nodeImageIter.hasNext()) {
                HostNode nodeImage = nodeImageIter.next();
                correct =
                    nodeImage instanceof ValueNode
                        || host.containsNode(nodeImage);
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
