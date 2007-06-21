package groove.control;

import groove.trans.Rule;

import java.util.Collection;
import java.util.Set;

public interface Location {
	
	public Set<ControlTransition> getTransitions(Rule rule);
	
}
