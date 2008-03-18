package groove.explore;

import groove.explore.result.ConditionalAcceptor;
import groove.explore.result.ExploreCondition;
import groove.explore.strategy.ConditionalStrategy;

/** A scenario handler that additionally allows to set a condition.
 * Typical usage would be with a scenario with a {@link ConditionalAcceptor}, and
 * with {@link ConditionalStrategy}.
 * 
 * @author Iovka Boneva
 * @param <C> Type of the condition.
 */
public interface ConditionalScenarioHandler<C> extends ScenarioHandler {

	/** Sets the condition.
	 * The condition should be set before a call of {@link #playScenario()}.
	 * @param condition
	 * @param name A short name for the condition, to be used for instance
	 * the name of the scenario.
	 */
	public void setCondition(ExploreCondition<C> condition, String name);
	
	/** Sets the condition, with a possibility to negate it.
	 * The condition should be set before a call of {@link #playScenario()}.
	 * @param condition
	 * @param name A short name for the condition, to be used for instance.
	 * @param negated Whether the condition should be negated.
	 * the name of the scenario.
	 */
	public void setCondition(ExploreCondition<C> condition, String name, boolean negated);
	
	/** The type of the condition. */
	public Class<?> getConditionType ();
}
