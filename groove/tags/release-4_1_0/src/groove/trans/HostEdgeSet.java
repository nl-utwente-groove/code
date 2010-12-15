package groove.trans;

import groove.util.TreeHashSet;

import java.util.Set;

/**
 * Specialisation of a set of edges that relies on the 
 * edge hashcode uniquely identifying the edge.
 */
public class HostEdgeSet extends TreeHashSet<HostEdge> {
    /** Creates an empty edge set. */
    public HostEdgeSet() {
        this(DEFAULT_CAPACITY);
    }

    /** Creates an empty edge set with a given initial capacity. */
    public HostEdgeSet(int capacity) {
        super(capacity, 2, 3);
    }

    /** Creates a copy of an existing edge set. */
    public HostEdgeSet(HostEdgeSet other) {
        super(other);
    }

    /** Creates a copy of a set of edges. */
    public HostEdgeSet(Set<HostEdge> other) {
        this(other.size());
        addAll(other);
    }

    @Override
    protected boolean allEqual() {
        return true;
    }

    @Override
    protected boolean areEqual(HostEdge newKey, HostEdge oldKey) {
        return true;
    }

    @Override
    protected int getCode(HostEdge key) {
        return key.getNumber();
    }
}