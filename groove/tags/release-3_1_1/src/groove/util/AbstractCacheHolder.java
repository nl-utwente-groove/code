/* $Id: AbstractCacheHolder.java,v 1.4 2008-01-30 09:32:10 iovka Exp $ */
package groove.util;

/**
 * Abstract implementation of the {@link CacheHolder} interface. Provides a
 * {@link CacheReference} field with the required get and set method, and a hook
 * for the initial value.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class AbstractCacheHolder<C> implements CacheHolder<C> {
    /**
     * Creates a holder initialised on a given cache reference. The reference of
     * this holder is initialised to a null reference, through a call of
     * {@link #createNullReference(CacheReference)}.
     */
    protected AbstractCacheHolder(CacheReference<C> template) {
        this.reference = createNullReference(template);
    }

    /**
     * Lazily creates and returns a cache. Cache creation is deferred to
     * {@link #createCache()}.
     * @return the cache stored at invocation time, or a fresh cache if the
     *         cache was cleared before
     */
    final public C getCache() {
        C result = getCacheReference().get();
        if (result == null) {
            result = createCache();
            setCacheReference(getCacheReference().newReference(this, result));
        }
        return result;
    }

    /**
     * Clears the stored graph cache reference. This frees the cache for
     * clearing, if that has not yet occurred, and saves memory by sharing a
     * single null reference.
     */
    public void clearCache() {
        getCacheReference().clear();
    }

    /**
     * Tests if the cache is currently cleared.
     */
    final public boolean isCacheCleared() {
        return getCacheReference().get() == null;
    }

    /**
     * Callback factory method for creating a fresh, empty cache for this
     * object.
     * @see #getCache()
     */
    abstract protected C createCache();

    /**
     * Sets the cache to garbage collectable. This is done by making the cache
     * reference soft. The content of the cache is not changed. Afterward
     * {@link #isCacheCollectable()} is guaranteed to hold.
     * @see CacheReference#setSoft()
     */
    final public void setCacheCollectable() {
        if (isCacheCleared()) {
            setCacheReference(getCacheReference().getNullReference());
        } else {
            getCacheReference().setSoft();
        }
    }

    /**
     * Tests if the cache is garbage collectable. This corresponds to testing if
     * the cache reference is not strong.
     * @return <code>true</code> if the cache reference is soft
     * @see CacheReference#isStrong()
     */
    final public boolean isCacheCollectable() {
        return !getCacheReference().isStrong();
    }

    final public CacheReference<C> getCacheReference() {
        return this.reference;
    }

    final public void setCacheReference(CacheReference<C> reference) {
        this.reference = reference;
    }

    /**
     * Callback method to provide the initial value of the cache reference. Note
     * that this method is called at construction time, so the <code>this</code>
     * object may not have been fully initialised.
     * @return either {@link CacheReference#newNullReference()} called on
     *         <code>template</code>, or, if <code>template</code> is
     *         <code>null</code>, through
     *         {@link CacheReference#newInstance()}.
     */
    protected CacheReference<C> createNullReference(CacheReference<C> template) {
        if (template == null) {
            return CacheReference.newInstance();
        } else {
            return template.newNullReference();
        }
    }

    /** The internally stored reference. */
    private CacheReference<C> reference;
}
