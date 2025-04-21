package nl.utwente.groove.util.collect;

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
    public Likeness areEqual(T newKey, T oldKey);

    /**
     * Signals if all objects with the same code are considered equal, i.e., if
     * {@link #areEqual(Object, Object)} always returns something else than {@link Likeness#DISTINCT}. If
     * so, the equality test can be skipped.
     * @return if Equality#DISTINCT, {@link #areEqual(Object, Object)} must be called;
     * otherwise, {@link #areEqual(Object, Object)} always returns this value
     */
    default public Likeness allEqual() {
        return Likeness.DISTINCT;
    }
}