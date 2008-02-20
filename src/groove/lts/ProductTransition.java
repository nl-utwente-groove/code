/*
 * GROOVE: GRaphs for Object Oriented VErification
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
 * $Id: ProductTransition.java,v 1.1 2008-02-20 07:43:19 kastenberg Exp $
 */
package groove.lts;

import groove.verify.BuchiGraphState;

/**
 * Models a transition in a product automaton consisting of
 * a graph-transition and a buchi-transition.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.1 $ $Date: 2008-02-20 07:43:19 $
 */
public class ProductTransition { //extends DefaultGraphTransition {

	private GraphTransition graphTransition;
//	private BuchiTransition buchiTransition;
	private BuchiGraphState source;
	private BuchiGraphState target;

    /**
     * Constructor.
     * @param source the source buchi graph-state
     * @param transition the underlying graph-transition
     * @param target the target buchi graph-state
     */
    public ProductTransition(BuchiGraphState source, GraphTransition transition, BuchiGraphState target) {
    	this.source = source;
    	this.graphTransition = transition;
    	this.target = target;
    }

    public GraphTransition graphTransition() {
    	return graphTransition;
    }

//    public BuchiTransition buchiTransition() {
//    	return buchiTransition;
//    }

    public BuchiGraphState source() {
    	return source;
    }

    public BuchiGraphState target() {
    	return target;
    }

    // ----------------------- OBJECT OVERRIDES -----------------------

    /**
     * This implementation compares objects on the basis of the
     * source graph, rule and anchor images.
     */
    protected boolean equalsSource(ProductTransition other) {
        return source() == other.source();
    }

    /**
     * This implementation compares objects on the basis of the
     * source graph, rule and anchor images.
     */
    protected boolean equalsEvent(ProductTransition other) {
        return graphTransition().source().equals(other.graphTransition().source()) &&
        graphTransition().getEvent().equals(other.graphTransition().getEvent());
    }

    public int hashCode() {
    	int result = 0;
    	result += source().hashCode() + target().hashCode();
    	if (graphTransition() != null) {
    		result += graphTransition().hashCode();
    	}
    	return result;
    }
    /**
     * This implementation delegates to <tt>{@link #equalsSource(GraphTransition)}</tt>.
    */
	@Override
    public boolean equals(Object obj) {
        return obj instanceof ProductTransition && equalsSource((ProductTransition) obj) && equalsEvent((ProductTransition) obj);
    }

	public String toString() {
		return source().toString() + "-->" + target.toString();
	}
	/**
     * This implementation combines the hash codes of the rule and the anchor images.
     */
//	@Override
//    protected int computeHashCode() {
//        return System.identityHashCode(source) + System.identityHashCode(event);
//    }
}