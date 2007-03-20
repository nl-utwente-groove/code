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
 * $Id: DefaultGraphTransition.java,v 1.1.1.1 2007-03-20 10:05:24 kastenberg Exp $
 */
package groove.lts;

import groove.graph.AbstractBinaryEdge;
import groove.graph.AbstractGraph;
import groove.graph.BinaryEdge;
import groove.graph.Element;
import groove.graph.NodeEdgeMap;
import groove.graph.Graph;
import groove.graph.InjectiveMorphism;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.trans.Matching;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;


/**
 * Models a transition built upon a rule application
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:24 $
 */
public class DefaultGraphTransition extends AbstractBinaryEdge implements GraphOutTransition, GraphTransition {
    /** The total number of anchor images created. */
    static private int anchorImageCount = 0;
    
    /**
     * Flag to indicate whether transitions should be labelled by rule name only.
     */
    static private boolean ruleLabelled = true;
    
    /**
     * Sets the transition labelling policy.
     * If <code>true</code>, transition labels are based on rule names only.
     * If <code>false</code>, event information is also included.
     * The standard value is <code>true</code>.
     * @see #isRuleLabelled()
     * @see #label()
     */
    static public void setRuleLabelled(boolean ruleLabelled) {
        DefaultGraphTransition.ruleLabelled = ruleLabelled;
    }
    
    /**
     * Returns  the current transition labelling policy.
     * If <code>true</code>, transition labels are based on rule names only.
     * If <code>false</code>, event information is also included.
     * The standard value is <code>true</code>.
     * @see #setRuleLabelled(boolean)
     */
    static public boolean isRuleLabelled() {
        return ruleLabelled;
    }
    
    /**
     * Retrieves the label from a rule event.
     * The label is either the event label or the rule name, depending
     * on the labelling policy as determined by {@link #isRuleLabelled()}.
     * @see RuleEvent#getLabel()
     * @see Rule#getName()
     */
    static public Label getLabel(RuleEvent event) {
        if (isRuleLabelled()) {
            return event.getRule().getName();
        } else {
            return event.getLabel();
        }
    }

    /** Returns the total number of anchor images created. */
    static public int getAnchorImageCount() {
        return anchorImageCount;
    }

    /**
     * Constructs a GraphTransition on the basis of a given rule and corresponding
     * footprint, between a given source and target state.
     */
    public DefaultGraphTransition(RuleEvent event, GraphState source, GraphState target) {
        super(source, event.getLabel(), target);
        this.event = event;
    }

    public RuleEvent getEvent() {
		return event;
	}

	public Rule getRule() {
        return getEvent().getRule();
    }

    public Matching matching() {
    	return getEvent().getMatching(source());
    }

    /**
     * This implementation throws an {@link IllegalArgumentException} if
     * <code>source</code> is not equal to the source of the transition,
     * otherwise it returns <code>this</code>.
	 */
	public GraphTransition createTransition(GraphState source) {
		if (source != source()) {
			throw new IllegalArgumentException("Source state incompatible");
		} else {
			return this;
		}
	}

	/**
     * This implementation reconstructs the rule application from the stored footprint,
     * and appends an isomorphism to the actual target if necessary.
	 */
    public Morphism morphism() {
        if (morphism == null) {
            morphism = computeMorphism();
        }
        return morphism;
    }
    
    /**
     * Constructs an underlying morphism for the transition from the stored footprint.
     */
    protected Morphism computeMorphism() {
        RuleApplication appl = getEvent().createApplication(source());
        Graph derivedTarget = appl.getTarget();
        Graph realTarget = target();
        if (derivedTarget.edgeSet().equals(realTarget.edgeSet())
                && derivedTarget.nodeSet().equals(realTarget.nodeSet())) {
            return appl.getMorphism();
        } else {
            InjectiveMorphism iso = derivedTarget.getIsomorphismTo(target());
            assert iso != null : "Can't reconstruct derivation from graph transition " + this
                    + ": \n" + AbstractGraph.toString(derivedTarget) + " and \n"
                    + AbstractGraph.toString(target()) + " \nnot isomorphic";
            return appl.getMorphism().then(iso);
        }
    }

    // --------------------- Element methods -----------------------

    /**
     * This implementation throws an {@link UnsupportedOperationException} always.
     */
    public Transition imageFor(NodeEdgeMap elementMap) {
        throw new UnsupportedOperationException("Transition images are currenty not supported");
    }

    // ----------------------- OBJECT OVERRIDES -----------------------

    /**
     * This implementation compares objects on the basis of the
     * source graph, rule and anchor images.
     */
    protected boolean equalsSource(GraphTransition other) {
        return source().equals(other.source());
    }

    /**
     * This implementation compares objects on the basis of the
     * source graph, rule and anchor images.
     */
    protected boolean equalsEvent(GraphTransition other) {
        return getEvent().equals(other.getEvent());
    }

    /**
     * This implementation delegates to <tt>{@link #equalsSource(GraphTransition)}</tt>.
    */
    public boolean equals(Object obj) {
        return obj instanceof GraphTransition && equalsSource((GraphTransition) obj) && equalsEvent((GraphTransition) obj);
    }

//    /**
//     * This implementation returns the name of the underlying rule,
//     * possibly with event information included, depending on the
//     * transition labelling policy.
//     * @see #isRuleLabelled()
//     */
//    public Label computeLabel() {
//        if (isRuleLabelled()) {
//            return getRule().getName();
//        } else {
//            return getEvent().getLabel();
//        }
//    }
//  }
    
    /** This implementation specialises the return type to a {@link DefaultGraphState}. */
    @Override
    public DefaultGraphState source() {
    	return (DefaultGraphState) source;
    }
    
    /** This implementation specialises the return type to a {@link DefaultGraphState}. */
    @Override
    public DefaultGraphState target() {
    	return (DefaultGraphState) target;
    }

    /** Always throws an <tt>UnsupportedOperationException</tt>. */
    public BinaryEdge newEdge(Node source, Label label, Node target) {
        throw new UnsupportedOperationException();
    }

    /**
     * 
     */
    @Override
	public int compareTo(Element obj) {
		// TODO Auto-generated method stub
		return super.compareTo(obj);
	}

	/**
     * This implementation combines the hash codes of the rule and the anchor images.
     */
    protected int computeHashCode() {
        return source.hashCode() + event.hashCode();
    }

    /**
     * The underlying rule of this transition.
     * @invariant <tt>rule != null</tt>
     */
    protected final RuleEvent event;
    /**
     * The underlying morphism of this transition.
     * Computed lazily (using the footprint) using {@link #computeMorphism()}.
     */
    protected Morphism morphism;
}