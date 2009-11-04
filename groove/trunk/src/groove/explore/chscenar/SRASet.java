package groove.explore.chscenar;


/** Represents a set of triples strategy - acceptor - result classes.
 * The containment operation takes into account subclassing. That is, if
 * (s, r, a) is in the set of triples, then (s', r', a') is considered contained
 * in the set whenever s' is a subclass of s, r' is a subclass of r' and
 * a' is a subclass of a.
 * @author Iovka Boneva
 */
@Deprecated
@SuppressWarnings("all")
public interface SRASet {

	/** Tests whether a triple strategy - result - acceptor is contained in the set. */
	public boolean contains (Class<?> strategy, Class<?> result, Class<?> acceptor);
	
}
