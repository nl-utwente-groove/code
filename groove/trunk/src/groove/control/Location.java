package groove.control;import groove.explore.util.LocationCache;import groove.lts.GraphState;import groove.trans.Rule;
import java.util.Set;/** * Interface representing the "location" part of a GraphState. * Implementations can enable reuse of instances by having * the methods decide their return value on the LocationCache * passed in the methods' parameters.  *  * @author Tom Staijen * @version $Revision $ */
public interface Location {	/** return the target of a location when traversing a rule, given a LocationCache **/
	public Location getTarget(Rule rule, LocationCache cache);	/** returns the set of rules that have not been explored yet, including rules that are enabled by failures in the cache **/	public Set<Rule> moreRules(LocationCache cache);	/** returns whether this would be a success given the associated graph state **/
	public boolean isSuccess(GraphState state);	/** create a LocationCache compatible with this location **/
	public LocationCache createCache();
}
