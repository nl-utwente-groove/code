package groove.explore;

import groove.explore.strategy.Strategy;

/** An object that holds a scenario. 
 * Handlers of this type are used by pre-defined scenarios in the {@link java.util.Generator} 
 * for compatibility with old versions of the {@link java.util.Generator}.
 * Such scenarios have an empty result and thus are completely defined by their strategy.
 * @author Iovka Boneva
 */
public interface GeneratorScenarioHandler extends ScenarioHandler {

	/** The strategy used by this scenario.
	 * @return The strategy used by this scenario.
	 */
	public Strategy getStrategy();
	
}
