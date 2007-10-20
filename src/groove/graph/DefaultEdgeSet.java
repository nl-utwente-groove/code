package groove.graph;

import groove.util.TreeHashSet;

/** 
 * Specialisation of a set of edges, for use inside this class.
 */
class DefaultEdgeSet extends TreeHashSet<DefaultEdge> {
    /** Creates an empty edge set. */
    public DefaultEdgeSet() {
        super();
    }
    
    /** Creates a copy of an existing edge set. */
    public DefaultEdgeSet(DefaultEdgeSet other) {
        super(other);
    }

	@Override
	protected boolean allEqual() {
		return false;
	}

	@Override
	protected boolean areEqual(DefaultEdge newKey, DefaultEdge oldKey) {
		return newKey == oldKey;
	}

	@Override
	protected int getCode(DefaultEdge key) {
		return key.hashCode();
	}	    
}