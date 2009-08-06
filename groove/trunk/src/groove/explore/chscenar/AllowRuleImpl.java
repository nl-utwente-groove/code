package groove.explore.chscenar;

import java.util.ArrayList;

/** A rule representing a set of allowed combinations of strategy - result - acceptor.
 * The set of allowed combinations is of the form ASet \ FC
 * where
 * - ASet is a set of triples represented by a {@link SRASet}
 * - FC is a union of triples, each component of which is represented by a {@link SRASet}.
 * 
 */
public class AllowRuleImpl implements AllowRule {
	
    public boolean isAllowedConfiguration(Class<?> strategy,
            Class<?> result,
            Class<?> acceptor) {
        if (!this.allowedSet.contains(strategy, result, acceptor)) {
            return false;
        }
        for (SRASet forbid : this.forbidden) {
            if (forbid.contains(strategy, result, acceptor)) {
                return false;
            }
        }
        return true;
    }
	
	/** Sets the allowed combinations. */
	public void setAllowed (SRASet set) {
		this.allowedSet = set;
	}
	
	/** Adds a component set to the forbidden combinations. */
	public void addForbidden (SRASet set) {
		this.forbidden.add(set);
	}
	
	@Override
	public String toString() {
		String result = new String();
		result += "::ALLOW\n";
		result += this.allowedSet.toString();
		
		for (SRASet set : this.forbidden) {
			result += "::DENY\n";
			result += set.toString();
		}
		result += "\n";
		return result;
	}
	
	// --------------------------------------------------------------------------
	// FIELDS, CONSTRUCTORS, STANDARD METHODS
	// --------------------------------------------------------------------------
	/** An array of allowed sets of triples. 
	 * The rule consists of the union of the allowed[i] \ forbidden[i], for all i.
	 * @see #forbidden 
	 */
	private SRASet allowedSet;
	/** An array of forbidden sets of triples. 
	 * @see #allowedSet
	 * */
	private ArrayList<SRASet> forbidden = new ArrayList<SRASet>();
		
}
