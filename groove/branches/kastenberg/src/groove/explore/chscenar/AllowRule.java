package groove.explore.chscenar;

/** Represents a rule that allows certain combinations
 * of strategy - result - acceptor to form a scenario.
 * 
 * @author Iovka Boneva
 *
 */
@Deprecated
@SuppressWarnings("all")
public interface AllowRule {
    /**
     * Checks whether the rule allows a combination of a strategy, result and
     * acceptor. To be used for non conditional strategies.
     */
    public boolean isAllowedConfiguration(Class<?> strategy, Class<?> result,
            Class<?> acceptor);
}
