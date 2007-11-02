package groove.graph;

import groove.util.TreeHashSet;

/** 
 * Specialisation of a set of edges, for use inside this class.
 */
public class DefaultEdgeSet extends TreeHashSet<DefaultEdge> {
    /** Creates an empty edge set. */
    public DefaultEdgeSet() {
        super(DEFAULT_CAPACITY);
    }
    
    /** Creates an empty edge set with a given initial capacity. */
    public DefaultEdgeSet(int capacity) {
        super(capacity);
    }
    
    /** Creates a copy of an existing edge set. */
    public DefaultEdgeSet(DefaultEdgeSet other) {
        super(other);
    }

	@Override
	protected boolean allEqual() {
		return true;
	}

	@Override
	protected boolean areEqual(DefaultEdge newKey, DefaultEdge oldKey) {
		return true;
	}

	@Override
	protected int getCode(DefaultEdge key) {
		return key.getNumber();
	}	    
}