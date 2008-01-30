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
 * $Id: AbstrStateGenerator.java,v 1.3 2008-01-30 09:33:47 iovka Exp $
 */
package groove.abs.lts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.StateGenerator;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.abs.AbstrGraph;
import groove.abs.AbstrTransformer;
import groove.abs.Abstraction;
import groove.abs.Util;
import groove.explore.util.ExploreCache;


/**
 * FIXME this class is not functioning
 * @author Iovka Boneva
 * @version $Revision $
 */
public class AbstrStateGenerator extends StateGenerator {

	private Abstraction.Parameters options;
	
	public AbstrStateGenerator (AGTS gts) {
		throw new UnsupportedOperationException();
		//super(gts);
	}
	
	/** */
	public AbstrStateGenerator (Abstraction.Parameters options) {
		throw new UnsupportedOperationException();
//		super();
//		this.options = options;
	}

	/** Has no effect if options were already set. */
	public void setOptions (Abstraction.Parameters options) {
		if (this.options == null) {	this.options = options; }
	}
	
	@Override
	public Set<? extends GraphState> addTransition(GraphState source, RuleMatch match, ExploreCache cache) {
		throw new UnsupportedOperationException();
	}
	
//	@Override
//	public Set<? extends GraphState> addTransition(GraphState source, RuleMatch match, ExploreCache cache) {
//		AbstrGraphState abstrSource = (AbstrGraphState) source;
//		Set<AbstrGraphState> result = new HashSet<AbstrGraphState>();
//		Collection<AbstrGraph> transfResult = new ArrayList<AbstrGraph>();
//		RuleEvent event = AbstrTransformer.transform(((AbstrGraphState) abstrSource).getGraph(), match, getRecord(), this.options, transfResult);
//		for (AbstrGraph transf : transfResult) {
//			AbstrGraphState target = new AbstrGraphStateImpl(transf);
//			AbstrGraphState oldState = (AbstrGraphState) this.getGTS().addState(target);
//			AbstrGraphTransition trans = new AbstrGraphTransitionImpl(abstrSource, event, oldState == null ? target : oldState);
//			this.getGTS().addTransition(trans);
//			result.add(trans.target());
//		}
//		return result;
//	}
	
	@Override
	/** @require gts is of type {@link AGTS} */
	public void setGTS(GTS gts) {
		assert gts instanceof AGTS : "The transition system should be of type AGTS.";
		super.setGTS((AGTS) gts);
	}
	@Override
	/** Specialises return type. */
	public AGTS getGTS() { return (AGTS) super.getGTS(); }
	
	// ---------------------------------------------------------------
	// INVARIANTS
	// ---------------------------------------------------------------
	private void checkInvariants() {
		if (! Util.ea()) { return; }
		// the applier is null, as not used
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	@Override
//	/** @require state is an AbstrGraphState */
//	public void explore(GraphState state) {
//		reporter.start(SUCC);
//		if (state.isClosed()) {
//			reporter.stop();
//			return;
//		}
//		this.collector.reset();
//
//		AbstrGraphState st = (AbstrGraphState) state;
//		final AbstrGraph ag = st.getGraph();
//		// Generate all matchings, for all rules
//		for (Rule rule : this.getGTS().getGrammar().getRules()) {
//			// TODO : debug
//			Collection<VarNodeEdgeMap> allMatches = Util.getMatchSet(rule.lhs(), ag, new NodeEdgeHashMap());
//			
// 			for (final VarNodeEdgeMap match : Util.getMatchesIter(rule.lhs(), ag, new NodeEdgeHashMap())) {
//				if (! ag.isInjectiveMap(match)) { continue; }
//
//				// Compute the possible concrete parts
//				ConcretePart.Typing typing = new ConcretePart.Typing() {
//					public GraphPattern typeOf(Node n) { return ag.typeOf(match.getNode(n)); }
//				};
//				Collection<ConcretePart> ext = ConcretePart.extensions(rule.lhs(), typing, this.getGTS().getFamily(), this.options.SYMMETRY_REDUCTION, getGTS().getRecord());
//				// TODO nothing allows to determine whether a given concrete part is indeed possible (w.r.t. multiplicities)
//
//				// For all concrete part, generate the set of materialisations and transform
//				for (ConcretePart cp : ext) {
//					SetMaterialisations smat = new SetMaterialisations(cp, (DefaultAbstrGraph) ag, match, this.options); // TODO this cast ?
//					
//					// Two rule events are needed, one for the actual transformation (with matching into the concrete part)
//					// and one for labelling the transition (with matching into to the abstract graph)
//					RuleEvent transfEvent = new SPOEvent((SPORule) rule, smat.updateMatch(match), this.getGTS().getRecord(), false);
//					RuleEvent transitionEvent = new SPOEvent((SPORule) rule, match, this.getGTS().getRecord(), false);
//					RuleApplication appl = new DefaultApplication(transfEvent, cp.graph());
//					
//					Collection<AbstrGraph> transformations = smat.transform(appl, this.getGTS().getRecord());
//					
//					for (AbstrGraph transf : transformations) {
//						AbstrGraphState target = new AbstrGraphStateImpl(transf);
//						AbstrGraphState oldState = (AbstrGraphState) this.getGTS().addState(target);
//						this.getGTS().addTransition(new AbstrGraphTransitionImpl(st, transitionEvent, oldState == null ? target : oldState));
//						
//						/* This is a version using AbstrGraphNextState 
//						// First try as a next state
//						AbstrGraphNextState nextState = new AbstrGraphNextStateImpl(st, transitionEvent, transf);
//						AbstrGraphState oldState = (AbstrGraphState) this.getGTS().addState(nextState);
//						if (oldState == null) { // this is a next state
//							this.getGTS().addTransition(nextState);
//						} else { // this is not a next state, so the correspondig transition should be added
//							AbstrGraphTransition agt = new AbstrGraphTransitionImpl(st, transfEvent, oldState);
//							this.getGTS().addTransition(agt);
//						}
//						*/
//					}
//				}
//			}
//		}
//		if (st.isWithoutOutTransition()) { getGTS().setFinal(state); }
//		state.setClosed();
//
//		reporter.stop();
//	}


//	@Override
//	public Iterator<? extends GraphState> getSuccessorIter(GraphState state) {
//		// TODO Need to be implemented for the linear strategy
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	/** @require state of type AbstrGraphState */
//	public Collection<GraphState> getSuccessors(GraphState state) {
//		assert state instanceof AbstrGraphState : "Typing error : " + state + "should be of type AbstrGraphSTate";
//		return super.getSuccessors((AbstrGraphState) state);
//	}


	
	
	// ---------------------------------------------------------------
	// CONSTRUCTORS, FIELDS AND STANDARD METHODS
	// ---------------------------------------------------------------

	
	
	// ---------------------------------------------------------------
	// NON PUBLIC METHODS ARE NOT IMPLEMENTED
	// ---------------------------------------------------------------
//	@Override
//	protected GraphTransition createTransition(RuleApplication appl, GraphState source, GraphState target, boolean symmetry) {
//		throw new UnsupportedOperationException();
//	}



//	@Override
//	/** This is used by PartialOrderStrategy, and by the getSuccessorIter() and explore() methods of the super class */
//	protected RuleApplier getApplier(GraphState state) {
//		throw new UnsupportedOperationException();
//	}


	
}
