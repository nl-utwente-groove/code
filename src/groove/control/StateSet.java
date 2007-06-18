package groove.control;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class StateSet {
	
	private Set<ControlState> states;
	
	
	public StateSet()
	{
		this.states = new HashSet<ControlState>();
	}
	
	public void add(ControlState state)
	{
		this.states.add(state);
	}
	
	public Iterator iterator() 
	{
		return states.iterator();
	}
}
