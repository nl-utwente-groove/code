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
 * $Id: AbstractEvent.java,v 1.4 2007-10-18 14:57:47 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.NodeSet;
import groove.graph.WrapperLabel;
import groove.util.AbstractCacheHolder;
import groove.util.CacheReference;
import groove.util.TreeHashSet;

import java.util.Set;

/**
 * Abstract class providing basic rule event functionality.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class AbstractEvent<R extends Rule,C> extends AbstractCacheHolder<C> implements RuleEvent {
	/** Constructs an event for a given rule. */
	protected AbstractEvent(CacheReference<C> template, R rule) {
		super(template);
		this.rule = rule;
	}

	public WrapperLabel<RuleEvent> getLabel() {
		return new WrapperLabel<RuleEvent>(this);
	}

	@Deprecated
	public RuleNameLabel getName() {
		return getRule().getName();
	}

	public R getRule() {
		return rule;
	}

    public int identityHashCode() {
        int result = identityHashCode;
        if (result == 0) {
            result = identityHashCode = System.identityHashCode(this);
            if (result == 0) {
                result = identityHashCode += 1;
            }
        }
        return result;
    }

	public RuleApplication newApplication(Graph source) {
		return new DefaultApplication(this, source);
	}

    /**
     * Callback factory method to create a fresh, empty node set.
     */
    protected Set<Node> createNodeSet() {
        return new NodeSet();
    }

    /**
     * Callback factory method to create a fresh, empty edge set.
     */
    protected Set<Edge> createEdgeSet() {
        return new TreeHashSet<Edge>();
    }
    
    /** The rule for which this is an event. */
    private final R rule;
    /** The pre-computed identity hash code for this object. */
    private int identityHashCode;
}
