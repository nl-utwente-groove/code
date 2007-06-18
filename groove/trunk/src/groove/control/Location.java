package groove.control;

import groove.trans.Rule;

import java.util.Collection;

public interface Location {

	public Collection<ControlTransition> getTransitions();

	public boolean isAllowed(Rule rule);
	
	public Location getTarget(Rule rule);
}
