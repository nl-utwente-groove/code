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
 * $Id: AutomatonBuilder.java,v 1.1 2007-11-22 15:39:12 fladder Exp $
 */
package groove.control.parse;

import groove.control.ControlShape;
import groove.control.ControlState;
import groove.control.ControlTransition;
import groove.control.ElseControlTransition;
import groove.control.LambdaControlTransition;
import groove.control.RuleControlTransition;

import java.util.HashSet;

import antlr.SemanticException;
import antlr.collections.AST;

public class AutomatonBuilder extends Namespace {
	
	private Namespace namespace;
	
	private HashSet<String> openScopes = new HashSet<String>();
	
	private ControlShape current;
	private String currentName;
	
	private HashSet<ControlTransition> transitions = new HashSet<ControlTransition>();
	
	
	public AutomatonBuilder() {
	}
	
	private ControlState currentStart;
	private ControlState currentEnd;
	
	public ControlState getStart() {
		return currentStart;
	}
	
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
		cs.setParent(current);
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
}