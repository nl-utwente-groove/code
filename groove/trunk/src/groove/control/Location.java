package groove.control;

import groove.explore.util.LocationCache;
import groove.lts.GraphState;
import groove.trans.Rule;

import java.util.Set;

public interface Location {

	public Location getTarget(Rule rule, LocationCache cache);

	public Set<Rule> moreRules(LocationCache cache);
	
	public Set<Rule> getFailureDependency(Rule rule);

	public boolean isSuccess(GraphState state);
	
	public LocationCache createCache();
}
