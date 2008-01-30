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
import groove.control.ElseControlTransition;
import groove.control.LambdaControlTransition;
import groove.control.RuleControlTransition;
import groove.trans.GraphGrammar;
import groove.trans.Rule;

import java.util.HashSet;
import java.util.Set;

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

	private HashSet<String> openScopes = new HashSet<String>();
	
	private ControlShape current;
	private String currentName;
	
	/** container for all transitions, to be iterated when merging states. */
	private HashSet<ControlTransition> transitions = new HashSet<ControlTransition>();
	
	/** Holds the active start state, a local startstate for the next parsed block. */
	private ControlState currentStart;
	
	/** Holds the active end state, a local endstate for the next parsed block */
	private ControlState currentEnd;
	
	/**
	 * Returns the current ControlState.
	 * @return ControlState
	 */
	public ControlState getStart() {
		return currentStart;
	}
	
	/**
	 * Returns the current ControlState.
	 * @return ControlState
	 */
	public ControlState getEnd() {
		return currentEnd;
	}
	
	public void restore(ControlState start, ControlState end) {
		this.currentStart = start;
		this.currentEnd = end;
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
		}
		else {
			currentName = null;
		}
	}
	
	/**
	 * Start method for building the automaton of a program
	 * @throws SemanticException
	 */
	public void startProgram() throws SemanticException {
		current = new ControlShape(null, null, "program");
		
		currentStart = new ControlState(current);
		currentEnd = new ControlState(current);
		
		current.setSource(currentStart);
		current.setTarget(currentEnd);
		
		currentEnd.setSuccess();
		current.setStart(currentStart);
		currentName = "program";

		current.addState(currentStart);
		current.addState(currentEnd);
	}
	
	public ControlShape currentShape() {
		return this.current;
	}
	
	/**
	 * Finished building the automaton of the program
	 * @throws SemanticException
	 */
	public void endProgram() throws SemanticException {
		closeScope();
		// TODO: remove this check if all works fine
		if( current != null )
			throw new SemanticException("Scope not closed: " + currentName);
	}
	

	/**
	 * A newly created transition between the current start and end is added to the current shape and the source node;
	 * @param label
	 */
	public void addTransition(String label) {
		ControlTransition ct = new RuleControlTransition(currentStart, currentEnd, label);
		//currentStart.add(ct);
		storeTransition(ct);
	}
	
	public void addLambda() {
		ControlTransition ct = new LambdaControlTransition(currentStart, currentEnd);
		//currentStart.add(ct);
		storeTransition(ct);
	}
	
	public void addElse() {
		ControlTransition ct = new ElseControlTransition(currentStart, currentEnd);
		//currentStart.add(ct);
		storeTransition(ct);
	}
	
	public ControlState newState() {
		ControlState newState = new ControlState(current);
		current.addState(newState);
		return newState;
	}
	
	
	public void storeTransition(ControlTransition ct) {
		this.transitions.add(ct);
		current.addTransition(ct);
		ct.setParent(current);
	}
	
	public void rmTransition(ControlTransition ct) {
		this.transitions.remove(ct);
		ct.getParent().removeTransition(ct);
	}
	
	public void rmState(ControlState state) {
		// TODO: make sure the state has no incoming or outgoing edges
		state.getParent().removeState(state);
	}
	
	/**
	 * Merges currentState and currentEnd
	 * Will result in currentStart to become currentEnd
	 */
	public void merge() {
		
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
		rmState(currentStart);
		currentStart = currentEnd;
	}
	
	
	/**
	 * TODO: Remove stupid automaton stuff, e.g. lambda's (by merging).
	 * 
	 */
	public void optimize() {
		// remove elses when there are lambda's

		Set<ControlTransition> remove = new HashSet<ControlTransition>();
		// find an else-transition
		for( ControlTransition t1 : transitions ) {
			if( t1 instanceof ElseControlTransition ) {
				ControlState source1 = t1.source();
				for( ControlTransition t2 : transitions ) {
					if( source1 == t2.source() && t2 instanceof LambdaControlTransition ) {
						remove.add(t1);
					}
				}
			}
		}
		
		
		Set<ControlState> checkOrphan = new HashSet<ControlState>();
		
		for( ControlTransition ct : remove ) {
			rmTransition(ct);
			checkOrphan.add(ct.target());
		}
		
		// removing unreachablestates
		for( ControlState t : checkOrphan ) {
			boolean delete = true;
			for( ControlTransition ct : transitions ) {
				if( ct.target() == checkOrphan ) {
					delete = false;
				}
			}
			if( delete ) {
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
	public void finalize(GraphGrammar grammar) {
		for( ControlTransition transition : this.transitions )
		{
			if( transition instanceof RuleControlTransition ) {
				RuleControlTransition RT = (RuleControlTransition) transition;
				
				Rule rule = grammar.getRule(RT.getText());
				if( rule != null ) {
					RT.setRule(rule);
					transition.source().add(transition);
				}
				else
				{
					// if the rulename is a group, this will add all child rules.
					Set<Rule> rules = grammar.getChildRules(RT.getText());
					if( !rules.isEmpty() ) {
						RuleControlTransition childTrans;
						for( Rule childRule : rules) {
							//automaton.removeTransition(transition);
							childTrans = new RuleControlTransition(transition.source(), transition.target(), childRule.getName().name());
							childTrans.setRule(childRule);
							transition.source().add(childTrans);
							// this is for viewing purposes only
							childTrans.setVisibleParent(transition);
						}
						// remove the original transition;
					}
				}
			}
			else {
				// must be either lambda or else..
				transition.source().add(transition);
			}
		}

		// update the failure sets of else-transitions
		// this assumes that there are no outgoing lambda's from states that have else-transitions
		for( ControlTransition transition : this.transitions ) {
			if( transition instanceof ElseControlTransition ) {
				((ElseControlTransition)transition).setFailureSet(transition.source().rules());
			}
		}
    }
}