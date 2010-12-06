package groove.graph;

import groove.util.TreeHashSet;

import java.util.Set;

/**
 * Specialisation of a set of edges that relies on the 
 * edge hashcode uniquely identifying the edge.
 */
public class DefaultEdgeSet extends TreeHashSet<DefaultEdge> {
    /** Creates an empty edge set. */
    public DefaultEdgeSet() {
        this(DEFAULT_CAPACITY);
    }

    /** Creates an empty edge set with a given initial capacity. */
    public DefaultEdgeSet(int capacity) {
        super(capacity, 2, 3);
    }

    /** Creates a copy of an existing edge set. */
    public DefaultEdgeSet(DefaultEdgeSet other) {
        super(other);
    }

    /** Creates a copy of a set of edges. */
    public DefaultEdgeSet(Set<DefaultEdge> other) {
        this(other.size());
        addAll(other);
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