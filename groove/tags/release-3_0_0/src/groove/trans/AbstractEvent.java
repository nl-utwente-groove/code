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
 * $Id: AbstractEvent.java,v 1.8 2008-02-29 11:02:20 fladder Exp $
 */
package groove.trans;

import groove.graph.DefaultEdgeSet;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeSet;
import groove.graph.WrapperLabel;
import groove.util.AbstractCacheHolder;
import groove.util.CacheReference;

import java.util.Set;

/**
 * Abstract class providing basic rule event functionality.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class AbstractEvent<R extends Rule,C extends AbstractEvent<R,C>.AbstractEventCache> extends AbstractCacheHolder<C> implements RuleEvent {
	/** Constructs an event for a given rule. */
	protected AbstractEvent(CacheReference<C> template, R rule) {
		super(template);
		this.rule = rule;
	}

	public Label getLabel() {
		return new WrapperLabel<RuleEvent>(this);
	}

	public R getRule() {
		return rule;
	}

    public int identityHashCode() {
        int result = identityHashCode;
        if (result == 0) {
            result = identityHashCode = super.hashCode();
            if (result == 0) {
                result = identityHashCode = 1;
            }
        }
        return result;
    }

	public RuleApplication newApplication(Graph source) {
		return new DefaultApplication(this, source);
	}

    /** Returns the cached set of nodes erased by the event. */
    public Set<Node> getErasedNodes() {
        return getCache().getErasedNodes();
    }

    /** Computes and returns the set of erased nodes. */
    abstract Set<Node> computeErasedNodes();

    /**
     * Callback factory method to create a fresh, empty node set.
     */
    protected Set<Node> createNodeSet() {
        return new NodeSet();
    }

    /**
     * Callback factory method to create a fresh, empty node set with a given initial capacity.
     */
    protected Set<Node> createNodeSet(int capacity) {
        return new NodeSet(capacity);
    }

    /**
     * Callback factory method to create a fresh, empty edge set.
     */
    protected Set<Edge> createEdgeSet() {
        return (Set) new DefaultEdgeSet();
    }

    /**
     * Callback factory method to create a fresh, empty edge set with a given initial capacity.
     */
    protected Set<Edge> createEdgeSet(int capacity) {
        return (Set) new DefaultEdgeSet(capacity);
    }
    
    /** The rule for which this is an event. */
    private final R rule;
    /** The pre-computed identity hash code for this object. */
    private int identityHashCode;
    
    /** Cache holding the anchor map. */
    abstract protected class AbstractEventCache {
        /** Returns the cached set of nodes erased by the event. */
		public final Set<Node> getErasedNodes() {
			if (erasedNodeSet == null) {
				erasedNodeSet = computeErasedNodes();
			}
			return erasedNodeSet;
		}
		
        /**
         * Set of nodes from the source that are to be erased in the target.
         */
        private Set<Node> erasedNodeSet;
    }
}
