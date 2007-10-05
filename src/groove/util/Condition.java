/* $Id: Condition.java,v 1.2 2007-10-05 08:31:52 rensink Exp $ */
package groove.util;

/**
 * Interface for a condition on a given object.
 * The only functionality is to check if the condition is satisfied.
 * Use this interface when the test for a certain condition can be
 * isolated well.
 * @author Arend Rensink
 * @version $Revision $
 * @deprecated due to name overlap with the (new) {@link groove.trans.Condition}, and lack of use
 */
@Deprecated
public interface Condition<S> {
	/** 
	 * Tests if this condition is satisfied on a given subject.
	 * @param subject the subject to be tested
	 * @return <code>true</code> if this condition is satisfied for <code>subject</code>.
	 */ 
	boolean isSatisfied(S subject);
}
