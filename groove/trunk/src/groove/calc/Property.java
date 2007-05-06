/* $Id: Property.java,v 1.2 2007-05-06 10:47:54 rensink Exp $ */
package groove.calc;

/**
 * Interface to wrap a simple condition on a subject type. 
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class Property<S> {
	/** Indicates if this property is satisfied by a given object of type <code>S</code>. */
	abstract public boolean isSatisfied(S value);
	
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
