package groove.explore.chscenar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/** An implementation of a {@link SRASet}.
 * The set is of the form SS x SR x SA, where
 * - x is the Cartesian product operator,
 * - SS is a set of strategy classes,
 * - SR is a set of result classes,
 * - SA is a set of acceptor classes.
 * @author Iovka Boneva
 *
 */
public class SRASetImpl implements SRASet {
	
	public boolean contains (Class<?> strategy, Class<?> result, Class<?> acceptor) {
		return isInStrategies(strategy) && isInResults(result) && isInAcceptors(acceptor);
	}
	
	
	/** Adds a strategy to the set of strategies. 
	 * No effect if <code>s</code> is not a subclass of {@link ScenarioChecker.STRATEGY_CLASS}. 
	 */
	public void addStrategy (Class<?> s) {
		addComponent (s, this.ss, ScenarioChecker.STRATEGY_CLASS);
	}

	/** Adds a result to the set of results. 
	 * No effect if <code>r</code> is not a subclass of {@link ScenarioChecker.RESULT_CLASS}. 
	 */
	public void addResult (Class<?> r) {
		addComponent (r, this.rs, ScenarioChecker.RESULT_CLASS);
	}
	
	/** Adds an acceptor to the set of acceptors. 
	 * No effect if <code>r</code> is not a subclass of {@link ScenarioChecker.RESULT_CLASS}. 
	 */
	public void addAcceptor (Class<?> a) {
		addComponent (a, this.as, ScenarioChecker.ACCEPTOR_CLASS);
	}
	
	/** A help method for {@link #addStrategy(Class)}, {@link #addResult(Class)}
	 * and {@link #setAcceptor(Class)}.
	 * @param comp The component to add
	 * @param toSet The field to which it should be added
	 * @param constraint The type-constraint on <code>comp</code>.
	 */
	private void addComponent (Class<?> comp, Set<Class<?>> toSet, Class<?> constraint) {
		if (constraint.isAssignableFrom(comp)) {
				toSet.add(comp);
		}
	}

	private boolean isInStrategies (Class<?> strategy) {
		return isSubclassInSet (strategy, ss);
	}

	private boolean isInResults (Class<?> result) {
		return isSubclassInSet (result, rs);
	}
	
	private boolean isInAcceptors (Class<?> acceptor) {
		return isSubclassInSet (acceptor, as);
	}
	
	/** Checks whether a class is subclass of some of the classes
	 * contained in the set.
	 */
	private <T> boolean isSubclassInSet (Class<?> c, Set<Class<? extends T>> set) {
		for (Class<?> cc : set) {
			if (cc.isAssignableFrom(c)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String result = new String();
		result += ":STRATEGY\t";
		result += listToString(ss) + "\n";

		result += ":RESULT\t\t";
		result += listToString(rs) + "\n";
		
		result += ":ACCEPTOR\t";
		result += listToString(as) + "\n";
		
		return result;
	}
	
	/** Helper method. */
	private String listToString (Set<Class<?>> set) {
		String result = new String();
		Iterator<Class<?>> it = set.iterator();
		if (it.hasNext()) {
			result += it.next().getName();
		}
		while (it.hasNext()) {
			result += ", " + it.next().getName();
		}
		return result;
	}
	
	// --------------------------------------------------------------------------
	// FIELDS, CONSTRUCTORS, STANDARD METHODS
	// --------------------------------------------------------------------------
	
	/** Creates a new, empty set. */
	public SRASetImpl () {
		this.ss = new HashSet<Class<?>>();
		this.rs = new HashSet<Class<?>>();
		this.as = new HashSet<Class<?>>();
	}
	
	/** The set of allowed strategies. */
	private Set<Class<?>> ss;
	/** The set of allowed results. */
	private Set<Class<?>> rs;
	/** The set of allowed acceptors. */
	private Set<Class<?>> as;

	
}
