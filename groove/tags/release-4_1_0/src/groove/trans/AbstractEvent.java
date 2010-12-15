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

import groove.graph.HostNodeSet;
import groove.graph.Node;
import groove.lts.DefaultGraphTransition;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.util.AbstractCacheHolder;
import groove.util.CacheReference;

import java.util.Set;

/**
 * Abstract class providing basic rule event functionality.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class AbstractEvent<R extends Rule,C extends AbstractEvent<R,C>.AbstractEventCache>
        extends AbstractCacheHolder<C> implements RuleEvent {
    /** Constructs an event for a given rule. */
    protected AbstractEvent(CacheReference<C> template, R rule, boolean reuse) {
        super(template);
        this.rule = rule;
        this.reuse = reuse;
    }

    /** This implementation returns the event itself. */
    @Override
    public RuleEvent getEvent() {
        return this;
    }

    boolean isReuse() {
        return this.reuse;
    }

    private String getLabelText(Node[] addedNodes) {
        StringBuilder result = new StringBuilder();
        result.append(getRule().getTransitionLabel());
        if (getRule().getSystemProperties().isUseParameters()) {
            result.append('(');
            boolean first = true;
            for (Node arg : getArguments(addedNodes)) {
                if (!first) {
                    result.append(',');
                }
                first = false;
                if (arg == null) {
                    result.append('_');
                } else {
                    result.append(arg);
                }
            }
            result.append(')');
        }
        return result.toString();
    }

    public R getRule() {
        return this.rule;
    }

    public int identityHashCode() {
        int result = this.identityHashCode;
        if (result == 0) {
            result = this.identityHashCode = super.hashCode();
            if (result == 0) {
                result = this.identityHashCode = 1;
            }
        }
        return result;
    }

    public RuleApplication newApplication(HostGraph source) {
        return new DefaultApplication(this, source);
    }

    /** Returns the cached set of nodes erased by the event. */
    public Set<HostNode> getErasedNodes() {
        return getCache().getErasedNodes();
    }

    /** Computes and returns the set of erased nodes. */
    abstract Set<HostNode> computeErasedNodes();

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
        return isReuse() ? identityHashCode() : eventHashCode();
    }

    /**
     * Two rule applications are equal if they have the same rule and anchor
     * images. Note that the source is not tested; do not collect rule
     * applications for different sources!
     */
    @Override
    public boolean equals(Object obj) {
        boolean result;
        if (obj == this) {
            result = true;
        } else if (obj instanceof RuleEvent) {
            result = !this.reuse && equalsEvent((RuleEvent) obj);
        } else {
            result = false;
        }
        return result;
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
     * Returns a {@link DefaultGraphTransition}. The method does not check if
     * this event is actually applicable at <code>source</code>.
     * @throws UnsupportedOperationException if the rule is not unmodifying
     */
    public GraphTransition toTransition(GraphState source) {
        if (getRule().isModifying()) {
            throw new UnsupportedOperationException(
                "Only unmodifying events can be used as graph transition stubs");
        }
        return new DefaultGraphTransition(this, EMPTY_NODE_ARRAY, source,
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

    /** The rule for which this is an event. */
    private final R rule;
    /** Flag indicating if sets should be stored for reuse. */
    private final boolean reuse;
    /**
     * The precomputed hash code.
     */
    private int hashCode;
    /** The pre-computed identity hash code for this object. */
    private int identityHashCode;
    /** Constant empty node array. */
    private static final HostNode[] EMPTY_NODE_ARRAY = new HostNode[0];

    /** Cache holding the anchor map. */
    abstract protected class AbstractEventCache {
        /** Returns the cached set of nodes erased by the event. */
        public final Set<HostNode> getErasedNodes() {
            if (this.erasedNodeSet == null) {
                this.erasedNodeSet = computeErasedNodes();
            }
            return this.erasedNodeSet;
        }

        /**
         * Set of nodes from the source that are to be erased in the target.
         */
        private Set<HostNode> erasedNodeSet;
    }
}
