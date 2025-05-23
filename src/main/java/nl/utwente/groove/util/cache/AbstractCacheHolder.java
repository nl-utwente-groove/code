/* $Id$ */
package nl.utwente.groove.util.cache;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.lts.GraphState;

/**
 * Abstract implementation of the {@link CacheHolder} interface. Provides a
 * {@link CacheReference} field with the required get and set method, and a hook
 * for the initial value.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class AbstractCacheHolder<C extends Cache> implements CacheHolder<C> {
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
     * Convenience method for {@code getCache(true)}
     * @return the cache stored at invocation time, or a fresh cache if the
     *         cache was cleared before
     * @see #getCache(boolean)
     * @see #hasCache()
     */
    public @NonNull C getCache() {
        @Nullable
        C result = getCache(true);
        assert result != null; // due to create parameter
        return result;
    }

    /**
     * Returns the current cache. If the cache is cleared,
     * optionally creates a fresh one.
     * @param create if {@code true}, the cache is created if it was cleared
     * before the call
     * @return the pre-existing cache, or a fresh cache if there was no
     * pre-existing one and {@code create} is set, or {@code null} otherwise
     */
    final public @Nullable C getCache(boolean create) {
        CacheReference<C> cacheReference = getCacheReference();
        C result = cacheReference.get();
        if (result == null && create) {
            assert cacheReference.refersTo(null) : "Old cache reference inconsistent for state "
                + this;
            if (DEBUG && (this instanceof GraphState s) && s.isFull()) {
                System.out
                    .printf("Recreating cache for complete state %s, reference #%s%n", this,
                            cacheReference);
            }
            result = createCache();
            cacheReference = cacheReference.newReference(this, result);
            assert cacheReference.refersTo(result) : "New cache reference inconsistent for state "
                + this;
            setCacheReference(cacheReference);
            result.init();
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
    final public boolean hasCache() {
        return !getCacheReference().refersTo(null);
    }

    /**
     * Callback factory method for creating a fresh, empty cache for this
     * object.
     * @see #getCache()
     */
    abstract protected @NonNull C createCache();

    /**
     * Sets the cache to garbage collectable. This is done by making the cache
     * reference soft. The content of the cache is not changed. Afterward
     * {@link #isCacheCollectable()} is guaranteed to hold.
     * @see CacheReference#setSoft()
     */
    final public void setCacheCollectable() {
        if (hasCache()) {
            getCacheReference().setSoft();
        } else {
            setCacheReference(getCacheReference().getNullReference(false));
        }
        assert isCacheCollectable();
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

    @Override
    final public CacheReference<C> getCacheReference() {
        return this.reference;
    }

    @Override
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

    private final static boolean DEBUG = false;
}
