/* $Id: Property.java,v 1.1 2007-04-27 22:06:59 rensink Exp $ */
package groove.calc;

/**
 * Interface to wrap a simple condition on a subject type. 
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class Property<S> {
	/** Indicates if this property is satisfied by a given object of type <code>S</code>. */
	abstract boolean isSatisfied(S graph);
	
	/** 
	 * Creates and returns a property that returns <code>true</code> on all 
	 * objects of a generic type.
	 */
	static public <T> Property<T> createTrue() {
		return new Property<T>() {
			@Override
			public boolean isSatisfied(T state) {
				return true;
			}
		};
	}
}
