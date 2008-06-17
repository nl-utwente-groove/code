package groove.explore.util;

import java.util.Iterator;


/** An iterator that additionally implements a {@ink #last()} method 
 * to access the last returned element. This allows to 
 * retrieve the same element of the iterator in different contexts.
 * This functionality is needed by an {@link ExploreCache}.
 * @author Iovka Boneva
 *
 * @param <T>
 */
public interface ResumableIterator<T> extends Iterator<T> {
	
	/** The object that was last returned by the Iterator's {@link #next()} method.
	 * @return the object that was last returned by the Iterator's 
	 * {@link #next()} method, or <code>null</code> if {@link #next()}
	 * was never called before.
	 */
	public T last();
}
