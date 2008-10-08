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
 * $Id: AutomatonBuilder.java,v 1.3 2008-01-30 09:33:34 iovka Exp $
 */
package groove.control.parse;

import groove.control.ControlShape;
import groove.control.ControlState;
import groove.control.ControlTransition;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.view.FormatException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.antlr.runtime.RecognitionException;

import antlr.SemanticException;

/**
 * 
 * The AutomatonBuilder is used by the parser, checker and builder (generated with antlr)
 * to create the ControlShape's representing the control program.
 * 
 * This class can be used to create en automaton by calling the public methods.
 * 
 * @author Arend Rensink
 * @version $Revision $
 */
public class AutomatonBuilder extends Namespace {

	/** label used to identify delta in init result. */
	public static final String _DELTA_ = "/%/__DELTA__/%/";

    /** label used to identify other keyword in transition label. */
    public static final String _OTHER_ = "/%/__OTHER__/%/";

    /** label used to identify any keyword in transition label. */
    public static final String _ANY_ = "/%/__ANY__/%/";
    
	private HashSet<ControlTransition> mergeCandidates = new HashSet<ControlTransition>();
	
	private HashSet<String> openScopes = new HashSet<String>();
	
	private ControlShape programShape;
	
	private ControlShape current;
	private String currentName;
	
	/** container for all transitions, to be iterated when merging states. */
	private HashSet<ControlTransition> transitions = new HashSet<ControlTransition>();
	
	/** Holds the active start state, a local startstate for the next parsed block. */
	private ControlState currentStart;
	
	/** Holds the active end state, a local endstate for the next parsed block */
	private ControlState currentEnd;
	
	/**
	 * Returns the current start state.
	 */
	public ControlState getStart() {
		return currentStart;
	}
	
	/**
	 * Returns the current end state.
	 */
	public ControlState getEnd() {
		return currentEnd;
	}
	
	/** Sets the current start and end states to a given pair of states. */
	public void restore(ControlState start, ControlState end) {
		this.currentStart = start;
		this.currentEnd = end;
		debug("restore: start = " + start + ", end = " + end);
	}
	
	public void openScope(String name) throws SemanticException {
		if( openScopes.contains(name)) 
			throw new SemanticException("Recursive procedure calls are not allowed (" + name + ")");
		
		ControlShape cs = new ControlShape(currentStart, currentEnd, name);
		storeTransition(cs);
		//cs.setParent(current); // now done by storeTransition for all transitions
		this.current = cs;
		this.currentName = name;
		openScopes.add(name);
	}
	
	
	public void closeScope() {
		openScopes.remove(currentName);
		this.current = current.getParent();
		if( this.current != null ) {
			currentName = this.current.getText();
		} else {
			currentName = null;
		}
	}
	
	/**
	 * Start method for building the automaton of a program
	 */
	public void startProgram() {
		current = new ControlShape(null, null, "program");
		
		programShape = current;
		
		currentStart = new ControlState(current);
		currentEnd = new ControlState(current);
		
		current.setSource(currentStart);
		current.setTarget(currentEnd);
		
		currentEnd.setSuccess();
		current.setStart(currentStart);
		currentName = current.getLabel();

		current.addState(currentStart);
		current.addState(currentEnd);
	}
	
	/** Returns the control shape currently being built. */
	public ControlShape currentShape() {
		return this.current;
	}
	
	/**
	 * Finished building the automaton of the program.
	 */
	public void endProgram() throws RecognitionException {
		closeScope();
	}
	
	/**
	 * Adds a new labelled transition between the current start and end states.
	 * @param label the label of the new transition
	 */
	public void addTransition(String label) {
		ControlTransition ct = new ControlTransition(currentStart, currentEnd, label);
		// basic init stuff: if an outgoing transitions is added, the init of the state gets the label added
		currentStart.addInit(label);
		
		debug("addTransition: " + ct);
		
		storeTransition(ct);
	}

    /**
     * Adds an "other" transition between the current start and end states.
     */
    public void addOther() {
        ControlTransition ct = new ControlTransition(currentStart, currentEnd, _OTHER_);
        
        // basic init stuff: if an outgoing transitions is added, the init of the state gets the label added
        currentStart.addInit(_OTHER_);
        
        debug("addOther: " + ct);
        
        storeTransition(ct);
    }

    /**
     * Adds an "any" transition between the current start and end states.
     */
    public void addAny() {
        ControlTransition ct = new ControlTransition(currentStart, currentEnd, _ANY_);
        
        // basic init stuff: if an outgoing transitions is added, the init of the state gets the label added
        currentStart.addInit(_ANY_);
        
        debug("addOther: " + ct);
        
        storeTransition(ct);
    }
    
