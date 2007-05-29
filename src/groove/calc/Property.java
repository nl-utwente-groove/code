/* $Id: Property.java,v 1.3 2007-05-29 21:36:09 rensink Exp $ */
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
	 * Provides a description of the value(s) that satisfy this property.
	 * This implementation returns <code>null</code>. 
	 */
	public String getDescription() {
		return null;
	}
	
	/** 
	 * Provides a comment on this property.
	 * This can be a description of the thing the property is testing.
	 * This implementation returns <code>null</code>. 
	 */
	public String getComment() {
		return null;
	}
	
	/** 
	 * Creates and returns a property that returns <code>true</code> on all 
	 * objects of a generic type.
	 */
	static public <T> Property<T> createTrue() {
		return new True<T>();
	}
	
	/** Property subclass that always returns true. */
	static public class True<S> extends Property<S> {
		@Override
		public boolean isSatisfied(S state) {
			return true;
		}
	}
}
