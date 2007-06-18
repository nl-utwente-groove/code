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

	public Location getTarget(Rule rule) {
		return ruleTransitionMap.get(rule).target();
	}

	private int stateNumber;
	
	private Collection<LambdaControlTransition> lambdas = new HashSet<LambdaControlTransition>();
	private Collection<ElseControlTransition> elses = new HashSet<ElseControlTransition>();
	
	/** Store the Rules by priority **/
	protected final SortedMap<Integer,Set<Rule>> priorityRuleMap = new TreeMap<Integer, Set<Rule>>(Rule.PRIORITY_COMPARATOR);
	
	/** Store the RuleControlTransitions by priority (not sure if this is needed **/
	protected final SortedMap<Integer,Set<ControlTransition>> priorityTransitionMap = new TreeMap<Integer,Set<ControlTransition>>(Rule.PRIORITY_COMPARATOR);

	/** Store the transitions in a plain set (not sure if this is needed **/
	private Collection<RuleControlTransition> transitionSet;
	
	/** Storage for the rules ordered by priority, auto generated from priorityRuleMap **/
	private Collection<Rule> ruleSet;
	private HashMap<Rule, RuleControlTransition> ruleTransitionMap = new HashMap<Rule, RuleControlTransition>();
	
	
	public ControlState(int stateNumber)
	{
		this.stateNumber = stateNumber;
	}
	
	public boolean isAllowed(Rule rule)
	{
		return getRules().contains(rule);
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
		if( transition instanceof LambdaControlTransition )
			lambdas.add((LambdaControlTransition)transition);
		else if (transition instanceof ElseControlTransition )
			elses.add((ElseControlTransition)transition);
		else if( transition instanceof RuleControlTransition )
		{
			int priority = transition.getPriority();
			RuleControlTransition rTransition = (RuleControlTransition) transition;
			Rule rule = rTransition.rule();
			
			// add the rule to the priority map
			Set<ControlTransition> priorityTransitionSet = priorityTransitionMap.get(priority);
			// if there is not yet any rule with this priority, create a set
			if (priorityTransitionSet == null) {
				priorityTransitionMap.put(priority, priorityTransitionSet = createTransitionSet());
			}
			priorityTransitionSet.add(transition);
			
			Set<Rule> priorityRuleSet = priorityRuleMap.get(priority);
			if( priorityRuleSet == null ) {
				priorityRuleMap.put(priority, priorityRuleSet = createRuleSet());
			}
			priorityRuleSet.add(rule);
			
			// add the transition to the rule->transition map
			
			ruleTransitionMap.put(rTransition.rule(), rTransition);
		}
	}

	
	public Collection<LambdaControlTransition> getLambdaTransitions() 
	{
		return this.lambdas;
	}
	
	public Collection<ElseControlTransition> getElseTransitions()
	{
		return this.elses;
	}
	
	
    /**
     * Returns an unmodifiable view upon the underlying collection of transitions.
     * The result is ordered by descending priority, and within each priority,
     * by alphabetical order of the names.
     * Don't invoke {@link Object#equals} on the result!
     * @ensure <tt>result: Label -> Rule</tt>
     */
    public Collection<ControlTransition> getTransitions() {
    	// TODO: Needs testing
    	Collection<ControlTransition> result = null;
    	//if (result == null) {
    		result = Arrays.asList(new CollectionOfCollections<ControlTransition>(priorityTransitionMap.values()).toArray(new ControlTransition[0]));
    			//ruleSet = result;
    	//}
    	return result;
    }
    
    public Collection<Rule> getRules() {
    	if( ruleSet == null ) {
    		ruleSet = new ArrayList<Rule>();
    		Collection<ControlTransition> transitions = getTransitions();
    		for( Iterator<ControlTransition> it = transitions.iterator(); it.hasNext(); )
    		{
    			ControlTransition cs = it.next();
    			if( cs instanceof RuleControlTransition )
    			{
    				ruleSet.add(((RuleControlTransition)cs).rule());
    			}
    		}
    	}
    	return ruleSet;
    }

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
	
}
