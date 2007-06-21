package groove.control;

import groove.graph.Element;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.lts.State;
import groove.trans.Rule;
import groove.util.CollectionOfCollections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class ControlState implements State, Location {

	private int stateNumber;
	
	//private Collection<ControlTransition> lambdas = new HashSet<ControlTransition>();

	/** Store the Rules by priority **/
	protected final SortedMap<Integer,Set<Rule>> priorityRuleMap = new TreeMap<Integer, Set<Rule>>(Rule.PRIORITY_COMPARATOR);
	
	/** Store the ControlTransitions by priority (not sure if this is needed **/
	//protected final SortedMap<Integer,Set<ControlTransition>> priorityTransitionMap = new TreeMap<Integer,Set<ControlTransition>>(Rule.PRIORITY_COMPARATOR);

	/** store all transitions in a set with the associated rule as key **/
	private HashMap<Rule, Set<ControlTransition>> ruleTransitionMap = new HashMap<Rule, Set<ControlTransition>>();
	
	
	public ControlState(int stateNumber)
	{
		this.stateNumber = stateNumber;
	}
	
	public boolean isClosed() {
		return true;
	}

	public int compareTo(Element obj) {
		// TODO Auto-generated method stub
		return getStateNumber() - ((ControlState) obj).getStateNumber();
	}

	public Element imageFor(NodeEdgeMap elementMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getStateNumber()
	{
		return stateNumber;
	}
	
	@Deprecated
	public State newState() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Deprecated
	public Node newNode() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void add(ControlTransition transition) {
		int priority = transition.getPriority();
		
		Rule rule = transition.rule();
		
		// store transition by priority
		/*
		Set<ControlTransition> priorityTransitionSet = priorityTransitionMap.get(priority);
		if (priorityTransitionSet == null) {
			priorityTransitionMap.put(priority, priorityTransitionSet = createTransitionSet());
		}
		priorityTransitionSet.add(transition);
		*/

		// store rule by priority
		Set<Rule> priorityRuleSet = priorityRuleMap.get(priority);
		if( priorityRuleSet == null ) {
			priorityRuleMap.put(priority, priorityRuleSet = createRuleSet());
		}
		priorityRuleSet.add(rule);

		// store transition by rule
		Set<ControlTransition> ruleTransitionSet = ruleTransitionMap.get(rule);
		if( ruleTransitionSet == null ) {
			ruleTransitionMap.put(rule, ruleTransitionSet = createTransitionSet());
		}
		ruleTransitionSet.add(transition);
	}

	
    /**
     * Returns an unmodifiable view upon the underlying collection of transitions.
     * The result is ordered by descending priority, and within each priority,
     * by alphabetical order of the names.
     * Don't invoke {@link Object#equals} on the result!
     * @ensure <tt>result: Label -> Rule</tt>
     */
    /*
	public Collection<ControlTransition> getTransitions() {
    	// TODO: Needs testing
    	Collection<ControlTransition> result = null;
    	//if (result == null) {
    		result = Arrays.asList(new CollectionOfCollections<ControlTransition>(priorityTransitionMap.values()).toArray(new ControlTransition[0]));
    			//ruleSet = result;
    	//}
    	return result;
    }
    */
    
    public SortedMap<Integer, Set<Rule>> getRuleMap()
    {
    	return priorityRuleMap;
    }
    
	private Set<ControlTransition> createTransitionSet()
	{
		return new TreeSet<ControlTransition>();
	}
	
	private Set<Rule> createRuleSet()
	{
		return new TreeSet<Rule>();
	}
	
	@Override
	public String toString()
	{
		return "ControlState(" + stateNumber +")";
	}
	
	public Set<ControlTransition> getTransitions(Rule rule) {
		return ruleTransitionMap.get(rule);
	}
	
}
