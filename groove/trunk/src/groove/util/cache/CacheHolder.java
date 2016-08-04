/**
 *
 */
package groove.util.cache;

/**
 * Holder of a {@link CacheReference} to a cache of type <code>C</code>. The
 * requirement on the holder is (essentially) that it has a
 * <code>cacheReference</code> field (of type <code>CacheReference<R></code>)
 * with a simple getter and setter.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface CacheHolder<C> {
    /**
     * Returns the cache reference of this holder. This may be <code>null</code>
     * if the reference has not (yet) been initialised.
     * @return the cache reference of this holder; possibly <code>null</code>
     */
    public CacheReference<C> getCacheReference();

    /**
     * Sets the cache reference. This is called from {@link CacheReference} if
     * the current cache has been garbage collected, i.e., the referent has been
     * set to <code>null</code>.
     * @param reference the new cache reference; non-<code>null</code>
     *        (although the referent may be <code>null</code>)
     */
    public void setCacheReference(CacheReference<C> reference);
}
