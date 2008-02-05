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
 * $Id: AbstrGraphTransitionImpl.java,v 1.4 2008-02-05 13:28:28 rensink Exp $
 */
package groove.abs.lts;

import groove.graph.AbstractBinaryEdge;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.WrapperLabel;
import groove.lts.GraphTransitionStub;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;

/**
 * @author Iovka Boneva
 * @version $Revision $
 */
public class AbstrGraphTransitionImpl extends AbstractBinaryEdge<AbstrGraphState, WrapperLabel<RuleEvent>, AbstrGraphState>  implements AbstrGraphTransition{
	
	public RuleMatch getMatch() {
		// TODO is it correct ?
		// This is needed for displaying matches
		return getEvent().getMatch(this.source().getGraph());
	}

	/** Not implemented
	 * This does not make sense for abstract transformation. */
	public Morphism getMorphism() {
		throw new UnsupportedOperationException();
		// TODO see whether it is used for the gui or by groove.calc
	}

	public boolean isSymmetry() {
		throw new UnsupportedOperationException();
		// TODO see whether it is used for the state generation
	}

	public GraphTransitionStub toStub() { 
		throw new UnsupportedOperationException();
		// TODO see whether it is used for the state generation
	}

	// ------------------------------------------------------------
	// FIELDS, CONSTRUCTORS, STANDARD METHODS
	// ------------------------------------------------------------
	/**
	 * @param source
	 * @param event
	 * @param target
	 */
	public AbstrGraphTransitionImpl(AbstrGraphState source, RuleEvent event, AbstrGraphState target) {
		super(source, event.getLabel(), target);
		if (true) throw new UnsupportedOperationException();
		this.event = event;
		// TODO Auto-generated constructor stub. Complete depending on the needs
	}
	

	public final RuleEvent getEvent() { return this.event; }
	
	public boolean isEquivalent (AbstrGraphTransition other) { 
		return this.source() == other.source() && 
			this.getEvent() == other.getEvent();
	}

	private RuleEvent event;
	
	@Override
	public boolean equals (Object o) {
		if (! (o instanceof AbstrGraphTransition)) {
			return false;
		}
		AbstrGraphTransition t = (AbstrGraphTransition) o;
		boolean result =  this.source().compareTo(t.source()) == 0 && 
		       this.target().compareTo(t.target()) == 0 &&
		       this.getEvent().equals(t.getEvent());
		assert (!result || t.hashCode() == this.hashCode()) : "The equals method does not comply with the hash code method !!!";
		return result;
	}
	
	// The hashCode() method is implemented as final and only depends on the hash code of the end points
	
	
	// ------------------------------------------------------------
	// UNIMPLEMENTED METHODS
	// ------------------------------------------------------------
	
	
	@Deprecated
	public Rule getRule() { throw new UnsupportedOperationException(); }

	@Deprecated
	public Morphism matching() { throw new UnsupportedOperationException(); }
	
	@Deprecated
	public Morphism morphism() { throw new UnsupportedOperationException(); }
	
	/**
	 * No implementation.
	 */
	public Node[] getAddedNodes() { throw new UnsupportedOperationException(); }

}
