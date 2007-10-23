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
 * $Id: ControlAutomaton.java,v 1.6 2007-08-26 07:23:33 rensink Exp $
 */
package groove.control;

import groove.graph.AbstractGraphShape;
import groove.graph.GraphShapeCache;
import groove.lts.LTS;
import groove.lts.State;
import groove.lts.Transition;
import groove.trans.Rule;
import groove.trans.RuleSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ControlAutomaton extends AbstractGraphShape<GraphShapeCache> implements LTS {
	
	/** holds the (and and only) startState of the graphshape **/ 
	private State startState;
	
	private Set nodeSet = new HashSet<State>();
	private Set edgeSet = new HashSet<Transition>();
	private Set<ControlState> finalStates = new HashSet<ControlState>();
	
	/** the current rulesystem, needed to fetch Rule's given a rulename **/
	private Set<String> ruleNames;
	private RuleSystem ruleSystem;
	
	public void clear()
	{
		this.nodeSet.clear();
		this.edgeSet.clear();
		this.startState = null;
		this.finalStates.clear();
	}
	
	public ControlAutomaton(Set<String> ruleNames) {
		this.ruleNames = ruleNames;
	}
	
	/**
	 * Return all edges in this graphshape 
	 */
	public Set<ControlTransition> edgeSet() {
		return edgeSet;
	}

	/**
	 * Set the startState of the automaton
	 * @param cs 
	 */
	public void setStartState(ControlState cs)
	{
		this.startState = cs;
	}
	
	public void addFinalState(ControlState cs)
	{
		this.finalStates.add(cs);
	}
	
	public Set<ControlState> nodeSet() {
		return nodeSet;
	}
	
	public Collection<ControlState> getFinalStates() {
		return finalStates;
	}

	public boolean hasFinalStates() {
		return !finalStates.isEmpty();
	}

	public boolean isFinal(State state) {
		return finalStates.contains(state);
	}

	public boolean isOpen(State state) {
		return false;
	}

	public State startState() {
		return startState;
	}

	/**
	 * 
	 * The method to create new instances of Control State such that 
	 * its stateNumber is unique in this ControlAutomaton.
	 * This state will be added to the ControlAutomaton before it is returned.
	 * 
	 * @return a fresh control state
	 */
	protected ControlState newState()
	{
		return addState(new ControlState(nodeSet().size()));
	}
	
	/**
	 * Stores a state and returns is.
	 * @param state
	 * @return
	 */
	public ControlState addState(ControlState state)
	{
		this.nodeSet.add(state);
		return state;
	}
	
	public Rule getRule(String name)
	{
		return this.ruleSystem.getRule(name);
	}
	
	public void removeTransition(ControlTransition trans) {
		this.edgeSet.remove(trans);
	}
	
	public void addTransition(ControlTransition transition) {
		this.edgeSet.add(transition);
	}
	
	public void addTransition(ControlState source, ControlState target, String rulename)
	{
		ControlTransition ct = new ControlTransition(source, target, rulename);
		this.edgeSet.add(ct);

		//source.add(ct);
	}
}