	/**
	 * Adds a new lambda-transition between the current start and end states.
	 */
	public void addLambda() {
		ControlTransition ct = new ControlTransition(currentStart, currentEnd);
		
		debug("addLambda: " + ct);
		
		testMerge(ct);
		
		storeTransition(ct);
	}
	
	public void tagDelta(ControlState state) {
		state.addInit(_DELTA_);
		debug("tagDelta: " + state);
	}
	
	public void initCopy(ControlState source, ControlState target) {
		debug("initCopy: " + source + " to " + target);
		
		target.addInit(source);
		
		debug("initCopy: updated target " + target);
	}

	public void deltaInitCopy(ControlState source, ControlState target) {
		debug("deltaInitCopy: " + source  + " to " + target);
		if( target.getInit().contains(_DELTA_)) {
			debug("deltaInitCopy: Delta found in " + target + target.getInit() );
			target.delInit(_DELTA_); 
			target.addInit(source);
			debug("deltaInitCopy: updated target " + target );
		} else {
			debug("deltaInitCopy: No delta found");
		}
	}
	
	public void fail(ControlState state, ControlTransition trans) {
		debug("fail: " + state + " to " + trans);
//		if( state.getInit().contains(_DELTA_) ) {
//			checkOrphan.add(trans.target());
//			rmTransition(trans);
//		} else {
			trans.setFailureFromInit(state);
//		}
		debug("fail: updated trans = " + trans);
	}
	
	
	public ControlTransition addElse() {
		ControlTransition ct = new ControlTransition(currentStart, currentEnd);
		storeTransition(ct);
		return ct;
	}
	
	public ControlState newState() {
		ControlState newState = new ControlState(current);
		current.addState(newState);
		debug("newState: created " + newState);
		return newState;
	}
	
	
	public void storeTransition(ControlTransition ct) {
		this.transitions.add(ct);
		current.addTransition(ct);
		ct.setParent(current);
	}
	
	public void rmTransition(ControlTransition ct) {
		debug("rmTransition: removed transition " + ct);
		this.transitions.remove(ct);
		// in case it is a merge candidate
		this.mergeCandidates.remove(ct);
		ct.getParent().removeTransition(ct);
	}
	
	public void rmState(ControlState state) {
		// TODO: make sure the state has no incoming or outgoing edges
		debug("rmState: " + state);
		state.getParent().removeState(state);
	}
	
	/**
	 * Merges currentState and currentEnd
	 * Will result in currentStart to become currentEnd
	 */
	public void merge() {
		
		debug("merge: Merging " + currentStart + " with " + currentEnd);
		
		// so: currentEnd becomes currentStart
		for( ControlTransition ct : this.transitions ) {
			if( ct.source() == currentStart ) {
				ct.setSource(currentEnd);
			}
			if( ct.target() == currentStart ) {
				ct.setTarget(currentEnd);
			}
		}

		if( currentStart.isSuccess() ) {
			currentEnd.setSuccess();
		}
		
		// copy any init values, since the states are considered only seperated by a lambda, 
		// the target init is reachable from the source also
		currentEnd.addInit(currentStart);
		
		rmState(currentStart);
		
		debug("merge: removed " + currentStart);
		debug("merge: updated " + currentEnd);
		
		currentStart = currentEnd;
	}
	
	
	/**
	 * TODO: Remove stupid automaton stuff, e.g. lambda's (by merging)
	 * 
	 */
	public void optimize() {
		
		Set<ControlState> checkOrphan = new HashSet<ControlState>();
		
		Set<ControlTransition> merge  = new HashSet<ControlTransition>();
		
		for( ControlTransition ct : mergeCandidates ) {
			
			boolean sourceProblem = false;
			boolean targetProblem = false;

			// let's first see if the source has any other outgoing transitions (with a different target)
			for( ControlTransition t : transitions ) {
				if( t != ct && t.source() == ct.source() && t.target() != ct.target() ) {
					sourceProblem = true;
				}
			}
			
			// there is only a problem if the target has any incoming transitions other then ct
			for( ControlTransition t : transitions ) {
				if( t != ct && t.target() == ct.target() ) {
					targetProblem = true;
				}
			}
			
			if( !targetProblem && !sourceProblem ) {
				merge.add(ct);	
			}
			
		}
		
		// now we know what to merge we can do it
		for( ControlTransition ct : merge ) {
			checkOrphan.add(ct.target());
			restore(ct.source(), ct.target());
			rmTransition(ct);
			merge();
		}
		
		// now we go over the failure transitions and see if there are delta's
		// transitions are removed, target may be unreachable 
		
		Set<ControlTransition> remove = new HashSet<ControlTransition>();
		
		for( ControlTransition ct : transitions ) {
			if( ct.getFailures().contains(_DELTA_)) {
				checkOrphan.add(ct.target());
				remove.add(ct);
			}
		}
		// do actual remove
		for( ControlTransition ct : remove ) {
			rmTransition(ct);
		}
		
		// removing unreachable states
		for( ControlState t : checkOrphan ) {
			boolean delete = true;
			for( ControlTransition ct : transitions ) {
				if( ct.target() == checkOrphan ) {
					delete = false;
				}
			}
			if( delete && this.programShape.source() != t) {
				rmState(t);
			}
		}
	}
	
