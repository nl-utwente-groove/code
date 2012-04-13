package groove.control;
import java.util.Set;
public interface Location {
	public Location getTarget(Rule rule, LocationCache cache);
	public boolean isSuccess(GraphState state);
	public LocationCache createCache();
}