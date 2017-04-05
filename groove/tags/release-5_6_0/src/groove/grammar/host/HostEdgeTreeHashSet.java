package groove.grammar.host;

import groove.util.collect.TreeHashSet;

import java.util.Collection;

/**
 * Specialisation of a set of edges that relies on the 
 * edge hashcode uniquely identifying the edge.
 */
abstract public class HostEdgeTreeHashSet extends TreeHashSet<HostEdge> {
    /** Creates an empty edge set. */
    public HostEdgeTreeHashSet() {
        this(DEFAULT_CAPACITY);
    }

    /** Creates an empty edge set with a given initial capacity. */
    public HostEdgeTreeHashSet(int capacity) {
        super(capacity, 2, 3);
    }

    /** Creates a copy of an existing edge set. */
    public HostEdgeTreeHashSet(HostEdgeTreeHashSet other) {
        super(other);
    }

    /** Creates a copy of a set of edges. */
    public HostEdgeTreeHashSet(Collection<? extends HostEdge> other) {
        this(other.size());
        addAll(other);
    }

    @Override
    protected boolean allEqual() {
        return true;
    }

    @Override
    protected boolean areEqual(HostEdge newKey, HostEdge oldKey) {
        assert newKey.equals(oldKey);
        return true;
    }

    @Override
    protected int getCode(HostEdge key) {
        return key.getNumber();
    }
}