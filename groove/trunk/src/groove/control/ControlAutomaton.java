package groove.control;

import groove.graph.AbstractGraphShape;
import groove.graph.GraphShapeCache;
import groove.lts.LTS;
import groove.lts.State;
import groove.lts.Transition;
import groove.trans.Rule;
import groove.trans.RuleSystem;
import groove.view.FormatException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ControlAutomaton extends AbstractGraphShape<GraphShapeCache> implements LTS {
	
	private String program;
	
	public static final String LAMBDA = "_";
	public static final String ELSE = "_e";
	
	private State startState;
	private Set nodeSet = new HashSet<State>();
	private Set edgeSet = new HashSet<Transition>();
	private Set<State> finalStates = new HashSet<State>();
	
	private RuleSystem ruleSystem;
	
	public void clear()
	{
		this.nodeSet.clear();
		this.edgeSet.clear();
		this.startState = null;
		this.finalStates.clear();
	}
	
	public ControlAutomaton(RuleSystem ruleSystem)
	{
		this.ruleSystem = ruleSystem;
	}
	
	public Set<? extends Transition> edgeSet() {
		return edgeSet;
	}

	public void setStartState(ControlState cs)
	{
		this.startState = cs;
	}
	
	public void addFinalState(ControlState cs)
	{
		this.finalStates.add(cs);
	}
	
	public Set<? extends State> nodeSet() {
		return nodeSet;
	}
	
	public Collection<? extends State> getFinalStates() {
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
	
	public ControlState addState(ControlState state)
	{
		this.nodeSet.add(state);
		return state;
	}
	
	public Rule getRule(String name)
	{
		return this.ruleSystem.getRule(name);
	}
	
	public void addTransition(ControlTransition transition)
	{
		this.edgeSet.add(transition);
	}
	
	public void addRuleTransition(ControlState source, ControlState target, String rulename)
	{
		Rule rule = ruleSystem.getRule(rulename);

		/*
		if( rule == null )
			throw new FormatException("Rule " + rulename + " not found in current rulesystem.");
		
		*/

		RuleControlTransition rct = new RuleControlTransition(source, target, rule);
		this.addTransition(rct);
		source.add(rct);
	}
	
	public void addLambdaTransition(ControlState source, ControlState target)
	{
		ControlTransition ct = new LambdaControlTransition(source, target);
		source.add(ct);
		this.addTransition(ct);
	}
	
	public void addElseTransition(ControlState source, ControlState target) {
		ControlTransition ct = new ElseControlTransition(source, target);
		source.add(ct);
		this.addTransition(ct);
	}

	public void setProgram(String program) {
		this.program = program;
	}
	
	public String getProgram() {
		return program;
	}
}
