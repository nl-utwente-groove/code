package groove.explore.chscenar;

import java.util.ArrayList;
/** Represents a union of {@link AllowRule}. 
 * @author Iovka Boneva
 *
 */
@Deprecated
@SuppressWarnings("all")
public class AllowRuleUnion implements AllowRule {

	public boolean isAllowedConfiguration(Class<?> strategy, Class<?> result, Class<?> acceptor) {
		for (AllowRule rule : this.rules) {
			if (rule.isAllowedConfiguration(strategy, result, acceptor)) {
				return true;
			}
		}
		return false;
	}

	/** Adds a rule to the union. */
	public void addRule (AllowRule rule) {
		this.rules.add(rule);
	}
	
	@Override
	public String toString () {
		String result = new String();
		for (AllowRule rule : this.rules) {
			result += "RULE\n";
			result += rule.toString();
		}
		return result;
	}
	
	private ArrayList<AllowRule> rules = new ArrayList<AllowRule>();
}
