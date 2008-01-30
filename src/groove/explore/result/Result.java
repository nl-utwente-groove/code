package groove.explore.result;


import groove.explore.Scenario;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/** A set of objects that are the result of the execution of
 * some {@link Scenario}. Whether the result is fully computed 
 * is defined by the {@link #done()} method.
 * Typical such objects are elements of a graph transition system
 * (states, paths, etc.).
 * @author 
 *
 * @param <T> The type of the objects stored in the result.
 */
public abstract class Result<T> {
	
	/** The elements stored in this result. */
	protected Collection<T> elements;
	
	/** Creates a result with an empty set of elements. */
	public Result() {
		elements = new TreeSet<T>();
	}

	/** Adds an element to the result.
	 * @param t
	 */
	public void add(T t) {
		elements.add(t);
	}
	
	
	/** The set of elements contained in the result.
	 * @return
	 */
	public Collection<T> getResult() {
		return elements;
	}
	

	/** Indicates whether the result is computed.
	 * @return <code>true</code> if the result is computed, <code>false</code> otherwise.
	 * When the result is completed, no more elements should be added to it.
	 */
	public abstract boolean done(); 
}
