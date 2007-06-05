/* $Id: Function.java,v 1.1 2007-04-27 22:06:59 rensink Exp $ */
package groove.calc;

/**
 * Interface wrapping a function definition.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class Function<S,T> {
	/** Applies this function to a parameter of type <code>S</code>. */
	abstract public T apply(S arg);
	
	/** 
	 * Creates and returns an identify function for a generic type.
	 */
	static public <T> Function<T,T> createId() {
		return new Function<T,T>() {
			@Override
			public T apply(T object) {
				return object;
			}
		};
	}
}
