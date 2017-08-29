package groove.util.collect;

/**
 * Interface for encoding hash codes and equality of objects of a given type.
 */
public interface Equator<T> {
    /**
     * Returns the hash code for a given object, according to this equator.
     */
    public int getCode(T key);

    /**
     * Method that determines if two objects, presumably with the same hash
     * codes, are actually to be considered equal. Where applicable,
     * <code>oldKey</code> is a known value, e.g., already in some set, and
     * <code>newKey</code> is a new object to be compared with the old one.
     * The method should only be called if
     * <code>getCode(newKey) == getCode(oldKey)</code>.
     * @param newKey the first object to be compared
     * @param oldKey the second object to be compared
     * @return <code>true</code> if <code>newKey</code> is considered equal
     *         to <code>oldKey</code>.
     */
    public boolean areEqual(T newKey, T oldKey);

    /**
     * Signals if all objects with the same code are considered equal, i.e., if
     * {@link #areEqual(Object, Object)} always returns <code>true</code>. If
     * so, the equality test can be skipped.
     * @return if <code>true</code>, {@link #areEqual(Object, Object)} always
     *         returns <code>true</code>
     */
    public boolean allEqual();
}