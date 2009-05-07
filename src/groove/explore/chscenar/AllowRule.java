package groove.explore.chscenar;

/** Represents a rule that allows certain combinations
 * of strategy - result - acceptor to form a scenario.
 * 
 * @author Iovka Boneva
 *
 */
public interface AllowRule {
	
	/** Checks whether the rule allows a combination of a strategy, result and acceptor.
	 * To be used for non conditional strategies.
	 * @param strategy 
	 * @param result 
	 * @param acceptor 
	 * @return
	 */
	public boolean isAllowedConfiguration (Class<?> strategy, 
											Class<?> result, 
											Class<?> acceptor);
}