	/**
	 * Adds a rule instance to the RuleControlTransitions and then adds the transitions
	 * to the source states of the transitions.
	 * 
	 * @param grammar
	 */
	public void finalize(GraphGrammar grammar) throws FormatException {
		Set<Rule> otherRules = new HashSet<Rule>(grammar.getRules());
		for( ControlTransition transition : this.transitions ) {
			if( transition instanceof ControlShape ) {
				// don't process these
			} else if (hasSpecialLabel(transition)) {
				// don't process these just yet
			} else if( transition.hasLabel() ) {
				Rule rule = grammar.getRule(transition.getLabel());
				if (rule != null) {
					transition.setRule(rule);
					transition.source().add(transition);
					otherRules.remove(rule);
				} else {
					// if the rulename is a group, this will add all child rules.
					Set<Rule> rules = grammar.getChildRules(transition.getText());
					if( !rules.isEmpty() ) {
						ControlTransition childTrans;
						for( Rule childRule : rules) {
							childTrans = new ControlTransition(transition.source(), transition.target(), childRule.getName().name());
							childTrans.setRule(childRule);
							transition.source().add(childTrans);
							otherRules.remove(childRule);
							// this is for viewing purposes only
							childTrans.setVisibleParent(transition);
						}
						// remove the original transition;
						//automaton.removeTransition(transition);
					} else {
						throw new FormatException("Control: Unknown rule reference: " + transition.getText());
					}
				}
			} else {
				// must be either lambda or else..
				transition.source().add(transition);
			}
		}

		// now process the OTHER-transitions
		Set<ControlTransition> otherTransitions = new HashSet<ControlTransition>();
		Iterator<ControlTransition> transIter = this.transitions.iterator();
		while (transIter.hasNext()) {
			ControlTransition trans = transIter.next();
			if (hasSpecialLabel(trans)) {
				transIter.remove();
				trans.getParent().removeTransition(trans);
				java.util.Collection<Rule> addedRules = trans.getLabel().equals(_OTHER_) ? otherRules : grammar.getRules();
				for (Rule rule: addedRules) {
					// create a corresponding transition for each of the other rules
					ControlTransition otherTrans = new ControlTransition(trans.source(), trans.target(), rule.getName().name());
					otherTrans.setRule(rule);
					otherTrans.setParent(trans.getParent());
					trans.source().add(otherTrans);
					otherTransitions.add(otherTrans);
					trans.getParent().addTransition(otherTrans);
				}
			}
		}
		this.transitions.addAll(otherTransitions);
		// collect the names of the other rules
		Set<String> otherRuleNames = new HashSet<String>();
		for (Rule rule: otherRules) {
			otherRuleNames.add(rule.getName().name());
		}
		// update the failure sets of else-transitions
		// this assumes that there are no outgoing lambda's from states that have else-transitions
		for( ControlTransition transition : this.transitions ) {
			Set<String> failureNames = transition.getFailures();
            if (failureNames.remove(_OTHER_)) {
                failureNames.addAll(otherRuleNames);
            }
            if (failureNames.remove(_ANY_)) {
                failureNames.addAll(getRuleNames());
            }
			Set<Rule> failures = new HashSet<Rule>();
			for (String s: failureNames) {
				failures.add(grammar.getRule(s));
			}
			transition.setFailureSet(failures);
		}
		return;
    }
	
	/** Tests if the transition label is special, such as {@link #_ANY_} or {@link #_OTHER_}.*/
	private boolean hasSpecialLabel(ControlTransition trans) {
	    return trans.hasLabel() && (trans.getLabel().equals(_OTHER_) || trans.getLabel().equals(_ANY_));
	}
	
	public void testMerge(ControlTransition ct) {
		mergeCandidates.add(ct);
	}
	
	public void debug(String msg) {
		//System.err.println("debug: " + msg);
	}
}